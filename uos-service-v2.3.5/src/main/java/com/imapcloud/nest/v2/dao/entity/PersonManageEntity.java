package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname PersonManageEntity
 * @Description 人员管理实体类
 * @Date 2023/3/28 14:05
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("person_manage")
public class PersonManageEntity extends GenericEntity {

    private String personId;

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
