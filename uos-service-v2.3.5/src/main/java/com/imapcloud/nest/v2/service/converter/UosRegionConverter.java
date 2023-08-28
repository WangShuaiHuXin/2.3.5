package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.UosRegionEntity;
import com.imapcloud.nest.v2.service.dto.out.UosRegionInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRegionQueryInfoOutDTO;
import org.mapstruct.Mapper;

/**
 * @Classname RegionConverter
 * @Description 区域管理转换器
 * @Date 2022/8/11 18:21
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface UosRegionConverter {

    UosRegionQueryInfoOutDTO convert(UosRegionEntity in);

    UosRegionInfoOutDTO convertInfo(UosRegionEntity entity);
}
