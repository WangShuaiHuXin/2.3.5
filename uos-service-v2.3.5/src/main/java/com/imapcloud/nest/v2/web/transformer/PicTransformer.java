package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.web.vo.req.DataAnalysisMarkReqVO;
import com.imapcloud.nest.v2.web.vo.req.PicVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * 账号信息转换器
 * @author Vastfy
 * @date 2022/4/25 16:09
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface PicTransformer {

    PicTransformer INSTANCES = Mappers.getMapper(PicTransformer.class);

    @Mappings({
            @Mapping(source = "x1", target = "recX"),
            @Mapping(source = "y1", target = "recY"),
            @Mapping(source = "w1", target = "recWidth"),
            @Mapping(source = "h1", target = "recHeight"),
            @Mapping(source = "x2", target = "relX"),
            @Mapping(source = "y2", target = "relY"),
            @Mapping(source = "w2", target = "cutWidth"),
            @Mapping(source = "h2", target = "cutHeight"),
            @Mapping(source = "scale", target = "picScale"),
            @Mapping(source = "ww", target = "width"),
            @Mapping(source = "hh", target = "height")
    })
    DataAnalysisMarkReqVO transform(PicVO picVO);

}
