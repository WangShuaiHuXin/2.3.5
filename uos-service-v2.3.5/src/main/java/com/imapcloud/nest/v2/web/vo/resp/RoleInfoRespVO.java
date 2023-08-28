package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色信息resp签证官
 *
 * @author boluo
 * @date 2022-05-20
 */
@Data
public class RoleInfoRespVO implements Serializable {

    private String id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色类型【1：默认（不能修改其权限，允许上级管理员可修改；0：其他】
     */
    private Integer roleType;

    /**
     * 角色权限编辑状态
     */
    private boolean editStatus;
}
