package com.imapcloud.nest.v2.service.dto.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname PersonManageOutDTO
 * @Description 人员管理信息类
 * @Date 2023/3/28 13:57
 * @Author Carnival
 */
@Data
public class PersonManageOutDTO {

    private String personId;

    private String gender;

    private String personType;

    private String orgCode;

    private String orgName;

    private String name;

    private String mobile;

    private String idCard;

    private String idCardType;

    private String idCardFrontUrl;

    private String idCardBackUrl;

    private String idCardDriveUrl;

    private String userType;

    private String creditCode;

    private String driveType;

    private String driveLicenseType;

    private String driveLicenseCode;

    @Data
    public static  class PersonManageCountOutDTO{
        private Integer total = Integer.valueOf(0);
        private Integer driTotal = Integer.valueOf(0);
        private Integer opeTotal = Integer.valueOf(0);
    }
}
