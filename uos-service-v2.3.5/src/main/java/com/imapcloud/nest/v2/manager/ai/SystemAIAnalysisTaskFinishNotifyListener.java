package com.imapcloud.nest.v2.manager.ai;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgAccountOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.rest.OrgAccountManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统AI分析识别任务完成通知监听器
 * @author Vastfy
 * @date 2022/12/5 14:07
 * @since 2.1.5
 */
@Slf4j
@Component
public class SystemAIAnalysisTaskFinishNotifyListener implements ApplicationListener<SystemAIAnalysisTaskFinishedEvent> {

    @Resource
    private OrgAccountManager orgAccountManager;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Async("bizAsyncExecutor")
    @Override
    public void onApplicationEvent(@NonNull SystemAIAnalysisTaskFinishedEvent event) {
        SystemAIAnalysisTaskFinishedEvent.EventInfo eventInfo = event.getEventInfo();
        log.info("监听AI任务[{}]自动执行完成", eventInfo.getAiTaskId());
        List<String> accounts = getOrgAccounts(eventInfo.getOrgCode());
        if(!CollectionUtils.isEmpty(accounts)){
            String globalMessage = WebSocketRes.ok()
                    .topic(WebSocketTopicEnum.AI_ANALYSIS_TASK_COMPLETED)
                    .data("data", eventInfo)
                    .toJSONString();
            boolean debugEnabled = log.isDebugEnabled();
            if(debugEnabled){
                log.debug("系统AI任务完成消息 ==> {}", globalMessage);
            }
            accounts.stream()
                    .peek(r -> {
                        if(debugEnabled){
                            log.debug("推送系统AI任务完成消息至账号 ==> {}", r);
                        }
                    })
                    .forEach(r -> ChannelService.sendMessageByType13Channel(r, globalMessage));
        }

    }

    private List<String> getOrgAccounts(String orgCode) {
        List<OrgAccountOutDO> refInfos = orgAccountManager.listOrgAccountRefInfos(Collections.singletonList(orgCode));
        if(!CollectionUtils.isEmpty(refInfos)){
            List<String> accountIds = refInfos.stream()
                    .map(OrgAccountOutDO::getAccountId)
                    .collect(Collectors.toList());
            Result<List<AccountOutDO>> result = accountServiceClient.listAccountInfos(accountIds);
            if(result.isOk()){
                return result.getData()
                        .stream()
                        .map(AccountOutDO::getAccount)
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

}
