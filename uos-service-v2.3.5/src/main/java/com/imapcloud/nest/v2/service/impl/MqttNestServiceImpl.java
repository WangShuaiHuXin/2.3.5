package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.imapcloud.nest.utils.TransformENUtils;
import com.imapcloud.nest.v2.common.enums.ImMessageActionEnum;
import com.imapcloud.nest.v2.common.enums.LanguageEnum;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.nest.v2.service.ImService;
import com.imapcloud.nest.v2.service.MqttNestService;
import com.imapcloud.nest.v2.service.NestAccountService;
import com.imapcloud.nest.v2.service.dto.ChannelInfoDTO;
import com.imapcloud.nest.v2.service.dto.nest.NestAlarmDTO;
import com.imapcloud.nest.v2.service.dto.nest.NestBaseDTO;
import com.imapcloud.nest.v2.service.dto.nest.NestUavDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * mqtt服务impl
 *
 * @author boluo
 * @date 2023-02-15
 */
@Slf4j
@Service
public class MqttNestServiceImpl implements MqttNestService {

    List<ImMessageActionEnum> CHANNEL_ACTION_LIST = Lists.newArrayList(ImMessageActionEnum.NEST_LIST_DTO_NEST
            , ImMessageActionEnum.NEST_LIST_DTO_UAV, ImMessageActionEnum.NEST_LIST_DTO_ALARM);

    @Resource
    private NestAccountService nestAccountService;

    @Resource
    private BaseNestManager baseNestManager;

    @Resource
    private ImService imService;

    private List<ChannelInfoDTO> getChannelInfoList(String nestUuid, String accountId) {

        List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectByUuid(nestUuid);
        if (CollUtil.isEmpty(baseNestEntityOutDOList)) {
            log.warn("#MqttNestServiceImpl.getAccountIdList# uuid={} not exist", nestUuid);
            return null;
        }
        BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO = baseNestEntityOutDOList.get(0);
        String nestId = baseNestEntityOutDO.getNestId();
        List<String> accountIdList = nestAccountService.getAccountIdByNest(nestId);

        if (CollUtil.isEmpty(accountIdList)) {
            log.warn("#MqttNestServiceImpl.getAccountIdList# uuid={} not exist account", nestUuid);
            return null;
        }
        if (accountId != null) {

            if (!accountIdList.contains(accountId)) {
                log.warn("#MqttNestServiceImpl.getAccountIdList# uuid={} not accountId={}", nestUuid, accountId);
                return null;
            }
            accountIdList = Lists.newArrayList(accountId);
        }

        return imService.channelInfoList(accountIdList, CHANNEL_ACTION_LIST);
    }

    private List<ChannelInfoDTO> getChannelInfoList(List<String> accountIdList) {

        return imService.channelInfoList(accountIdList, CHANNEL_ACTION_LIST);
    }

    @Override
    public void nestAll(NestBaseDTO nestBaseDTO, NestUavDTO nestUavDTO, NestAlarmDTO nestAlarmDTO, String accountId, String uuid) {

        log.info("#MqttNestServiceImpl.nestAll# uuid={}, nestBaseDTO={}", uuid, nestBaseDTO);


        List<ChannelInfoDTO> channelInfoDTOList = getChannelInfoList(uuid, accountId);
        if (CollUtil.isEmpty(channelInfoDTOList)) {
            log.warn("#MqttNestServiceImpl.nestAll# uuid={} not exist channel", uuid);
            return;
        }
        if (nestBaseDTO != null) {
            nestBase(Lists.newArrayList(nestBaseDTO), channelInfoDTOList);
        }

        if (nestUavDTO != null) {
            nestUav(Lists.newArrayList(nestUavDTO), channelInfoDTOList);
        }

        if(nestAlarmDTO != null) {
            nestAlarm(Lists.newArrayList(nestAlarmDTO), channelInfoDTOList);
        }
    }

    @Override
    public void batchNestAll(List<NestBaseDTO> nestBaseDTOList, List<NestUavDTO> nestUavDTOList, List<NestAlarmDTO> alarmDTOList, String accountId) {

        List<ChannelInfoDTO> channelInfoList = getChannelInfoList(Lists.newArrayList(accountId));

        if (CollUtil.isEmpty(channelInfoList)) {
            log.warn("#MqttNestServiceImpl.batchNestAll# accountId={} not exist channel", accountId);
            return;
        }
        nestBase(nestBaseDTOList, channelInfoList);
        nestUav(nestUavDTOList, channelInfoList);
        nestAlarm(alarmDTOList, channelInfoList);
    }

    private void nestBase(List<NestBaseDTO> nestBaseDTOList, List<ChannelInfoDTO> channelInfoDTOList) {
        if (CollUtil.isEmpty(nestBaseDTOList)) {
            return;
        }

        Map<String, List<ChannelInfoDTO>> collect = channelInfoDTOList.stream().collect(Collectors.groupingBy(ChannelInfoDTO::getLanguage));
        collect.forEach((language, infoList) -> {
            String msg;
            if (LanguageEnum.US.getCode().equals(language)) {

                List<NestBaseDTO> usBaseDTOList = Lists.newArrayList();
                for (NestBaseDTO nestBaseDTO : nestBaseDTOList) {
                    NestBaseDTO usDto = new NestBaseDTO();
                    BeanUtils.copyProperties(nestBaseDTO, usDto);
                    usDto.setBaseState(TransformENUtils.transformNestState(usDto.getBaseState()));
                    usBaseDTOList.add(usDto);
                }
                msg = JSONUtil.toJsonStr(usBaseDTOList);
            } else {
                msg = JSONUtil.toJsonStr(nestBaseDTOList);
            }
            List<String> channelIdList = infoList.stream().map(ChannelInfoDTO::getChannelId).collect(Collectors.toList());
            imService.sendMsg(ImMessageActionEnum.NEST_LIST_DTO_NEST, msg, channelIdList);
        });


    }

    public void nestUav(List<NestUavDTO> nestUavDTOList, List<ChannelInfoDTO> channelInfoDTOList) {
        if (CollUtil.isEmpty(nestUavDTOList)) {
            return;
        }
        String msg = JSONUtil.toJsonStr(nestUavDTOList);
        List<String> channelIdList = channelInfoDTOList.stream().map(ChannelInfoDTO::getChannelId).collect(Collectors.toList());
        imService.sendMsg(ImMessageActionEnum.NEST_LIST_DTO_UAV, msg, channelIdList);
    }

    public void nestAlarm(List<NestAlarmDTO> nestAlarmDTOList, List<ChannelInfoDTO> channelInfoDTOList) {

        if (CollUtil.isEmpty(nestAlarmDTOList)) {
            return;
        }
        String msg = JSONUtil.toJsonStr(nestAlarmDTOList);
        List<String> channelIdList = channelInfoDTOList.stream().map(ChannelInfoDTO::getChannelId).collect(Collectors.toList());
        imService.sendMsg(ImMessageActionEnum.NEST_LIST_DTO_ALARM, msg, channelIdList);
    }
}
