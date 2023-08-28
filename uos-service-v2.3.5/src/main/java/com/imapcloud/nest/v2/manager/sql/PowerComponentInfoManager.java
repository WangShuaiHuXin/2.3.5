package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.PowerComponentInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerComponentInfoOutDO;

import java.util.Collection;
import java.util.List;

/**
 * 电力部件库信息表
 * 参数：PowerComponentInfoInDO
 * 返回值：PowerComponentInfoOutDO
 *
 * @author boluo
 * @date 2022-11-24
 */
public interface PowerComponentInfoManager {

    /**
     * 查询部件库信息
     *
     * @param componentIdCollection 部件库id
     * @return {@link PowerComponentInfoOutDO}
     */
    List<PowerComponentInfoOutDO> selectByComponentIdCollection(Collection<String> componentIdCollection);

    /**
     * 更新
     *
     * @param powerComponentInfoInDO 做电源组件信息
     * @return int
     */
    int update(PowerComponentInfoInDO powerComponentInfoInDO);

    /**
     * 保存
     *
     * @param powerComponentInfoInDO 电源组件信息
     * @return int
     */
    int save(PowerComponentInfoInDO powerComponentInfoInDO);

    /**
     * 部件库删除
     *
     * @param componentId 组件id
     * @param accountId   帐户id
     * @return int
     */
    int deleteByComponentId(String componentId, String accountId);

    /**
     * 分页合计
     *
     * @param listInfoInDO 在做列表信息
     * @return long
     */
    long listSum(PowerComponentInfoInDO.ListInfoInDO listInfoInDO);

    /**
     * 列表页面
     *
     * @param listInfoInDO 在做列表信息
     * @return {@link List}<{@link PowerComponentInfoOutDO}>
     */
    List<PowerComponentInfoOutDO> listPage(PowerComponentInfoInDO.ListInfoInDO listInfoInDO);

    /**
     * 根据单位信息查询单位下的部件
     *
     * @param orgCode
     * @return
     */
    List<PowerComponentInfoOutDO> queryListByOrg(String orgCode);

    /**
     * 根据单位和名称模糊搜索
     * @param orgCode
     * @param keyWord
     * @return
     */
    List<PowerComponentInfoOutDO> queryListByOrgAKeyWord(String orgCode,String keyWord,String equipmentType);
}
