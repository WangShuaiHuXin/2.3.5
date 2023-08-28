package com.imapcloud.nest.v2.manager.ai;

import com.geoai.common.core.bean.DefaultTrustedAccessInformation;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.rocketmq.spring.annotation.RocketMQMessageListener;
import com.geoai.rocketmq.spring.core.MessageMetadata;
import com.geoai.rocketmq.spring.core.RocketMQListener;
import com.imapcloud.nest.v2.common.utils.AsyncBusinessUtils;
import com.imapcloud.nest.v2.service.AIAnalysisService;
import com.imapcloud.nest.v2.service.dto.out.AIAnalysisPicResultDataOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

/**
 * AI分析图片变更监听器
 *
 * @author Vastfy
 * @date 2022/11/3 17:59
 * @since 2.1.4
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "geoai-uos-service", topic = "${geoai.uos.analysis.ai-task-topic:tx-ai_analysis_task}", filterExpression = "pic||task")
public class AIAnalysisPhotoChangedListener implements RocketMQListener<AIAnalysisPicResultDataOutDTO> {

    private static final String TAG_PIC = "pic";
    private static final String TAG_TASK = "task";

    @Resource
    private AIAnalysisService aiAnalysisService;

    @Override
    public void onMessage(AIAnalysisPicResultDataOutDTO messageData, MessageMetadata msgMetadata) {
        log.info("监听到AI分析推送消息[messageId={}, topic={}, tag={}]：==> {}", msgMetadata.getMessageId(), msgMetadata.getTopic(), msgMetadata.getTag(), messageData);
        Optional<String> optional = msgMetadata.getTag();
        if(!optional.isPresent()){
            log.warn("AI分析推送数据未检索到标签信息，忽略此消息");
            return;
        }
        String tag = optional.get();
        try {
            AsyncBusinessUtils.executeBusiness(() -> {
                if(Objects.equals(tag, TAG_PIC)){
                    // 处理图片分析结果
                    aiAnalysisService.handlePhotoAnalysisResults(messageData);
                    return;
                }
                if(Objects.equals(tag, TAG_TASK)){
                    // 处理任务分析结果
                    aiAnalysisService.handleTaskAnalysisResult(messageData.getTaskId());
                }
            });
        }catch (Exception e){
            log.error("处理AI分析推送消息异常", e);
            throw e;
        }
    }

}
