package cn.com.kun.component.redo.annotation;

import cn.com.kun.component.redo.configuration.MyConfiguration;
import cn.com.kun.component.redo.core.InnerMapperBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Redo组件开关
 * author:xuyaokun_kzx
 * date:2021/10/29
 * desc:
*/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
//注意，这里加了自定义的mapper扫描之后，必须要将原有自带的自动配置显示启用
//因为它的逻辑是判断到已有MapperFactoryBean就不会启动自动配置
//直接在Import这加MybatisAutoConfiguration.AutoConfiguredMapperScannerRegistrar.class。会报错
//正确的方式是加@MapperScan注解，指定扫描的本业务应用工程的dao包目录
@Import({InnerMapperBeanDefinitionRegistrar.class
        , MyConfiguration.class
})
public @interface EnableRedo {


}
