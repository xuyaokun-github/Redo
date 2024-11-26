package cn.com.kun.component.redo.bean.vo;

import java.util.HashMap;
import java.util.Map;

public class RedoReqParam {

    /**
     * 业务层参数
     */
    private Map<String, Object> params;

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void put(String paramName, Object value) {

        if (params == null){
            params = new HashMap<>();
        }
        params.put(paramName, value);
    }

}
