package com.imapcloud.nest.v2.manager.dataobj.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 单位主题信息
 */
@Data
public class OrgThemeInfoOutDO implements Serializable {

    private String systemType;

    private String systemTitle;

    private String iconUrl;

    private String faviconUrl;

    private Integer themeType;

    private String orgCode;

    private Integer loginSetting;

    private Integer pageType;
}
