package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

/**
 * 单位识别功能查询条件
 * @author Vastfy
 * @date 2023/1/4 18:19
 * @since 2.1.7
 */
@Data
public class OrgVideoRecFunQueryInDO {

    private String orgCode;

    private String functionId;

}
