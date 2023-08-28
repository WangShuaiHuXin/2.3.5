package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.out.LayerVectorsOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.LayerVectorsRespVO;
import org.mapstruct.Mapper;

/**
 * @Classname LayerVectorsTransformer
 * @Description 矢量图层转换器
 * @DATE 2022/12/09 14:11
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface LayerVectorsTransformer {

    LayerVectorsRespVO transform(LayerVectorsOutDTO dto);

}
