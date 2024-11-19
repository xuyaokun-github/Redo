package cn.com.kun.component.redo.annotation;

import java.lang.annotation.*;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedoSupport {

    String redoTaskId();

    int maxAttempts() default 0;

}
