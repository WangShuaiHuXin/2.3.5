package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkPageInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkSaveInDTO;
import com.imapcloud.nest.v2.service.dto.in.MarkAddrInfoInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMarkOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMarkPageOutDTO;
import com.imapcloud.nest.v2.web.vo.req.DataAnalysisMarkQueryPageReqVO;
import com.imapcloud.nest.v2.web.vo.req.DataAnalysisMarkQueryReqVO;
import com.imapcloud.nest.v2.web.vo.req.DataAnalysisMarkReqVO;
import com.imapcloud.nest.v2.web.vo.req.MarkAddrInfoReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataAnalysisMarkRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 账号信息转换器
 * @author Vastfy
 * @date 2022/4/25 16:09
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface DataAnalysisMarkTransformer {

    DataAnalysisMarkTransformer INSTANCES = Mappers.getMapper(DataAnalysisMarkTransformer.class);

    /**
     * 转换入口
     * @param reqVO
     * @return
     */
    DataAnalysisMarkPageInDTO transform(DataAnalysisMarkQueryPageReqVO reqVO);

    /**
     * 转换入口
     * @param reqVO
     * @return
     */
    DataAnalysisMarkInDTO transform(DataAnalysisMarkQueryReqVO reqVO);

    /**
     * 转换入口
     * @param reqVO
     * @return
     */
    DataAnalysisMarkSaveInDTO transform(DataAnalysisMarkReqVO reqVO);

    /**
     * 转换出口
     * @param respVO
     * @return
     */
    DataAnalysisMarkRespVO transform(DataAnalysisMarkPageOutDTO respVO);

    /**
     * 转换出口
     * @param respVO
     * @return
     */
    DataAnalysisMarkRespVO transform(DataAnalysisMarkOutDTO respVO);

    MarkAddrInfoInDTO transform(MarkAddrInfoReqVO body);

}
