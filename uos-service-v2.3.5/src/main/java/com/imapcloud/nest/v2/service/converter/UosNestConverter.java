package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.manager.dataobj.in.GimbalAutoFollowDO;
import com.imapcloud.nest.v2.service.dto.in.GimbalAutoFollowDTO;
import com.imapcloud.nest.v2.web.vo.req.GimbalAutoFollowVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UosNestConverter {
    UosNestConverter INSTANCES = Mappers.getMapper(UosNestConverter.class);

    GimbalAutoFollowDO gimbalAutoFollowVO2GimbalAutoFollowDO(GimbalAutoFollowDTO dto);
}
