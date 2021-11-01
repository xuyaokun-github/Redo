package cn.com.kun.component.redo.annotation;

import cn.com.kun.component.redo.configuration.MyConfiguration;
import cn.com.kun.component.redo.configuration.RedoBeanAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Redo组件开关
 * 将组件的启用权交给用户，用户自行决定是否启用补偿重试组件
 * 之前是只要引入jar包就自动启用，这样会带来一些多余和风险性
 *
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
//最粗暴的解决方式
//使用Redo组件时，需要同时指定@MapperScan，不然自动配置的扫描将会失效
//例子：@MapperScan(basePackages={"com.kun.kunwebdemo.mapper"})
//更简便的方式是让RedoBeanAutoConfiguration负责原basePackages的扫描
@Import({
        RedoBeanAutoConfiguration.class,
        MyConfiguration.class
})
public @interface EnableRedo {


}
