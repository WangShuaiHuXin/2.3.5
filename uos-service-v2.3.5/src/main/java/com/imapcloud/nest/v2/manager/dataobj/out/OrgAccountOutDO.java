package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询org信息
 *
 * @author boluo
 * @date 2022-05-23
 */

@Data
public class OrgAccountOutDO implements Serializable {

    private String accountId;

    private String orgCode;

}
