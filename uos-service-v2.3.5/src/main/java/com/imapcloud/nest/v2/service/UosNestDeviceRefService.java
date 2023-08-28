package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.dao.entity.UosNestDeviceRefEntity;
import com.imapcloud.nest.v2.dao.entity.UosNestStreamRefEntity;
import com.imapcloud.nest.v2.service.dto.out.UosNestDeviceRefOutDTO;

import java.util.List;

/**
 * @Classname UosNestDeviceRefService
 * @Description 基站设备关联API
 * @Date 2023/4/7 14:50
 * @Author Carnival
 */
public interface UosNestDeviceRefService {

    /**
     * 查询设备绑定基站列表接口
     */
    List<UosNestDeviceRefEntity> listNestDeviceRef(String nestId);

    /**
     * 删除基站设备纪录
     */
    void deleteNestDeviceRef(String nestId);

    /**
     * 插入基站设备纪录
     */
    boolean insertNestDeviceRef(List<UosNestDeviceRefEntity> newEntityList);

    /**
     * 流媒体基站关联列表接口
     */
    List<UosNestStreamRefEntity> listNestStreamRef(String nestId);

    /**
     * 查询设备
     * @param nestId
     * @return
     */
    List<UosNestDeviceRefOutDTO> findByNestId(String nestId);
}
