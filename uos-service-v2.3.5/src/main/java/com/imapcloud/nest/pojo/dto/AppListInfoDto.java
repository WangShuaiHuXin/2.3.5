package com.imapcloud.nest.pojo.dto;

import lombok.Data;

/**
 * Created by wmin on 2020/11/4 10:41
 *
 * @author wmin
 */
@Data
public class AppListInfoDto {
    private Integer id;
    private String name;
    private Integer state;
    private String driver;
    private String deviceId;
    private String unitId;
    private String unitName;
    private Double longitude;
    private Double latitude;
    private String picSendUrl;
    private String pullHttp;
    //展示监控的状态
    private Integer showStatus;
}
