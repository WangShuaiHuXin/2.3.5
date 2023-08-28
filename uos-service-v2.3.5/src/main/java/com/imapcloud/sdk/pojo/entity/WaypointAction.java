package com.imapcloud.sdk.pojo.entity;

import com.imapcloud.sdk.pojo.constant.ActionTypeEnum;

/**
 * @author wmin
 */
public class WaypointAction {
    private ActionTypeEnum actionType;
    private Integer actionParam;

    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public void setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
    }

    public Integer getActionParam() {
        return actionParam;
    }

    public void setActionParam(Integer actionParam) {
        this.actionParam = actionParam;
    }
}
