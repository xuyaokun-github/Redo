package cn.com.kun.component.redo.lock.clusterlock.dblock.aspect;

import cn.com.kun.component.redo.lock.clusterlock.annotation.DBClusterLock;
import cn.com.kun.component.redo.lock.clusterlock.dblock.DBClusterLockHandler;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * author:xuyaokun_kzx
 * date:2021/7/7
 * desc:
*/
@Component
@Aspect
public class DBClusterLockAspect {

    private final static Logger LOGGER = LoggerFactory.getLogger(DBClusterLockAspect.class);

    @Autowired
    DBClusterLockHandler dbClusterLockHandler;

    @Value("${dbclusterlock.clusterCode:}")
    private String clusterCode;

    @Pointcut("@annotation(cn.com.kun.component.redo.lock.clusterlock.annotation.DBClusterLock)")
    public void pointCut(){

    }

    @Around(value = "pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        // 获取方法上的EvictCacheNotice注解对象
        DBClusterLock dbClusterLock = method.getAnnotation(DBClusterLock.class);
        //锁资源
        String resourceName = dbClusterLock.resourceName();
        //是否拼接集群名称
        boolean withClusterCode = dbClusterLock.withClusterCode();
        if (withClusterCode && StringUtils.isEmpty(clusterCode)){
            resourceName = clusterCode + ":" + resourceName;
        }
        boolean isLock = dbClusterLockHandler.lock(resourceName);
        if (!isLock){
            throw new RuntimeException("切面获取锁失败");
        }
        Object obj = pjp.proceed();
        boolean isUnLock = dbClusterLockHandler.unlock(resourceName);
        if (!isUnLock){
            throw new RuntimeException("切面释放锁失败");
        }
        return obj;
    }


}
