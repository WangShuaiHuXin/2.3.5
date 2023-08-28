package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.WorkVectorsInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkVectorsInfoMapper extends BaseMapper<WorkVectorsInfoEntity> {
    public int saveBatch(@Param("list") List<WorkVectorsInfoEntity> infoEntities);
}
