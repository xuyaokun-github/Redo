package cn.com.kun.component.redo.bean.vo;

import java.util.Date;

/**
 * 补偿任务定义
 * author:xuyaokun_kzx
 * date:2021/10/27
 * desc:
*/
public class RedoTask {

    private String redoTaskId;

    /**
     * 最大重试次数.
     * 默认是0，表示不限次数
     */
    private int maxAttempts;

    /**
     * 是否一直重试
     * 假如设置为true，则maxAttempts无效
     */
    private boolean tryForever;

    /**
     * 过期日期
     * 任务超过了这个日期，将不会再被重试
     */
    private Date expiredDate;

    /**
     * 过期时间（N秒之后过期）
     */
    private int expiredSeconds;

    /**
     * 是否自旋保存：默认为false
     */
    private boolean spinSave;

    public RedoTask(String redoTaskId) {
        this.redoTaskId = redoTaskId;
    }

    public String getRedoTaskId() {
        return redoTaskId;
    }

    public void setRedoTaskId(String redoTaskId) {
        this.redoTaskId = redoTaskId;
    }

    public boolean isTryForever() {
        return tryForever;
    }

    public void setTryForever(boolean tryForever) {
        this.tryForever = tryForever;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public int getExpiredSeconds() {
        return expiredSeconds;
    }

    public void setExpiredSeconds(int expiredSeconds) {
        this.expiredSeconds = expiredSeconds;
    }

    public static RedoTaskBuilder newBuilder(String redoTaskId){
        RedoTaskBuilder redoTaskBuilder = new RedoTaskBuilder();
        redoTaskBuilder.setRedoTask(new RedoTask(redoTaskId));
        return redoTaskBuilder;
    }

    public static class RedoTaskBuilder {

        private RedoTask redoTask;

//        public RedoTaskBuilder redoTaskId(String redoTaskId){
//            this.redoTask.setRedoTaskId(redoTaskId);
//            return this;
//        }

        /**
         * 设置最大补偿次数
         * @param maxAttempts
         * @return
         */
        public RedoTaskBuilder maxAttempts(int maxAttempts){
            if (maxAttempts < 1){
                maxAttempts = 1;
            }
            this.redoTask.setMaxAttempts(maxAttempts);
            return this;
        }

        /**
         * 设置截止日期
         * @param expiredDate
         * @return
         */
        public RedoTaskBuilder expiredDate(Date expiredDate){
            if (expiredDate != null && expiredDate.after(new Date())){
                this.redoTask.setExpiredDate(expiredDate);
            }
            return this;
        }

        public RedoTaskBuilder expiredSeconds(int expiredSeconds){
            if (expiredSeconds > 0){
                this.redoTask.setExpiredSeconds(expiredSeconds);
            }
            return this;
        }

        /**
         * 是否自旋保存（一直保存到成功为止）
         *
         * @param spinSave
         * @return
         */
        public RedoTaskBuilder spinSave(boolean spinSave){
            this.redoTask.setSpinSave(spinSave);
            return this;
        }

        public RedoTask build(){
            return this.redoTask;
        }

        private void setRedoTask(RedoTask redoTask) {
            this.redoTask = redoTask;
        }
    }

    public boolean isSpinSave() {
        return spinSave;
    }

    public void setSpinSave(boolean spinSave) {
        this.spinSave = spinSave;
    }
}
