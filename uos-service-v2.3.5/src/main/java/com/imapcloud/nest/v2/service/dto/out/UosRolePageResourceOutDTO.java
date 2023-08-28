package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色-页面资源输出实体
 * @author Vastfy
 * @date 2022/5/19 9:57
 * @since 1.0.0
 */
@Data
public class UosRolePageResourceOutDTO implements Serializable {

    private String roleId;

    private String pageResourceId;

}
