package com.imapcloud.nest.v2.service.dto.in;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Classname AirspaceManageInDTO
 * @Description 空域申请信息
 * @Date 2023/3/8 18:05
 * @Author Carnival
 */
@Data
public class AirspaceManageInDTO implements Serializable {


    private String airspaceName;

    private String orgCode;

    private String orgName;

    private String uavCode;

    private Integer uavCount;

    private String startTime;

    private String endTime;

    private Double altitude;

    private Double aglAltitude;

    private String address;

    private MultipartFile file;

    private String photoUrl;

    private String airCoor;

    private Integer airCoorCount;

    private Integer applicantType;
    
    private Integer missionType;
}
