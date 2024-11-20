package cn.com.kun.component.redo.core;

import cn.com.kun.component.redo.bean.vo.RedoResultVo;
import cn.com.kun.component.redo.configuration.RedoProperties;
import cn.com.kun.component.redo.invoke.DefaultRedoExecInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class RedoStopQuerier {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedoStopQuerier.class);

    private ScheduledExecutorService executorPool = Executors.newScheduledThreadPool(1);

    @Autowired
    private RedoProperties redoProperties;

    @Autowired
    private DefaultRedoExecInvoker defaultRedoExecInvoker;

    //初始化
    @PostConstruct
    public void init(){
        if (redoProperties.isEnabled()){
            LOGGER.info("启动补偿重试组件节点连接调度器");
            executorPool.scheduleAtFixedRate(new RedoStopQueryTask(), 1, 5, TimeUnit.SECONDS);
        }
    }

    private final class RedoStopQueryTask implements Runnable {

        RedoStopQueryTask() {

        }

        @Override
        public void run() {

            try {
                if (redoProperties.isEnabled()){
                    RedoResultVo<String> resultVo = defaultRedoExecInvoker.stopQuery();
                    if (resultVo != null && resultVo.getValue().equals("N")){
                        //表示停止
                        /*
                            设置为false，下一次不会再查
                            假如没停止的Pod,会继续查，逐渐会停下来（但是这个效率不高，还不如加表放这个开关，让它立刻停下来 TODO）
                         */
                        redoProperties.setEnabled(false);
                    }
                }

            }catch (Throwable e){
                LOGGER.error("RedoStopQueryTask error", e);
            }
        }

    }

}
