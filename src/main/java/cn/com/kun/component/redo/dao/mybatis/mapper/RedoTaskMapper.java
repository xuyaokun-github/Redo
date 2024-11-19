package cn.com.kun.component.redo.dao.mybatis.mapper;

import cn.com.kun.component.redo.bean.entiy.RedoTaskDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RedoTaskMapper {

    @Insert("insert into tbl_redo_task(redo_task_id,application_name,max_attempts,try_forever,expired_date,req_param,create_time,query_time) " +
            "values(#{redoTaskId},#{applicationName},#{maxAttempts},#{tryForever},#{expiredDate},#{reqParam},#{createTime},#{queryTime})")
    int insert(RedoTaskDO redoTask);

    /**
     * 只查本应用对应的待补偿记录
     * @param applicationName
     * @return
     */
    @Select("select * from tbl_redo_task where application_name=#{applicationName} order by query_time asc limit 500")
    List<RedoTaskDO> selectRedoTaskList(String applicationName);

    @Select("select * from tbl_redo_task where application_name=#{applicationName} and redo_task_id=#{redoTaskId} order by create_time desc limit 500")
    List<RedoTaskDO> selectByRedoTaskId(String applicationName, String redoTaskId);

    @Delete("delete from tbl_redo_task where id=#{id}")
    int delete(long id);

    @Update("update tbl_redo_task set exec_times=exec_times+1 where id=#{id}")
    int updateExecTimes(long id);

    @Update("update tbl_redo_task set query_time=DATE_ADD(query_time,INTERVAL #{seconds} second) where id=#{id}")
    int updateQueryTime(long seconds, long id);

    @Delete("delete from tbl_redo_task where create_time <= DATE_ADD(NOW(),INTERVAL -#{retentionDays} DAY) limit 100")
    int clear(int retentionDays);

}
