package com.imapcloud.nest.service;

import com.imapcloud.nest.model.MultiNestMissionDefaultRoleEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 多基站任务默认架次表 服务类
 * </p>
 *
 * @author wmin
 * @since 2021-02-03
 */
public interface MultiNestMissionDefaultRoleService extends IService<MultiNestMissionDefaultRoleEntity> {
    /**
     * 批量查询架次Id通过基站id列表
     * @param nestIdList
     * @return
     */
    List<Integer> listMissionIdByNestIds(List<Integer> nestIdList);

    String getMissionNameByNestUuid(String nestUuid);
}
