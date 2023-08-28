package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.service.dto.out.UosNestCodeOutDTO;
import com.imapcloud.sdk.manager.general.entity.NestNetworkStateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosUavCodeConverter.java
 * @Description UosUavCodeConverter
 * @createTime 2022年07月08日 17:03:00
 */
@Mapper(componentModel = "spring")
public interface UosNestCodeConverter {

    UosNestCodeConverter INSTANCES = Mappers.getMapper(UosNestCodeConverter.class);

    /**
     * 转换入口
     * @param en
     * @return
     */
    UosNestCodeOutDTO.NestNetworkStateOutDTO convert(NestNetworkStateEntity en);


}
