package cn.com.kun.component.redo.controller;

import cn.com.kun.component.redo.bean.vo.RedoExecResVo;
import cn.com.kun.component.redo.bean.vo.RedoResultVo;
import cn.com.kun.component.redo.core.RedoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@ConditionalOnProperty(prefix = "kunghsu.redo", value = {"enabled"}, havingValue = "true", matchIfMissing = false)
@RequestMapping("/redo-exec")
@RestController
public class RedoExecController {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedoExecController.class);

    @Autowired
    private RedoManager redoManager;

    @GetMapping("/exec")
    public RedoResultVo<RedoExecResVo> exec(@RequestParam String applicationName){

        /**
         * 因为这个是http接口，不能长时间执行
         */
        RedoResultVo<RedoExecResVo> resultVo = redoManager.redoExec(applicationName);
        return resultVo;
    }

}
