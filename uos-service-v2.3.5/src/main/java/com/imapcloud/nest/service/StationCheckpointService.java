package com.imapcloud.nest.service;

import com.imapcloud.nest.model.StationCheckpointEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 巡检点，对应拍照点坐标、角度等信息 服务类
 * </p>
 *
 * @author daolin
 * @since 2020-11-02
 */
public interface StationCheckpointService extends IService<StationCheckpointEntity> {

    void deleteByNestId(String nestId);

    Integer insert(StationCheckpointEntity point);

    List<StationCheckpointEntity> selectInDeviceIdList(List<Integer> deviceIds);

    Integer updateByUuid(StationCheckpointEntity pt);
}
