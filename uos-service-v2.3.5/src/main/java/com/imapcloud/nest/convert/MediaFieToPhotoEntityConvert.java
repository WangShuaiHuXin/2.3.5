package com.imapcloud.nest.convert;

import com.imapcloud.nest.common.convert.intf.Translator;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.sdk.pojo.entity.MediaFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName MediaFieToPhotoEntityConvert.java
 * @Description 转换对象
 * @createTime 2022年03月09日 10:05:00
 */
@Mapper
public interface MediaFieToPhotoEntityConvert extends Translator<MediaFile,MissionPhotoEntity> {

    MediaFieToPhotoEntityConvert INSTANCES = Mappers.getMapper(MediaFieToPhotoEntityConvert.class);

    @Override
    List<MediaFile> doToDto(List<MissionPhotoEntity> mediaFileList);

    @Override
    List<MissionPhotoEntity> dtoToDo(List<MediaFile> mediaFileList);

    /**
     *  更新实体
     * @param mediaFile
     * @param missionPhotoEntity
     */
    @Mappings({
            @Mapping(target = "timeCreated", ignore = true),
            @Mapping(source = "waypointIndex", target = "waypointIndex",defaultValue = "-1"),
            @Mapping(source = "execMissionID", target = "execId"),
            @Mapping(source = "fileSize", target = "photoSize")
    })
    void updateMissionPhotoEntity(MediaFile mediaFile, @MappingTarget MissionPhotoEntity missionPhotoEntity);

    @Override
    @Mappings({
            @Mapping(target = "timeCreated", ignore = true),
            @Mapping(source = "execId",target = "execMissionID")
    })
    MediaFile doToDto(MissionPhotoEntity mediaFile);

    @Override
    @Mappings({
            @Mapping(target = "timeCreated", ignore = true),
            @Mapping(source = "execMissionID",target = "execId")
    })
    MissionPhotoEntity dtoToDo(MediaFile mediaFile);

}
