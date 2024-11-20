package cn.com.kun.component.redo.lock;

import cn.com.kun.component.distributedlock.dblock.DistributedDbLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLockControl implements LockControl{

    @Autowired
    private DistributedDbLock dbClusterLockHandler;

    @Override
    public boolean lock(String resourcName) {
        return dbClusterLockHandler.tryLock(resourcName);
    }

    @Override
    public void unlock(String resourcName) {
        dbClusterLockHandler.unlock(resourcName);
    }

}
