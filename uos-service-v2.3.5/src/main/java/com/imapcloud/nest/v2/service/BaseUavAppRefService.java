package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.SaveUavAppInDTO;

/**
 * <p>
 * 移动终端无人机关联表 服务类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
public interface BaseUavAppRefService {

    Boolean saveUavAppRef(SaveUavAppInDTO saveUavAppInDTO);

    Boolean softDeleteRef(String appId, String uavId);
}
