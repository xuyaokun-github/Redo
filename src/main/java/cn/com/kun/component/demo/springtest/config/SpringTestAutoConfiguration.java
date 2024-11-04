package cn.com.kun.component.demo.springtest.config;

import cn.com.kun.component.redo.core.RedoTaskRegisterFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@ConditionalOnProperty(prefix = "springtest.autoconfig", value = {"enabled"}, havingValue = "true", matchIfMissing = true)
@ComponentScans(value =
        {@ComponentScan(value = "cn.com.kun.component.demo.springtest")})
@EntityScan(basePackages = {"cn.com.kun.component.demo.springtest"})
@Configuration
public class SpringTestAutoConfiguration implements EnvironmentAware {

    private final static Logger LOGGER = LoggerFactory.getLogger(SpringTestAutoConfiguration.class);

    public SpringTestAutoConfiguration() {
        LOGGER.info("SpringTestAutoConfiguration自动配置初始化");
    }

    @Override
    public void setEnvironment(Environment environment) {

    }

}
