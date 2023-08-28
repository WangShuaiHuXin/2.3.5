package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 应用页面资源树信息
 * @author Vastfy
 * @date 2022/08/29 11:12
 * @since 2.0.0
 */
@Data
public class UosPageResourceNodeOutDTO implements Serializable {

    private String appType;

    private Integer resourceType;

    private String pageResourceKey;

    private String pageResourceName;

    private String pageResourceId;

    private String parentResourceId;

    private Integer seq;

    private List<UosPageResourceNodeOutDTO> children;
}
