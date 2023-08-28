package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.GridDataEntity;
import com.imapcloud.nest.v2.dao.entity.GridManageEntity;
import com.imapcloud.nest.v2.dao.entity.GridManageOrgRelEntity;
import com.imapcloud.nest.v2.dao.entity.GridRegionEntity;
import com.imapcloud.nest.v2.dao.po.out.GridManageOrgCodeOutPO;
import com.imapcloud.nest.v2.service.dto.in.GridInDTO;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;
import org.mapstruct.Mapper;

/**
 * @Classname GridRegionConverter
 * @Description 区域网格管理转换器
 * @Date 2022/8/11 18:21
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface GridRegionConverter {

    GridManageEntity convert(GridInDTO.GridManageInDTO dto);

    GridOutDTO.RegionOutDTO convert(GridRegionEntity entity);

    GridOutDTO.GridManageOutDTO convert(GridManageEntity entity);

    GridOutDTO.GridDataOutDTO convert(GridDataEntity entity);

    GridManageOrgRelEntity convert(GridInDTO.GridManageOrgCodeDTO inDTO);

    GridInDTO.GridManageOrgCodeDTO convert(GridManageOrgCodeOutPO po);


}
