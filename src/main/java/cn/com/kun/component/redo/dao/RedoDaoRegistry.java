package cn.com.kun.component.redo.dao;

public class RedoDaoRegistry {

    private static RedoDao defaultRedoDao = null;

    public static RedoDao getRedoDao() {
        return defaultRedoDao;
    }

    /**
     * 扩展点
     * 假如有需要自定义数据库实现，可以选择注册
     *
     * @param redoDao
     */
    public static void registerRedoDao(RedoDao redoDao) {
        defaultRedoDao = redoDao;
    }
}
