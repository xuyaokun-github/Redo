package cn.com.kun.component.redo.dao;

import cn.com.kun.component.redo.bean.entiy.RedoTaskDO;

import java.util.List;

public interface RedoDao {

    int insert(RedoTaskDO redoTask);

    List<RedoTaskDO> selectRedoTaskList(String applicationName);

    int delete(long id);

    int updateExecTimes(long id);

    int updateQueryTime(int seconds, long id);

    List<RedoTaskDO> selectByRedoTaskId(String applicationName, String redoTaskId);

    int clear(int retentionDays);

}
