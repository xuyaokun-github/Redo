package cn.com.kun.component.redo.aspect;

import cn.com.kun.component.redo.annotation.RedoSupport;
import cn.com.kun.component.redo.bean.vo.RedoReqParam;
import cn.com.kun.component.redo.bean.vo.RedoTask;
import cn.com.kun.component.redo.callback.DefualtRedoTaskCallback;
import cn.com.kun.component.redo.callback.RedoTaskCallback;
import cn.com.kun.component.redo.common.exception.RedoRetriableException;
import cn.com.kun.component.redo.core.RedoExecFlagHolder;
import cn.com.kun.component.redo.core.RedoManager;
import cn.com.kun.component.redo.core.RedoResultHolder;
import cn.com.kun.component.redo.core.RedoTaskRegisterFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * RedoSupport注解处理切面
 *
 * author:xuyaokun_kzx
 * date:2024/11/5 17:40
 * desc:
*/
@Component
@Aspect
public class RedoSupportAspect {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedoSupportAspect.class);

    @Autowired
    private RedoManager redoManager;

    @Pointcut("@annotation(cn.com.kun.component.redo.annotation.RedoSupport)")
    public void pointCut(){

    }

    @Around(value = "pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        String execFlag = RedoExecFlagHolder.get();
        if (execFlag != null && execFlag.contains("Y")){
            //表示在补偿过程中再次进入切面，此时不需要再进补偿表
            return pjp.proceed();
        }
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        // 获取方法上的RedoSupport注解对象
        RedoSupport redoSupport = method.getAnnotation(RedoSupport.class);
        String redoTaskId = redoSupport.redoTaskId();

        //补偿Task检查与注册
        redoTaskCheck(redoTaskId, redoSupport);
        //补偿回调实现检查与注册
        redoTaskCallbackCheck(redoTaskId, pjp);

        Object obj = null;
        try {
            obj = pjp.proceed();
        }catch (Throwable e){
            if (e instanceof RedoRetriableException){
                //识别到可重试异常，放入补偿组件
                LOGGER.warn("识别到可重试异常", e);
                RedoReqParam redoReqParam = buildRedoReqParam(pjp);
                LOGGER.info("保存待补偿记录");
                redoManager.saveRedoTask(redoTaskId, redoReqParam);
                /*
                    此时给上游返回什么呢？
                    得分两类情况：
                    假如这个method,上层不关心返回值，则这里返回null即可。
                    假如上层关心返回值，需要基于具体的返回值保留现场或者做其他操作，则这里就不能随便返回了，
                    那此时应该返回什么呢？
                    第一种情况：调用RedoResultHolder设定返回值
                    第二种方式：不用注解方式，得用编程式代码侵入保存补偿记录，同时向上游返回上游的期待结果
                 */
                Object result = RedoResultHolder.get();
                if (result != null){
                    //假如抛出异常的位置设定了返回值，则取用户指定的返回值
                    obj = result;
                }

            }else {
                //其他异常，正常往上抛
                throw e;
            }
        }

        return obj;
    }

    private void redoTaskCallbackCheck(String redoTaskId, ProceedingJoinPoint pjp) {

        /*
            补偿任务都不存在了，补偿回调会存在吗？ 可能存在，也可能不存在。
            有可能用户不想定义补偿task,但又需要针对原方法做一些扩展，可能需要重新定义回调实现。
        */
        if (!RedoTaskRegisterFactory.checkRedoTaskCallback(redoTaskId)){

            RedoTaskCallback autoRegisterCallback = RedoTaskRegisterFactory.getAutoRegisterCallback(redoTaskId);
            if (autoRegisterCallback != null){
                RedoTaskRegisterFactory.registerRedoTaskCallback(redoTaskId, autoRegisterCallback);
                return;
            }

            MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
            Method method = methodSignature.getMethod();
            //注册回调实现
            //注意这里要用getThis，拿到代理对象，假如用getTarget拿到源对象，可能会在重试的过程中丢失一些切面逻辑，产生意向不到的问题。
            RedoTaskCallback callback = new DefualtRedoTaskCallback(pjp.getThis(), method);
            RedoTaskRegisterFactory.registerRedoTaskCallback(redoTaskId, callback);
        }

    }

    private void redoTaskCheck(String redoTaskId, RedoSupport redoSupport) {

        //先确定是否存在注册的补偿Task
        if (!RedoTaskRegisterFactory.checkRedoTask(redoTaskId)){
            //假如不存在，则创建一个默认的补偿任务
            //默认1天后过期,重试次数默认为0
            RedoTask redoTask = RedoTask.newBuilder(redoTaskId).expiredSeconds(24 * 3600).build();
            if (redoSupport.maxAttempts() > 0){
                redoTask.setMaxAttempts(redoSupport.maxAttempts());
            }
            RedoTaskRegisterFactory.registerRedoTask(redoTask);
        }

    }

    private RedoReqParam buildRedoReqParam(ProceedingJoinPoint pjp) {

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = pjp.getArgs();
        Parameter[] parameters = method.getParameters();
        RedoReqParam redoReqParam = new RedoReqParam();
        if (args != null && args.length > 0 && parameters != null && args.length == parameters.length){
            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < args.length; i++) {
                //以方法签名上的参数起名作为key
                params.put(parameters[i].getName(), args[i]);
            }
            redoReqParam.setParams(params);
        }
        return redoReqParam;
    }

}
