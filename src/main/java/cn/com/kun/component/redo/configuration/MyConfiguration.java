package cn.com.kun.component.redo.configuration;

import cn.com.kun.component.redo.utils.RedoSleeper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通过Import的方式，加载bean
 * author:xuyaokun_kzx
 * date:2021/10/29
 * desc:
*/
@Configuration
public class MyConfiguration {

    /**
     * 简单的bean可以通过这样的方式放入容器
     * @return
     */
    @Bean
    public RedoSleeper redoSleeper(){
        return new RedoSleeper();
    }
}
