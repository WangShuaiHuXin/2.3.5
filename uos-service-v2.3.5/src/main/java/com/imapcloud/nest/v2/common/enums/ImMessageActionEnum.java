package com.imapcloud.nest.v2.common.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

/**
 * im消息类型枚举
 *
 * @author boluo
 * @date 2023-02-14
 */
@Getter
@AllArgsConstructor
public enum ImMessageActionEnum {

    /**
     * IM消息类型
     */
    NEST_LIST_DTO_NEST("NEST_LIST_DTO-NEST", "基站基本信息"),
    NEST_LIST_DTO_UAV("NEST_LIST_DTO-UAV", "无人机及任务信息"),
    NEST_LIST_DTO_ALARM("NEST_LIST_DTO-ALARM", "基站告警信息"),

    POWER_TASK_ANALYSIS("POWER_TASK_ANALYSIS", "电力分析"),
    ;

    private final String code;

    private final String msg;

    /**
     * key：页面路由  value：需要的消息类型
     */
    private static final Map<String, List<String>> PAGE_ACTION_MAP = new HashMap<>();

    static {
        PAGE_ACTION_MAP.put("none", Lists.newLinkedList());
        List<String> channelOne = Lists.newArrayList(
                NEST_LIST_DTO_NEST.code,
                NEST_LIST_DTO_UAV.code,
                NEST_LIST_DTO_ALARM.code);
        for (String page : ImPageEnum.CHANNEL_ONE_LIST) {
            PAGE_ACTION_MAP.put(page, channelOne);
        }

        List<String> powerTaskList = Lists.newArrayList(
                POWER_TASK_ANALYSIS.code);
        PAGE_ACTION_MAP.put(ImPageEnum.ANALYSE_CENTER.getCode(), powerTaskList);
    }

    public static List<String> getActionList(String page) {
        List<String> stringList = PAGE_ACTION_MAP.get(page);
        return stringList == null ? Collections.emptyList() : stringList;
    }
}
