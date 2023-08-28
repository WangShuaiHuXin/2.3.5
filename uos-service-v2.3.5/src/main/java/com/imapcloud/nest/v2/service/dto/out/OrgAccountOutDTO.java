package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询org信息
 *
 * @author boluo
 * @date 2022-05-23
 */

@Data
public class OrgAccountOutDTO implements Serializable {

    private Long accountId;

    private Integer unitId;

}
