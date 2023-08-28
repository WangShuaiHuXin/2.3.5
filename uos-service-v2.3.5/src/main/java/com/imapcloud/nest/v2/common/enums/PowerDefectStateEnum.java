package com.imapcloud.nest.v2.common.enums;

import com.google.common.base.Enums;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 缺陷识别状态
 *
 * @author boluo
 * @date 2023-02-28
 */
@Getter
@AllArgsConstructor
public enum PowerDefectStateEnum {

    /**
     * 识别状态
     */
    DEFECT_PRE(0, "未识别"),
    DEFECT_ING(1, "识别中"),
    DEFECT_YES(2, "有缺陷"),
    DEFECT_NO(3, "无缺陷"),
    ;
    private final int code;

    private final String msg;

    public static String getMsgByCode(Integer code) {
        for (PowerDefectStateEnum e : PowerDefectStateEnum.values()) {
            if (e.getCode() == code.intValue()) {
                return e.getMsg();
            }
        }
        return null;
    }
}
