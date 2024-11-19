package cn.com.kun.component.redo.callback;

import cn.com.kun.component.redo.bean.vo.RedoReqParam;
import cn.com.kun.component.redo.bean.vo.RedoResult;
import cn.com.kun.component.redo.core.RedoExecFlagHolder;
import cn.com.kun.component.redo.utils.RedoSpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 *
 * author:xuyaokun_kzx
 * date:2024/11/5 19:45
 * desc:
*/
public class DefualtRedoTaskCallback implements RedoTaskCallback{

    private final static Logger LOGGER = LoggerFactory.getLogger(DefualtRedoTaskCallback.class);

    private Object targetBean;

    private Method method;

    private String beanName;

    public DefualtRedoTaskCallback(Object targetBean, Method method) {
        this.targetBean = targetBean;
        this.method = method;
    }

    public DefualtRedoTaskCallback(Object targetBean, Method method, String beanName) {
        this.targetBean = targetBean;
        this.method = method;
        this.beanName = beanName;
    }

    @Override
    public RedoResult redo(RedoReqParam redoReqParam) {

        RedoResult result = RedoResult.SUCCESS;
        try {
            //初始化补偿执行标志
            RedoExecFlagHolder.set("Y");
            //反射调用
            invokeBizMethod(redoReqParam);

        }catch (Exception e){
            LOGGER.error("DefualtRedoTaskCallback process fail", e);
            result = RedoResult.BIZ_ERROR;
        }finally {
            RedoExecFlagHolder.remove();
        }

        return result;
    }

    private Object invokeBizMethod(RedoReqParam redoReqParam) throws InvocationTargetException, IllegalAccessException {

        Object result = null;
        /*
            注意：假如执行业务逻辑时，可能会再次进入补偿，不能一直循环进重试表，制造出多余的补偿记录
            这里用的是反射触发，不会触发AOP，不会再次保存补偿记录
            但因为没触发AOP，所以这个方法可能会少执行一些原有的切面逻辑，这个也可能会带来问题。
            TODO 这是个问题，需要处理

         */
        Object[] args = null;
        try {
            Map<String, Object> paramMap = redoReqParam.getParams();
            if (paramMap != null && !paramMap.isEmpty()){
                args = new Object[paramMap.size()];
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    args[i] = paramMap.get(parameters[i].getName());
                }
            }
            Object bizBean = getBizBean();
            result = method.invoke(bizBean, args);
        } catch (Throwable e) {
            LOGGER.error("DefualtRedoTaskCallback invokeBizMethod fail", e);
            throw e;
        }
        return result;
    }

    private Object getBizBean() {

        /*
            bean: 容器中的bean
            targetBean: BeanPostProcessor阶段获取到的bean
         */
        Object bean = null;
        if (beanName != null){
            //从容器中获取bean
            bean = RedoSpringContextUtil.getBean(beanName);
        }

        return bean != null ? bean : targetBean;
    }

}
