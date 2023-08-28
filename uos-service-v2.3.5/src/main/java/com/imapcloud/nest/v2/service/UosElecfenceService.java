package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.ElecfenceCreationInDTO;
import com.imapcloud.nest.v2.service.dto.in.ElecfenceModificationInDTO;
import com.imapcloud.nest.v2.service.dto.out.ElecfenceInfoOutDTO;

import java.util.List;

/**
 * UOS电子围栏业务接口定义
 *
 * @author Vastfy
 * @date 2022/9/26 15:36
 * @since 2.1.0
 */
public interface UosElecfenceService {

    /**
     * 获取指定单位拥有的专属的电子围栏信息
     * @param orgCode   单位编码
     * @param includeSub   是否包含子孙单位数据
     * @return  电子围栏信息
     */
    List<ElecfenceInfoOutDTO> getExclusiveElecfenceInfos(String orgCode, Integer state, boolean includeSub);

    /**
     * 获取指定单位拥有的共享电子围栏信息
     * @param orgCode   单位编码
     * @return  电子围栏信息
     */
    List<ElecfenceInfoOutDTO> getSharedElecfenceInfos(String orgCode);

    /**
     * 获取登录用户可见的电子围栏信息【用户所属单位及子孙单位拥有的电子围栏+用户所属单位的上级单位共享的电子围栏】
     * @return  电子围栏信息
     */
    List<ElecfenceInfoOutDTO> listVisibleElecfenceInfos();

    /**
     * 新建电子围栏
     * @param data 电子围栏信息
     * @return  电子围栏ID
     */
    String createElecfence(ElecfenceCreationInDTO data);

    /**
     * 修改电子围栏
     * @param elecfenceId 电子围栏ID
     */
    void modifyElecfence(String elecfenceId, ElecfenceModificationInDTO data);

    /**
     * 删除电子围栏
     * @param elecfenceId 电子围栏ID
     */
    void deleteElecfence(String elecfenceId);

    /**
     * 修改单位的共享电子围栏
     * @param orgCode 单位编码
     * @param elecfenceId 电子围栏ID
     * @param status 电子围栏开启状态
     */
    void modifyOrgElecfenceStatus(String orgCode, String elecfenceId, Integer status);

}
