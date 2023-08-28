package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.EarlyWarningKeyEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EarlyWarningKeyDto extends EarlyWarningKeyEntity {

    private String unitId;

}
