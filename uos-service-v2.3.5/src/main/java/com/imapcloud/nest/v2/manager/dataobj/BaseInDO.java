package com.imapcloud.nest.v2.manager.dataobj;

import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author boluo
 * @date 2022-08-25
 */
@Data
public class BaseInDO {

    /**
     * 操作人ID
     */
    private String accountId;

    public void setInsertAccount(GenericEntity genericEntity) {
        genericEntity.setCreatorId(accountId);
        genericEntity.setModifierId(accountId);
        LocalDateTime now = LocalDateTime.now();
        genericEntity.setCreatedTime(now);
        genericEntity.setModifiedTime(now);
    }

    public void setUpdateAccount(GenericEntity genericEntity) {
        genericEntity.setModifierId(accountId);
        genericEntity.setModifiedTime(LocalDateTime.now());
    }
}
