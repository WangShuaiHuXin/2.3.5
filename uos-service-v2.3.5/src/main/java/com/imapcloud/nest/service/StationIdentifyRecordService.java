package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.DefectInfoElectricEntity;
import com.imapcloud.nest.model.DefectInfoEntity;
import com.imapcloud.nest.model.StationIdentifyRecordEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.pojo.dto.StationDevicePhotoDTO;
import com.imapcloud.nest.pojo.dto.StationIdentifyPhotoDTO;
import com.imapcloud.nest.pojo.dto.reqDto.StationIdentifyDefectReqDto;
import com.imapcloud.nest.utils.RestRes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 变电站的设备出现的缺陷记录 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-12-29
 */
public interface StationIdentifyRecordService extends IService<StationIdentifyRecordEntity> {



    /**
     * 根据设备uuid获取该设备的图片分页列表
     *
     * @param photoName
     * @param missionRecordId 
     * @return
     */
    IPage<StationIdentifyPhotoDTO> getPhotoListPage(IPage<StationIdentifyPhotoDTO> page, Integer tagId, String photoName, String missionRecordId,Integer defectStatus);

    /**
     * 根据photoName获取该设备的表计读数的全部数值
     *
     * @param photoName
     * @return
     */
    List<StationDevicePhotoDTO> getAllPhotoMeterNum(Integer tagId, String photoName, String startTime, String endTime);



}
