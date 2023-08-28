package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.DataEquipmentPointRelEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataEquipmentPointRelMapper extends BaseMapper<DataEquipmentPointRelEntity> {
    void saveBatch(@Param("list") List<DataEquipmentPointRelEntity> collect);

    void deleteBatch(@Param("list") List<String> collect);
}
