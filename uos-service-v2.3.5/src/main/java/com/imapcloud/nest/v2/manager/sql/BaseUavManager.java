package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.BaseUavInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseUavOutDO;

import java.util.List;

/**
 * 无人机信息
 *
 * @author boluo
 * @date 2022-08-25
 */
public interface BaseUavManager {

    /**
     * 根据无人机ID查询
     *
     * @param uavId 无人机id
     * @return {@link List}<{@link BaseUavOutDO.BaseUavEntityOutDO}>
     */
    BaseUavOutDO.BaseUavEntityOutDO selectOneByUavId(String uavId);

    /**
     * 通过无人机ID更新
     *
     * @param baseUavEntityInDO 基地无人机实体
     * @return int
     */
    int updateByUavId(BaseUavInDO.BaseUavEntityInDO baseUavEntityInDO);

    /**
     * 插入
     *
     * @param baseUavEntityInDO 基地无人机实体
     * @return int
     */
    int insert(BaseUavInDO.BaseUavEntityInDO baseUavEntityInDO);

    /**
     * 删除
     *
     * @param uavIdList 无人机id列表
     * @param accountId 帐户id
     * @return int
     */
    int deleteByUavIdList(List<String> uavIdList, String accountId);

    /**
     * 列表
     *
     * @param uavIdList 无人机id列表
     * @return {@link List}<{@link BaseUavOutDO.BaseUavEntityOutDO}>
     */
    List<BaseUavOutDO.BaseUavEntityOutDO> selectListByUavIdList(List<String> uavIdList);
}
