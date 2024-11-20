package cn.com.kun.component.redo.lock;

public final class LockControlRegistry {

    private static LockControl defaultLockControl = null;

    public static LockControl getLockControl() {
        return defaultLockControl;
    }

    /**
     * 扩展点
     * 假如有需要自定义锁实现，可以选择注册
     *
     * @param lockControl
     */
    public static void registerLockControl(LockControl lockControl) {
        defaultLockControl = lockControl;
    }

}
