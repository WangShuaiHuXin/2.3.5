package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.DataPanoramaRecordsInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaRecordsOutDTO;
import com.imapcloud.nest.v2.web.vo.req.DataPanoramaRecordsReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataPanoramaRecordsRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaRecordsTransformer.java
 * @Description DataPanoramaRecordsTransformer
 * @createTime 2022年07月08日 17:03:00
 */
@Mapper(componentModel = "spring")
public interface DataPanoramaRecordsTransformer {

    DataPanoramaRecordsTransformer INSTANCES = Mappers.getMapper(DataPanoramaRecordsTransformer.class);

    /**
     * 转换入口
     * @param recordsReqVO
     * @return
     */
    DataPanoramaRecordsInDTO.RecordsInDTO transform(DataPanoramaRecordsReqVO.RecordsReqVO recordsReqVO);

    /**
     * 转换入口
     * @param picReqVO
     * @return
     */
    DataPanoramaRecordsInDTO.PicInDTO transform(DataPanoramaRecordsReqVO.PicReqVO picReqVO);

    /**
     *  转换出口
     * @param recordsOutDTO
     * @return
     */
    @Mappings({
    })
    DataPanoramaRecordsRespVO.RecordsRespVO transform(DataPanoramaRecordsOutDTO.RecordsOutDTO recordsOutDTO);


    /**
     *  转换出口
     * @param recordsOutDTO
     * @return
     */
    @Mappings({
            @Mapping(source = "flyIndex",target = "missionFlyIndex")
    })
    DataPanoramaRecordsRespVO.RecordsRespVO transform(DataPanoramaRecordsOutDTO.RecordsPageOutDTO recordsOutDTO);


    /**
     *  转换出口
     * @param airPointOutDTO
     * @return
     */
    @Mappings({
    })
    DataPanoramaRecordsRespVO.AirPointRespVO transform(DataPanoramaRecordsOutDTO.AirPointOutDTO airPointOutDTO);

    /**
     *  转换出口
     * @param picOutDTO
     * @return
     */
    @Mappings({
    })
    DataPanoramaRecordsRespVO.PicRespVO transform(DataPanoramaRecordsOutDTO.PicOutDTO picOutDTO);

    /**
     *  转换出口
     * @param taskOutDTO
     * @return
     */
    @Mappings({
            @Mapping(source = "id",target = "taskId")
    })
    DataPanoramaRecordsRespVO.TaskRespVO transform(DataPanoramaRecordsOutDTO.TaskOutDTO taskOutDTO);

}
