package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 */
public enum FinishActionEnum {
    /**
     * 完成任务后悬停，此时，飞机可由遥控器控制
     */
    NO_ACTION(0),
    /**
     * 任务完成后返航，如果当前高度大于无人机设置的返航高度则以该高度返航，
     * 否则上升至无人机设置的返航高度再返航。（注意在距离home点20米以内可能直接降落）
     */
    GO_HOME(1),
    /**
     * 无人机在最后一个航点自动降落
     */
    AUTO_LAND(2),
    /**
     * 无人机返回第一个航点，并悬停
     */
    GO_FIRST_WAYPOINT(3),
    /**
     * 完成任务后直接以最后一个航点高度直接返航
     */
    GO_HOME_DIRECTLY(4);


    private Integer value;

    FinishActionEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }


    public static FinishActionEnum getInstance(Integer value) {
        for (FinishActionEnum m : FinishActionEnum.values()) {
            if (m.getValue().equals(value)) {
                return m;
            }
        }

        return null;
    }
}
