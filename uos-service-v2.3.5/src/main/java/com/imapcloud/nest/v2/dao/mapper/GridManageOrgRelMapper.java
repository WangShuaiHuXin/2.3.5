package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.GridDataEntity;
import com.imapcloud.nest.v2.dao.entity.GridManageOrgRelEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Classname GridManageOrgRelMapper
 * @Description 网格与单位关联 Mapper
 * @Date 2023/4/11 16:51
 * @Author Carnival
 */
public interface GridManageOrgRelMapper extends BaseMapper<GridManageOrgRelEntity> {

    int batchInsert(List<GridManageOrgRelEntity> entityList);

    int batchDeleteTask(@Param("idList") List<Integer> idList);

    void batchUpdateTaskId(@Param("entityList") List<GridManageOrgRelEntity> gridManageOrgRelEntityList);

    List<String> queryOrgCode(@Param("orgCode") String orgCode);

    List<String> queryGridManage(@Param("orgCode") String orgCode);
}
