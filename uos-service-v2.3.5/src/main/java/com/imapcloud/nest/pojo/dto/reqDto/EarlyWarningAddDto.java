package com.imapcloud.nest.pojo.dto.reqDto;

import com.imapcloud.nest.model.EarlyWarningEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 天气/区域警告配置 dto
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class EarlyWarningAddDto extends EarlyWarningEntity {

    /**
     * 关联单位id数组
     */
    private List<String> unitIds;

}
