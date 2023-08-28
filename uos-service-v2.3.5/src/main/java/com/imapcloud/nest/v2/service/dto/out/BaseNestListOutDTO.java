package com.imapcloud.nest.v2.service.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseNestListOutDTO {
    private String regionId;
    private String regionName;
    private String nestName;
    /**
     * 0 - 正常
     * 1 - 执行中
     * 2 - 异常
     */
    private Integer nestStatus;
    private String nestAddress;
    private String uuid;

    /**
     * 机巢纬度
     */
    private Double lat;
    /**
     * 机巢经度
     */
    private Double lon;
    /**
     * 机巢海拔
     */
    private Double alt;

    /**
     * 对地高度
     */
    private BigDecimal aglAltitude;

    /**
     * 基站ID
     */
    private String nestId;

    /**
     * 飞机图传
     *  @deprecated 2.3.2
     */
    @Deprecated
    private String picTranUrl;

    /**
     * 巢外监控
     * @deprecated 2.3.2
     */
    @Deprecated
    private String outerVideoUrl;

    /**
     * 巢内监控
     * @deprecated 2.3.2
     */
    @Deprecated
    private String innerVideoUrl;

    /**
     * 机巢类型
     */
    private Integer nestType;
    /**
     * 单位Id
     */
    private List<String> unitIds;

    //展示监控的状态
    private Integer showStatus;

    /**
     * 机巢编号
     */
    private String nestNumber;

    /**
     * g503 无人机图传地址
     * @deprecated 2.3.2
     */
    @Deprecated
    private Map<String, String> g503PicTranMap;

    /**
     * 无人机ID列表
     * @since 2.3.2
     */
    List<String> uavIds;

}
