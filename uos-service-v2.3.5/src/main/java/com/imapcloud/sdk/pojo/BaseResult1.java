package com.imapcloud.sdk.pojo;

import java.util.Map;

/**
 * @author wmin
 */
public class BaseResult1 extends BaseResult implements IBaseResult{
    private Map<String,Object> param;

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
}
