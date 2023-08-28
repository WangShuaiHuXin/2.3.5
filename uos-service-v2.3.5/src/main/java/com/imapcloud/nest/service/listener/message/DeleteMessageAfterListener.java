package com.imapcloud.nest.service.listener.message;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.constant.MessageConstant;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.model.PubMessageEntity;
import com.imapcloud.nest.service.PubMessageService;
import com.imapcloud.nest.service.event.message.DeleteMessageAfterEvent;
import com.imapcloud.nest.service.quarzt.QuartzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UpdateMessageAfterListener.java
 * @Description UpdateMessageAfterListener
 * @createTime 2022年04月14日 15:12:00
 */
@Slf4j
@Service
public class DeleteMessageAfterListener extends AbstractEventListener<DeleteMessageAfterEvent> {

    @Resource
    private QuartzService quartzService;

    @Resource
    private PubMessageService pubMessageService;

    /**
     * 消息删除后监听-取消掉定时任务
     *
     * @param deleteMessageAfterEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(DeleteMessageAfterEvent deleteMessageAfterEvent) {
        log.info("【DeleteMessageAfterListener】:{}",deleteMessageAfterEvent.toString());
        List<Integer> idList = deleteMessageAfterEvent.getSource();
        List<PubMessageEntity> pubMessageEntityList = this.pubMessageService.lambdaQuery()
                .in(PubMessageEntity::getId,idList)
                .eq(PubMessageEntity::getDeleted,0)
                .select()
                .list();
        if(CollectionUtil.isNotEmpty(pubMessageEntityList)){
            pubMessageEntityList.stream().forEach(pubMessage -> {
                this.removeJob(pubMessage);
            });
        }
        log.info("【DeleteMessageAfterListener】-end");
    }

    /**
     * 删除定时任务
     * @param pubMessage
     */
    public void removeJob(PubMessageEntity pubMessage){
        //删除定时任务
        this.quartzService.deleteJob( String.format("%d-%s",pubMessage.getId(),pubMessage.getBeginTime()), MessageConstant.Job.PUB_MESSAGE_GROUP);
    }


}
