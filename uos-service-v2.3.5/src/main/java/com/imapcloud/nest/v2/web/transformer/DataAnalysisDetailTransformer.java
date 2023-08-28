package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.DataAnalysisDetailPageInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisDetailSumInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisHistoryPicPageDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisDetailOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisDetailPageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisStateSumOutDTO;
import com.imapcloud.nest.v2.web.vo.req.*;
import com.imapcloud.nest.v2.web.vo.resp.DataAnalysisCenterDetailRespVO;
import com.imapcloud.nest.v2.web.vo.resp.DataAnalysisDetailMarkAllRespVO;
import com.imapcloud.nest.v2.web.vo.resp.DataAnalysisDetailMarkRespVO;
import com.imapcloud.nest.v2.web.vo.resp.DataAnalysisSumPicRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * 账号信息转换器
 * @author Vastfy
 * @date 2022/4/25 16:09
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface DataAnalysisDetailTransformer {

    DataAnalysisDetailTransformer INSTANCES = Mappers.getMapper(DataAnalysisDetailTransformer.class);

    /**
     * 转换入口
     * @param reqVO
     * @return
     */
    DataAnalysisDetailSumInDTO transform(DataAnalysisTaskSumPicForDateReqVO reqVO);

    /**
     * 转换入口
     * @param reqVO
     * @return
     */
    DataAnalysisDetailSumInDTO transform(DataAnalysisTaskSumPicForRecordReqVO reqVO);

    /**
     * 转换入口
     * @param reqVO
     * @return
     */
    DataAnalysisDetailSumInDTO transform(DataAnalysisTaskSumPicForAllReqVO reqVO);

    /**
     * 转换入口
     * @param reqVO
     * @return
     */
    DataAnalysisHistoryPicPageDTO transform(DataAnalysisHistoryPicPageReqVO reqVO);

    /**
     * 转换入口
     * @param reqVO
     * @return
     */
    @Mappings({
            @Mapping(target = "photoState",expression = "java(com.imapcloud.nest.v2.common.utils.WebPicStatusConverterUtils.getPhotoState(reqVO.getPicStatus()) )"),
            @Mapping(target = "pushState",expression = "java(com.imapcloud.nest.v2.common.utils.WebPicStatusConverterUtils.getPushState(reqVO.getPicStatus()) )")
    })
    DataAnalysisDetailPageInDTO transform(DataAnalysisTaskSumPicPageReqVO reqVO);

    /**
     * 转换入口
     * @param reqVO
     * @return
     */
    @Mappings({
            @Mapping(target = "photoState",expression = "java(com.imapcloud.nest.v2.common.utils.WebPicStatusConverterUtils.getPhotoState(reqVO.getPicStatus()) )"),
            @Mapping(target = "pushState",expression = "java(com.imapcloud.nest.v2.common.utils.WebPicStatusConverterUtils.getPushState(reqVO.getPicStatus()) )")
    })
    DataAnalysisDetailPageInDTO transform(DataAnalysisPicPageReqVO reqVO);

    /**
     *  转换出口
     * @param respVO
     * @return
     */
    @Mappings({
            @Mapping(source = "needAnalyzeSum",target = "needAnalyzeSum",defaultValue = "0")
            ,@Mapping(source = "needConfirmProblemSum",target = "needConfirmProblemSum",defaultValue = "0")
            ,@Mapping(source = "needConfirmNoProblemSum",target = "needConfirmNoProblemSum",defaultValue = "0")
            ,@Mapping(source = "problemSum",target = "problemSum",defaultValue = "0")
            ,@Mapping(source = "noProblemSum",target = "noProblemSum",defaultValue = "0")

    })
    DataAnalysisSumPicRespVO transform(DataAnalysisStateSumOutDTO respVO);

    /**
     *  转换出口
     * @param respVO
     * @return
     */
    @Mappings({
            @Mapping(target = "picStatus",expression = "java(com.imapcloud.nest.v2.common.utils.WebPicStatusConverterUtils.getWebPicStatus(respVO.getPushState() ,respVO.getPhotoState()) )")
    })
    DataAnalysisCenterDetailRespVO transform(DataAnalysisDetailPageOutDTO respVO);

    /**
     *  转换出口
     * @param respVO
     * @return
     */
    @Mappings({
            @Mapping(target = "picStatus",expression = "java(com.imapcloud.nest.v2.common.utils.WebPicStatusConverterUtils.getWebPicStatus(respVO.getPushState() ,respVO.getPhotoState()) )"),
            @Mapping(source = "orgCode", target = "orgId")
    })
    DataAnalysisDetailMarkRespVO transform(DataAnalysisDetailOutDTO respVO);

    /**
     *  转换出口
     * @param respVO
     * @return
     */
    @Mappings({
            @Mapping(target = "picStatus",expression = "java(com.imapcloud.nest.v2.common.utils.WebPicStatusConverterUtils.getWebPicStatus(respVO.getPushState() ,respVO.getPhotoState()) )")
    })
    DataAnalysisDetailMarkAllRespVO transformForBrowse(DataAnalysisDetailOutDTO respVO);

}
