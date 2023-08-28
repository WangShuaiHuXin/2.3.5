package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 基站-单位关系数据
 *
 * @author Vastfy
 * @date 2022/8/25 15:25
 * @since 2.0.0
 */
@Data
public class NestOrgRefOutDTO implements Serializable {

    private String nestId;

    private String orgCode;

    private String orgName;

}
