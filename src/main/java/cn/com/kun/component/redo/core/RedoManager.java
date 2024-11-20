package cn.com.kun.component.redo.core;

import cn.com.kun.component.redo.bean.entiy.RedoTaskDO;
import cn.com.kun.component.redo.bean.vo.*;
import cn.com.kun.component.redo.callback.RedoTaskCallback;
import cn.com.kun.component.redo.common.exception.RedoTaskCallbackNotFoundException;
import cn.com.kun.component.redo.configuration.RedoProperties;
import cn.com.kun.component.redo.dao.RedoDao;
import cn.com.kun.component.redo.inform.RedoExecInformInterface;
import cn.com.kun.component.redo.inform.RedoExecInformRegistry;
import cn.com.kun.component.redo.invoke.RedoExecInvoker;
import cn.com.kun.component.redo.lock.LockControl;
import cn.com.kun.component.redo.lock.LockControlRegistry;
import cn.com.kun.component.redo.threadpool.RedoExecTaskExecutor;
import cn.com.kun.component.redo.utils.RedoJacksonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
public class RedoManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedoManager.class);

    @Autowired
    private RedoProperties redoProperties;

    @Autowired
    private LockControl lockControl;

    @Autowired
    private RedoTaskDaoVisitor redoTaskDaoVisitor;

    @Autowired
    private RedoExecInvokerVisitor redoExecInvokerVisitor;

    private String lockResourceName;

    /**
     * 微服务应用名
     */
    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired(required = false)
    private RedoExecTaskExecutor redoExecTaskExecutor;

    @PostConstruct
    public void init(){
        lockResourceName = buildLockResourceName();
    }

    private String buildLockResourceName() {
        return "cn.com.kun.component.redo.core.RedoManager.findAndRedo:" + applicationName;
    }

    public boolean addRedoTask(String redoTaskId, RedoReqParam redoReqParam){

        return addRedoTask(redoTaskId, RedoJacksonUtils.toJSONString(redoReqParam));
    }

    /**
     * 任务ID,可以根据内存里注册好的，找到相应的任务，然后入库
     *
     * @param redoTaskId
     * @return
     */
    public boolean addRedoTask(String redoTaskId, String reqParam){

        if (!redoProperties.isEnabled()){
            LOGGER.warn("补偿组件开关未开启，请检查");
            return false;
        }

        //添加补偿任务入库
        RedoTask redoTask = RedoTaskRegisterFactory.findRedoTask(redoTaskId);
        RedoTaskDO redoTaskDO = buildRedoTaskDO(redoTask, reqParam);
        int res = getRedoDao().insert(redoTaskDO);
        return res > 0;
    }

    private RedoTaskDO buildRedoTaskDO(RedoTask redoTask, String reqParam) {

        RedoTaskDO redoTaskDO = new RedoTaskDO();
        redoTaskDO.setApplicationName(applicationName);
        redoTaskDO.setRedoTaskId(redoTask.getRedoTaskId());
        redoTaskDO.setMaxAttempts(redoTask.getMaxAttempts());
        redoTaskDO.setTryForever(redoTask.isTryForever());
        redoTaskDO.setReqParam(reqParam);
        redoTaskDO.setExpiredDate(redoTask.getExpiredSeconds() > 0 ? buildExpiredDate(redoTask.getExpiredSeconds()) : redoTask.getExpiredDate());
        //假如用LocalDateTime类型
        //redoTaskDO.setExpiredDate(LocalDateTime.ofInstant(redoTask.getExpiredDate().toInstant(), ZoneId.systemDefault()));
        //默认用当前时间做查询时间
        redoTaskDO.setQueryTime(new Date());

        return redoTaskDO;
    }

    private Date buildExpiredDate(int expiredSeconds) {

        Date now = new Date();
        return new Date(now.getTime() + expiredSeconds * 1000);
    }

    private RedoDao getRedoDao() {

        return redoTaskDaoVisitor.getRedoDao();
    }

    /**
     *
     * @param redoTaskId
     * @param redoReqParam
     * @return
     */
    public RedoResult invokeRedoTaskCallback(String redoTaskId, RedoReqParam redoReqParam){

        RedoResult redoResult;
        try {
            RedoTaskCallback redoTaskCallback = RedoTaskRegisterFactory.findRedoTaskCallback(redoTaskId);
            redoResult = redoTaskCallback.redo(redoReqParam);
        }catch (Exception e){
            LOGGER.error("invokeRedoTaskCallback error", e);
            //框架是一个组件，假如数据库加载的记录，在本应用中找不到对应的回调实例，说明不是本应用注册的，因此跳过执行
            if (e instanceof RedoTaskCallbackNotFoundException){
                //假如是这种异常，也可以是漏配了，也可能是别的应用注册，不是本应用该处理的，
                // 这种情况，是否应该让各个应用只关注自己相关的，在查询时假如应用标识
                return RedoResult.REDOTASK_CALLBACK_NOT_FOUND;
            }
            //假如执行业务时，再次抛出了异常，只给日志，向上层返回失败标志
            return RedoResult.BIZ_ERROR;
        }
        return redoResult;
    }

    public List<RedoTaskDO> selectRedoTaskList() {

        //扩展点:用户可以决定是否使用数据库作为保存补偿任务的介质，可以使用redis替代
        return getRedoDao().selectRedoTaskList(applicationName);
    }


    /**
     * 不再需要锁逻辑(由定时任务提供锁逻辑)
     *
     * @param applicationName
     */
    public void runRedoByScheduledTask(String applicationName) {

        //根据微服务名获取实现
        RedoExecInvoker invoker = redoExecInvokerVisitor.getExecInvoker(applicationName);
        if (invoker != null){
            RedoExecResVo temp = null;
            while (true){
                //远程访问
                RedoResultVo<RedoExecResVo> resultVo = invoker.exec(applicationName);
                if (resultVo.isSuccess()){
                    RedoExecResVo redoExecResVo = resultVo.getValue();
                    if ("0".equals(redoExecResVo.getFlag())){
                        //结束本次调度
                        return;
                    }
                    temp = runCheck(redoExecResVo, temp);
                }else {
                    LOGGER.warn("远程执行返回失败", resultVo.toString());
                    return;
                }
            }
        }
    }

    private RedoExecResVo runCheck(RedoExecResVo redoExecResVo, RedoExecResVo temp) {

        if (temp != null){
            int newSize = redoExecResVo.getSize();
            int oldSize = temp.getSize();
            if (newSize == oldSize && redoExecResVo.getFirstId() == temp.getFirstId()
                    && redoExecResVo.getLastId() == temp.getLastId() ){
                //假如上一次执行和当前执行的内容头尾相同，则认为内容几乎一样
                //执行退避，延迟调度，让调度慢下来，不要做无用功
                LOGGER.info("补偿全局退避");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        temp  = redoExecResVo;
        return temp;
    }


    public RedoResultVo<RedoExecResVo> redoExec(String applicationName) {

        RedoResultVo resultVo = null;
        try {
            RedoExecResVo redoExecResVo = new RedoExecResVo();
            List<RedoTaskDO> redoTaskDOList = getRedoDao().selectRedoTaskList(applicationName);
            if (CollectionUtils.isEmpty(redoTaskDOList)){
                if (LOGGER.isTraceEnabled()){
                    LOGGER.trace("本次未发现待补偿任务");
                }
                redoExecResVo.setFlag("0");
            }else {
                redoProcess(redoTaskDOList);
                redoExecResVo.setFlag("1");
                redoExecResVo.setSize(redoTaskDOList.size());
                redoExecResVo.setFirstId(redoTaskDOList.get(0).getId());
                redoExecResVo.setLastId(redoTaskDOList.get(redoExecResVo.getSize() -1).getId());
            }
            resultVo = RedoResultVo.valueOfSuccess(redoExecResVo);
        }catch (Exception e){
            LOGGER.error("redoExec error", e);
            resultVo = RedoResultVo.valueOfError("redoExec执行异常");
        }

        return resultVo;
    }


    /**
     * //这个过程应该上锁，多节点互斥
     * <p>
     * 第一个方案，引入数据库锁进行互斥
     * 实现思路：每个微服务抢自己的锁，各个微服务之间不受影响，查库时，只查属于自己应用的记录
     * <p>
     * 第二个方案，是否能用select for update
     * 假如多个微服务都用select for update，锁之间可能会有干扰
     * 这种方案即使where条件里指定了app标识，只查本服务的待补偿记录，但是记录的顺序无法保证
     * 即使app标识加了索引，也无法保证锁一定会走行锁，所以多个服务之间还是会有互相影响
     * <p>
     * 第三个方案，上redis锁或者zk锁，也是可以的，
     * 假如锁出问题，会导致补偿任务被执行多次！业务中最好是加上防重的判断，例如状态机
     */
    public void runRedo() {

        //获取锁控制实例
        LockControl lockControl = getLockControlInstantce();
        //锁资源名称，每个微服务应该使用不同的锁名
        try {
            lockControl.lock(lockResourceName);
            //一次调度，处理完所有记录才开始下一次调度
            List<RedoTaskDO> tempList = new ArrayList<>();
            while (true){
                List<RedoTaskDO> redoTaskDOList = selectRedoTaskList();
                if (CollectionUtils.isEmpty(redoTaskDOList)){
                    if (LOGGER.isTraceEnabled()){
                        LOGGER.trace("本次未发现待补偿任务");
                    }
                    return;
                }

                redoProcess(redoTaskDOList);
                //调度退避逻辑
                runCheck(redoTaskDOList, tempList);
            }
        }catch (Exception e){
            LOGGER.error("findAndRedo error", e);
        }finally {
            lockControl.unlock(lockResourceName);
        }
    }

    /**
     * 调度退避逻辑
     *
     * @param redoTaskDOList
     * @param tempList
     */
    private void runCheck(List<RedoTaskDO> redoTaskDOList, List<RedoTaskDO> tempList) {

        int newSize = redoTaskDOList.size();
        int oldSize = tempList.size();
        if (newSize == oldSize && redoTaskDOList.get(0).getId() == tempList.get(0).getId()
                && redoTaskDOList.get(newSize -1).getId() == tempList.get(oldSize -1).getId() ){
            //假如上一次执行和当前执行的内容头尾相同，则认为内容几乎一样
            //执行退避，延迟调度，让调度慢下来，不要做无用功
            LOGGER.info("补偿全局退避");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        tempList.clear();
        tempList.addAll(redoTaskDOList);
    }

    private void redoProcess(List<RedoTaskDO> redoTaskDOList) {

        CountDownLatch countDownLatch = new CountDownLatch(redoTaskDOList.size());
        try {
            //引入线程池并行处理所有待补偿任务，提高处理速度
            for (RedoTaskDO redoTaskDO : redoTaskDOList){
                redoExecTaskExecutor.execute(()->{
                    try {
                        processRedoTask(redoTaskDO);
                    }catch (Exception e){
                        LOGGER.error("补偿数据处理异常", e);
                    }finally {
                        countDownLatch.countDown();
                    }
                });
            }
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processRedoTask(RedoTaskDO redoTaskDO) {

        boolean isExpire = checkExpireDate(redoTaskDO.getExpiredDate());
//            boolean isExpire = checkExpireDate(Date.from(redoTaskDO.getExpiredDate().atZone(ZoneId.systemDefault()).toInstant()));
        if (isExpire) {
            LOGGER.warn("补偿记录过期，删除记录。id:{} redoTaskId:{}", redoTaskDO.getId(), redoTaskDO.getRedoTaskId());
            deleteRedoTask(redoTaskDO);
            return;
        }
        boolean isReachMaxAttempts = checkMaxAttempts(redoTaskDO);
        if (isReachMaxAttempts){
            LOGGER.warn("补偿次数已达上限，删除记录。id:{} redoTaskId:{}", redoTaskDO.getId(), redoTaskDO.getRedoTaskId());
            deleteRedoTask(redoTaskDO);
            return;
        }
        //该条记录未过期，开始执行补偿
        //反序列化得到补偿参数
        RedoReqParam redoReqParam = RedoJacksonUtils.toJavaObject(redoTaskDO.getReqParam(), RedoReqParam.class);
        //执行业务补偿逻辑
        RedoResult redoResult = invokeRedoTaskCallback(redoTaskDO.getRedoTaskId(), redoReqParam);
        if (redoResult.isSuccess()){
            //执行成功，删除数据库记录（补偿成功也算关键日志，建议输出）
            LOGGER.warn("补偿成功。id:{} redoTaskId:{}", redoTaskDO.getId(), redoTaskDO.getRedoTaskId());
            deleteRedoTask(redoTaskDO);
        }else {
            //执行失败，这里应该立刻循环重试吗？还是先执行一次，然后等下一次从数据库查出之后再执行下一次
            //假如这里立刻开启循环，会影响其他补偿任务，为了公平性，可以先重试一次
            //这里可以设置一个饥饿策略，假如设置了饥饿策略，就循环重试 TODO

            //执行次数 +1
            updateExecTimes(redoTaskDO);
            //修改queryTime
            updateQueryTime(redoTaskDO);
        }

    }


    /**
     * 可以根据系统需要，替换不同的锁实现
     *
     * @return
     */
    private LockControl getLockControlInstantce() {
        //用户注册的锁控制实现
        LockControl userLockControl = LockControlRegistry.getLockControl();
        return userLockControl != null? userLockControl : lockControl;
    }

    private boolean checkMaxAttempts(RedoTaskDO redoTaskDO) {

        //假如执行次数超出N次，发出通知
        if(redoTaskDO.getExecTimes() > redoProperties.getExecTimesOfInform()){
            RedoExecInformInterface informInterface = RedoExecInformRegistry.getRedoExecInformInterface();
            String msg = String.format("RedoTaskId:%s 重试执行次数:%s ID:%s 请关注补偿执行情况", redoTaskDO.getRedoTaskId(),
                    redoTaskDO.getExecTimes(), redoTaskDO.getId());
            if (informInterface != null){
                informInterface.execTimesInform(redoTaskDO.getId(), redoTaskDO.getRedoTaskId(), redoTaskDO.getExecTimes(), msg);
            }else {
                //假如没有注册通知实现，则输出日志
                LOGGER.warn(msg);
            }
        }


        return redoTaskDO.getMaxAttempts() != 0 && redoTaskDO.getExecTimes() >= redoTaskDO.getMaxAttempts();
    }

    /**
     * 避免僵尸数据问题，更新执行次数的同时更新query_time
     *
     * @param redoTaskDO
     */
    private void updateExecTimes(RedoTaskDO redoTaskDO) {
        getRedoDao().updateExecTimes(redoTaskDO.getId());
    }

    private void updateQueryTime(RedoTaskDO redoTaskDO) {

        getRedoDao().updateQueryTime(redoTaskDO.getExecTimes(), redoTaskDO.getId());

    }
    private boolean checkExpireDate(Date expiredDate) {

        if (expiredDate == null){
            //假如未指定过期时间，默认为永不过期
            return false;
        }
        return new Date().after(expiredDate);
    }

    private void deleteRedoTask(RedoTaskDO redoTaskDO) {

        //将执行记录记到 补偿流水表，能反馈组件的执行情况，记录是被成功执行或是已超过待补偿期限
        if (getRedoDao().delete(redoTaskDO.getId()) > 0){
            recordRedoHistory(redoTaskDO);
        }
    }

    private void recordRedoHistory(RedoTaskDO redoTaskDO) {

        //可以设置开关，让用户自行选择，是否需要记补偿流水
        if (redoProperties.isSaveHistoryEnabled()){
            //保存补偿流水
            LOGGER.info("保存补偿记录流水：{}", redoTaskDO.getId());
        }
    }


    public void recordDelete() {

        //每次删除一些
        long count = 0;
        while (true){
            int res = getRedoDao().clear(redoProperties.getRetentionDays());
            count = count + res;
            if (res <= 0){
                break;
            }
        }
        LOGGER.info("补偿记录自动清理完成，本次清理总数：{}", count);
    }
}
