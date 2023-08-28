package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.utils.LocalDateUtil;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.DjiPilotConfig;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.service.BaseMqttBrokerService;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.DJIPilotCommonService;
import com.imapcloud.nest.v2.service.dto.out.BaseNestInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DJIPilotCommonResultOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MqttBrokerInfoOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJICommonServiceImpl.java
 * @Description DJICommonServiceImpl
 * @createTime 2022年10月19日 15:53:00
 */
@Slf4j
@Service
public class DJIPilotCommonServiceImpl implements DJIPilotCommonService {


    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private BaseMqttBrokerService baseMqttBrokerService;

    @Resource
    private RedisService redisService;
    /**
     * 获取授权信息
     *
     * @return
     */
    @Override
    public DJIPilotCommonResultOutDTO.PilotLicenseResultOutDTO getLicense() {
        DjiPilotConfig djiPilotConfig = this.geoaiUosProperties.getDjiPilot();
        String appId = djiPilotConfig.getAppId()
                , appSecret = djiPilotConfig.getAppSecret()
                , appLicense = djiPilotConfig.getAppLicense();
        Long currentTime = LocalDateUtil.localDateTimeToTimestamp(LocalDateTime.now());
        String appInfo = String.format("%s,%s,%s,%d" , appId , appSecret , appLicense , currentTime);
        try {
            appInfo = Base64.encode(appInfo.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("转换出错，请检查!",e);
        }
        return DJIPilotCommonResultOutDTO.PilotLicenseResultOutDTO
                .builder()
                .appInfo(appInfo)
                .build();
    }

    /**
     * 根据uuid获取Mqtt信息
     *
     * @param nestUUid
     * @return
     */
    @Override
    public DJIPilotCommonResultOutDTO.PilotMqttResultOutDTO getMqttInfo(String nestUUid) {
        BaseNestInfoOutDTO baseNestInfoOutDTO = this.baseNestService.getBaseNestInfoByUuid(nestUUid);
        DJIPilotCommonResultOutDTO.PilotMqttResultOutDTO pilotMqttResultOutDTO = new DJIPilotCommonResultOutDTO.PilotMqttResultOutDTO();
        if(baseNestInfoOutDTO == null){
            return pilotMqttResultOutDTO;
        }
        String mqttId = baseNestInfoOutDTO.getMqttBrokerId();
        List<MqttBrokerInfoOutDTO> mqttBrokerInfoOutDTOList = this.baseMqttBrokerService.listMqttBrokerInfos(CollectionUtil.toList(mqttId));
        if(CollectionUtil.isEmpty(mqttBrokerInfoOutDTOList)){
            return pilotMqttResultOutDTO;
        }
        MqttBrokerInfoOutDTO mqttBrokerInfoOutDTO = mqttBrokerInfoOutDTOList.stream()
                .sorted(Comparator.comparing(MqttBrokerInfoOutDTO::getMqttBrokerId))
                .findFirst()
                .orElseThrow(()
                        ->new BusinessException(String.format("基站：%s , 查找不到对应的mqtt记录，请检查！",baseNestInfoOutDTO.getName())));
        try {
            pilotMqttResultOutDTO.setMqttBrokerId(mqttBrokerInfoOutDTO.getMqttBrokerId())
                    .setUsername(mqttBrokerInfoOutDTO.getUsername())
                    .setPassword(Base64.encode(mqttBrokerInfoOutDTO.getPassword().getBytes("UTF-8")))
                    .setMqttBrokerInnerUrl(mqttBrokerInfoOutDTO.getMqttBrokerInnerUrl());
        } catch (Exception e) {
            log.error("加密报错",e);
        }
        return pilotMqttResultOutDTO;
    }

    /**
     * 绑定无人机跟基站
     *
     * @param nestSn
     * @param uavSn
     * @return
     */
    @Override
    public boolean bindNestAndUav(String nestSn, String uavSn) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.DJI_PILOT_UAV_BIND , uavSn);
        String nestSnValue = (String) this.redisService.get(redisKey);
        if(StringUtils.isEmpty(nestSnValue)){
            this.redisService.set(redisKey , nestSn , 24*60*60);
        }
        return true;
    }
}
