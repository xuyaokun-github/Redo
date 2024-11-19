package cn.com.kun.component.redo.dao;

import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedoDaoStrategyFactory {

    private static Map<String, RedoDao> daos = new ConcurrentHashMap<String, RedoDao>();

    public static RedoDao getByType(String type){
        return daos.get(type);
    }

    public static void register(String type, RedoDao redoDao){
        Assert.notNull(type, "RedoDao can't be null");
        daos.put(type, redoDao);
    }
}
