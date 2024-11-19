package cn.com.kun.component.redo.core;

import cn.com.kun.component.redo.bean.vo.RedoTask;
import cn.com.kun.component.redo.callback.RedoTaskCallback;
import cn.com.kun.component.redo.common.exception.RedoTaskCallbackNotFoundException;
import cn.com.kun.component.redo.common.exception.RedoTaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 补偿任务注册工厂
 *
 * author:xuyaokun_kzx
 * date:2021/10/27
 * desc:
*/
public class RedoTaskRegisterFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedoTaskRegisterFactory.class);


    /**
     * 重试任务定义集合
     */
    private static Map<String, RedoTask> redoTasks = new ConcurrentHashMap();

    /**
     * 重试任务回调逻辑集合
     */
    private static Map<String, RedoTaskCallback> redoTaskCallbacks = new ConcurrentHashMap<>();

    /**
     * 重试任务回调逻辑集合(系统自动注册)
     */
    private static Map<String, RedoTaskCallback> autoRegisterRedoTaskCallbacks = new ConcurrentHashMap<>();

    public static void register(RedoTask redoTask, RedoTaskCallback redoTaskCallback){
        registerRedoTask(redoTask);
        registerRedoTaskCallback(redoTask.getRedoTaskId(), redoTaskCallback);
    }

    public static void registerRedoTask(RedoTask redoTask){
        Assert.notNull(redoTask.getRedoTaskId(), "补偿任务业务ID不能为空");
        redoTasks.put(redoTask.getRedoTaskId(), redoTask);
    }

    public static RedoTask findRedoTask(String redoTaskId) throws RedoTaskNotFoundException {
        RedoTask redoTask = redoTasks.get(redoTaskId);
        if (redoTask == null){
            LOGGER.warn("RedoTask [{}] Not found.", redoTaskId);
            throw new RedoTaskCallbackNotFoundException();
        }
        return redoTask;
    }



    /**
     * 注册重试任务回调逻辑
     * @param redoTaskId
     * @param redoTaskCallback
     */
    public static void registerRedoTaskCallback(String redoTaskId, RedoTaskCallback redoTaskCallback){
        Assert.notNull(redoTaskCallback, "补偿任务逻辑不能为空");
        redoTaskCallbacks.put(redoTaskId, redoTaskCallback);
    }

    public static RedoTaskCallback findRedoTaskCallback(String redoTaskId) throws RedoTaskCallbackNotFoundException {
        RedoTaskCallback redoTaskCallback = redoTaskCallbacks.get(redoTaskId);
        if (redoTaskCallback == null){
            LOGGER.warn("redoTaskId:[] can not find RedoTaskCallback");
            throw new RedoTaskCallbackNotFoundException();
        }
        return redoTaskCallback;
    }

    public static RedoTaskCallback getRedoTaskCallback(String redoTaskId) throws RedoTaskCallbackNotFoundException {
        RedoTaskCallback redoTaskCallback = redoTaskCallbacks.get(redoTaskId);
        return redoTaskCallback;
    }

    public static boolean checkRedoTask(String redoTaskId) {

        return redoTasks.containsKey(redoTaskId);
    }

    public static boolean checkRedoTaskCallback(String redoTaskId) {

        return redoTaskCallbacks.containsKey(redoTaskId);
    }

    public static void addAutoRegisterCallback(String redoTaskId, RedoTaskCallback callback) {

        autoRegisterRedoTaskCallbacks.put(redoTaskId, callback);
    }

    public static RedoTaskCallback getAutoRegisterCallback(String redoTaskId) {

        return autoRegisterRedoTaskCallbacks.get(redoTaskId);
    }
}
