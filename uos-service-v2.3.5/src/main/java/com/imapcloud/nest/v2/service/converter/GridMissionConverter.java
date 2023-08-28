package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.v2.dao.entity.GridMissionRecordEntity;
import com.imapcloud.nest.v2.dao.po.in.GridHistoryPhotoPO;
import com.imapcloud.nest.v2.dao.po.out.GridStatisticsPO;
import com.imapcloud.nest.v2.service.dto.out.GridMissionRecordPageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;
import org.mapstruct.Mapper;

/**
 * @Classname GridMissionConverter
 * @Description 网格任务管理转换器
 * @Date 2022/8/11 18:21
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface GridMissionConverter {

    GridOutDTO.Photo convert(MissionPhotoEntity entity);
    GridOutDTO.Photo convertPhotoPo(GridHistoryPhotoPO po);
    GridOutDTO.MissionRecordsDTO convert(MissionRecordsEntity entity);
    GridMissionRecordPageOutDTO convert(GridMissionRecordEntity entity);
    GridOutDTO.GridStatisticsDTO convert(GridStatisticsPO po);



}
