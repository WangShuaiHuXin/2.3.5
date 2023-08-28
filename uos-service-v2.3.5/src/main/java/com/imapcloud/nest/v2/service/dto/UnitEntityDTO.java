package com.imapcloud.nest.v2.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 兼容旧版单位实体
 *
 * @author Vastfy
 * @date 2022/8/25 10:23
 * @since 2.0.0
 */
@Data
public class UnitEntityDTO implements Serializable {

    /**
     * 兼容orgCode
     */
    private String id;

    /**
     * 部门名字
     */
    private String name;
    /**
     * 单位描述
     */
    private String description;

    /**
     * 单位LOGO url
     */
    private String iconUrl;

    /**
     * 单位图标url
     */
    private String faviconUrl;

    /**
     * 单位标题
     */
    private String title;

    /**
     * 经度
     */
    private Double latitude;

    /**
     * 纬度
     */
    private Double longitude;

    /**
     * 系统主题：0-绿色，1-蓝色
     */
    private Integer theme;

    private String parentId;

    private Integer loginSetting;
}
