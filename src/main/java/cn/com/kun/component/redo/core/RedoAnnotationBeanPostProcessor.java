package cn.com.kun.component.redo.core;


import cn.com.kun.component.redo.annotation.RedoEntrance;
import cn.com.kun.component.redo.annotation.RedoSupport;
import cn.com.kun.component.redo.callback.DefualtRedoTaskCallback;
import cn.com.kun.component.redo.callback.RedoTaskCallback;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 *
 * author:xuyaokun_kzx
 * date:2024/11/6 9:10
 * desc:
*/
@ConditionalOnProperty(prefix = "kunghsu.redo", value = {"enabled"}, havingValue = "true", matchIfMissing = false)
@Component
public class RedoAnnotationBeanPostProcessor implements BeanPostProcessor {


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Class clazz = bean.getClass();

        //仅供业务服务层使用（需要补偿的业务逻辑必须交给spring管理）
        if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Service.class)){

            Method[] methods = clazz.getMethods();
            for (Method method: methods) {
                RedoSupport redoSupport = AnnotationUtils.findAnnotation(method, RedoSupport.class);
                if (redoSupport != null) {
                    // registry jobhandler
                    if (RedoTaskRegisterFactory.getRedoTaskCallback(redoSupport.redoTaskId()) == null){
                        RedoTaskCallback callback = new DefualtRedoTaskCallback(bean, method, beanName);
                        RedoTaskRegisterFactory.addAutoRegisterCallback(redoSupport.redoTaskId(), callback);
                        RedoTaskRegisterFactory.registerRedoTaskCallback(redoSupport.redoTaskId(), callback);
                    }
                }

                //RedoEntrance定义的补偿入口优先级更高，直接覆盖
                RedoEntrance redoEntrance = AnnotationUtils.findAnnotation(method, RedoEntrance.class);
                if (redoEntrance != null){
                    RedoTaskCallback callback = new DefualtRedoTaskCallback(bean, method, beanName);
                    RedoTaskRegisterFactory.addAutoRegisterCallback(redoEntrance.redoTaskId(), callback);
                    RedoTaskRegisterFactory.registerRedoTaskCallback(redoEntrance.redoTaskId(), callback);
                }

            }

        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
