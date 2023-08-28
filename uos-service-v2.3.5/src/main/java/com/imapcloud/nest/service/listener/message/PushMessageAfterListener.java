package com.imapcloud.nest.service.listener.message;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.enums.message.PubMessageStateEnum;
import com.imapcloud.nest.model.PubMessageEntity;
import com.imapcloud.nest.service.PubMessageService;
import com.imapcloud.nest.service.event.message.PushMessageAfterEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PushMessageAfterListener.java
 * @Description PushMessageAfterListener
 * @createTime 2022年04月14日 15:12:00
 */
@Slf4j
@Service
public class PushMessageAfterListener extends AbstractEventListener<PushMessageAfterEvent> {

    @Resource
    private PubMessageService pubMessageService;
    /**
     * 消息推送后监听
     *
     * @param pushMessageAfterEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(PushMessageAfterEvent pushMessageAfterEvent) {
        log.info("【PushMessageAfterListener】:{}",pushMessageAfterEvent.toString());
        List<PubMessageEntity> pubMessageEntityList = pushMessageAfterEvent.getSource();
        this.writeBack(pubMessageEntityList);
        log.info("【PushMessageAfterListener】-end");
    }

    /**
     * 回写动作，如果推送后，状态不是推送状态，则回写为推送状态。
     * @param pubMessageEntityList
     */
    public void writeBack(List<PubMessageEntity> pubMessageEntityList){
        List<Integer> ids = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(pubMessageEntityList)){
            ids = pubMessageEntityList.stream().filter(pubMessage ->
                !PubMessageStateEnum.MESSAGE_STATE_2.getCode().equals(pubMessage.getMessageState())
                        && pubMessage.getId() != null
            ).map(PubMessageEntity::getId).collect(Collectors.toList());
            log.info("【PushMessageAfterListener】-【writeBack】:{}",ids);
            this.pubMessageService.lambdaUpdate()
                    .set(PubMessageEntity::getMessageState,PubMessageStateEnum.MESSAGE_STATE_2.getCode())
                    .in(PubMessageEntity::getId,ids)
                    .update();
        }
    }

}
