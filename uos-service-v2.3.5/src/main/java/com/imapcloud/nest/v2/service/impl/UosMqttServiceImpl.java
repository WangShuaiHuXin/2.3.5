package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.dao.entity.UosMqttEntity;
import com.imapcloud.nest.v2.dao.mapper.UosMqttMapper;
import com.imapcloud.nest.v2.dao.po.UosMqttQueryCriteriaPO;
import com.imapcloud.nest.v2.manager.cps.CommonManager;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.UosMqttService;
import com.imapcloud.nest.v2.service.converter.UosMqttConverter;
import com.imapcloud.nest.v2.service.dto.in.UosMqttCreationInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosMqttModifyInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosMqttPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.MqttInitParamOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosMqttQueryOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosMqttSimpleOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Classname UosMqttServiceImpl
 * @Description Mqtt代理地址实现类
 * @Date 2022/8/16 15:38
 * @Author Carnival
 */

@Slf4j
@Service
public class UosMqttServiceImpl implements UosMqttService {

    @Resource
    private UosMqttMapper uosMqttMapper;

    @Resource
    private UosMqttConverter uosMqttConverter;

    @Resource
    private BaseNestManager baseNestManager;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private CommonManager commonManager;

    @Resource
    private BaseNestService baseNestService;

    private static final Integer Fail = 0;

    @Override
    public String addMqtt(UosMqttCreationInDTO mqttCreationInDTO) {
        Optional<UosMqttEntity> mqttByName = findMqttByName(mqttCreationInDTO.getMqttName());
        if(mqttByName.isPresent()) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALREADY_EXIST_MQTT.getContent()));
        }
        UosMqttEntity entity = new UosMqttEntity();
        entity.setMqttName(mqttCreationInDTO.getMqttName());
        entity.setMqttBrokerId(BizIdUtils.snowflakeIdStr());
        entity.setOuterDomain(mqttCreationInDTO.getOuterDomain());
        entity.setInnerDomain(mqttCreationInDTO.getInnerDomain());
        entity.setAccount(mqttCreationInDTO.getAccount());
        entity.setPassword(mqttCreationInDTO.getPassword());
        uosMqttMapper.insert(entity);
        return entity.getMqttBrokerId();
    }

    @Override
    public Boolean deleteMqtt(String mqttBrokerId) {
        Optional<UosMqttEntity> mqttById = findMqttById(mqttBrokerId);
        if(!mqttById.isPresent()) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_MQTT.getContent()));
        }
        int tatal = uosMqttMapper.queryMqttUsed(mqttBrokerId);
        if (tatal > 0) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_BE_DELETED_MQTT_IS_USE.getContent()));
        }

        return uosMqttMapper.deleteById(mqttById.get().getId()) != Fail;
    }

    @Override
    public Boolean modifyMqttInfo(String mqttBrokerId, UosMqttModifyInDTO mqttModifyInDTO) {
        Optional<UosMqttEntity> mqttById = findMqttById(mqttBrokerId);
        if(!mqttById.isPresent()) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CURRENT_MQTT_PROXY_ADDRESS_DOES_NOT_EXIST.getContent()));
        }

        UosMqttEntity entity = mqttById.get();
        entity.setMqttName(mqttModifyInDTO.getMqttName());
        entity.setOuterDomain(mqttModifyInDTO.getOuterDomain());
        entity.setInnerDomain(mqttModifyInDTO.getInnerDomain());
        entity.setAccount(mqttModifyInDTO.getAccount());
        entity.setPassword(mqttModifyInDTO.getPassword());
        entity.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
        entity.setModifiedTime(LocalDateTime.now());
        return uosMqttMapper.updateById(entity) != Fail;
    }

    @Override
    public UosMqttQueryOutDTO queryMqttInfo(String mqttBrokerId) {
        Optional<UosMqttEntity> mqttById = findMqttById(mqttBrokerId);
        if(!mqttById.isPresent()) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CURRENT_MQTT_PROXY_ADDRESS_DOES_NOT_EXIST.getContent()));
        }
        return uosMqttConverter.transform(mqttById.get());
    }

    @Override
    public PageResultInfo<UosMqttQueryOutDTO> pageMqttList(UosMqttPageInDTO mqttPageInDTO) {
        UosMqttQueryCriteriaPO mqttQueryCriteriaPO = buildMqttCriteria(mqttPageInDTO);
        long total = uosMqttMapper.countByCondition(mqttQueryCriteriaPO);
        List<UosMqttEntity> rows = null;
        if(total > 0) {
            rows = uosMqttMapper.selectByCondition(mqttQueryCriteriaPO,
                    PagingRestrictDo.getPagingRestrict(mqttPageInDTO));
        }
        return PageResultInfo.of(total, rows).map(r -> uosMqttConverter.transform(r));
    }

    @Override
    public List<UosMqttSimpleOutDTO> listMqttSimpleInfo() {
        List<UosMqttSimpleOutDTO> collect = Collections.emptyList();
        Optional<List<UosMqttEntity>> uosMqttEntities = Optional.ofNullable(uosMqttMapper.selectList(null));
        if (uosMqttEntities.isPresent()) {
            collect = uosMqttEntities.get().stream().map(r -> {
                UosMqttSimpleOutDTO dto = new UosMqttSimpleOutDTO();
                dto.setMqttId(r.getMqttBrokerId());
                dto.setMqttName(r.getMqttName());
                return dto;
            }).collect(Collectors.toList());
        }
        return collect;
    }

    @Override
    public PageResultInfo<UosMqttSimpleOutDTO> pageMqttSimpleInfo(UosMqttPageInDTO uosMqttPageInDTO) {
        UosMqttQueryCriteriaPO uosMqttQueryCriteriaPO = buildMqttCriteria(uosMqttPageInDTO);
        long total = uosMqttMapper.countByCondition(uosMqttQueryCriteriaPO);
        List<UosMqttEntity> rows = null;
        if(total > 0) {
            rows = uosMqttMapper.selectByCondition(uosMqttQueryCriteriaPO,
                    PagingRestrictDo.getPagingRestrict(uosMqttPageInDTO));
        }
        List<UosMqttSimpleOutDTO> collect = null;
        if (!CollectionUtils.isEmpty(rows)) {
            collect = rows.stream().map(r -> {
                UosMqttSimpleOutDTO dto = new UosMqttSimpleOutDTO();
                dto.setMqttId(r.getMqttBrokerId());
                dto.setMqttName(r.getMqttName());
                return dto;
            }).collect(Collectors.toList());
        }
        return PageResultInfo.of(total, collect);
    }

    private Optional<UosMqttEntity> findMqttByName(String name) {
        if (StringUtils.hasText(name)) {
            LambdaQueryWrapper<UosMqttEntity> con = Wrappers.lambdaQuery(UosMqttEntity.class)
                    .eq(UosMqttEntity::getMqttName, name);
            return Optional.ofNullable(uosMqttMapper.selectOne(con));
        }
        return Optional.empty();
    }

    private Optional<UosMqttEntity> findMqttById(String mqttBrokerId) {
        if (StringUtils.hasText(mqttBrokerId)) {
            LambdaQueryWrapper<UosMqttEntity> con = Wrappers.lambdaQuery(UosMqttEntity.class)
                    .eq(UosMqttEntity::getMqttBrokerId, mqttBrokerId);
            return Optional.ofNullable(uosMqttMapper.selectOne(con));
        }
        return Optional.empty();
    }

    private UosMqttQueryCriteriaPO buildMqttCriteria(UosMqttPageInDTO mqttPageInDTO) {
        return UosMqttQueryCriteriaPO.builder()
                .mqttName(mqttPageInDTO.getMqttName())
                .build();
    }

    @Override
    public void clientReset(String nestId) {

        List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectByNestId(nestId);
        if (CollUtil.isEmpty(baseNestEntityOutDOList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_NEST.getContent()));
        }
        BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO = baseNestEntityOutDOList.get(0);
        reset(baseNestEntityOutDO);
    }

    private void reset(BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO) {
        // 查询mqtt信息
        List<UosMqttEntity> uosMqttEntityList = uosMqttMapper.selectList(Wrappers.lambdaQuery(UosMqttEntity.class).eq(UosMqttEntity::getMqttBrokerId, baseNestEntityOutDO.getMqttBrokerId()));
        if (CollUtil.isEmpty(uosMqttEntityList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MQTT_CONNECTION_INFORMATION_OF_THE_BASE_STATION_DOES_NOT_EXIST.getContent()));
        }
        UosMqttEntity uosMqttEntity = uosMqttEntityList.get(0);

        String url;
        if (geoaiUosProperties.isMqttOuterDomain()) {
            url = uosMqttEntity.getOuterDomain();
        } else {
            url = uosMqttEntity.getInnerDomain();
        }
        String nestUuid = baseNestEntityOutDO.getUuid();
        CommonInDO.MqttOptionsInDO mqttOptionsInDO = new CommonInDO.MqttOptionsInDO();
        mqttOptionsInDO.setNestUuid(nestUuid);
        mqttOptionsInDO.setUsername(uosMqttEntity.getAccount());
        mqttOptionsInDO.setPassword(uosMqttEntity.getPassword());
        mqttOptionsInDO.setServerUri(url);
        mqttOptionsInDO.setNestType(baseNestEntityOutDO.getType());
