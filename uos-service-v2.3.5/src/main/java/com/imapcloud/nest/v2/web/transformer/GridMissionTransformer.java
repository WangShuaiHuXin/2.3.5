package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.v2.service.dto.in.GridHistoryInDTO;
import com.imapcloud.nest.v2.service.dto.in.GridInDTO;
import com.imapcloud.nest.v2.service.dto.in.GridMissionRecordPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.GridMissionRecordPageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;
import com.imapcloud.nest.v2.web.vo.req.GridHistoryReqVO;
import com.imapcloud.nest.v2.web.vo.req.GridMissionRecordPageReqVO;
import com.imapcloud.nest.v2.web.vo.req.GridReqVO;
import com.imapcloud.nest.v2.web.vo.resp.GridMissionRecordPageRespVO;
import com.imapcloud.nest.v2.web.vo.resp.GridRespVO;
import org.mapstruct.Mapper;

/**
 * @Classname GridRegionTransformer
 * @Description 网格区域转换器
 * @DATE 2022/12/09 14:11
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface GridMissionTransformer {

    GridRespVO.GridPhotoVO transform(GridOutDTO.PhotoDTO req);

    GridRespVO.MissionRecordsVO transform(GridOutDTO.MissionRecordsDTO dto);

    GridRespVO.InspectRecordVO transform(GridOutDTO.InspectRecordDTO dto);

    GridRespVO.GridStatisticsVO transform(GridOutDTO.GridStatisticsDTO dto);

    GridHistoryInDTO transform(GridHistoryReqVO vo);

    GridMissionRecordPageInDTO transform(GridMissionRecordPageReqVO vo);

    GridMissionRecordPageRespVO transform(GridMissionRecordPageOutDTO dto);

    GridRespVO.GridManageHasDataVO transform(GridOutDTO.GridManageHasDataDTO dto);

    GridRespVO.MissionStatusVO transform(GridOutDTO.MissionStatusDTO dto);
}
