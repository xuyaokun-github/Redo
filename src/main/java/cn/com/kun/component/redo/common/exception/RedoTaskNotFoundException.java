package cn.com.kun.component.redo.common.exception;

public class RedoTaskNotFoundException extends RuntimeException {

    public RedoTaskNotFoundException() {
        super("RedoTask Not Found!");
    }

    public RedoTaskNotFoundException(String message) {
        super(message);
    }

}
