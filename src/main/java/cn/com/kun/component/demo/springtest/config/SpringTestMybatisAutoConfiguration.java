package cn.com.kun.component.demo.springtest.config;

import cn.com.kun.component.redo.core.RedoInnerMapperBeanDefinitionRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自动配置类，由spring.factories定义该类 或者 通过开关打开
 *
 * author:xuyaokun_kzx
 * date:2021/10/29
 * desc:
*/
@ConditionalOnProperty(prefix = "springtest.mybatis", value = {"enabled"}, havingValue = "true", matchIfMissing = false)
@Configuration
@ComponentScan({"cn.com.kun.component.demo.springtest.dao"})
@Import({SpringTestInnerMapperBeanDefinitionRegistrar.class})
public class SpringTestMybatisAutoConfiguration {


}