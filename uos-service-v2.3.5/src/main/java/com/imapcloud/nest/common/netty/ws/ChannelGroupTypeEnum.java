package com.imapcloud.nest.common.netty.ws;

/**
 * Created by wmin on 2020/11/11 9:32
 * 连接进来的channelGroup类型
 */
public enum ChannelGroupTypeEnum {
    /**
     * 传输主题：NEST_LIST_DTO
     */
    TYPE1("1"),
    /**
     * 传输主题：NEST_AIRCRAFT_INFO_DTO，MINI_NEST_AIRCRAFT_INFO_DTO，NEST_SERVER_LINKED_STATE，FIX_DEBUG_PANEL
     */
    TYPE2("2"),
    /**
     * 传输主题：FLY_BEFORE_CHECK_DTO，TASK_PROGRESS_DTO，AEROGRAPHY_INFO_DTO，START_BEFORE_CHECK，
     * STARTING_CHECK，AIRCRAFT_LOCATION，DIAGNOSTICS，DOWNING_PHOTO
     */
    TYPE3("3"),
    /**
     * 成果管理页面的传输
     */
    TYPE4("4"),
    /**
     * web页面连接进来的接受消息服务的websocket,传输app是否在线消息
     */
    TYPE5("5"),
    /**
     * web页面连接进来的接受消息服务的websocket,传输app飞机飞行的消息
     */
    TYPE6("6"),
    /**
     * 易飞app接入的websocket
     */
    TYPE7("7"),

    TYPE8("8"),

    TYPE9("9"),
    /**
     * 任务通用状态 MissionState
     */
    TYPE10("10"),
    /**
     * 基站日志包上传以及基站apk更新问题
     */
    TYPE11("11"),
    /**
     * 基站CPS更新进度
     */
    TYPE12("12"),

    /**
     * 全局通知
     */
    TYPE13("13"),

    /**
     * AI识别通道
     */
    TYPE14("14");

    private final String value;

    ChannelGroupTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
