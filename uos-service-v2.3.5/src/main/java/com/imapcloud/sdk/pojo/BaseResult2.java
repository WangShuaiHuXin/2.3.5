package com.imapcloud.sdk.pojo;

import java.util.List;
import java.util.Map;

public class BaseResult2 extends BaseResult implements IBaseResult{

    private List<Map<String, Object>> param;

    public List<Map<String, Object>> getParam() {
        return param;
    }

    public void setParam(List<Map<String, Object>> param) {
        this.param = param;
    }
}
