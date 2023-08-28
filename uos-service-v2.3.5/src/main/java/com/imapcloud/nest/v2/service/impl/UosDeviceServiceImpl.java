package com.imapcloud.nest.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.exception.BizException;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.UosNestDeviceRefEntity;
import com.imapcloud.nest.v2.dao.mapper.UosNestDeviceRefMapper;
import com.imapcloud.nest.v2.manager.rest.MediaManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.UosDeviceService;
import com.imapcloud.nest.v2.service.dto.out.NestSimpleOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname UosDeviceServiceImpl
 * @Description 国标设备管理实现类
 * @Date 2023/4/7 10:46
 * @Author Carnival
 */
@Slf4j
@Service
public class UosDeviceServiceImpl implements UosDeviceService {

    @Resource
    private UosNestDeviceRefMapper uosNestDeviceRefMapper;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private MediaManager mediaManager;

    @Override
    public List<String> queryNestInfo(String deviceCode) {
        LambdaQueryWrapper<UosNestDeviceRefEntity> conDevice = Wrappers.lambdaQuery(UosNestDeviceRefEntity.class)
                .eq(UosNestDeviceRefEntity::getDeviceId, deviceCode);
        List<UosNestDeviceRefEntity> nestDeviceRefEntityList = uosNestDeviceRefMapper.selectList(conDevice);
        List<String> res = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(nestDeviceRefEntityList)) {
            List<String> nestIds = nestDeviceRefEntityList.stream().map(UosNestDeviceRefEntity::getNestId).collect(Collectors.toList());
            List<NestSimpleOutDTO> nests = baseNestService.listNestInfos(nestIds);
            res = nests.stream().map(NestSimpleOutDTO::getName).collect(Collectors.toList());
        }
        return res;
    }

    @Override
    public void deleteDevice(String deviceCode) {
        LambdaQueryWrapper<UosNestDeviceRefEntity> conDevice = Wrappers.lambdaQuery(UosNestDeviceRefEntity.class)
                .eq(UosNestDeviceRefEntity::getDeviceId, deviceCode);
        List<UosNestDeviceRefEntity> nestDeviceRefEntityList = uosNestDeviceRefMapper.selectList(conDevice);
        if (!CollectionUtils.isEmpty(nestDeviceRefEntityList)) {
            throw new BizException("设备已绑定基站，无法删除！");
        }
        mediaManager.deleteGbDevice(deviceCode);
    }

    @Override
    public String getDeviceCodeByNestId(String nestId, Integer deviceUse) {
        if(StringUtils.hasText(nestId)){
            LambdaQueryWrapper<UosNestDeviceRefEntity> conDevice = Wrappers.lambdaQuery(UosNestDeviceRefEntity.class)
                    .eq(UosNestDeviceRefEntity::getNestId, nestId)
                    .eq(UosNestDeviceRefEntity::getDeviceUse, deviceUse);
            List<UosNestDeviceRefEntity> nestDeviceRefEntityList = uosNestDeviceRefMapper.selectList(conDevice);
            if(!CollectionUtils.isEmpty(nestDeviceRefEntityList)){
                if(nestDeviceRefEntityList.size() > 1){
                    log.warn("基站管理国标设备存在重复记录 ==> nestId={}, deviceUse={}", nestId, deviceUse);
                }
                return nestDeviceRefEntityList.get(0).getDeviceId();
            }
        }
        return null;
    }
}
