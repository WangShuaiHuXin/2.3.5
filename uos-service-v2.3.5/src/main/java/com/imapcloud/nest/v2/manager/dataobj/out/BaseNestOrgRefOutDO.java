package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * 单位基站关系
 *
 * @author boluo
 * @date 2022-08-26
 */
@Data
public class BaseNestOrgRefOutDO {

    /**
     * 基站id
     */
    private String nestId;

    /**
     * 单位code
     */
    private String orgCode;
}
