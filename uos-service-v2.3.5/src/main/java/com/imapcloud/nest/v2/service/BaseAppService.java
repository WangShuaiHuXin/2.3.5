package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.SaveBaseAppInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;

import java.util.List;

/**
 * <p>
 * 终端信息表 服务类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
public interface BaseAppService {

    List<AppListInfoOutDTO> listAppInfos();

    Boolean setShowStatusByAppId(String appId, Integer showStatus);

    Boolean setShowStatusByOrgCode(String orgCode, Integer showStatus);

    AppFlowPageOutDTO listAppFlowPage(Integer currentPage, Integer pageSize);

    String getDeviceIdByAppId(String appId);

    String getAppIdByDeviceId(String deviceId);

    BaseAppInfoOutDTO getAppInfoByAppId(String appId);

    BaseAppInfoOutDTO getAppInfoByDeviceId(String deviceId);

    BaseAppInfoOutDTO getAppInfoByAppName(String appName);

    Boolean saveOrUpdateBaseApp(SaveBaseAppInDTO saveBaseAppInDTO);

    BaseUavAppInfoOutDTO getBaseUavAppInfoByAppId(String appId);

    BaseAppPageOutDTO listSysAppByPages(Integer pageNo, Integer pageSize);

    /**
     * 软删除
     *
     * @param appId
     * @return
     */
    Boolean softDeleteSysApp(String appId);

    Boolean updateAppShowStatusByAppId(String appId,Integer showStatus);

    Boolean updateAppShowStatusByOrgCode(String orgCode,Integer showStatus);
}
