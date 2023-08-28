package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.PowerEquipmentLegerInfoEntity;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerEquipmentInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerEquipmentQueryDO;
import com.imapcloud.nest.v2.service.dto.out.PowerEquipmentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

@Mapper
public interface PowerEquipmentLegerInfoMapper extends BaseMapper<PowerEquipmentLegerInfoEntity>{

    int saveBatch(@Param("list") List<PowerEquipmentLegerInfoEntity> list);

    List<PowerEquipmentLegerInfoEntity> queryByCondition( PowerEquipmentQueryDO powerEquipmentQueryDO);

    void updateList(@Param("list")List<PowerEquipmentInDO> updateList);

    List<PowerEquipmentLegerInfoEntity> queryEquipmentById(String equipmentId);

    List<PowerEquipmentLegerInfoEntity> queryEquipmentsByIds(List<String> equipmentList);

    void updateEquipmentToDelete(List<String> equipmentList);
}
