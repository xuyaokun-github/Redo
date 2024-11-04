package cn.com.kun.component.demo.springtest.dao;

import cn.com.kun.component.demo.springtest.bean.entiy.SpringTestDO;
import cn.com.kun.component.redo.bean.entiy.RedoTaskDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpringTestMapper {

    @Insert("insert into tbl_redotask(redo_task_id,application_name,max_attempts," +
            "try_forever,expired_date,req_param,create_time) " +
            "values(#{redoTaskId},#{applicationName},#{maxAttempts}," +
            "#{tryForever},#{expiredDate},#{reqParam},#{createTime})")
    int insert(SpringTestDO springTestDO);

}
