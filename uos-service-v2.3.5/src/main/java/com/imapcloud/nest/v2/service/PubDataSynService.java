package com.imapcloud.nest.v2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public abstract class PubDataSynService extends SuperDataSynService{

    public abstract void checkStatus(String nestUuid , Integer recordId);

    public abstract List getMediaData(String nestUuid , Integer recordId);

    public abstract List handleSyn(String nestUuid , Integer recordId);

    public abstract void callback(String nestUuid , Integer recordId , CountDownLatch countDownLatch);

    public abstract String sendCommand(String nestUuid , Integer recordId , List fileIdList , List mediaList);
}
