package cn.com.kun.component.redo.annotation;

import cn.com.kun.component.redo.configuration.RedoBeanConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Redo组件开关
 * 将组件的启用权交给用户，用户自行决定是否启用补偿重试组件
 * 之前的设计是只要引入jar包就自动启用，这样会带来一些多余加载和额外风险
 *
 * author:xuyaokun_kzx
 * date:2021/10/29
 * desc:
*/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({RedoBeanConfiguration.class})
public @interface EnableRedo {


}
