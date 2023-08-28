package com.imapcloud.nest.v2.manager.ai;

import com.geoai.common.core.util.JsonUtils;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgAccountOutDO;
import com.imapcloud.nest.v2.manager.rest.OrgAccountManager;
import com.imapcloud.nest.v2.service.dto.out.AIAnalysisTaskDataOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AI分析任务数据推送监听器
 * @author Vastfy
 * @date 2022/11/3 16:20
 * @since 2.1.4
 */
@Slf4j
@Component
public class AIAnalysisDataNotifyListener implements ApplicationListener<AIAnalysisDataChangedEvent> {

    @Resource
    private OrgAccountManager orgAccountManager;

    @Async("bizAsyncExecutor")
    @Override
    public void onApplicationEvent(@NonNull AIAnalysisDataChangedEvent event) {
        AIAnalysisTaskDataOutDTO changedData = event.getChangedData();
        if(Objects.nonNull(changedData)){
            boolean debugEnabled = log.isDebugEnabled();
            if(debugEnabled){
                log.debug("监听到AI分析任务[{}]数据已更新", changedData.getAiTaskId());
            }
            try {
                String websocketMessage = JsonUtils.writeJson(changedData);
                if(debugEnabled){
                    log.debug("AI任务进度消息 ==> {}", websocketMessage);
                }
                // 人工创建任务
                if(!changedData.isAuto()){
                    if(!StringUtils.hasText(changedData.getAccountId())){
                        log.warn("人工创建AI识别任务进度信息推送失败，无推送账号信息");
                        return;
                    }
                    if(debugEnabled){
                        log.debug("推送AI任务进度消息至账号 ==> {}", changedData.getAccountId());
                    }
                    ChannelService.sendMessageByType14Channel(changedData.getAccountId(), websocketMessage);
                    return;
                }
                // 系统创建任务
                if(!StringUtils.hasText(changedData.getOrgCode())){
                    log.warn("系统创建AI识别任务进度信息推送失败，无推送单位信息");
                    return;
                }
                // 推送单位下所有账号
                List<String> accountIds = getOrgAccountIds(changedData);
                if(CollectionUtils.isEmpty(accountIds)){
                    log.warn("系统创建AI识别任务进度信息推送失败，单位[{}]无账号信息", changedData.getOrgCode());
                    return;
                }
                accountIds.stream()
                        .peek(r -> {
                            if(debugEnabled){
                                log.debug("推送AI任务进度消息至账号 ==> {}", r);
                            }
                        })
                        .forEach(r -> ChannelService.sendMessageByType14Channel(r, websocketMessage));
            } catch (Exception e){
                log.error("推送AI分析任务最新数据失败，原因：{}", e.getMessage());
            }
        }
    }

    private List<String> getOrgAccountIds(AIAnalysisTaskDataOutDTO changedData) {
        List<OrgAccountOutDO> refInfos = orgAccountManager.listOrgAccountRefInfos(Collections.singletonList(changedData.getOrgCode()));
        if(!CollectionUtils.isEmpty(refInfos)){
            return refInfos.stream()
                    .map(OrgAccountOutDO::getAccountId)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
