package com.imapcloud.nest.v2.manager.ai.video;

import com.geoai.rocketmq.spring.annotation.RocketMQMessageListener;
import com.geoai.rocketmq.spring.core.MessageMetadata;
import com.geoai.rocketmq.spring.core.RocketMQListener;
import com.imapcloud.nest.v2.service.AIStreamingService;
import com.imapcloud.nest.v2.service.dto.in.AiStreamingTerminateInDTO;
import com.imapcloud.nest.v2.service.dto.out.NestAiStreamingInfoOutDTO;
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
@RocketMQMessageListener(consumerGroup = "geoai-uos-service", topic = "${geoai.uos.analysis.ai-stream-ttl-topic:delay-uos_ai_stream_check}")
public class VideoAITimeoutCheckListener implements RocketMQListener<NestAiStreamingInfoOutDTO> {

    @Resource
    private AIStreamingService aiStreamingService;

    @Override
    public void onMessage(NestAiStreamingInfoOutDTO message, MessageMetadata metadata) {
        log.info("监听到视频AI识别任务超时检查消息[messageId={}, topic={}]：==> {}", metadata.getMessageId(), metadata.getTopic(), message);

        try {
            AiStreamingTerminateInDTO data = new AiStreamingTerminateInDTO();
            data.setNestId(message.getNestId());
            data.setWhich(message.getWhich());
            data.setProcessTaskId(message.getProcessId());
            data.setOrgCode(message.getOrgCode());
            aiStreamingService.timeoutTerminateAiStreaming(data);
        }catch (Exception e){
            log.error("处理视频AI识别任务超时检查消息异常", e);
            throw e;
        }
    }

}
