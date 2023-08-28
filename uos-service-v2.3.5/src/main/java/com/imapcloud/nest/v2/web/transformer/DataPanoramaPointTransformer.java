package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.DataPanoramaPointInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaPointOutDTO;
import com.imapcloud.nest.v2.web.vo.req.DataPanoramaPointReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataPanoramaPointRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaPointTransformer.java
 * @Description DataPanoramaPointTransformer
 * @createTime 2022年07月08日 17:03:00
 */
@Mapper(componentModel = "spring")
public interface DataPanoramaPointTransformer {

    DataPanoramaPointTransformer INSTANCES = Mappers.getMapper(DataPanoramaPointTransformer.class);

    /**
     * 转换入口
     * @param addPointReqVO
     * @return
     */
    DataPanoramaPointInDTO.AddPointInDTO transform(DataPanoramaPointReqVO.AddPointReqVO addPointReqVO);

    /**
     * 转换入口
     * @param updatePointReqVO
     * @return
     */
    DataPanoramaPointInDTO.UpdatePointInDTO transform(DataPanoramaPointReqVO.UpdatePointReqVO updatePointReqVO);

    /**
     * 转换入口
     * @param queryPageReqVO
     * @return
     */
    DataPanoramaPointInDTO.QueryPageInDTO transform(DataPanoramaPointReqVO.QueryPageReqVO queryPageReqVO);


    /**
     * 转换入口
     * @param queryOneReqVO
     * @return
     */
    DataPanoramaPointInDTO.QueryOneInDTO transform(DataPanoramaPointReqVO.QueryOneReqVO queryOneReqVO);

    /**
     * 转换入口
     * @param queryReqVO
     * @return
     */
    DataPanoramaPointInDTO.QueryInDTO transform(DataPanoramaPointReqVO.QueryReqVO queryReqVO);

    /**
     * 转换出口
     * @param queryPageOutDTO
     * @return
     */
    DataPanoramaPointRespVO.QueryPageRespVO transform(DataPanoramaPointOutDTO.QueryPageOutDTO queryPageOutDTO);

    /**
     * 转换出口
     * @param queryLessOutDTO
     * @return
     */
    DataPanoramaPointRespVO.QueryLessRespVO transform(DataPanoramaPointOutDTO.QueryLessOutDTO queryLessOutDTO);

    /**
     * 转换出口
     * @param queryOutDTO
     * @return
     */
    DataPanoramaPointRespVO.QueryRespVO transform(DataPanoramaPointOutDTO.QueryOutDTO queryOutDTO);


}
