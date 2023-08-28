package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 基站信息查询条件
 * @author Vastfy
 * @date 2022/07/08 11:35
 * @since 1.9.7
 */
@Data
public class NestQueryInDTO implements Serializable {

    /**
     * 关键字，同时支持基站编号、基站UUID和基站名称（支持模糊检索）
     */
    private String keyword;

}
