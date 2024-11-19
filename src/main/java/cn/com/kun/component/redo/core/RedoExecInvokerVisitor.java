package cn.com.kun.component.redo.core;

import cn.com.kun.component.redo.invoke.DefaultRedoExecInvoker;
import cn.com.kun.component.redo.invoke.RedoExecInvoker;
import cn.com.kun.component.redo.invoke.RedoExecInvokerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedoExecInvokerVisitor {

    @Autowired
    private DefaultRedoExecInvoker defaultRedoExecInvoker;

    public RedoExecInvoker getExecInvoker(String applicationName) {

        RedoExecInvoker redoExecInvoker = RedoExecInvokerRegistry.getRedoExecInvoker(applicationName);
        return redoExecInvoker == null ? defaultRedoExecInvoker : redoExecInvoker;
    }

}
