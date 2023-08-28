package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_nest_account")
public class BaseNestAccountEntity extends GenericEntity {


    private String nestId;
    private Long accountId;
    private String baseNestId;


}
