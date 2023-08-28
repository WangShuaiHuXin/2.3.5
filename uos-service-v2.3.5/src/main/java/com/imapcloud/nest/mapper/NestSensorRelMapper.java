package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.NestSensorRelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.SensorEntity;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 传感器表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2020-09-22
 */
public interface NestSensorRelMapper extends BaseMapper<NestSensorRelEntity> {

    void deleteNestSensor(@Param("nestId") String nestId, @Param("idList") List<Integer> idList);

    List<NestSensorRelEntity> getSensorByNestId(String nestId);

    NestSensorRelEntity getSpeakerSensorByNestId(Integer nestId);
//    @Update("UPDATE nest_sensor_rel SET deleted = 1 WHERE nest_id = #{nestId}")
//    void deleteByNestId(Integer nestId);

    List<Integer> getNestSensorId(@Param("nestId") Integer nestId, @Param("idList") List<Integer> idList);

    void deleteByNestId(String nestId);

    int updateVolumeByNestId(@Param("volume") Integer volume, @Param("nestId") String nestId);
    int updateRepeatByNestId(@Param("repeat") Integer repeat, @Param("nestId") Integer nestId);

    /**
     * 批量插入
     *
     * @param nestSensorRelEntityList 巢传感器rel实体列表
     * @return int
     */
    int batchInsert(@Param("entityList") List<NestSensorRelEntity> nestSensorRelEntityList);

    /**
     * 批量更新
     *
     * @param nestSensorRelEntityList 巢传感器rel实体列表
     * @return int
     */
    int batchUpdate(@Param("entityList") List<NestSensorRelEntity> nestSensorRelEntityList);

    /**
     * 查询基站的传感器信息
     *
     * @param nestId 巢id
     * @return {@link List}<{@link NestSensorRelEntity}>
     */
    List<NestSensorRelEntity> getListByNestId(String nestId);

    /**
     * 逻辑删除
     *
     * @param sensorIdList 传感器id列表
     * @param time         时间
     * @return int
     */
    int deleteBySensorIdList(@Param("sensorIdList") List<Integer> sensorIdList, @Param("time") LocalDateTime time);

    /**
     * 删除
     *
     * @param nestId 巢id
     * @return int
     */
    int deleteByBaseNestId(@Param("nestId") String nestId);
}
