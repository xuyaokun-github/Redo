package cn.com.kun.component.redo.dao.jdbc;

import cn.com.kun.component.jdbc.CommonNoTxJdbcStore;
import cn.com.kun.component.jdbc.PreparedStatementParamProvider;
import cn.com.kun.component.redo.bean.entiy.RedoTaskDO;
import cn.com.kun.component.redo.dao.RedoDao;
import cn.com.kun.component.redo.dao.RedoDaoStrategyFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class JdbcRedoDao implements RedoDao, InitializingBean {

    @Autowired
    private CommonNoTxJdbcStore commonNoTxJdbcStore;

    private final static String PATTERN_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private String insertSql = "insert into tbl_redo_task(redo_task_id,application_name,max_attempts,try_forever," +
            "expired_date,req_param,create_time,query_time) " +
            "values('%s','%s',%s,%s,'%s','%s','%s','%s')";

    private String insertSql2 = "insert into tbl_redo_task(redo_task_id,application_name,max_attempts,try_forever," +
            "expired_date,req_param,create_time,query_time) " +
            "values(?,?,?,?,?,?,?,?)";

    private String deleteSql = "delete from tbl_redo_task where id=%s";

    private String updateSql = "update tbl_redo_task set exec_times=exec_times+1 where id=%s";

    private String updateQueryTimeSql = "update tbl_redo_task set query_time=DATE_ADD(query_time,INTERVAL %s second) where id=%s";

    private String clearSql = "delete from tbl_redo_task where create_time <= DATE_ADD(NOW(),INTERVAL -%s DAY) limit 100";

    @Override
    public int insert(RedoTaskDO redoTask) {

        String expiredDateStr = redoTask.getExpiredDate() == null ? "9999:12:31 00:00:00" : toStr(redoTask.getExpiredDate(), PATTERN_YYYY_MM_DD_HH_MM_SS);

        String targetSql = String.format(insertSql,
                redoTask.getRedoTaskId(),
                redoTask.getApplicationName(),
                redoTask.getMaxAttempts(),
                redoTask.isTryForever(),
                expiredDateStr,
                redoTask.getReqParam(),
                toStr(redoTask.getCreateTime(), PATTERN_YYYY_MM_DD_HH_MM_SS),
                toStr(redoTask.getQueryTime(), PATTERN_YYYY_MM_DD_HH_MM_SS));
        //int res = commonNoTxJdbcStore.update(targetSql);
        int res = commonNoTxJdbcStore.update(insertSql2, new PreparedStatementParamProvider() {
            @Override
            public void initPreparedStatementParam(PreparedStatement ps) {
                try {
                    ps.setString(1, redoTask.getRedoTaskId());
                    ps.setString(2, redoTask.getApplicationName());
                    ps.setInt(3, redoTask.getMaxAttempts());
                    ps.setBoolean(4, redoTask.isTryForever());
                    ps.setTimestamp(5, new Timestamp(redoTask.getExpiredDate().getTime()));
                    ps.setString(6, redoTask.getReqParam());
                    ps.setTimestamp(7, new Timestamp(redoTask.getCreateTime().getTime()));
                    ps.setTimestamp(8, new Timestamp(redoTask.getQueryTime().getTime()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        return res;
    }

    private String toStr(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
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
                "create_time," +
                "query_time" +
                " from tbl_redo_task " +
                "where application_name='" + applicationName + "' " +
                "order by query_time asc limit 500";

        return commonNoTxJdbcStore.selectList(selectSql, RedoTaskDO.class);
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
                "create_time," +
                "query_time" +
                " from tbl_redo_task " +
                "where application_name='" + applicationName + "' " +
                "and redo_task_id='" + redoTaskId + "' " +
                "order by query_time asc limit 500";

        return commonNoTxJdbcStore.selectList(selectSql, RedoTaskDO.class);
    }


    @Override
    public int delete(long id) {
        String targetSql = String.format(deleteSql, id);
        return commonNoTxJdbcStore.update(targetSql);
    }

    @Override
    public int updateExecTimes(long id) {
        String targetSql = String.format(updateSql, id);
        return commonNoTxJdbcStore.update(targetSql);
    }

    @Override
    public int updateQueryTime(int seconds, long id) {
        String targetSql = String.format(updateQueryTimeSql, seconds, id);
        return commonNoTxJdbcStore.update(targetSql);
    }


    @Override
    public int clear(int retentionDays) {

        String targetSql = String.format(clearSql, retentionDays);
        return commonNoTxJdbcStore.update(targetSql);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RedoDaoStrategyFactory.register("jdbc", this);
    }

}
