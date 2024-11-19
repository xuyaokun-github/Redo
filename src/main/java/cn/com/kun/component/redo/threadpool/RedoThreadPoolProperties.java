package cn.com.kun.component.redo.threadpool;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;


@Component
@ConfigurationProperties(prefix ="kunghsu.redo.thread-pool")
public class RedoThreadPoolProperties implements Serializable {

    /**
     * 按场景进行限流配置
     * 假如是向后限流，会用在任何位置，限制本系统里的某个方法代码的执行频率
     * 常用场景：限制调用外部第三方接口的频率
     */
    private Map<String, ThreadPoolConfigItem> items;

    public Map<String, ThreadPoolConfigItem> getItems() {
        return items;
    }

    public void setItems(Map<String, ThreadPoolConfigItem> items) {
        this.items = items;
    }

    /**
     * 这个类必须是public static，否则会抛异常
     */
    public static class ThreadPoolConfigItem {

        private int corePoolSize;

        private int maxPoolSize;

        private int queueCapacity;

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }
    }

}
