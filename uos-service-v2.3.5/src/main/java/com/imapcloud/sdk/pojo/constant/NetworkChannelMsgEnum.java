package com.imapcloud.sdk.pojo.constant;

public enum NetworkChannelMsgEnum {
    /**
     * 网络服务未启动
     */
    DISABLED,

    /**
     * 账号过期
     */
    ACCOUNT_EXPIRED,

    /**
     * 无法从移动设备访问网络
     */
    NETWORK_NOT_REACHABLE,

    /**
     * 无人机未连接
     */
    AIRCRAFT_DISCONNECTED,

    /**
     * RTK加密狗未连接
     */
    RTK_DONGLE_DISCONNECTED,

    /**
     * SDK登录失败
     */
    LOGIN_FAILURE,

    /**
     * 通道正在进行数据传输
     */
    TRANSMITTING,

    /**
     * 通道未连接且服务不可用
     */
    DISCONNECTED,

    /**
     * 账号异常
     */
    ACCOUNT_ERROR,

    /**
     * 服务无法连接
     */
    SERVER_NOT_REACHABLE,

    /**
     * 正在连接服务
     */
    CONNECTING,

    /**
     * 当前RTK网络方案的账号已过期，请激活其他方案
     */
    SERVICE_SUSPENSION,

    /**
     * 无效请求
     */
    INVALID_REQUEST,

    /**
     * 未知错误
     */
    UNKNOWN,


}
