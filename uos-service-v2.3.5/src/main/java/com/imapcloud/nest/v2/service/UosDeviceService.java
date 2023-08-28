package com.imapcloud.nest.v2.service;

import com.geoai.common.web.rest.Result;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Classname UosDeviceService
 * @Description 国标设备管理API
 * @Date 2023/4/7 10:44
 * @Author Carnival
 */
public interface UosDeviceService {

    /**
     * 查询设备绑定基站列表接口
     */
    List<String> queryNestInfo(String deviceCode);

    /**
     * 删除未绑定基站设备接口
     */
    void deleteDevice(String deviceCode);

    /**
     * 获取基站关联设备信息
     * @param nestId    基站ID
     * @param deviceUse    设备用途
     * @return  设备ID
     */
    String getDeviceCodeByNestId(String nestId, Integer deviceUse);

}
