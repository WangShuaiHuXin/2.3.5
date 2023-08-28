package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.ElectronicFenceEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2021-09-26
 */
public interface ElectronicFenceMapper extends BaseMapper<ElectronicFenceEntity> {

    /**
     *  查询记录
     * @param orgIds
     * @param name
     * @return
     */
    List<ElectronicFenceEntity> selectAllList(@Param("orgCode") String orgCode, @Param("name") String name, @Param("containsChild")Integer containsChild);

}
