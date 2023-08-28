package com.imapcloud.nest.v2.manager.ai;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 自动AI任务队列
 *
 * @author Vastfy
 * @date 2022/12/2 11:12
 * @since 2.1.5
 */
@SuperBuilder
@Getter
@EqualsAndHashCode
@ToString
public class WaitingTaskElementSupport implements WaitingTaskElement {

    private String orgCode;

    private String taskId;

}
