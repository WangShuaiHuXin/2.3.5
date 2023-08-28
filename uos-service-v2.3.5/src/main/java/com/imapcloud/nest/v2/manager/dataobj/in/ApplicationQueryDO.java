package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 应用查询条件
 * @author Vastfy
 * @date 2022/08/16 16:12
 * @since 2.0.0
 */
@Data
public class ApplicationQueryDO implements Serializable {

    private String appType;

}
