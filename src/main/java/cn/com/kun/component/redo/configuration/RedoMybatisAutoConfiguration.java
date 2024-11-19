package cn.com.kun.component.redo.configuration;

import cn.com.kun.component.redo.core.RedoInnerMapperBeanDefinitionRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自动配置类，由spring.factories定义该类
 * author:xuyaokun_kzx
 * date:2021/10/29
 * desc:
*/
@ConditionalOnProperty(prefix = "kunghsu.redo.mybatis", value = {"enabled"}, havingValue = "true", matchIfMissing = false)
@Configuration
@ComponentScan({"cn.com.kun.component.redo"})
@Import({RedoInnerMapperBeanDefinitionRegistrar.class})
public class RedoMybatisAutoConfiguration {


}
