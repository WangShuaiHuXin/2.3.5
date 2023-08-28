package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.PowerComponentInDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerComponentOutDTO;
import com.imapcloud.nest.v2.service.dto.out.ComponentOptionListOutDTO;

import java.util.List;
import java.util.Map;

/**
 * 部件库
 *
 * @author boluo
 * @date 2022-11-25
 */
public interface PowerComponentService {

    /**
     * 保存或更新部件库
     *
     * @param saveOrUpdateInDTO 保存或更新dto
     */
    void saveOrUpdate(PowerComponentInDTO.SaveOrUpdateInDTO saveOrUpdateInDTO);

    /**
     * 部件库规则
     *
     * @param componentRuleInDTO 组件在dto统治
     */
    void componentRuleEdit(PowerComponentInDTO.ComponentRuleInDTO componentRuleInDTO);

    List<ComponentOptionListOutDTO> componentOptionList(String orgCode,String keyWord,String wayPointStationId);
    /**
     * 部件库和规则删除
     *
     * @param componentId 组件id
     * @param accountId   帐户id
     */
    void componentDeleteByComponentId(String componentId, String accountId);

    /**
     * 详情
     *
     * @param componentId 组件id
     * @return {@link PowerComponentOutDTO.PowerComponentInfoOutDTO}
     */
    PowerComponentOutDTO.PowerComponentInfoOutDTO componentDetail(String componentId);

    /**
     * 组件列表
     *
     * @param componentListInDTO 在dto组件列表
     * @return {@link PageResultInfo}<{@link PowerComponentOutDTO.PowerComponentInfoOutDTO}>
     */
    PageResultInfo<PowerComponentOutDTO.PowerComponentInfoOutDTO> componentList(PowerComponentInDTO.ComponentListInDTO componentListInDTO);

    /**
     * 查询航点的巡检类型
     * 返回值：     key：航点id
     *            value：巡检类型列表
     *
     * @param waypointIdList 路标id列表
     * @param orgCode orgCode
     * @return {@link Map}<{@link String}, {@link List}<{@link Integer}>>
     */
    Map<String, List<Integer>> selectComponentRuleByWaypointIdList(List<String> waypointIdList, String orgCode);

    /**
     * 组件红外规则编辑
     *
     * @param componentRuleInfraredInDTO 组件在dto统治
     */
    void componentInfraredRuleEdit(PowerComponentInDTO.ComponentRuleInfraredInDTO componentRuleInfraredInDTO);
}
