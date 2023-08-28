package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.pojo.dto.UosNestStreamRefDTO;
import com.imapcloud.nest.v2.dao.entity.UosNestStreamRefEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Classname UosNestStreamRefMapper
 * @Description 基站——设备关联 Mapper
 * @Date 2023/4/6 13:43
 * @Author Carnival
 */
public interface UosNestStreamRefMapper extends BaseMapper<UosNestStreamRefEntity> {


    int batchInsert(@Param("entityList") List<UosNestStreamRefEntity> newEntityList);

    //int insert(UosNestStreamRefEntity entity);

    int updateStreamIdByNestId(String nestId, String streamId, Integer streamUse);

    int delByNestId(String nestId);

    int checkByNestId(@Param("nestId") String nestId, @Param("streamUse") Integer streamUse);

    UosNestStreamRefDTO findByNestId(String nestId, Integer streamUse);

    List<UosNestStreamRefDTO> listByNestId(String nestId);
}
