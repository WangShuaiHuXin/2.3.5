package com.imapcloud.nest.v2.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum NhOrderBuryPointEnum {

    ToBeReleased(1, "创建工单", true, 1,true),
    ToBeReleaseF(1, "工单已撤回发布", false, 2,false),
    ToBeSigned(2, "发布工单", true, 2,true),
    ToBeReleasedF(2, "重新发布工单", false, 2,true),
    ToBeReviewedF(3, "工单已拒收", false, 3,false),
    ToBeReviewed(3, "工单已拒收", true, 3,false),
    ToBeRejected(4, "工单已签收", true, 3,true),
    ToBeAcceptance(5, "工单已提审", true, 4,true),
    ToBeAcceptanceF(5, "重新提审工单", false, 4,true),
    ToBeFinishF(6, "工单审核不通过", false, 5,false),
    ToBeFinishF2(6, "工单审核不通过", true, 5,false),
    ToBeFinish(7, "工单审核通过", true, 5,true),
    ToBeFinish2(7, "工单审核通过", false, 5,true);


    private int status;
    //埋点文案
    private String desc;
    //正向，反向
    private Boolean flag;
    //流程状态
    private int process;
    //流程方向
    private Boolean processDir;


    public static NhOrderBuryPointEnum getEnumByCode(int code, boolean flag) {
        NhOrderBuryPointEnum[] typesEnums = values();
        for (NhOrderBuryPointEnum typeenum : typesEnums) {
            if (typeenum.getStatus() == code && typeenum.getFlag() == flag) {
                return typeenum;
            }
        }
        return null;
    }
}
