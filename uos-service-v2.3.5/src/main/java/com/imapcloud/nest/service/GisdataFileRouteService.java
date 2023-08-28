package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.GisdataFileRouteEntity;

import java.util.List;

/**
 * <p>
 * 地理信息文件路径表 服务类
 * </p>
 *
 * @author root
 * @since 2020-09-23
 */
public interface GisdataFileRouteService extends IService<GisdataFileRouteEntity> {

    /**
     * 查询图层数据
     * @deprecated 2.1.0, 将在后续版本移除该方法
     */
    @Deprecated
    List<GisdataFileRouteEntity>  listFileRouteBy(GisdataFileRouteEntity gisdataFileRouteEntity);

}
