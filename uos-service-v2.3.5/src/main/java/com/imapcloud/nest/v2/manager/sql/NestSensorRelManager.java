package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.NestSensorRelInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.NestSensorRelOutDO;

import java.util.List;

/**
 * 传感器信息
 *
 * @author boluo
 * @date 2022-08-25
 */
public interface NestSensorRelManager {

    /**
     * 查询基站的传感器
     *
     * @param nestId 巢id
     * @return {@link List}<{@link NestSensorRelOutDO.NestSensorRelEntityOutDO}>
     */
    List<NestSensorRelOutDO.NestSensorRelEntityOutDO> selectListByNestId(String nestId);

    /**
     * 批量逻辑删除
     *
     * @param sensorIdList 删除传感器
     * @return int
     */
    int deleteBySensorIdList(List<Integer> sensorIdList);

    /**
     * 批量插入
     *
     * @param nestSensorRelEntityInDOList 巢传感器rel dolist实体
     * @return int
     */
    int batchInsert(List<NestSensorRelInDO.NestSensorRelEntityInDO> nestSensorRelEntityInDOList);

    /**
     * 删除
     *
     * @param nestId 巢id
     * @return int
     */
    int deleteByNestId(String nestId);
}
