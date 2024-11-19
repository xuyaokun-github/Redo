package cn.com.kun.component.redo.configuration;

import cn.com.kun.component.redo.lock.DatabaseLockControl;
import cn.com.kun.component.redo.lock.LockControl;
import cn.com.kun.component.redo.utils.RedoLogger;
import cn.com.kun.component.redo.utils.RedoSleeper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 通过Import的方式，加载bean
 * author:xuyaokun_kzx
 * date:2021/10/29
 * desc:
 */
@ComponentScan({"cn.com.kun.component.redo",
        "cn.com.kun.component.jdbc",
        "cn.com.kun.component.distributedlock"})
@Configuration
public class RedoBeanConfiguration {

    /**
     * 简单的bean可以通过这样的方式放入容器
     * @return
     */
    @Bean
    public RedoSleeper redoSleeper(){
        return new RedoSleeper();
    }


    @Bean
    public RedoLogger redoLogger(){
        return new RedoLogger();
    }


    @ConditionalOnMissingBean(LockControl.class)
    @Bean
    public LockControl databaseLockControl(DatabaseLockControl databaseLockControl){
        return databaseLockControl;
    }

}
