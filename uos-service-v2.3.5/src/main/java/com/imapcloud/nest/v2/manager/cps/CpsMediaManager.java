package com.imapcloud.nest.v2.manager.cps;

/**
 * media指令
 *
 * @author boluo
 * @date 2023-04-13
 */
public interface CpsMediaManager {

    /**
     * 终止同步
     *
     * @param nestId 巢id
     * @param which  哪一个
     */
    void resetMediaManager(String nestId, int which);
}
