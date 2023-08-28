package com.imapcloud.nest.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 机巢列表传输类
 *
 * @author wmin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NestListDto {
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

    private String nestId;

    /**
     * 飞机图传
     */
    private String picTranUrl;

    /**
     * 巢外监控
     */
    private String outerVideoUrl;

    /**
     * 巢内监控
     */
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
}
