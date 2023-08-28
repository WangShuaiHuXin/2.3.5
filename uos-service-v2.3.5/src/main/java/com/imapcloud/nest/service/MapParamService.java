package com.imapcloud.nest.service;

import com.imapcloud.nest.model.MapParamEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.utils.RestRes;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zheng
 * @since 2021-08-17
 */
public interface MapParamService extends IService<MapParamEntity> {

    RestRes saveMapParam(MapParamEntity mapParamEntity);

    MapParamEntity getMapParam(String orgCode);
}
