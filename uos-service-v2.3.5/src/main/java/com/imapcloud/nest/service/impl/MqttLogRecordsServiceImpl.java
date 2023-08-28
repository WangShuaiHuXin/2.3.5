package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.model.MqttLogRecordsEntity;
import com.imapcloud.nest.mapper.MqttLogRecordsMapper;
import com.imapcloud.nest.service.MqttLogRecordsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * mqtt日志解析列表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2022-02-18
 */
@Service
public class MqttLogRecordsServiceImpl extends ServiceImpl<MqttLogRecordsMapper, MqttLogRecordsEntity> implements MqttLogRecordsService {

}
