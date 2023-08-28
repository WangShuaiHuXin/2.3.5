package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.NestExternalEquipEntity;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName NestExternalEquipService.java
 * @Description NestExternalEquipService
 * @createTime 2022年06月21日 17:49:00
 */
public interface NestExternalEquipService extends IService<NestExternalEquipEntity> {

    /**
     *  通过设备号获取实体
     * @param serialNo
     * @param equipType
     * @return
     */
    NestExternalEquipEntity getEntityByNo(String serialNo , Integer equipType);

    /**
     *  通过设备号获取实体列表
     * @param serialNo
     * @param equipType
     * @return
     */
    List<NestExternalEquipEntity> getEntityListByNo(String serialNo , Integer equipType , String nestUuid);

    /**
     *  通过基站查询对应绑定信息 -- 设备跟基站默认一对一
     * @param nestUuid
     * @param nestId
     * @param equipType 设备类型
     * @return
     */
    NestExternalEquipEntity getEntityByNest(String nestUuid , String nestId , Integer equipType);

    /**
     * 新增绑定
     * @param entity
     */
    void addEquipEntity(NestExternalEquipEntity entity);

    /**
     *  解除绑定
     * @param nestUuid
     * @param nestId
     * @param serialNo - equipNo
     */
    void delEquipEntity(String nestUuid , String nestId , String serialNo , Integer equipType , boolean realDel);

}
