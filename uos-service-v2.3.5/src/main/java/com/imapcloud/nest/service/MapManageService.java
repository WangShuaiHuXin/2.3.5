package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.MapManageEntity;

import java.util.List;
import java.util.Map;

/**
 * 地图管理 业务层
 */
public interface MapManageService extends IService<MapManageEntity>{
    IPage<MapManageEntity> queryPage(Map<String, Object> params);

    List<MapManageEntity> listAll(Map<String, Object> params);

    MapManageEntity byId(Integer id);

    boolean saveMap(MapManageEntity entity);

    void saveUnitAddMap(String unitId);
}
