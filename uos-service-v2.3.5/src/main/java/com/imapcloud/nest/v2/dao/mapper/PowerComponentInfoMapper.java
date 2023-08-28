package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.PowerComponentInfoEntity;
import com.imapcloud.nest.v2.dao.po.in.PowerComponentInfoInPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 电力部件库信息表
 *
 * @author boluo
 * @date 2022-11-24
 */
@Mapper
public interface PowerComponentInfoMapper extends BaseMapper<PowerComponentInfoEntity> {

    /**
     * 更新
     *
     * @param powerComponentInfoEntity 电源组件信息实体
     * @return int
     */
    int updateByComponentId(PowerComponentInfoEntity powerComponentInfoEntity);

    /**
     * 插入
     *
     * @param powerComponentInfoEntity 电源组件信息实体
     * @return int
     */
    int insertOne(PowerComponentInfoEntity powerComponentInfoEntity);

    /**
     * 按组件id删除
     *
     * @param componentId 组件id
     * @param accountId   帐户id
     * @return int
     */
    int deleteByComponentId(@Param("componentId") String componentId, @Param("accountId") String accountId);

    /**
     * 分页总计
     *
     * @param toListInfoInPO 在订单列表信息
     * @return long
     */
    long listSum(PowerComponentInfoInPO.ListInPO toListInfoInPO);

    /**
     * 列表页面
     *
     * @param listInPO 在阿宝
     * @return {@link List}<{@link PowerComponentInfoEntity}>
     */
    List<PowerComponentInfoEntity> listPage(PowerComponentInfoInPO.ListInPO listInPO);
}
