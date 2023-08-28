package com.imapcloud.nest.convert;

import com.imapcloud.nest.common.convert.intf.Translator;
import com.imapcloud.nest.model.PubMessageEntity;
import com.imapcloud.nest.pojo.dto.message.PubMessageUpdDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PubMessageUpdToEntityConvert.java
 * @Description 转换对象
 * @createTime 2022年03月09日 10:05:00
 */
@Mapper
public interface PubMessageUpdToEntityConvert extends Translator<PubMessageUpdDTO, PubMessageEntity> {

    PubMessageUpdToEntityConvert INSTANCES = Mappers.getMapper(PubMessageUpdToEntityConvert.class);

    @Override
    List<PubMessageUpdDTO> doToDto(List<PubMessageEntity> pubMessageEntityList);

    @Override
    List<PubMessageEntity> dtoToDo(List<PubMessageUpdDTO> pubMessageUpdDTOList);

    /**
     *  更新实体
     * @param pubMessageUpdDTO
     * @param pubMessageEntity
     */
    @Mappings({
            @Mapping(source = "createUserId", target = "creatorId")
    })
    void updatePubMessageEntity(PubMessageUpdDTO pubMessageUpdDTO, @MappingTarget PubMessageEntity pubMessageEntity);

    @Override
    @Mappings({
//            @Mapping(target = "createTime", ignore = true),
//            @Mapping(source = "execId",target = "execMissionID")
    })
    PubMessageUpdDTO doToDto(PubMessageEntity pubMessageEntity);

    @Override
    @Mappings({
//            @Mapping(target = "createTime", ignore = true),
//            @Mapping(source = "execMissionID",target = "execId")
    })
    PubMessageEntity dtoToDo(PubMessageUpdDTO pubMessageUpdDTO);

}
