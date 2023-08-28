package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.NestParamInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.NestParamOutDO;

import java.util.Collection;
import java.util.List;

/**
 * 基站参数
 *
 * @author boluo
 * @date 2022-08-25
 */
public interface NestParamManager {

    /**
     * 插入
     *
     * @param nestParamEntityInDO 巢参数实体
     * @return int
     */
    int insert(NestParamInDO.NestParamEntityInDO nestParamEntityInDO);

    /**
     * 删除
     *
     * @param nestId    巢id
     * @return int
     */
    int deleteByNestId(String nestId);

    /**
     * 更新基站电池信息
     *
     * @param batteryInDO 电池在做
     * @return int
     */
    int updateBatteryByNestId(NestParamInDO.BatteryInDO batteryInDO);

    /**
     * 查询列表
     *
     * @param nestIdCollection 巢id集合
     * @return {@link List}<{@link NestParamOutDO}>
     */
    List<NestParamOutDO> selectListByNestIdCollection(Collection<String> nestIdCollection);
}
