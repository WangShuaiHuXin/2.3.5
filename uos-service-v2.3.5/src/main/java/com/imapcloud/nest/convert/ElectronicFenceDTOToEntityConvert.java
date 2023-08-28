package com.imapcloud.nest.convert;

import com.imapcloud.nest.common.convert.intf.Translator;
import com.imapcloud.nest.model.ElectronicFenceEntity;
import com.imapcloud.nest.pojo.dto.ElectronicFenceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName ElectronicFenceDTOToEntityConvert.java
 * @Description 转换对象
 * @createTime 2022年03月09日 10:05:00
 */
@Mapper
public interface ElectronicFenceDTOToEntityConvert extends Translator<ElectronicFenceDTO, ElectronicFenceEntity> {

    ElectronicFenceDTOToEntityConvert INSTANCES = Mappers.getMapper(ElectronicFenceDTOToEntityConvert.class);

    /**
     *
     * @param electronicFenceEntities
     * @return
     */
    @Override
    List<ElectronicFenceDTO> doToDto(List<ElectronicFenceEntity> electronicFenceEntities);

    /**
     *
     * @param electronicFenceDTOS
     * @return
     */
    @Override
    List<ElectronicFenceEntity> dtoToDo(List<ElectronicFenceDTO> electronicFenceDTOS);

    /**
     * 更新实体
     * @param electronicFenceDTO
     * @param electronicFenceEntity
     */
    @Mappings({
    })
    void updatePubMessageEntity(ElectronicFenceDTO electronicFenceDTO, @MappingTarget ElectronicFenceEntity electronicFenceEntity);

    /**
     *
     * @param electronicFenceEntity
     * @return
     */
    @Override
    @Mappings({
    })
    ElectronicFenceDTO doToDto(ElectronicFenceEntity electronicFenceEntity);

    /**
     *
     * @param electronicFenceDTO
     * @return
     */
    @Override
    @Mappings({
    })
    ElectronicFenceEntity dtoToDo(ElectronicFenceDTO electronicFenceDTO);

}
