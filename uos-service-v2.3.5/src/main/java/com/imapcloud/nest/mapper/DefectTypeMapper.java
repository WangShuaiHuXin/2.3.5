package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.DefectTypeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2021-02-23
 */
public interface DefectTypeMapper extends BaseMapper<DefectTypeEntity> {
    /**
     * 根据类型、单位获取全部类型
     * @param name
     * @return
     */
    List<DefectTypeEntity> getAllListByUnitId(@Param("name") String name);

}
