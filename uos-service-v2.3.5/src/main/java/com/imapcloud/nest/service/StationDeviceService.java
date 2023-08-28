package com.imapcloud.nest.service;

import com.imapcloud.nest.model.StationDeviceEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author daolin
 * @since 2020-11-02
 */
public interface StationDeviceService extends IService<StationDeviceEntity> {

    Integer initDevice(MultipartFile deviceJson, MultipartFile pointJson, MultipartFile mapsJson, MultipartFile wholeUnitJson,String nestId);

    Integer checkPointDetail(Integer airLineStartId, Integer airLineEndId, HttpServletResponse response);

    void deleteByNestId(String nestId);

    RestRes selectByNestId(String nestId);

    String getDeviceName(String uuid);
}
