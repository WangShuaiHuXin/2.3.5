package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.UosMqttEntity;
import com.imapcloud.nest.v2.dao.mapper.UosMqttMapper;
import com.imapcloud.nest.v2.manager.dataobj.out.UosMqttOutDO;
import com.imapcloud.nest.v2.manager.sql.UosMqttManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * mqtt
 *
 * @author boluo
 * @date 2022-08-26
 */
@Component
public class UosMqttManagerImpl implements UosMqttManager {

    @Resource
    private UosMqttMapper uosMqttMapper;

    private UosMqttOutDO toUosMqttOutDO(UosMqttEntity uosMqttEntity) {

        UosMqttOutDO uosMqttOutDO = new UosMqttOutDO();
        uosMqttOutDO.setMqttBrokerId(uosMqttEntity.getMqttBrokerId());
        uosMqttOutDO.setMqttName(uosMqttEntity.getMqttName());
        uosMqttOutDO.setOuterDomain(uosMqttEntity.getOuterDomain());
        uosMqttOutDO.setInnerDomain(uosMqttEntity.getInnerDomain());
        uosMqttOutDO.setAccount(uosMqttEntity.getAccount());
        uosMqttOutDO.setPassword(uosMqttEntity.getPassword());
        return uosMqttOutDO;
    }

    @Override
    public UosMqttOutDO selectOneByMqttBrokerId(String mqttBrokerId) {
        LambdaQueryWrapper<UosMqttEntity> eq = Wrappers.lambdaQuery(UosMqttEntity.class).eq(UosMqttEntity::getMqttBrokerId, mqttBrokerId);
        List<UosMqttEntity> uosMqttEntityList = uosMqttMapper.selectList(eq);
        if (CollUtil.isNotEmpty(uosMqttEntityList)) {

            return toUosMqttOutDO(uosMqttEntityList.get(0));
        }
        return null;
    }
}
