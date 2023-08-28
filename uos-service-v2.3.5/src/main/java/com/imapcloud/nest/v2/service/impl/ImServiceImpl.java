package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.ImMessageActionEnum;
import com.imapcloud.nest.v2.common.enums.ImPageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.dataobj.in.ImMessageInDO;
import com.imapcloud.nest.v2.manager.feign.ImMessageClient;
import com.imapcloud.nest.v2.manager.job.nest.NestStateProcessor;
import com.imapcloud.nest.v2.service.ImService;
import com.imapcloud.nest.v2.service.PowerTaskService;
import com.imapcloud.nest.v2.service.dto.ChannelInfoDTO;
import com.imapcloud.nest.v2.service.dto.in.ImInDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 即时通讯服务impl
 *
 * @author boluo
 * @date 2023-02-13
 */
@Slf4j
@Service
public class ImServiceImpl implements ImService {

    private static final String LOCK = "ImServiceImpl:lock:%s";

    private static final String ACCOUNT = "ImServiceImpl:account:%s";

    @Resource
    private RedisService redisService;

    @Resource
    private ImMessageClient imMessageClient;

    @Resource
    private NestStateProcessor nestStateProcessor;

    @Resource
    private PowerTaskService powerTaskService;

    @Resource
    private ExecutorService wsExecutorService;

    private String getLockKey(String accountId) {
        return String.format(LOCK, accountId);
    }

    private String getAccountKey(String accountId) {
        return String.format(ACCOUNT, accountId);
    }

    @Override
    public void callback(ImInDTO.CallbackInDTO callbackInDTO) {

        String accountId = callbackInDTO.getAccountId();
        log.info("#ImServiceImpl.callback# accountId={}, callbackInDTO={}", accountId, callbackInDTO);
        String lockKey = getLockKey(accountId);
        try {
            redisService.lock(lockKey, "1", 5 * 60);
            if (callbackInDTO.getType() == 0) {
                login(callbackInDTO);
            } else if (callbackInDTO.getType() == 1) {
                logout(callbackInDTO);
            } else if (callbackInDTO.getType() == 2) {
                clear();
            }
        } catch (Exception e) {
            log.error("#ImServiceImpl.callback# accountId={}", accountId, e);
        } finally {
            redisService.releaseLock(lockKey, "1");
        }
    }

    private void login(ImInDTO.CallbackInDTO callbackInDTO) {

        ChannelInfoDTO channelInfo = new ChannelInfoDTO();
        channelInfo.setLanguage(callbackInDTO.getLanguage());
        channelInfo.setChannelId(callbackInDTO.getChannelId());
        redisService.hSet(getAccountKey(callbackInDTO.getAccountId()), callbackInDTO.getChannelId(), channelInfo);
    }

    private void logout(ImInDTO.CallbackInDTO callbackInDTO) {

        redisService.hDel(getAccountKey(callbackInDTO.getAccountId()), callbackInDTO.getChannelId());
    }

    private void clear() {

        Set<String> stringSet = redisService.keys(ACCOUNT.replace("%s", "*"));
        redisService.del(stringSet);
    }

    @Override
    public void changePage(ImInDTO.PageInDTO pageInDTO) {

        String accountId = pageInDTO.getAccountId();
        log.info("#ImServiceImpl.callback# accountId={}, pageInDTO={}", accountId, pageInDTO);
        String lockKey = getLockKey(accountId);
        try {
            redisService.lock(lockKey, "1", 5 * 60);
            ChannelInfoDTO channelInfo = (ChannelInfoDTO) redisService.hGet(getAccountKey(pageInDTO.getAccountId()), pageInDTO.getChannelId());
            log.info("#ImServiceImpl.callback# accountId={}, channelInfo={}", accountId, channelInfo);
            if (channelInfo == null) {
                throw new BusinessException("account not login websocket.");
            }
            List<String> actionList = ImMessageActionEnum.getActionList(pageInDTO.getPage());
            channelInfo.setActionList(actionList);
            channelInfo.setUuid(pageInDTO.getNestUuid());
            log.info("#ImServiceImpl.callback# accountId={}, channelInfo={}", accountId, channelInfo);
            redisService.hSet(getAccountKey(pageInDTO.getAccountId()), pageInDTO.getChannelId(), channelInfo);

            wsExecutorService.submit(() -> {
                if (ImPageEnum.CHANNEL_ONE_LIST.contains(pageInDTO.getPage())) {
                    nestStateProcessor.manualProcess(accountId);
                } else if (ImPageEnum.ANALYSE_CENTER.getCode().equals(pageInDTO.getPage())) {
                    powerTaskService.manualTask(pageInDTO.getOrgCode());
                }
            });
        } finally {
            redisService.releaseLock(lockKey, "1");
        }
    }

    @Override
    public List<ChannelInfoDTO> channelInfoList(List<String> accountIdList, List<ImMessageActionEnum> imMessageActionEnumList) {

        if (CollUtil.isEmpty(accountIdList)) {
            return Collections.emptyList();
        }
        Set<String> actionSet = imMessageActionEnumList.stream().map(ImMessageActionEnum::getCode).collect(Collectors.toSet());
        List<ChannelInfoDTO> channelInfoList = Lists.newLinkedList();
        for (String accountId : Sets.newHashSet(accountIdList)) {
            List<Object> objectList = redisService.hGetAll(getAccountKey(accountId));
            for (Object o : objectList) {
                ChannelInfoDTO channelInfo = (ChannelInfoDTO) o;
                if (CollUtil.isEmpty(channelInfo.getActionList())) {
                    continue;
                }

                for (String action : actionSet) {

                    if (channelInfo.getActionList().contains(action)) {
                        channelInfoList.add(channelInfo);
                        break;
                    }
                }
            }
        }
        return channelInfoList;
    }

    private static final int PARTITION_SIZE = 500;

    @Override
    public void sendMsg(ImMessageActionEnum nestListDtoNest, String msg, List<String> channelIdList) {

        if (CollUtil.isEmpty(channelIdList)) {
            return;
        }

        if (channelIdList.size() <= PARTITION_SIZE) {
            send(nestListDtoNest, msg, channelIdList);
            return;
        }
        List<List<String>> partition = Lists.partition(channelIdList, PARTITION_SIZE);
        for (List<String> strings : partition) {
            send(nestListDtoNest, msg, strings);
        }
    }

    private void send(ImMessageActionEnum nestListDtoNest, String msg, List<String> channelIdList) {
        ImMessageInDO imMessageInDO = new ImMessageInDO();
        imMessageInDO.setReceiverList(channelIdList);
        imMessageInDO.setTitle(nestListDtoNest.getMsg());
        imMessageInDO.setData(msg);
        imMessageInDO.setAction(nestListDtoNest.getCode());
        imMessageClient.send(imMessageInDO);
    }
}
