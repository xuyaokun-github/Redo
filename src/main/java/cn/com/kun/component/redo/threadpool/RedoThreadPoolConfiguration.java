package cn.com.kun.component.redo.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * author:xuyaokun_kzx
 * date:2024/11/6 14:56
 * desc:
*/
@ConditionalOnProperty(prefix = "kunghsu.redo", value = {"enabled"}, havingValue = "true", matchIfMissing = false)
@Configuration
public class RedoThreadPoolConfiguration {

    @Autowired
    private RedoThreadPoolProperties redoThreadPoolProperties;

    @Bean("redoRemoveDataExecutor")
    public Executor redoRemoveDataExecutor(RedoThreadPoolRejectedExecutionHandler rejectedExecutionHandler) {

        String key = "redoRemoveDataExecutor";
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("redoRemoveDataExecutor-Thread-");
        executor.setCorePoolSize(redoThreadPoolProperties.getItems().get(key).getCorePoolSize());
        executor.setMaxPoolSize(redoThreadPoolProperties.getItems().get(key).getMaxPoolSize());
        executor.setQueueCapacity(redoThreadPoolProperties.getItems().get(key).getQueueCapacity());
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        return executor;
    }

    /**
     * 扩展点：假如用户没定义RedoExecTaskExecutor bean,则框架创建一个默认的
     *
     * @param rejectedExecutionHandler
     * @return
     */
    @ConditionalOnMissingBean(RedoExecTaskExecutor.class)
    @Bean("redoExecTaskExecutor")
    public RedoExecTaskExecutor redoExecTaskExecutor(RedoThreadPoolRejectedExecutionHandler rejectedExecutionHandler) {

        String key = "redoExecTaskExecutor";
        RedoExecTaskExecutor executor = new RedoExecTaskExecutor();
        executor.setThreadNamePrefix("redoExecTaskExecutor-Thread-");
        executor.setCorePoolSize(redoThreadPoolProperties.getItems().get(key).getCorePoolSize());
        executor.setMaxPoolSize(redoThreadPoolProperties.getItems().get(key).getMaxPoolSize());
        executor.setQueueCapacity(redoThreadPoolProperties.getItems().get(key).getQueueCapacity());
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        return executor;
    }


}
