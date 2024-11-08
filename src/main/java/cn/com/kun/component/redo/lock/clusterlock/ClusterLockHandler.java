package cn.com.kun.component.redo.lock.clusterlock;

/**
 * 分布式锁处理器接口
 * 一开始我通过接口暴露了四个方法：悲观的上锁解锁 + 乐观的上锁解锁
 * 后面发现其实没必要，其实应该将悲观和乐观分别作为一种具体的实现方式，而不是接口方法
 * author:xuyaokun_kzx
 * date:2021/7/7
 * desc:
*/
public interface ClusterLockHandler {

    boolean lock(String resourceName);

    boolean unlock(String resourceName);

//    boolean lock(String resourceName);
//
//    boolean unlock(String resourceName);

//    /**
//     * 乐观锁-上锁
//     */
//    void lockOptimism(String resourceName);
//
//    /**
//     * 乐观锁-解锁
//     */
//    void unlockOptimism();


}
