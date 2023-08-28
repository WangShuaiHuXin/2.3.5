package com.imapcloud.sdk.pojo.entity;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.sdk.pojo.constant.AircraftStateEnum;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wmin
 * 机巢状态
 */
public class NestState {
    /**
     * 飞机状态
     */
    private AircraftStateEnum aircraftStateConstant;
    /**
     * 一代机巢,飞机是否连接
     */
    private Boolean isConnected;
    /**
     * 飞机是否连接
     */
    private Boolean isAircraftConnected;
    /**
     * 机巢状态
     */
    private NestStateEnum nestStateConstant;
    /**
     * TODO
     */
    private Boolean isAntPowerOn;
    /**
     * 遥控是否连接
     */
    private Boolean isRemoteControllerConnected;
    /**
     * 遥控电池是否开
     */
    private Boolean isRemotePowerOn;
    /**
     * 飞机电池得可用数量
     */
    private Integer nestBatteryAvailable;


    /**
     * -2 => 未知
     * -1 => 底层通讯未成功
     * 0 => 正常
     */
    private NestErrorCodeEnum nestErrorCode;

    /**
     * mps是否连接
     */
    private Boolean mpsConnected;

    private Boolean isUsbDeviceConnected;

    /**
     * 基站调度状态
     */
    private Integer mpsDispatchState;

    /**
     * 飞行调度状态
     */
    private String cpsDispatchState;

    public Boolean getUsbDeviceConnected() {
        return isUsbDeviceConnected;
    }

    public void setUsbDeviceConnected(Boolean usbDeviceConnected) {
        isUsbDeviceConnected = usbDeviceConnected;
    }

    public NestState() {
        this.aircraftStateConstant = AircraftStateEnum.UNKNOWN;
        this.isConnected = false;
        this.isAircraftConnected = false;
        this.nestStateConstant = NestStateEnum.OFF_LINE;
        this.isAntPowerOn = false;
        this.isRemoteControllerConnected = false;
        this.isRemotePowerOn = false;
        this.nestBatteryAvailable = 0;
        this.nestErrorCode = NestErrorCodeEnum.UNKNOWN;
        this.mpsConnected = false;
        this.isUsbDeviceConnected = false;
        this.mpsDispatchState = 0;
        this.cpsDispatchState = CpsDispatchStateEnum.UNKNOWN.name();
    }

    public AircraftStateEnum getAircraftStateConstant() {
        return this.aircraftStateConstant;
    }

    public void setAircraftStateConstant(AircraftStateEnum aircraftStateConstant) {
        if (aircraftStateConstant != null) {
            this.aircraftStateConstant = aircraftStateConstant;
        }

    }

    public Boolean getIsConnected() {
        return this.isConnected;
    }

    public void setIsConnected(Boolean isConnected) {
        if (isConnected != null) {
            this.isConnected = isConnected;
        }

    }

    public NestStateEnum getNestStateConstant() {
        return this.nestStateConstant;
    }

    public void setNestStateConstant(NestStateEnum nestStateConstant) {
        if (nestStateConstant != null) {
            this.nestStateConstant = nestStateConstant;
        }
    }

    public Boolean getAircraftConnected() {
        return this.isAircraftConnected;
    }

    public void setIsAircraftConnected(Boolean isAircraftConnected) {
        if (isAircraftConnected != null) {
            this.isAircraftConnected = isAircraftConnected;
        }

    }

    public Boolean getAntPowerOn() {
        return this.isAntPowerOn;
    }

    public void setIsAntPowerOn(Boolean isAntPowerOn) {
        if (isAntPowerOn != null) {
            this.isAntPowerOn = isAntPowerOn;
        }

    }

    public Boolean getRemoteControllerConnected() {
        return this.isRemoteControllerConnected;
    }

    public void setIsRemoteControllerConnected(Boolean isRemoteControllerConnected) {
        if (isRemoteControllerConnected != null) {
            this.isRemoteControllerConnected = isRemoteControllerConnected;
        }

    }

    public Boolean getRemotePowerOn() {
        return this.isRemotePowerOn;
    }

    public void setIsRemotePowerOn(Boolean isRemotePowerOn) {
        if (isRemotePowerOn != null) {
            this.isRemotePowerOn = isRemotePowerOn;
        }

    }

    public Integer getNestBatteryAvailable() {
        return this.nestBatteryAvailable;
    }

    public void setNestBatteryAvailable(Integer nestBatteryAvailable) {
        if (nestBatteryAvailable != null) {
            this.nestBatteryAvailable = nestBatteryAvailable;
        }
    }

    public NestErrorCodeEnum getNestErrorCode() {
        return nestErrorCode;
    }

    public void setNestErrorCode(Integer nestErrorCode) {
        if (nestErrorCode != null) {
            this.nestErrorCode = NestErrorCodeEnum.getInstance(nestErrorCode);
        }
    }

    public Boolean getMpsConnected() {
        return mpsConnected;
    }

    public void setMpsConnected(Boolean mpsConnected) {
        if (mpsConnected != null) {
            this.mpsConnected = mpsConnected;
        }
    }

    public Integer getMpsDispatchState() {
        return mpsDispatchState;
    }

