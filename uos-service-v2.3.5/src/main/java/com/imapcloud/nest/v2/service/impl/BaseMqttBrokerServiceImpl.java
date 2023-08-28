package com.imapcloud.nest.v2.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.BaseMqttBrokerEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseMqttBrokerMapper;
import com.imapcloud.nest.v2.service.BaseMqttBrokerService;
import com.imapcloud.nest.v2.service.converter.BaseMqttBrokerConverter;
import com.imapcloud.nest.v2.service.dto.out.MqttBrokerInfoOutDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * mqtt代理信息表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
@Service
public class BaseMqttBrokerServiceImpl implements BaseMqttBrokerService {

    @Resource
    private BaseMqttBrokerMapper baseMqttBrokerMapper;

    @Override
    public List<MqttBrokerInfoOutDTO> listMqttBrokerInfos(List<String> mqttBrokerIdList) {
        if (CollectionUtil.isNotEmpty(mqttBrokerIdList)) {
            LambdaQueryWrapper<BaseMqttBrokerEntity> con = Wrappers.lambdaQuery(BaseMqttBrokerEntity.class).in(BaseMqttBrokerEntity::getMqttBrokerId, mqttBrokerIdList);
            List<BaseMqttBrokerEntity> baseMqttBrokerEntities = baseMqttBrokerMapper.selectList(con);
            return Optional.ofNullable(baseMqttBrokerEntities)
                    .map(ls -> ls.stream().map(BaseMqttBrokerConverter.INSTANCES::convert)
                    .collect(Collectors.toList()))
                    .orElse(Collections.emptyList());
        }
        return Collections.emptyList();
    }
}
