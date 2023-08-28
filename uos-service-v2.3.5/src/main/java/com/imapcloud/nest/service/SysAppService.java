package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.SysAppEntity;
import com.imapcloud.nest.pojo.dto.SaveSysAppDTO;
import com.imapcloud.nest.pojo.dto.SysAppDto;
import com.imapcloud.nest.pojo.dto.VisibleAppFlowParam;
import com.imapcloud.nest.utils.RestRes;

import java.util.List;

/**
 * <p>
 * 终端信息表 服务类
 * </p>
 *
 * @author kings
 * @since 2020-10-26
 */
public interface SysAppService extends IService<SysAppEntity> {

    @Deprecated
    IPage<SysAppEntity> listSysAppByPages(Integer pageNo, Integer pageSize, SysAppEntity sysAppEntity);

    @Deprecated
    SysAppDto getInfoById(Integer id);

    /**
     * @deprecated 2.0.0，使用{@link SysAppService#listByOrgCode(java.lang.String)}替代
     */
    List<SysAppEntity> listByUnitId(String unitId);

    List<SysAppEntity> listByOrgCode(String orgCode);

    /**
     * 批量查询易飞终端
     *
     * @return
     */
    RestRes listSysAppByUnitId();

    RestRes updateSysAppShowStatus(Integer appId, String unitId, Integer showStatus);

    /**
     * 获取腾讯云推流地址
     *
     * @param appId
     * @return
     */
    RestRes getTxDefaultLive(String appId);

    /**
     * 获取移动终端推流地址
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    RestRes listAppFlowUrl(Integer currentPage, Integer pageSize);

    /**
     * 设置App是否可以看流
     *
     * @param visibleAppFlowParam
     * @return
     */
    RestRes setVisibleAppFlow(VisibleAppFlowParam visibleAppFlowParam);

    /**
     * 添加或者更新app
     *
     * @param sysAppDto
     * @return
     */
    RestRes addOrUpdateApp(SaveSysAppDTO sysAppDto);

    /**
     * 获取App本地航线
     *
     * @param appId
     * @return
     */
    RestRes getAppLocalRoute(String appId);

    RestRes setAppPushStream(String appId, Boolean enable);

}