    public void setMpsDispatchState(Integer mpsDispatchState) {
        if (Objects.nonNull(mpsDispatchState)) {
            this.mpsDispatchState = mpsDispatchState;
        }
    }

    public String getCpsDispatchState() {
        return cpsDispatchState;
    }

    public void setCpsDispatchState(String cpsDispatchState) {
        if (Objects.nonNull(cpsDispatchState)) {
            this.cpsDispatchState = cpsDispatchState;
        }
    }

    public enum NestErrorCodeEnum {
        UNKNOWN(-1, "未知", "geoai_uos_NestErrorCodeEnum_UNKNOWN"),
        ERROR_CODE_0(0, "正常", "geoai_uos_NestErrorCodeEnum_ERROR_CODE_0"),
        ERROR_CODE_1(1, "自检失败", "geoai_uos_NestErrorCodeEnum_ERROR_CODE_1"),
        ERROR_CODE_16(16, "系统自检超时", "geoai_uos_NestErrorCodeEnum_ERROR_CODE_16"),
        ERROR_CODE_17(17, "一键重置超时", "geoai_uos_NestErrorCodeEnum_ERROR_CODE_17"),
        ERROR_CODE_18(18, "一键开启超时", "geoai_uos_NestErrorCodeEnum_ERROR_CODE_18"),
        ERROR_CODE_19(19, "一键回收超时", "geoai_uos_NestErrorCodeEnum_ERROR_CODE_19"),
        ERROR_CODE_20(20, "起飞引导超时", "geoai_uos_NestErrorCodeEnum_ERROR_CODE_20"),
        ERROR_CODE_21(21, "降落引导超时", "geoai_uos_NestErrorCodeEnum_ERROR_CODE_21"),
        ERROR_CODE_22(22, "电池装载超时", "geoai_uos_NestErrorCodeEnum_ERROR_CODE_22"),
        ERROR_CODE_23(23, "电池卸载超时", "geoai_uos_NestErrorCodeEnum_ERROR_CODE_23"),
        ERROR_CODE_24(24, "电池替换超时", "geoai_uos_NestErrorCodeEnum_ERROR_CODE_24"),
        ;
        private int value;
        private String express;

        private String key;

        NestErrorCodeEnum(int value, String express, String key) {
            this.value = value;
            this.express = express;
            this.key = key;
        }

        public static NestErrorCodeEnum getInstance(Integer value) {
            if (value != null) {
                Optional<NestErrorCodeEnum> first = Arrays.stream(NestErrorCodeEnum.values()).filter(e -> e.value == value).findFirst();
                return first.orElse(UNKNOWN);
            }
            return UNKNOWN;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getExpress() {
            return MessageUtils.getMessage(key);
        }

        public void setExpress(String express) {
            this.express = express;
        }

    }

    public enum CpsDispatchStateEnum {

        UNKNOWN(-1, "未知"),
        IDLE(0,"空闲"),
        FINISH_MISSION(1, "结束任务"),
        UPLOAD_DROPPING1_POS(2, "上升至悬停点1高度中"),
        UPLOAD_DROPPING2_POS(3, "上升至悬停点2高度中"),
        UPLOAD_DROPPING3_POS(4, "上升至悬停点3高度中"),
        GO_TO_DROPPING1_POS(5, "前往降落点1位置"),
        GO_TO_DROPPING2_POS(6, "前往降落点2位置"),
        GO_TO_DROPPING3_POS(7, "前往降落点3位置"),
        DROPPING1_HOVER(8, "降落点1位置悬停"),
        DROPPING2_HOVER(9, "降落点2位置悬停"),
        DROPPING3_HOVER(10, "降落点3位置悬停"),
        UPLOAD_LANDING_POS(11, "起飞上升至悬停点高度中"),
        GO_TO_LANDING_POS(12, "前往降落位置"),
        LANDING(13, "着陆中"),
        INTERRUPT_GO_HOME(14, "打断返航"),
        GOING_HOME(15, "返航中"),
        WORKING(16,"工作中"),
        ERROR(-1, "错误");;

        private int value;
        private String express;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getExpress() {
            return express;
        }

        public void setExpress(String express) {
            this.express = express;
        }

        CpsDispatchStateEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static CpsDispatchStateEnum getInstance(int value) {
            for (CpsDispatchStateEnum e : CpsDispatchStateEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }

        public static CpsDispatchStateEnum getInstance(String name) {
            try {
                return CpsDispatchStateEnum.valueOf(name);
            } catch (Exception e) {
                return UNKNOWN;
            }

        }

    }

    public enum MpsDispatchStateEnum {
        UNKNOWN(-1, "未知"),
        /**
         * 空闲
         */
        IDLE(0, "空闲"),
        /**
         * 使用中
         */
        USING(1, "使用中");
        private int value;
        private String express;

        MpsDispatchStateEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static String getExpressByValue(Integer value) {
            if (Objects.nonNull(value)) {
                for (MpsDispatchStateEnum e : MpsDispatchStateEnum.values()) {
                    if (e.value == value) {
                        return e.getExpress();
                    }
                }
            }
            return UNKNOWN.getExpress();
        }


        public int getValue() {
            return value;
        }

        public String getExpress() {
            return express;
        }
    }
}
