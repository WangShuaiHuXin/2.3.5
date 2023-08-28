package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.DefectTypeTrafficEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 */
public interface DefectTypeTrafficMapper extends BaseMapper<DefectTypeTrafficEntity> {
    /**
     * 根据类型、单位获取全部类型
     */
    List<DefectTypeTrafficEntity> getAllList(@Param("name") String name, @Param("orgCode") String orgCode, @Param("type") Integer type);

}
