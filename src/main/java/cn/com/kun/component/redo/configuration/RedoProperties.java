package cn.com.kun.component.redo.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
@ConfigurationProperties(prefix ="kunghsu.redo")
public class RedoProperties implements Serializable {

    private boolean enabled = false;

    private boolean scheduleEnabled = false;

    private boolean saveHistoryEnabled = false;

    private boolean deleteScheduleEnabled = false;

    /**
     * 补偿频率,单位：秒
     */
    private Long scheduleRate = 15L;

    private String redoDaoMode = "jdbc";

    private Map<String, String> domainMap;

    /**
     * 补偿记录保留天数：默认7天
     */
    private int retentionDays = 7;

    private Long deleteScheduleRate = 3600L;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSaveHistoryEnabled() {
        return saveHistoryEnabled;
    }

    public void setSaveHistoryEnabled(boolean saveHistoryEnabled) {
        this.saveHistoryEnabled = saveHistoryEnabled;
    }

    public Long getScheduleRate() {
        return scheduleRate;
    }

    public void setScheduleRate(Long scheduleRate) {
        this.scheduleRate = scheduleRate;
    }

    public String getRedoDaoMode() {
        return redoDaoMode;
    }

    public void setRedoDaoMode(String redoDaoMode) {
        this.redoDaoMode = redoDaoMode;
    }

    public boolean isScheduleEnabled() {
        return scheduleEnabled;
    }

    public void setScheduleEnabled(boolean scheduleEnabled) {
        this.scheduleEnabled = scheduleEnabled;
    }

    public Map<String, String> getDomainMap() {
        return domainMap;
    }

    public void setDomainMap(Map<String, String> domainMap) {
        this.domainMap = domainMap;
    }

    public boolean isDeleteScheduleEnabled() {
        return deleteScheduleEnabled;
    }

    public void setDeleteScheduleEnabled(boolean deleteScheduleEnabled) {
        this.deleteScheduleEnabled = deleteScheduleEnabled;
    }

    public int getRetentionDays() {
        return retentionDays;
    }

    public void setRetentionDays(int retentionDays) {
        this.retentionDays = retentionDays;
    }

    public Long getDeleteScheduleRate() {
        return deleteScheduleRate;
    }

    public void setDeleteScheduleRate(Long deleteScheduleRate) {
        this.deleteScheduleRate = deleteScheduleRate;
    }
}
