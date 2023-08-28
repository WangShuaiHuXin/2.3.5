package com.imapcloud.nest.v2.service.dto.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname AirspaceManageOutDTO
 * @Description 空域管理信息类
 * @Date 2023/3/8 18:07
 * @Author Carnival
 */
@Data
public class AirspaceManageOutDTO implements Serializable {

    private String airspaceId;

    private String orgName;

    private String orgCode;

    private String photoUrl;

    private String airspaceName;

    private String startTime;

    private String endTime;

    private String uavName;

    private Double aglAltitude;

    private Integer isApproval;

    /*中科天网*/
    private Integer applicantType;

    private Integer missionType;
}
