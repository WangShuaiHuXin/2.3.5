package com.imapcloud.nest.convert;

import com.imapcloud.nest.common.convert.intf.Translator;
import com.imapcloud.nest.model.PubMessageEntity;
import com.imapcloud.nest.pojo.vo.PubMessageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PubMessageVOToEntityConvert.java
 * @Description 转换对象
 * @createTime 2022年03月09日 10:05:00
 */
@Mapper
public interface PubMessageVOToEntityConvert extends Translator<PubMessageVO, PubMessageEntity> {

    PubMessageVOToEntityConvert INSTANCES = Mappers.getMapper(PubMessageVOToEntityConvert.class);

    @Override
    List<PubMessageVO> doToDto(List<PubMessageEntity> pubMessageEntityList);

    @Override
    List<PubMessageEntity> dtoToDo(List<PubMessageVO> pubMessageVOList);

    /**
     *  更新实体
     * @param pubMessageVO
     * @param pubMessageEntity
     */
    @Mappings({
//            @Mapping(target = "timeCreated", ignore = true),
//            @Mapping(source = "waypointIndex", target = "waypointIndex",defaultValue = "-1"),
//            @Mapping(source = "execMissionID", target = "execId"),
//            @Mapping(source = "fileSize", target = "photoSize")
    })
    void updatePubMessageEntity(PubMessageVO pubMessageVO, @MappingTarget PubMessageEntity pubMessageEntity);


    /**
     *  更新实体
     * @param pubMessageVO
     * @param pubMessageEntity
     */
    @Mappings({
            @Mapping(source = "creatorId", target = "createUserId")
//            @Mapping(target = "timeCreated", ignore = true),
//            @Mapping(source = "waypointIndex", target = "waypointIndex",defaultValue = "-1"),
//            @Mapping(source = "execMissionID", target = "execId"),
//            @Mapping(source = "fileSize", target = "photoSize")
    })
    void updatePubMessageVO(PubMessageEntity pubMessageEntity, @MappingTarget PubMessageVO pubMessageVO);

    @Override
    @Mappings({
//            @Mapping(target = "createTime", ignore = true),
//            @Mapping(source = "execId",target = "execMissionID")
    })
    PubMessageVO doToDto(PubMessageEntity pubMessageEntity);

    @Override
    @Mappings({
//            @Mapping(target = "createTime", ignore = true),
//            @Mapping(source = "execMissionID",target = "execId")
    })
    PubMessageEntity dtoToDo(PubMessageVO pubMessageVO);

}
