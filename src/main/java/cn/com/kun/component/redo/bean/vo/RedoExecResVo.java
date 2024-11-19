package cn.com.kun.component.redo.bean.vo;

public class RedoExecResVo {

    /**
     * 执行标记
     * 0 表示已经执行完
     * 1 表示仍需继续请求
     */
    private String flag;

    private int size;

    private long firstId;

    private long lastId;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getFirstId() {
        return firstId;
    }

    public void setFirstId(long firstId) {
        this.firstId = firstId;
    }

    public long getLastId() {
        return lastId;
    }

    public void setLastId(long lastId) {
        this.lastId = lastId;
    }

    @Override
    public String toString() {
        return "RedoExecResVo{" +
                "flag='" + flag + '\'' +
                ", size=" + size +
                ", firstId=" + firstId +
                ", lastId=" + lastId +
                '}';
    }
}
