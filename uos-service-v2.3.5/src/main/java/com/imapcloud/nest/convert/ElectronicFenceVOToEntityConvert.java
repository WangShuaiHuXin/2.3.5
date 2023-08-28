package com.imapcloud.nest.convert;

import com.imapcloud.nest.common.convert.intf.Translator;
import com.imapcloud.nest.model.ElectronicFenceEntity;
import com.imapcloud.nest.pojo.vo.ElectronicFenceVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName ElectronicFenceVOToEntityConvert.java
 * @Description 转换对象
 * @createTime 2022年03月09日 10:05:00
 */
@Mapper
public interface ElectronicFenceVOToEntityConvert extends Translator<ElectronicFenceVO, ElectronicFenceEntity> {

    ElectronicFenceVOToEntityConvert INSTANCES = Mappers.getMapper(ElectronicFenceVOToEntityConvert.class);

    /**
     *
     * @param electronicFenceEntities
     * @return
     */
    @Override
    List<ElectronicFenceVO> doToDto(List<ElectronicFenceEntity> electronicFenceEntities);

    /**
     *
     * @param electronicFenceVOs
     * @return
     */
    @Override
    List<ElectronicFenceEntity> dtoToDo(List<ElectronicFenceVO> electronicFenceVOs);

    /**
     * 更新实体
     * @param electronicFenceVO
     * @param electronicFenceEntity
     */
    @Mappings({
    })
    void updatePubMessageEntity(ElectronicFenceVO electronicFenceVO, @MappingTarget ElectronicFenceEntity electronicFenceEntity);

    /**
     *
     * @param electronicFenceEntity
     * @return
     */
    @Override
    @Mappings({
    })
    ElectronicFenceVO doToDto(ElectronicFenceEntity electronicFenceEntity);

    /**
     *
     * @param electronicFenceVO
     * @return
     */
    @Override
    @Mappings({
    })
    ElectronicFenceEntity dtoToDo(ElectronicFenceVO electronicFenceVO);

}
