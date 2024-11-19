package cn.com.kun.component.redo.core;

public class RedoExecFlagHolder {

    private static ThreadLocal<String> execFlag = new ThreadLocal<String>();

    public static void set(String flag) {

        execFlag.set(flag);
    }

    public static String get() {

        return execFlag.get();
    }


    public static void remove() {
        execFlag.remove();
    }

}
