package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.MultiNestMissionDefaultRoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 多基站任务默认架次表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2021-02-03
 */
public interface MultiNestMissionDefaultRoleMapper extends BaseMapper<MultiNestMissionDefaultRoleEntity> {

    /**
     * 批量查询missionId通过nestIds
     * @param nestIds
     * @return
     */
    List<Integer> batchSelectMissionIdByNestIds(@Param("nestIds") List<Integer> nestIds);

}
