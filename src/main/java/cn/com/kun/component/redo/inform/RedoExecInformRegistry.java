package cn.com.kun.component.redo.inform;

public class RedoExecInformRegistry {

    private static RedoExecInformInterface informInterface = null;

    public static RedoExecInformInterface getRedoExecInformInterface() {
        return informInterface;
    }

    /**
     * 扩展点
     * 假如有需要自定义通知实现，可以选择注册
     *
     * @param inf
     */
    public static void registerRedoExecInform(RedoExecInformInterface inf) {
        informInterface = inf;
    }
}
