package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname UosNestStreamRefEntity
 * @Description 基站——设备关联实体类
 * @Date 2023/4/3 14:06
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("uos_nest_stream_ref")
public class UosNestStreamRefEntity extends GenericEntity {

    private String nestId;

    private String streamId;

    private Integer streamUse;

}
