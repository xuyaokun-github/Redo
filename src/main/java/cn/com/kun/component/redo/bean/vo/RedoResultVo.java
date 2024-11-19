package cn.com.kun.component.redo.bean.vo;

import java.io.Serializable;
import java.util.HashMap;

public class RedoResultVo<T> implements Serializable {

    private String message;
    private T value;
    private boolean success;
    private String msgCode;
    private HashMap resultMap;

    private RedoResultVo() {
    }

    public static RedoResultVo valueOfSuccess(Object value) {
        RedoResultVo vo = new RedoResultVo();
        vo.value = value;
        vo.success = true;
        vo.msgCode = "000000";
        vo.message = "处理成功";
        return vo;
    }

    public static RedoResultVo valueOfSuccess() {
        return valueOfSuccess(null);
    }

    public static RedoResultVo valueOfError(String msg, Object value) {
        return valueOfError(msg, "999999", null, value);
    }

    public static RedoResultVo valueOfError(String msg, String msgCode, Class source, Object value) {

        RedoResultVo vo = new RedoResultVo();
        vo.value = value;
        vo.message = msg;
        vo.success = false;
        vo.msgCode = msgCode;
        return vo;
    }

    public static RedoResultVo valueOfError(String msg) {
        return valueOfError(msg, "999999", null, null);
    }

    public static RedoResultVo valueOfError(String msg, String msgCode) {
        return valueOfError(msg, msgCode, null, null);
    }

    public String getMessage() {
        return message;
    }

    public RedoResultVo setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public RedoResultVo setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public T getValue() {
        return value;
    }

    public RedoResultVo setValue(T value) {
        this.value = value;
        return this;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public RedoResultVo setMsgCode(String msgCode) {
        this.msgCode = msgCode;
        return this;
    }

    public HashMap getResultMap() {
        return resultMap;
    }

    public void setResultMap(HashMap resultMap) {
        this.resultMap = resultMap;
    }

    /**
     * 失败
     */
    public static RedoResultVo error(String smgCode, String message) {
        RedoResultVo rb = new RedoResultVo();
        rb.setMsgCode(smgCode);
        rb.setMessage(message);
        rb.setValue(null);
        return rb;
    }

    @Override
    public String toString() {
        return "RedoResultVo{" +
                "message='" + message + '\'' +
                ", value=" + value +
                ", success=" + success +
                ", msgCode='" + msgCode + '\'' +
                ", resultMap=" + resultMap +
                '}';
    }
}

