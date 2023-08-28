package com.imapcloud.sdk.pojo.constant;

/**
 * Created by wmin on 2020/10/26 18:07
 *
 * @author wmin
 */
public enum CameraFpvModeEnum {
    //仅可见光
    VISUAL_ONLY(1),
    //仅红外线
    THERMAL_ONLY(2),
    //红外与双光融合模式
    MSX(3);
    private int value;

    CameraFpvModeEnum(int value) {
        this.value = value;
    }

    public static CameraFpvModeEnum getInstance(int value) {
        for (CameraFpvModeEnum e : CameraFpvModeEnum.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null;
    }
}
