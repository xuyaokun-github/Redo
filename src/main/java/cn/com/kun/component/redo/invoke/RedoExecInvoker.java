package cn.com.kun.component.redo.invoke;

import cn.com.kun.component.redo.bean.vo.RedoExecResVo;
import cn.com.kun.component.redo.bean.vo.RedoResultVo;

public interface RedoExecInvoker {

    RedoResultVo<RedoExecResVo> exec(String applicationName);

    RedoResultVo<String> stopQuery();

}
