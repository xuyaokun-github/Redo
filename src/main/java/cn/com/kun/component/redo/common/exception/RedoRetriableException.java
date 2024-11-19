package cn.com.kun.component.redo.common.exception;

/**
 *
 * author:xuyaokun_kzx
 * date:2024/11/5 17:33
 * desc:
*/
public class RedoRetriableException extends RuntimeException {

    public RedoRetriableException() {
        super("可重试异常");
    }

    public RedoRetriableException(String message) {
        super(message);
    }

    public static Exception build() {

        return new RedoRetriableException(null);
    }

    public static Exception build(String message) {

        return new RedoRetriableException(message);
    }

}
