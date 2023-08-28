package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.pojo.entity.NestState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class S110DataSynService extends PubDataSynService {

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private CommonNestStateService commonNestStateService;


    @Override
    public void checkStatus(String nestUuid , Integer recordId) {
        //检测基站是否在传输文件
        NestTypeEnum nestType = baseNestService.getNestTypeByUuidCache(nestUuid);
        if (NestTypeEnum.S110_MAVIC3.equals(nestType)) {
            NestState nestState = this.commonNestStateService.getNestState(nestUuid);
            if (!nestState.getRemoteControllerConnected() && !nestState.getRemotePowerOn()) {
                throw new BusinessException("御3无人机同步数据请先打开遥控器");
            }
        }
    }

    @Override
    public List getMediaData(String nestUuid , Integer recordId) {
        return super.getMediaDataPub(nestUuid,recordId);
    }

    @Override
    public List handleSyn(String nestUuid , Integer recordId) {
        return super.handleSynPub(nestUuid,recordId);
    }

    @Override
    public void callback(String nestUuid , Integer recordId , CountDownLatch countDownLatch) {
        super.callbackPub(nestUuid , recordId , countDownLatch);
    }

    @Override
    public String sendCommand(String nestUuid , Integer recordId , List fileIdList , List mediaList) {
        return super.sendCommandPub(nestUuid,recordId,fileIdList,mediaList);
    }
}
