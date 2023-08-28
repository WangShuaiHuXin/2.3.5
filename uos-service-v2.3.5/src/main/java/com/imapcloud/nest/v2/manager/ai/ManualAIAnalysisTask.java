package com.imapcloud.nest.v2.manager.ai;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 人工AI识别分析任务
 * @author Vastfy
 * @date 2022/11/30 18:13
 * @since 2.1.5
 */
@Slf4j
public class ManualAIAnalysisTask extends AbstractAIAnalysisTask {

    /**
     * 账号已开启任务映射数据
     * key: 账号ID
     * value: 分析任务ID列表
     */
    private static final Map<String, Set<String>> ACCOUNT_AI_ANALYSIS_TASKS = new ConcurrentHashMap<>();

    @Getter
    private final String accountId;

    ManualAIAnalysisTask(IAITaskProcess aiTaskProcess) {
        super(aiTaskProcess);
        if(aiTaskProcess instanceof IManualAITaskProcess){
            this.accountId = ((IManualAITaskProcess) aiTaskProcess).getAccountId();
            return;
        }
        throw new IllegalArgumentException("ManualAIAnalysisTask accept only ManualAITaskProcess");
    }

    public static int getManualTaskCounts(String accountId){
        if(StringUtils.hasText(accountId)){
            return ACCOUNT_AI_ANALYSIS_TASKS.getOrDefault(accountId, Collections.emptySet()).size();
        }
        return 0;
    }

    @Override
    protected void postInitialized() {
        String accountId = this.accountId;
        String taskId = this.getTaskId();
        // 初始化账号-任务信息
        Set<String> accountTaskCounts = ACCOUNT_AI_ANALYSIS_TASKS.computeIfAbsent(accountId, key -> new CopyOnWriteArraySet<>());
        accountTaskCounts.add(taskId);
        if(log.isDebugEnabled()){
            log.debug("人工AI识别任务[{}]初始化完成，账号[{}]运行中任务信息：{}", taskId, accountId, ACCOUNT_AI_ANALYSIS_TASKS.getOrDefault(accountId, null));
        }
    }

    @Override
    protected void postDestruction() {
        String accountId = this.accountId;
        String taskId = this.getTaskId();
        // 清理账号运行中任务信息
        ACCOUNT_AI_ANALYSIS_TASKS.computeIfPresent(accountId, (k, v) -> {
            v.remove(taskId);
            return v;
        });
        if(log.isDebugEnabled()){
            log.debug("人工AI识别任务[{}]清理完成，账号[{}]运行中任务信息：{}", taskId, accountId, ACCOUNT_AI_ANALYSIS_TASKS.getOrDefault(accountId, null));
        }
    }

    @Override
    public IAITaskProcess getTaskProcess() {
        return new ManualAITaskProcessSnapshot(this);
    }

}
