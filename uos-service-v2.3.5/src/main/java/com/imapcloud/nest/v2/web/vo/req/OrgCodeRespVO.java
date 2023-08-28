package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import java.io.Serializable;

/**
 * 单位编码检索视图
 * @author Vastfy
 * @date 2022/8/23 17:00
 * @since 2.0.0
 */
@Data
public class OrgCodeRespVO implements Serializable {

    private String orgCode;

}
