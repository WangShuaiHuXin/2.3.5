package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.MissionParamEntity;

import java.util.List;

/**
 * <p>
 * 架次参数表 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface MissionParamService extends IService<MissionParamEntity> {

    /**
     * 批量软删除
     * @param idList
     * @return
     */
    int batchSoftDeleteByIds(List<Integer> idList);

    MissionParamEntity queryMissionParamInfo(Integer id);
}
