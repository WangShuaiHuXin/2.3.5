package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.FirmwarePackageInDTO;
import com.imapcloud.nest.v2.service.dto.in.NestFirmwarePackageInDTO;
import com.imapcloud.nest.v2.service.dto.in.NestFirmwareVersionInDTO;
import com.imapcloud.nest.v2.service.dto.out.FirmwarePackageBasicOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestFirmwarePackageInfoOutDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 基站固件业务接口定义
 * @author Vastfy
 * @date 2022/7/12 13:57
 * @since 1.9.7
 */
public interface UosNestFirmwareService {

    /**
     * 分页查询基站固件安装包更新记录信息
     * @param condition 查询条件
     * @return  查询结果
     */
    PageResultInfo<NestFirmwarePackageInfoOutDTO> pageNestFirmwarePackageInfos(NestFirmwareVersionInDTO condition);

    /**
     * 获取基站最新的各类型固件版本信息
     * @param nestIds   基站ID
     * @return      结果
     */
    Map<String, List<FirmwarePackageBasicOutDTO>> getNestLatestFirmwareVersionInfos(Collection<String> nestIds);

    /**
     * 新增基站固件安装包更新记录
     * @param nfpInfo   基站固件安装包信息
     * @return  更新记录ID
     */
    Long incrNestFirmwarePackageUpdateRecord(NestFirmwarePackageInDTO nfpInfo);

    /**
     * 更新基站固件安装包安装状态
     * @param updateRecordId    更新记录ID
     * @param nfpInstallState   固件安装包安装状态
     */
    boolean updateNestFirmwarePackageInstallState(Long updateRecordId, Integer nfpInstallState, String updaterId);

}
