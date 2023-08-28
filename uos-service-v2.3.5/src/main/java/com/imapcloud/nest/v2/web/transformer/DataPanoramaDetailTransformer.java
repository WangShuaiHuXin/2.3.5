package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.DataPanoramaDetailInDTO;
import com.imapcloud.nest.v2.service.dto.in.PanoramaDataDetailInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaDetailOutDTO;
import com.imapcloud.nest.v2.web.vo.req.DataPanoramaDetailReqVO;
import com.imapcloud.nest.v2.web.vo.req.PanoramaDataDetailReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataPanoramaDetailRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaDetailTransformer.java
 * @Description DataPanoramaDetailTransformer
 * @createTime 2022年07月08日 17:03:00
 */
@Mapper(componentModel = "spring")
public interface DataPanoramaDetailTransformer {

    DataPanoramaDetailTransformer INSTANCES = Mappers.getMapper(DataPanoramaDetailTransformer.class);

    /**
     * 转换入口
     * @param queryPageReqVO
     * @return
     */
    DataPanoramaDetailInDTO.QueryPageInDTO transform(DataPanoramaDetailReqVO.QueryPageReqVO queryPageReqVO);


    /**
     * 转换入口
     * @param queryOneReqVO
     * @return
     */
    DataPanoramaDetailInDTO.QueryOneInDTO transform(DataPanoramaDetailReqVO.QueryOneReqVO queryOneReqVO);

    /**
     * 转换入口
     * @param queryReqVO
     * @return
     */
    DataPanoramaDetailInDTO.QueryInDTO transform(DataPanoramaDetailReqVO.QueryReqVO queryReqVO);

    /**
     * 转换入口
     * @param detailUploadReqVO
     * @return
     */
    DataPanoramaDetailInDTO.DetailUploadInDTO transform(DataPanoramaDetailReqVO.DetailUploadReqVO detailUploadReqVO);

    /**
     * 转换出口
     * @param queryPageOutDTO
     * @return
     */
    DataPanoramaDetailRespVO.QueryPageRespVO transform(DataPanoramaDetailOutDTO.QueryPageOutDTO queryPageOutDTO);

    /**
     * 转换出口
     * @param queryLessOutDTO
     * @return
     */
    DataPanoramaDetailRespVO.QueryLessRespVO transform(DataPanoramaDetailOutDTO.QueryLessOutDTO queryLessOutDTO);

    /**
     * 转换出口
     * @param queryOutDTO
     * @return
     */
    DataPanoramaDetailRespVO.QueryRespVO transform(DataPanoramaDetailOutDTO.QueryOutDTO queryOutDTO);

    PanoramaDataDetailInDTO transform(PanoramaDataDetailReqVO data);

}
