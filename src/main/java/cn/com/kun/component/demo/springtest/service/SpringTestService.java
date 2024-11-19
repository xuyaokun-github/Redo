package cn.com.kun.component.demo.springtest.service;

import cn.com.kun.component.demo.springtest.bean.entiy.SpringTestDO;
import cn.com.kun.component.demo.springtest.dao.SpringTestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpringTestService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SpringTestService.class);

    @Autowired
    private SpringTestMapper springTestMapper;

    public void show(){
        LOGGER.info("SpringTestService");
    }

    public void testSaveDb(SpringTestDO springTestDO){
        LOGGER.info("testSaveDb");
        springTestMapper.insert(springTestDO);
    }


}
