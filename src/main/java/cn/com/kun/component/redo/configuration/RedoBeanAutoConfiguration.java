package cn.com.kun.component.redo.configuration;

import cn.com.kun.component.redo.core.InnerMapperBeanDefinitionRegistrar;
import cn.com.kun.component.redo.utils.RedoLogger;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

/**
 * 自动配置类，由spring.factories定义该类
 * author:xuyaokun_kzx
 * date:2021/10/29
 * desc:
*/
@ComponentScan({"cn.com.kun.component"})
@Configuration
@Import({InnerMapperBeanDefinitionRegistrar.class})
public class RedoBeanAutoConfiguration {

    @PostConstruct
    public void init(){

    }

    @Bean
    public RedoLogger redoLogger(){
        return new RedoLogger();
    }

    /*
        定义需要使用到的bean
        下面这种方式虽然定义bean，但这样相当于我们自己创建了实例，而不是由spring创建
        有其他方式，是让spring负责扫描这些bean，由spring自己来加载bean定义
        方式1：在factories文件中增加类的全限定名：
        org.springframework.boot.autoconfigure.EnableAutoConfiguration=class1,class2,class3
     */
//    @Bean
//    public RedoManager redoManager(){
//        return new RedoManager();
//    }
//
//    @Bean
//    public DBClusterLockHandler dbClusterLockHandler(){
//        return new DBClusterLockHandler();
//    }
//
//    @Bean
//    public DatabaseLockControl databaseLockControl(){
//        return new DatabaseLockControl();
//    }
//
//    @Bean
//    public RedoTaskScheduler redoTaskScheduler(){
//        return new RedoTaskScheduler();
//    }


}
