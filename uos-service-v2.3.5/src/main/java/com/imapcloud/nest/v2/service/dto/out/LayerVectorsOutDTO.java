package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

/**
 * @Classname LayerVectorsOutDTO
 * @Description LayerVectorsOutDTO
 * @Date 2022/12/14 19:47
 * @Author Carnival
 */
@Data
public class LayerVectorsOutDTO {

    private String layerVectorId;

    private String layerVectorName;

    private String orgCode;

    private String layerVectorJson;
}
