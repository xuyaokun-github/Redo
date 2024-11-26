package cn.com.kun.component.redo.dao.dbutils;

import cn.com.kun.component.jdbc.CommonDbUtilsJdbcStore;
import cn.com.kun.component.redo.bean.entiy.RedoTaskDO;
import cn.com.kun.component.redo.dao.RedoDao;
import cn.com.kun.component.redo.dao.RedoDaoStrategyFactory;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Primary
@Component
public class DbtuilsRedoDao implements RedoDao, InitializingBean {

    @Autowired
    private CommonDbUtilsJdbcStore commonDbUtilsJdbcStore;

    private String insertSql = "insert into tbl_redo_task(redo_task_id,application_name,max_attempts,try_forever," +
            "expired_date,req_param,create_time,query_time) " +
            "values(?,?,?,?,?,?,?,?)";

    private String deleteSql = "delete from tbl_redo_task where id=?";

    private String updateSql = "update tbl_redo_task set exec_times=exec_times+1 where id=?";

    private String updateQueryTimeSql = "update tbl_redo_task set query_time=DATE_ADD(query_time,INTERVAL ? second) where id=?";

    private String clearSql = "delete from tbl_redo_task where create_time <= DATE_ADD(NOW(),INTERVAL -? DAY) limit 100";

    private ResultSetHandler<List<RedoTaskDO>> resultSetHandler;

    @PostConstruct
    public void init(){
        //实现一个自定义的ResultSetHandler
        resultSetHandler = new ResultSetHandler<List<RedoTaskDO>>() {
            @Override
            public List<RedoTaskDO> handle(ResultSet resultset)  throws SQLException {
                List<RedoTaskDO> result = new ArrayList<RedoTaskDO>();
                while (resultset.next()) {

                    RedoTaskDO redoTaskDO = new RedoTaskDO();
                    redoTaskDO.setId(resultset.getInt("id"));
                    redoTaskDO.setApplicationName(resultset.getString("applicationName"));
                    redoTaskDO.setRedoTaskId(resultset.getString("redoTaskId"));
                    redoTaskDO.setTryForever(resultset.getBoolean("tryForever"));
                    redoTaskDO.setMaxAttempts(resultset.getInt("maxAttempts"));
                    redoTaskDO.setExecTimes(resultset.getInt("execTimes"));
                    redoTaskDO.setReqParam(resultset.getString("reqParam"));
                    //对mysql datetime类型的处理(正例，对应的是Timestamp)
                    redoTaskDO.setExpiredDate(resultset.getTimestamp("expiredDate"));
                    redoTaskDO.setCreateTime(resultset.getTimestamp("createTime"));
                    redoTaskDO.setQueryTime(resultset.getTimestamp("queryTime"));

                    result.add(redoTaskDO);
                }
                return result;
            }
        };

    }

    @Override
    public int insert(RedoTaskDO redoTask) {

        return commonDbUtilsJdbcStore.update(insertSql, redoTask.getRedoTaskId(),
                redoTask.getApplicationName(), redoTask.getMaxAttempts(), redoTask.isTryForever(), redoTask.getExpiredDate(),
                redoTask.getReqParam(), redoTask.getCreateTime(), redoTask.getCreateTime());
    }

    @Override
    public List<RedoTaskDO> selectRedoTaskList(String applicationName) {

        String selectSql = "select id as id, redo_task_id as redoTaskId," +
                "application_name as applicationName," +
                "max_attempts as maxAttempts," +
                "exec_times as execTimes," +
                "try_forever as tryForever," +
                "expired_date as expiredDate," +
                "req_param as reqParam," +
                "create_time as createTime," +
                "query_time as queryTime" +
                " from tbl_redo_task " +
                "where application_name='" + applicationName + "' " +
                "order by query_time asc limit 500";



        return (List<RedoTaskDO>) commonDbUtilsJdbcStore.selectList(selectSql, resultSetHandler);
    }

    @Override
    public List<RedoTaskDO> selectByRedoTaskId(String applicationName, String redoTaskId) {

        String selectSql = "select id as id, redo_task_id as redoTaskId," +
                "application_name as applicationName," +
                "max_attempts as maxAttempts," +
                "exec_times as execTimes," +
                "try_forever as tryForever," +
                "expired_date as expiredDate," +
                "req_param as reqParam," +
                "create_time as createTime" +
                " from tbl_redo_task " +
                "where application_name=? " +
                "and redo_task_id=? " +
                "order by create_time asc limit 500";

        return (List<RedoTaskDO>) commonDbUtilsJdbcStore.selectList(selectSql, resultSetHandler, applicationName, redoTaskId);
    }


    @Override
    public int delete(long id) {
        return commonDbUtilsJdbcStore.update(deleteSql, id);
    }

    @Override
    public int updateExecTimes(long id) {
        return commonDbUtilsJdbcStore.update(updateSql, id);
    }

    @Override
    public int updateQueryTime(int seconds, long id) {

        return commonDbUtilsJdbcStore.update(updateQueryTimeSql, seconds, id);
    }

    @Override
    public int clear(int retentionDays) {

        return commonDbUtilsJdbcStore.update(clearSql, retentionDays);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RedoDaoStrategyFactory.register("dbutils", this);
    }
}
