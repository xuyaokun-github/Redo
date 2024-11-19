package cn.com.kun.component.redo.invoke;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedoExecInvokerRegistry {

    private static Map<String, RedoExecInvoker> redoExecInvokers = new ConcurrentHashMap<String, RedoExecInvoker>();

    public static RedoExecInvoker getRedoExecInvoker(String applicationName) {
        return redoExecInvokers.get(applicationName);
    }

    /**
     * 扩展点
     * 假如有需要自定义远程访问实现
     *
     * @param invoker
     */
    public static void registerRedoExecInvoker(String applicationName, RedoExecInvoker invoker) {
        redoExecInvokers.put(applicationName, invoker);
    }
}
