package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

/**
 * @Classname PersonManageInDTO
 * @Description 人员管理信息类
 * @Date 2023/3/28 13:40
 * @Author Carnival
 */
@Data
public class PersonManageInDTO {

    private String personType;

    private String gender;

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
}
