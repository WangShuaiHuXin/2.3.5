package com.imapcloud.nest.service;

import com.imapcloud.nest.model.DataAirEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hc
 * @since 2021-08-02
 */
public interface DataAirService extends IService<DataAirEntity> {

    /**
     * 设置气体传感器设备号ID
     * @param nestUuid
     * @param serialId
     * @return
     */
    void setSerialId(String nestUuid , String serialId);

    /**
     * 获取气体传感器设备号ID
     * @param nestUuid
     * @return
     */
    String getSerialId(String nestUuid);

    /**
     * 通过设备号获取已经绑定的基站
     * @param serialId
     * @return
     */
    List<String> getNestBySerialId(String serialId , String nestUuid);

}
