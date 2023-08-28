package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.FirmwarePackageInDTO;
import com.imapcloud.nest.v2.service.dto.in.FirmwareVersionInDTO;
import com.imapcloud.nest.v2.service.dto.out.FirmwarePackageInfoOutDTO;

import java.io.InputStream;
import java.util.List;

/**
 * 固件安装包业务接口定义
 * @author Vastfy
 * @date 2022/7/12 13:57
 * @since 1.9.7
 */
public interface UosFirmwareService {

    /**
     * 分页查询固件安装包信息
     * @param condition 查询条件
     * @return  分页结果
     */
    PageResultInfo<FirmwarePackageInfoOutDTO> pageFirmwarePackageInfos(FirmwareVersionInDTO condition);

    /**
     * 根据固件安装包的文件类型获取安装包版本
     * @param fpFileType 固件安装包的文件类型【目前仅支持.zip和.apk】
     * @param inputStream   安装包输入流
     * @return  版本信息
     */
    String getFirmwarePackageVersion(String fpFileType, InputStream inputStream);

    /**
     * 上传固件安装包并保存信息
     * @param inputStream   固件安装包输入流
     * @param firmwarePackage   固件安装包信息
     * @return  固件安装包唯一标识
     */
    String uploadFirmwarePackage(InputStream inputStream, FirmwarePackageInDTO firmwarePackage);

    /**
     * 新增固件安装包信息
     * @param firmwarePackage   固件安装包信息
     * @return  固件安装包唯一标识
     */
    String addFirmwarePackage(FirmwarePackageInDTO firmwarePackage);

    /**
     * 批量删除固件安装包信息
     * @param packageIds    固件安装包ID
     * @return  true：删除成功
     */
    Boolean dropFirmwarePackages(List<String> packageIds);

    /**
     * 推送固件安装包给基站进行下载和安装
     * @param nestId    基站ID
     * @param packageId 安装包ID
     * @return  true：推送成功
     */
    Boolean pushFirmwarePackage2NestInstall(String nestId, String packageId , Integer uavWhich);

    /**
     * 取消基站安装固件安装包
     * @param nestId    基站ID
     * @param fpType    固件类型，具体可参见{@link com.imapcloud.nest.v2.common.enums.FirmwareTypeEnum}
     * @return  true：取消成功
     */
    Boolean cancelFirmwarePackage2NestInstall(String nestId, Integer fpType,Integer uavWhich);

}
