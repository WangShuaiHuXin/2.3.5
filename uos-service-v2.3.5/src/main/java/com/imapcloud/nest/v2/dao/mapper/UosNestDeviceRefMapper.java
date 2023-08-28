package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.UosNestDeviceRefEntity;
import com.imapcloud.nest.v2.service.dto.out.UosNestDeviceRefOutDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Classname UosNestDeviceRef
 * @Description 基站——设备关联 Mapper
 * @Date 2023/4/3 14:15
 * @Author Carnival
 */
public interface UosNestDeviceRefMapper extends BaseMapper<UosNestDeviceRefEntity>{

    int batchInsert(@Param("entityList") List<UosNestDeviceRefEntity> entityList);

    int batchUpdate(@Param("entityList") List<UosNestDeviceRefEntity> entityList);

    List<UosNestDeviceRefOutDTO> findByNestId(String nestId);
}
