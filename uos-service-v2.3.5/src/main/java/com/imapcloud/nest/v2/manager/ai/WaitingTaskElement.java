package com.imapcloud.nest.v2.manager.ai;

/**
 * 自动AI任务队列
 *
 * @author Vastfy
 * @date 2022/12/2 11:12
 * @since 2.1.5
 */
public interface WaitingTaskElement {

    /**
     * 获取单位编码
     * @return  单位编码
     */
    String getOrgCode();

    /**
     * 获取AI识别任务ID
     * @return  AI识别任务ID
     */
    String getTaskId();

}
