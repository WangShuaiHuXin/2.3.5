package com.imapcloud.nest.v2.manager.listener;

import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.v2.manager.dataobj.in.DataSynInDO;
import com.imapcloud.nest.v2.manager.event.DataSynEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataSynUnloadListener.java
 * @Description DataSynUnloadListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class DataSynUnloadListener extends AbstractEventListener<DataSynEvent> {

    @Resource
    private MissionPhotoService missionPhotoService;

    /**
     * 消息监听-处理
     *
     * @param dataSynEvent 消息事件
     */
    @Override
//    @EventListener
    public void eventListener(DataSynEvent dataSynEvent) {
        log.info("【DataSynEvent】-【DataSynUnloadListener】事件:{}", dataSynEvent.toString());
        DataSynInDO dataSynInDO = dataSynEvent.getSource();
        String nestUuid = dataSynInDO.getNestUuid();
//        this.missionPhotoService.closeNest(nestUuid);
    }

}
