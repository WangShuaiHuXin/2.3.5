package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 基站-用户关联信息
 */

@Data
public class NestUnitOutDO implements Serializable {

    private String nestId;

    private List<String> unitIds;

}
