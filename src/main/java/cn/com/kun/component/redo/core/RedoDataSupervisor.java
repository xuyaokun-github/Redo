package cn.com.kun.component.redo.core;

import cn.com.kun.component.redo.bean.entiy.RedoTaskDO;
import cn.com.kun.component.redo.threadpool.RedoThreadPoolRejectedExecutionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

@Component
public class RedoDataSupervisor {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedoThreadPoolRejectedExecutionHandler.class);

    @Autowired
    private RedoTaskDaoVisitor redoTaskDaoVisitor;

    @Qualifier("redoRemoveDataExecutor")
    @Autowired(required = false)
    private Executor redoRemoveDataExecutor;

    /**
     * 微服务应用名
     */
    @Value("${spring.application.name}")
    private String applicationName;

    public void remove(String redoTaskId) {

        //删除任务放入线程池
        redoRemoveDataExecutor.execute(()->{
            try {
                List<RedoTaskDO> redoTaskDOList = null;
                if (redoTaskId == null || redoTaskId.length() < 1){
                    redoTaskDOList = redoTaskDaoVisitor.getRedoDao().selectRedoTaskList(applicationName);
                }else {
                    //使用redoTaskId进行查询
                    redoTaskDOList = redoTaskDaoVisitor.getRedoDao().selectByRedoTaskId(applicationName, redoTaskId);
                }
                if (redoTaskDOList != null && redoTaskDOList.size() > 0){
                    redoTaskDOList.stream().parallel().forEach(redoTaskDO -> {
                        LOGGER.info("Redo数据清理,数据id:{} redoTaskId:{}", redoTaskDO.getId(), redoTaskDO.getRedoTaskId());
                        redoTaskDaoVisitor.getRedoDao().delete(redoTaskDO.getId());
                    });
                }
            }catch (Exception e){
                LOGGER.error("Redo清理数据异常,redoTaskId:{}", redoTaskId, e);
            }
        });
    }


}
