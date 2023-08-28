package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

@Data
public class DictItemInfoOutDO {
    /**
     * 字典项ID
     */
    private String itemId;

    /**
     * 字典项名称
     */
    private String itemName;

    /**
     * 字典项值
     */
    private String itemValue;

    /**
     * 字典项状态
     */
    private Integer itemStatus;

}
