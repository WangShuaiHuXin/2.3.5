package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname LayerVectorsEntity
 * @Description 图层矢量实体类
 * @Date 2022/12/9 15:21
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("layer_vectors")
public class LayerVectorsEntity extends GenericEntity {

    private Long Id;

    private String layerVectorId;

    private String layerVectorName;

    private String orgCode;

    private String layerVectorJson;
}
