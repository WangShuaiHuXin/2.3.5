package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrgInfoOutDO implements Serializable {

    private String orgCode;

    private String orgName;

    private String latitude;

    private String longitude;

    private String parentCode;

    private String parentName;

}
