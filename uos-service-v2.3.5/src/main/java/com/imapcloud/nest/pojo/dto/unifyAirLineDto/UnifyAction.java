package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import com.imapcloud.sdk.pojo.constant.ActionTypeEnum;

import java.util.List;

/**
 * Created by wmin on 2020/11/17 15:51
 */
public class UnifyAction {
    private ActionTypeEnum actionType;
    private Integer value;
    private List<Integer> identifyType;
    private String byname;

    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public void setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public List<Integer> getIdentifyType() {
        return identifyType;
    }

    public void setIdentifyType(List<Integer> identifyType) {
        this.identifyType = identifyType;
    }

    public String getByname() {
        return byname;
    }

    public void setByname(String byname) {
        this.byname = byname;
    }
}
