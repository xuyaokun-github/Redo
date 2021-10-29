package cn.com.kun.component.redo.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedoLogger {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedoLogger.class);

    public void log(){
        LOGGER.info("logging..........");
    }

}
