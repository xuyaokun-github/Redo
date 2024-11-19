package cn.com.kun.component.redo.core;

import cn.com.kun.component.redo.configuration.RedoProperties;
import cn.com.kun.component.redo.dao.RedoDao;
import cn.com.kun.component.redo.dao.RedoDaoRegistry;
import cn.com.kun.component.redo.dao.RedoDaoStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedoTaskDaoVisitor {

    @Autowired
    private RedoProperties redoProperties;

    public RedoDao getRedoDao(){

        RedoDao redoDao = null;
        //简单策略模式
        if (RedoDaoRegistry.getRedoDao() != null){
            redoDao = RedoDaoRegistry.getRedoDao();
        }else {
            redoDao = RedoDaoStrategyFactory.getByType(redoProperties.getRedoDaoMode());
        }

        return redoDao;
    }

}
