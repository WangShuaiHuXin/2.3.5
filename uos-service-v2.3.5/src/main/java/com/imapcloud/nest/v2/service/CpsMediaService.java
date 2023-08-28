package com.imapcloud.nest.v2.service;

/**
 * media指令
 *
 * @author boluo
 * @date 2023-04-13
 */
public interface CpsMediaService {

    /**
     * 终止数据同步
     *
     * @param nestId 巢id
     * @param which  哪一个
     */
    void dataSynStop(String nestId, Integer which);
}
