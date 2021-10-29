package cn.com.kun.component.redo.configuration;

import cn.com.kun.component.clusterlock.dblock.DBClusterLockHandler;
import cn.com.kun.component.redo.core.RedoManager;
import cn.com.kun.component.redo.core.RedoLogger;
import cn.com.kun.component.redo.lock.DatabaseLockControl;
import cn.com.kun.component.redo.schedule.RedoTaskScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置类，由spring.factories定义该类
 * author:xuyaokun_kzx
 * date:2021/10/29
 * desc:
*/
@Configuration
public class RedoBeanConfiguration {

    @Bean
    public RedoLogger redoLogger(){
        return new RedoLogger();
    }

    /*
        定义需要使用到的bean
        虽然能达到目的，但这样相当于我们自己创建了实例，而不是由spring创建
        有另一种方式，是让spring负责扫描这些bean，由spring自己来加载bean定义 TODO
     */
    @Bean
    public RedoManager redoManager(){
        return new RedoManager();
    }

    @Bean
    public DBClusterLockHandler dbClusterLockHandler(){
        return new DBClusterLockHandler();
    }

    @Bean
    public DatabaseLockControl databaseLockControl(){
        return new DatabaseLockControl();
    }

    @Bean
    public RedoTaskScheduler redoTaskScheduler(){
        return new RedoTaskScheduler();
    }


}
