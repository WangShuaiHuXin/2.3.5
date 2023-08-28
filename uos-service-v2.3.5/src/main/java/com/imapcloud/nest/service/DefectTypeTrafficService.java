package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.DefectTypeTrafficEntity;
import com.imapcloud.nest.utils.RestRes;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 */
public interface DefectTypeTrafficService extends IService<DefectTypeTrafficEntity> {
    /**
     * 添加、编辑缺陷类型信息
     * @param defectTypeTrafficEntity
     * @return
     */
    RestRes saveOrUpdateDefectType(DefectTypeTrafficEntity defectTypeTrafficEntity);

    /**
     * 获取全部缺陷类型信息
     */
    RestRes getAllList(String typeName, String orgCode,Integer type,Integer tagId);

    /**
     * 批量软删除
     * @param idList
     * @return
     */
    RestRes deleteDefectType(List<Integer> idList);
}
