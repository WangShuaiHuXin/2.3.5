package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.UosMqttEntity;
import com.imapcloud.nest.v2.dao.po.UosMqttQueryCriteriaPO;

/**
 * @Classname UosMqttMapper
 * @Description Mqtt代理地址 Mapper 接口
 * @Date 2022/8/16 15:39
 * @Author Carnival
 */
public interface UosMqttMapper extends BaseMapper<UosMqttEntity>,
        IPageMapper<UosMqttEntity, UosMqttQueryCriteriaPO, PagingRestrictDo>{

    int queryMqttUsed(String mqttBrokerId);
}
