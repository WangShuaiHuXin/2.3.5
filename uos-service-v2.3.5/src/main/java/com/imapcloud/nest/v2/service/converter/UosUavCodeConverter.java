package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.service.dto.in.UosUavCodeInDTO;
import com.imapcloud.sdk.manager.camera.entity.InfraredTestTempeParamEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosUavCodeConverter.java
 * @Description UosUavCodeConverter
 * @createTime 2022年09月22日 14:16:00
 */
@Mapper(componentModel = "spring")
public interface UosUavCodeConverter {

    UosUavCodeConverter INSTANCES = Mappers.getMapper(UosUavCodeConverter.class);

    /**
     * 转换 入口
     * @param inDTO
     * @return
     */
    @Mappings({
    })
    InfraredTestTempeParamEntity convert(UosUavCodeInDTO.InfraredTestTempeInDTO inDTO);


}
