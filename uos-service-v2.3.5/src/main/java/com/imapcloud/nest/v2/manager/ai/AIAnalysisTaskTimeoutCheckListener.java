package com.imapcloud.nest.v2.manager.ai;

import com.geoai.rocketmq.spring.annotation.RocketMQMessageListener;
import com.geoai.rocketmq.spring.core.MessageMetadata;
import com.geoai.rocketmq.spring.core.RocketMQListener;
import com.imapcloud.nest.v2.common.utils.AsyncBusinessUtils;
import com.imapcloud.nest.v2.service.AIAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * AI分析任务超时检查监听器
 * @author Vastfy
 * @date 2022/11/11 17:29
 * @since 2.1.4
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "geoai-uos-service", topic = "${geoai.uos.analysis.ai-task-timeout-check-topic:delay-uos_ai_task_timeout_check}")
public class AIAnalysisTaskTimeoutCheckListener implements RocketMQListener<String> {

    @Resource
    private AIAnalysisService aiAnalysisService;

    @Override
    public void onMessage(String messageData, MessageMetadata msgMetadata) {
        log.info("监听到AI分析任务超时检查消息[messageId={}, topic={}]：==> {}", msgMetadata.getMessageId(), msgMetadata.getTopic(), messageData);

        try {
            AsyncBusinessUtils.executeBusiness(() -> {
                aiAnalysisService.handleIfAITaskTimeout(messageData);
            });
        }catch (Exception e){
            log.error("处理AI分析任务超时检查消息异常", e);
            throw e;
        }
    }

}
