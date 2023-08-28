package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.util.List;

/**
 * 表计数据信息
 *
 * @author Vastfy
 * @date 2022/11/29 15:38
 * @since 2.1.5
 */
@Data
public class PowerMeterFlightDataOutDTO {

    /**
     * 业务数据ID
     */
    private String dataId;

    /**
     * 飞行任务ID
     */
    private String flightTaskId;

    /**
     * 飞行任务标签信息[名称]
     */
    private String flightTaskTag;

    /**
     * 飞行任务名称（冗余）
     */
    private String taskName;

    /**
     * 架次顺序号
     */
    private Long missionSeqId;

    /**
     * 架次飞行次数
     */
    private Integer flyIndex;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * AI任务名称
     * 规则1：任务名称(taskName) + "-架次" + 第几个架次(missionSeqId)
     * 规则2（flyIndex > 0）：任务名称(taskName) + "-架次" + 第几个架次(missionSeqId) + "#" + 飞了第几次(flyIndex)
     */
    private String aiTaskName;

    /**
     * 飞行数据详情信息列表
     */
    private List<MeterDetailInfo> detailInfos;

    @Data
    public static class MeterDetailInfo {

        /**
         * 表计详情ID
         */
        private String detailId;

        /**
         * 图片名字
         */
        private String photoName;

        /**
         * 飞行数据原图URL
         */
        private String originalPicUrl;

        /**
         * 部件ID
         */
        private String componentId;

    }

}
