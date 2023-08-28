package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.BaseNestInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOutDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 基站表
 *
 * @author boluo
 * @date 2022-08-25
 */
public interface BaseNestManager {


    /**
     *
     * 查询基站的uuid
     *
     * @param nestId 巢id
     * @return {@link String}
     */
    String getUuidByNestId(String nestId);

    /**
     * 通过基站ID查询
     *
     * @param nestId 巢id
     * @return {@link List}<{@link BaseNestOutDO.BaseNestEntityOutDO}>
     */
    List<BaseNestOutDO.BaseNestEntityOutDO> selectByNestId(String nestId);

    /**
     * 通过基站ID查询
     *
     * @param nestIdList 巢id
     * @return {@link List}<{@link BaseNestOutDO.BaseNestEntityOutDO}>
     */
    List<BaseNestOutDO.BaseNestEntityOutDO> selectListByNestIdList(List<String> nestIdList);

    /**
     * 通过基站uuid查询
     *
     * @param uuid 巢id
     * @return {@link List}<{@link BaseNestOutDO.BaseNestEntityOutDO}>
     */
    List<BaseNestOutDO.BaseNestEntityOutDO> selectByUuid(String uuid);

    /**
     * 通过基站编号查询
     *
     * @param uuid 巢id
     * @return {@link List}<{@link BaseNestOutDO.BaseNestEntityOutDO}>
     */
    List<BaseNestOutDO.BaseNestEntityOutDO> selectByNumber(String number);

    /**
     * 更新机巢信息
     *
     * @param baseNestEntityInDO 基地巢实体
     * @return int
     */
    int updateByNestId(BaseNestInDO.BaseNestEntityInDO baseNestEntityInDO);

    /**
     * @param baseNestEntityInDO 基地巢实体
     * @return int
     */
    int insertOne(BaseNestInDO.BaseNestEntityInDO baseNestEntityInDO);

    /**
     * 列表数量
     *
     * @param listInDO 在做上市
     * @return long
     */
    long countByCondition(BaseNestInDO.ListInDO listInDO);

    /**
     * 列表
     *
     * @param listInDO 在做上市
     * @return {@link List}<{@link BaseNestOutDO.ListOutDO}>
     */
    List<BaseNestOutDO.ListOutDO> selectByCondition(BaseNestInDO.ListInDO listInDO);

    /**
     * 删除
     *
     * @param nestId    巢id
     * @param accountId 帐户id
     * @return int
     */
    int deleteByNestId(String nestId, String accountId);

    /**
     * 新增/修改基站后清除缓存
     *
     * @param nestId    巢id
     * @param accountId 帐户id
     */
    void clearRedisCache(String nestId, String accountId);

    /**
     * 删除基站后，清除缓存
     *
     * @param userName 用户名
     */
    void clearDeleteRedisCache(String userName);

    /**
     * 清除缓存
     */
    void clearNestListRedisCache();

    /**
     * 查找关联的无人机信息
     * @param nestIdList
     * @return
     */
    List<BaseNestOutDO.BaseNestUavInfoOutDO> getNestUavInfoByIds( List<String> nestIdList);

    List<BaseNestOutDO.ListOutDO> selectAllCondition(BaseNestInDO.ListInDO listInDO);

    long typeCountByCondition(BaseNestInDO.ListInDO listInDO);
}
