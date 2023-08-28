package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 账号新建信息
 * @author Vastfy
 * @date 2022/5/18 17:35
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AccountModificationInDTO extends AccountCreationInDTO {

    /**
     * 账号ID
     */
    private String id;

    private List<String> incRoleIds;

    private List<String> decRoleIds;

}