package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.DataEquipmentPointEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataEquipmentPointMapper extends BaseMapper<DataEquipmentPointEntity> {
    void updateOne(@Param("bean") DataEquipmentPointEntity equipmentPointEntity);

    void deleteBatch(@Param("list") List<String> deletes);
}
