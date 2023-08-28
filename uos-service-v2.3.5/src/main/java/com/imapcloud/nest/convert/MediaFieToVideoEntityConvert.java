package com.imapcloud.nest.convert;

import com.imapcloud.nest.common.convert.intf.Translator;
import com.imapcloud.nest.model.MissionVideoEntity;
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
 * @ClassName MediaFieToVideoEntityConvert.java
 * @Description 转换对象
 * @createTime 2022年03月09日 10:05:00
 */
@Mapper
public interface MediaFieToVideoEntityConvert extends Translator<MediaFile, MissionVideoEntity> {

    MediaFieToVideoEntityConvert INSTANCES = Mappers.getMapper(MediaFieToVideoEntityConvert.class);

    @Override
    List<MediaFile> doToDto(List<MissionVideoEntity> mediaFileList);

    @Override
    List<MissionVideoEntity> dtoToDo(List<MediaFile> mediaFileList);

    /**
     *  更新实体
     * @param mediaFile
     * @param missionVideoEntity
     */
    @Mappings({
            @Mapping(target = "timeCreated", ignore = true),
            @Mapping(source = "execMissionID",target = "execId"),
            @Mapping(source = "fileSize", target = "videoSize")
    })
    void updateMissionVideoEntity(MediaFile mediaFile, @MappingTarget MissionVideoEntity missionVideoEntity);

    @Override
    @Mappings({
            @Mapping(target = "timeCreated", ignore = true),
            @Mapping(source = "execId",target = "execMissionID")
    })
    MediaFile doToDto(MissionVideoEntity mediaFile);

    @Override
    @Mappings({
            @Mapping(target = "timeCreated", ignore = true),
            @Mapping(source = "execMissionID",target = "execId")
    })
    MissionVideoEntity dtoToDo(MediaFile mediaFile);

}
