package cn.com.kun.component.redo.schedule;

import cn.com.kun.component.redo.configuration.RedoProperties;
import cn.com.kun.component.redo.core.RedoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class RedoRecordDeleteScheduler implements CommandLineRunner {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedoRecordDeleteScheduler.class);

    private ScheduledExecutorService executorPool = Executors.newScheduledThreadPool(1);

    @Autowired
    private RedoProperties redoProperties;

    @Autowired
    private RedoManager redoManager;

    //初始化
    @PostConstruct
    public void init(){
        //注意，放在这里启动调度，mapper文件可能还没注册完毕，所以建议是放到CommandLineRunner里启动
    }

    @Override
    public void run(String... args) throws Exception {

        if (redoProperties.isEnabled() && redoProperties.isDeleteScheduleEnabled()){
            LOGGER.info("启动补偿重试组件Redo数据清理调度器");
            executorPool.scheduleAtFixedRate(new RedoRecordDeleteTask(), 1, redoProperties.getDeleteScheduleRate(), TimeUnit.SECONDS);
        }
    }

    private final class RedoRecordDeleteTask implements Runnable {

        RedoRecordDeleteTask() {

        }

        @Override
        public void run() {
            if (LOGGER.isDebugEnabled()){
                LOGGER.debug("RedoRecordDeleteTask running!");
            }
            try {
                redoManager.recordDelete();
            }catch (Throwable e){
                LOGGER.error("RedoRecordDeleteTask error", e);
            }
        }
    }

}
