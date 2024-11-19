package cn.com.kun.component.redo.annotation;

import java.lang.annotation.*;

/**
 * 用法：加在自定义的补偿入口方法上
 *
 * author:xuyaokun_kzx
 * date:2024/11/13 12:01
 * desc:
*/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedoEntrance {

    String redoTaskId();

}
