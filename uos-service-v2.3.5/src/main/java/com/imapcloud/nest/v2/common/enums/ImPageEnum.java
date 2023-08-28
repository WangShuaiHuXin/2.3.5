package com.imapcloud.nest.v2.common.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 页面枚举
 *
 * @author boluo
 * @date 2023-02-16
 */
@Getter
@AllArgsConstructor
public enum ImPageEnum {

    /**
     * 页面
     */
    HOME_PAGE("/home-page", "首页"),
    DISPATCH_CENTER_DESC("/dispatch-center-desc", "指挥调度"),
    DATA_MANAGE("/data-manage", "数据管理"),
    AIR_PLAN("/air-plan", "航线计划"),
    DEVICE_MANAGE("/device-manage", "基站管理"),
    WORK_ORDER("/work-order", "任务工单"),
//    SYSTEM_SETUP("/system-setup", "系统设置"),
    ANALYSE_CENTER("/analyse-center", "电力分析"),
    ;
    private String code;
    private String msg;

    public static final List<String> CHANNEL_ONE_LIST = Lists.newArrayList(
            HOME_PAGE.code, DISPATCH_CENTER_DESC.code, DATA_MANAGE.code,
            AIR_PLAN.code, DEVICE_MANAGE.code, WORK_ORDER.code);
}
