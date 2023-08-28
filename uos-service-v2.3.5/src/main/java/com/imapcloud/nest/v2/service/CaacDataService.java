package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.out.CaacCloudUavOutDTO;

import java.util.List;

/**
 * 民航数据业务接口定义
 *
 * @author Vastfy
 * @date 2023/3/8 15:50
 * @since 2.2.5
 */
public interface CaacDataService {

    /**
     * 获取民航云端无人机信息列表
     * @return  民航云端无人机信息列表
     */
    List<CaacCloudUavOutDTO> listCloudUav();

}
