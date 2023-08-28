package com.imapcloud.nest.v2.service;

import cn.hutool.core.lang.Pair;
import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.BaseNestInDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseNestOutDTO;
import com.imapcloud.nest.v2.web.vo.req.AdminNestReqVO;

import java.util.List;

/**
 * 基站后台服务
 *
 * @author boluo
 * @date 2022-08-26
 */
public interface AdminBaseNestService {

    /**
     * 基站列表
     *
     * @param listInDTO dto
     * @return {@link PageResultInfo}<{@link BaseNestOutDTO.ListOutDTO}>
     */
    PageResultInfo<BaseNestOutDTO.ListOutDTO> list(BaseNestInDTO.ListInDTO listInDTO);

    /**
     * 基站基本信息新增/修改
     *
     * @param nestBaseInDTO dto
     * @return {@link String} 基站ID
     */
    String nestBaseSave(BaseNestInDTO.NestBaseInDTO nestBaseInDTO);

    /**
     * 基站的基站信息保存
     *
     * @param nestNestInDTO 窝巢dto
     */
    void nestNestSave(BaseNestInDTO.NestNestInDTO nestNestInDTO);

    /**
     * 基站详情
     *
     * @param nestId 基站ID
     * @return {@link BaseNestOutDTO.NestDetailOutDTO}
     */
    BaseNestOutDTO.NestDetailOutDTO nestDetail(String nestId);

    /**
     * 获取基站的版本信息
     *
     * @param nestId     基站
     * @param clearCache 清除缓存
     * @return {@link BaseNestOutDTO.VersionOutDTO}
     */
    BaseNestOutDTO.VersionOutDTO getVersion(String nestId, boolean clearCache);

    /**
     * 逻辑删除基站相关数据
     *
     * @param nestId    巢id
     * @param accountId 巢id
     * @param userName  巢id
     * @return boolean
     */
    boolean delete(String nestId, String accountId, String userName);

    /**
     * 获取监控信息
     *
     * @param nestId 巢id
     * @return {@link List}<{@link BaseNestOutDTO.CameraInfoOutDTO}>
     */
    List<BaseNestOutDTO.CameraInfoOutDTO> getCameraInfo(String nestId);

    /**
     * 查询基站所关联的单位
     *
     * @param nestId 巢id
     * @return {@link List}<{@link String}>
     */
    List<Pair<String, String>> queryOrgInfoByNestId(String nestId);

    /**
     * 电池设置
     *
     * @param batteryInDTO 电池在dto
     */
    void nestBattery(BaseNestInDTO.BatteryInDTO batteryInDTO);

    /**
     * 基站电池详情
     *
     * @param nestId 巢id
     * @return {@link BaseNestOutDTO.BatteryDetailOutDTO}
     */
    BaseNestOutDTO.BatteryDetailOutDTO nestBatteryDetail(String nestId);

    /**
     * 基站类型编辑
     *
     * @param nestTypeInDTO 嵌套类型dto
     */
    void nestTypeEdit(BaseNestInDTO.NestTypeInDTO nestTypeInDTO);

    /**
     * 基站型号详情
     *
     * @return {@link BaseNestOutDTO.NestTypeOutDTO}
     */
    List<BaseNestOutDTO.NestTypeOutDTO> nestTypeDetail();

    /**
     * 获取统计信息
     *
     * @param listReqVO
     * @return
     */
    BaseNestInDTO.AdminNestTypeCountOutDto getTypeCount(BaseNestInDTO.ListInDTO listReqVO);
}
