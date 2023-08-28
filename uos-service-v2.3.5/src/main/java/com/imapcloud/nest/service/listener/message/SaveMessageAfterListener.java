package com.imapcloud.nest.service.listener.message;

import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.convert.PubMessageVOToEntityConvert;
import com.imapcloud.nest.model.PubMessageEntity;
import com.imapcloud.nest.pojo.vo.PubMessageVO;
import com.imapcloud.nest.service.PubMessageService;
import com.imapcloud.nest.service.event.message.SaveMessageAfterEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName SaveMessagedAfterListener.java
 * @Description SaveMessagedAfterListener
 * @createTime 2022年04月14日 15:12:00
 */
@Slf4j
@Service
public class SaveMessageAfterListener extends AbstractEventListener<SaveMessageAfterEvent> {

    @Resource
    private PubMessageService pubMessageService;
    /**
     * 消息保存后监听
     *
     * @param saveMessageAfterEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(SaveMessageAfterEvent saveMessageAfterEvent) {
        log.info("【SaveMessageAfterListener】:{}", saveMessageAfterEvent.toString());
        PubMessageEntity pubMessageEntity = (PubMessageEntity) saveMessageAfterEvent.getSource();
        //转换Entity
        log.info("【SaveMessageAfterListener】entity->{}",pubMessageEntity.toString());
        PubMessageVOToEntityConvert.INSTANCES.updatePubMessageVO(pubMessageEntity , (PubMessageVO) saveMessageAfterEvent.getV());
        log.info("【SaveMessageAfterListener】VO->{}",saveMessageAfterEvent.getV().toString());
    }

}
