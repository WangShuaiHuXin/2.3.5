package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname PersonInfoEntity
 * @Description 个人信息实体类
 * @Date 2023/3/10 9:09
 * @Author Carnival
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("person_info")
public class PersonInfoEntity extends GenericEntity {

    /**
     * 账号ID
     */
    private String accountId;

    /**
     * 个人信息ID
     */
    private String personInfoId;

    /**
     * 身份证
     */
    private String personIp;

    /**
     * 执照
     */
    private String licenceCode;
}
