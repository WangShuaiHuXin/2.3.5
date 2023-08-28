package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.BaseUavEntity;
import com.imapcloud.nest.v2.dao.po.out.BaseAppUavOutPO;
import com.imapcloud.nest.v2.dao.po.out.BaseNestUavOutPO;
import com.imapcloud.nest.v2.service.dto.in.SaveUavInDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseUavInfoOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BaseUavConverter {
    BaseUavConverter INSTANCES = Mappers.getMapper(BaseUavConverter.class);

    BaseUavInfoOutDTO convert(BaseUavEntity baseUavEntity);

    BaseUavInfoOutDTO convert(BaseNestUavOutPO po);

    BaseUavInfoOutDTO convert(BaseAppUavOutPO po);

    BaseUavEntity convert(SaveUavInDTO saveUavInDTO);
}
