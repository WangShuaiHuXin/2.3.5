package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.PowerMeterDefectMarkEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 缺陷识别标注
 *
 * @author boluo
 * @date 2023-03-08
 */
@Mapper
public interface PowerMeterDefectMarkMapper extends BaseMapper<PowerMeterDefectMarkEntity> {

    /**
     * 批量添加
     *
     * @param powerMeterDefectMarkEntityList 功率计缺陷马克实体列表
     * @return int
     */
    int batchAdd(@Param("entityList") List<PowerMeterDefectMarkEntity> powerMeterDefectMarkEntityList);
}
