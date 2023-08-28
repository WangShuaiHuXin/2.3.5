package com.imapcloud.nest.pojo.DO;

import com.imapcloud.nest.model.EarlyWarningEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 天气/区域警告配置 DO
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class EarlyWarningDo extends EarlyWarningEntity {

    private String orgCode;

    private Integer nestId;

}
