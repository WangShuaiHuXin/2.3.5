package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.ElectronicFenceEntity;
import com.imapcloud.nest.pojo.dto.ElectronicFenceDTO;
import com.imapcloud.nest.utils.RestRes;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zheng
 * @since 2021-09-26
 */
public interface ElectronicFenceService extends IService<ElectronicFenceEntity> {

    /**
     * 获取单位全部围栏
     * @param orgId
     * @param name
     * @param containsChild
     * @return
     */
    RestRes selectAllList(String orgId, String name,Integer containsChild);

    RestRes addElectronicFence(ElectronicFenceDTO electronicFenceDTO);

    /**
     * 修改围栏信息
     * @param electronicFenceDTO
     * @return
     */
    RestRes updateElectronicFence(ElectronicFenceDTO electronicFenceDTO);

    /**
     * 修改围栏状态
     * @param id
     * @param state
     * @return
     */
    RestRes updateState(Integer id, Integer state);
    /**
     * 删除
     * @param ids
     * @return
     */
    RestRes deleteElectronicFence(List<Integer> ids);

}
