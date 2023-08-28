package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * @Classname PersonManagePageOutDTO
 * @Description 人员管理分页信息类
 * @Date 2023/3/28 14:01
 * @Author Carnival
 */
@Data
public class PersonManagePageOutDTO implements Serializable {

    private String personId;

    private String personType;

    private String orgCode;

    private String name;

    private String mobile;

    private String orgName;

    private String gender;
}
