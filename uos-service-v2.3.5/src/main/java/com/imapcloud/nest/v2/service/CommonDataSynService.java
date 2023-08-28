package com.imapcloud.nest.v2.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
public class CommonDataSynService extends PubDataSynService{


    @Override
    public void checkStatus(String nestUuid , Integer recordId) {
        super.checkStatusPub(nestUuid , recordId);
    }

    @Override
    public List getMediaData(String nestUuid , Integer recordId) {
        return super.getMediaDataPub(nestUuid , recordId);
    }

    @Override
    public List handleSyn(String nestUuid , Integer recordId) {
       return super.handleSynPub(nestUuid , recordId);
    }

    @Override
    public void callback(String nestUuid , Integer recordId , CountDownLatch countDownLatch) {
        super.callbackPub(nestUuid , recordId , countDownLatch);
    }

    @Override
    public String sendCommand(String nestUuid , Integer recordId , List fileIdList , List mediaList) {
        return super.sendCommandPub(nestUuid , recordId , fileIdList , mediaList);
    }
}
