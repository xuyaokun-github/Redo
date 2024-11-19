package cn.com.kun.component.redo.dao.mybatis;

import cn.com.kun.component.redo.bean.entiy.RedoTaskDO;
import cn.com.kun.component.redo.dao.RedoDao;
import cn.com.kun.component.redo.dao.RedoDaoStrategyFactory;
import cn.com.kun.component.redo.dao.mybatis.mapper.RedoTaskMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MybatisRedoDao implements RedoDao, InitializingBean {

    @Autowired(required = false)
    private RedoTaskMapper redoTaskMapper;

    @Override
    public int insert(RedoTaskDO redoTask) {
        return redoTaskMapper.insert(redoTask);
    }

    @Override
    public List<RedoTaskDO> selectRedoTaskList(String applicationName) {
        return redoTaskMapper.selectRedoTaskList(applicationName);
    }

    @Override
    public List<RedoTaskDO> selectByRedoTaskId(String applicationName, String redoTaskId) {

        return redoTaskMapper.selectByRedoTaskId(applicationName, redoTaskId);
    }

    @Override
    public int delete(long redoTaskId) {
        return redoTaskMapper.delete(redoTaskId);
    }

    @Override
    public int updateExecTimes(long id) {
        return redoTaskMapper.updateExecTimes(id);
    }

    @Override
    public int updateQueryTime(int seconds, long id) {

        return redoTaskMapper.updateQueryTime(seconds, id);
    }


    @Override
    public int clear(int retentionDays) {

        return redoTaskMapper.clear(retentionDays);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RedoDaoStrategyFactory.register("mybatis", this);
    }
}
