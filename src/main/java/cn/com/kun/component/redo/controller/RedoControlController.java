package cn.com.kun.component.redo.controller;

import cn.com.kun.component.redo.bean.vo.RedoResultVo;
import cn.com.kun.component.redo.core.RedoDataSupervisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@ConditionalOnProperty(prefix = "kunghsu.redo", value = {"enabled"}, havingValue = "true", matchIfMissing = false)
@RequestMapping("/redo-control")
@RestController
public class RedoControlController {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedoControlController.class);

    @Autowired
    private RedoDataSupervisor redoDataSupervisor;

    @GetMapping("/remove")
    public RedoResultVo<String> remove(@RequestParam(required = false) String redoTaskId){

        redoDataSupervisor.remove(redoTaskId);
        return RedoResultVo.valueOfSuccess("清理数据成功");
    }

}
