package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.po.out.MissionRecordsOutPO;
import com.imapcloud.nest.v2.service.dto.in.PanoramaLocationDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaRecordsOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MissionPhotoPointOutDTO;
import com.imapcloud.nest.v2.service.dto.out.TaskOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaRecordsConverter.java
 * @Description DataPanoramaRecordsConverter
 * @createTime 2022年09月22日 14:16:00
 */
@Mapper(componentModel = "spring")
public interface DataPanoramaRecordsConverter {

    DataPanoramaRecordsConverter INSTANCES = Mappers.getMapper(DataPanoramaRecordsConverter.class);

    /**
     * 转换 出口
     * @param outPO
     * @return
     */
    @Mappings({
    })
    DataPanoramaRecordsOutDTO.RecordsOutDTO convert(MissionRecordsOutPO outPO);

    /**
     * 转换 出口
     * @param outPO
     * @return
     */
    @Mappings({
    })
    DataPanoramaRecordsOutDTO.RecordsPageOutDTO convertPage(MissionRecordsOutPO outPO);

    /**
     * 转换 出口
     * @param dto
     * @return
     */
    @Mappings({
            @Mapping(source="lng",target = "longtitude"),
            @Mapping(source="lat",target = "latitude"),
            @Mapping(source="alt",target = "altitude")
    })
    DataPanoramaRecordsOutDTO.AirPointOutDTO convert(PanoramaLocationDTO dto);


    /**
     * 转换 出口
     * @param dto
     * @return
     */
    @Mappings({
    })
    DataPanoramaRecordsOutDTO.PicOutDTO convert(MissionPhotoPointOutDTO dto);

    /**
     * 转换 出口
     * @param outPO
     * @return
     */
    @Mappings({
    })
    DataPanoramaRecordsOutDTO.TaskOutDTO convert(TaskOutDTO outPO);

}