//        //TODO 本地调试
//        if("4TADK8B0000020".equals(mqttOptionsInDO.getNestUuid())){
//            mqttOptionsInDO.setServerUri("tcp://124.71.10.164:1883");
//        }

        List<MqttInitParamOutDTO> mqttInitParamOutDTOS = baseNestService.listMqttInitParams(Collections.singletonList(baseNestEntityOutDO.getNestId()));
        if(CollectionUtils.isEmpty(mqttInitParamOutDTOS)) {
            return;
        }
        MqttInitParamOutDTO mqttInitParamOutDTO = mqttInitParamOutDTOS.get(0);
        mqttOptionsInDO.setDjiTslSnParam(mqttInitParamOutDTO.getDjiTslSnParam());
        commonManager.initComponentManager(mqttOptionsInDO);
        commonManager.initCommonNestState(nestUuid,baseNestEntityOutDO.getType());
    }

    @Override
    public void clientResetByUuid(String uuid) {

        try {
            List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectByUuid(uuid);
            if (CollUtil.isEmpty(baseNestEntityOutDOList)) {
                return;
            }
            BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO = baseNestEntityOutDOList.get(0);
            reset(baseNestEntityOutDO);
        } catch (Exception e) {
            log.error("#UosMqttServiceImpl.clientResetByUuid# uuid={}", uuid, e);
        }
    }
}
