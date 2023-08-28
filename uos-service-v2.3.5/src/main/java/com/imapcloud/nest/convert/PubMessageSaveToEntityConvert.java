package com.imapcloud.nest.convert;

import com.imapcloud.nest.common.convert.intf.Translator;
import com.imapcloud.nest.model.PubMessageEntity;
import com.imapcloud.nest.pojo.dto.message.PubMessageSaveDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PubMessageSaveToEntityConvert.java
 * @Description 转换对象
 * @createTime 2022年03月09日 10:05:00
 */
@Mapper
public interface PubMessageSaveToEntityConvert extends Translator<PubMessageSaveDTO, PubMessageEntity> {

    PubMessageSaveToEntityConvert INSTANCES = Mappers.getMapper(PubMessageSaveToEntityConvert.class);

    @Override
    List<PubMessageSaveDTO> doToDto(List<PubMessageEntity> pubMessageEntityList);

    @Override
    List<PubMessageEntity> dtoToDo(List<PubMessageSaveDTO> pubMessageSaveDTOList);

    /**
     *  更新实体
     * @param pubMessageSaveDTO
     * @param pubMessageEntity
     */
    @Mappings({
            @Mapping(target = "messageCode" , expression = "java(cn.hutool.core.lang.UUID.randomUUID().toString())"),
            @Mapping(source = "messageState",target = "messageState" ,defaultValue = "0"),
            @Mapping(source = "messageType",target = "messageType",defaultValue = "0"),
            @Mapping(source = "createUserId", target = "creatorId")
    })
    void updatePubMessageEntity(PubMessageSaveDTO pubMessageSaveDTO, @MappingTarget PubMessageEntity pubMessageEntity);

    @Override
    @Mappings({
            @Mapping(source = "messageState",target = "messageState" ,defaultValue = "0"),
            @Mapping(source = "messageType",target = "messageType",defaultValue = "0")
    })
    PubMessageSaveDTO doToDto(PubMessageEntity pubMessageEntity);

    @Override
    @Mappings({
            @Mapping(target = "messageCode" , expression = "java(cn.hutool.core.lang.UUID.randomUUID().toString())"),
            @Mapping(source = "messageState",target = "messageState" ,defaultValue = "0"),
            @Mapping(source = "messageType",target = "messageType",defaultValue = "0"),
            @Mapping(source = "createUserId", target = "creatorId")
    })
    PubMessageEntity dtoToDo(PubMessageSaveDTO pubMessageSaveDTO);

}
