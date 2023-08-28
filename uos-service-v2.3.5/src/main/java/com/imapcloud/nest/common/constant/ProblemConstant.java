package com.imapcloud.nest.common.constant;

/**
 * 分析应用模块使用
 *
 * @author: zhengxd
 * @create: 2021/6/21
 **/
public interface ProblemConstant {

    /**
     * 有无问题（0-无问题；1-有问题）
     */
    interface FlagType {
        Integer NOT_PROBLEM = 0;
        Integer HAS_PROBLEM = 1;
    }

    /**
     * 问题的状态（0：未识别，1：没问题，2：有问题，3：已解决）
     */
    interface ProblemStatus {
        Integer NOT_IDENTIFY = 0;
        Integer NOT_PROBLEM = 1;
        Integer HAS_PROBLEM = 2;
        Integer HAS_SOLVE = 3;
    }

}
