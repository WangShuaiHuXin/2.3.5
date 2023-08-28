package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.LayerVectorsEntity;
import com.imapcloud.nest.v2.service.dto.out.LayerVectorsOutDTO;
import org.mapstruct.Mapper;

/**
 * @Classname LayerVectorsConverter
 * @Description 矢量图层转换器
 * @Date 2022/8/11 18:21
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface LayerVectorsConverter {


    LayerVectorsOutDTO convert(LayerVectorsEntity entity);

}
