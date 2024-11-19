package cn.com.kun.component.redo.core;

public class RedoResultHolder {

    private static ThreadLocal<Object> RESULT_OBJECT = new ThreadLocal<Object>();

    public static void set(Object result) {

        RESULT_OBJECT.set(result);
    }

    public static Object get() {

        return RESULT_OBJECT.get();
    }


    public static void remove() {
        RESULT_OBJECT.remove();
    }
}
