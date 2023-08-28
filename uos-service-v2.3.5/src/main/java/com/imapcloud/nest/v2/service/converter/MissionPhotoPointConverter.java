package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.v2.service.dto.out.MissionPhotoPointOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName MissionPhotoPointConverter.java
 * @Description MissionPhotoPointConverter
 * @createTime 2022年09月22日 14:16:00
 */
@Mapper(componentModel = "spring")
public interface MissionPhotoPointConverter {

    MissionPhotoPointConverter INSTANCES = Mappers.getMapper(MissionPhotoPointConverter.class);

    /**
     * 转换 出口
     * @param outPO
     * @return
     */
    @Mappings({
            @Mapping(source="id",target = "missionPhotoId"),
            @Mapping(source="name",target = "missionPhotoName"),
            @Mapping(source="thumbnailUrl",target = "thumbnailUrl"),
            @Mapping(source="photoUrl",target = "photoUrl"),
            @Mapping(source="waypointIndex",target = "waypointsIndex")
    })
    MissionPhotoPointOutDTO convert(MissionPhotoEntity outPO);


}
