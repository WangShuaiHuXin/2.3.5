package com.imapcloud.nest.service;

import com.imapcloud.nest.model.NestSensorRelEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.SensorEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 传感器表 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-09-22
 */
public interface NestSensorRelService extends IService<NestSensorRelEntity> {

    void deleteNestSensor(String nestId, List<Integer> delSensorIdList);

    List<NestSensorRelEntity> getSensorByNestId(String nestId);

    @Deprecated
    NestSensorRelEntity getSpeakerSensorByNestId(Integer nestId);

    void deleteByNestId(String nestId);

    @Deprecated
    List<Integer> getNestSensorId(Integer nestId, List<Integer> idList);

    int updateVolumeByNestId(Integer volume, String nestId);

    @Deprecated
    int updateRepeatByNestId(Integer repeat, Integer nestId);
}
