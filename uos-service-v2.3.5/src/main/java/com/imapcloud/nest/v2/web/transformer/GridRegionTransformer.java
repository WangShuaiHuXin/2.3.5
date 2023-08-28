package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;
import com.imapcloud.nest.v2.web.vo.req.*;
import com.imapcloud.nest.v2.web.vo.resp.GridRespVO;
import org.mapstruct.Mapper;

/**
 * @Classname GridRegionTransformer
 * @Description 网格区域转换器
 * @DATE 2022/12/09 14:11
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface GridRegionTransformer {

    GridInDTO.RegionInDTO transform(GridReqVO.RegionReqVO req);

    GridInDTO.GridManageInDTO transform(GridReqVO.GridManageReqVO req);

    GridRespVO.GridManageRespVO transform(GridOutDTO.GridManageOutDTO dto);

    GridRespVO.RegionRespVO transform(GridOutDTO.RegionOutDTO dto);

    GridRespVO.GridDateRespVO transform(GridOutDTO.GridDataOutDTO dto);

    GridRespVO.GridDataBatchVO transform(GridOutDTO.GridDataBatchDTO dto);

    GridInDTO.GridManageOrgCodeDTO transform(GridReqVO.GridManageOrgCodeVO vo);

    GridInDTO.GridCancelRelInDTO transform(GridReqVO.GridCancelRelReqVO vo);

    GridRespVO.OrgAndTaskRespVO transform(GridOutDTO.OrgAndTaskOutDTO dto);


}
