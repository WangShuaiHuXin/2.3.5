package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName SysOrgPilotSpaceEntity.java
 * @Description SysOrgPilotSpaceEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_org_pilot_space")
public class SysOrgPilotSpaceEntity extends GenericEntity {

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * pilot工作空间
     */
    private String workSpaceId;


}
