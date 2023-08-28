package com.imapcloud.nest.v2.manager.cps;

/**
 * cps
 *
 * @author boluo
 * @date 2022-08-27
 */
public interface SystemManager {

    /**
     * 得到cps版本
     *
     * @param nestId 巢id
     * @return {@link String}
     */
    String getCpsVersion(String nestId, Integer uavWhich);

    /**
     * 获取mps版本
     *
     * @param nestId 巢id
     * @return {@link String}
     */
    String getMpsVersion(String nestId);
}
