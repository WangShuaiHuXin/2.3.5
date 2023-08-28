package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.out.BaseNestUavOutDTO;

import java.util.List;

/**
 * <p>
 * 基站与无人机关系表 服务类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
public interface BaseUavNestRefService {

    /**
     * 查询无人机与基站id关联关系
     *
     * @param nestIdList
     * @return
     */
    List<BaseNestUavOutDTO> listNestUavIds(List<String> nestIdList);

    /**
     * 根据uavSn查询基站Sn
     * @param uavSn
     * @return
     */
    String getNestUUIDByUavSN(String uavSn);
}
