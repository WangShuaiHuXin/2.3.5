package com.imapcloud.nest.service;

import com.imapcloud.nest.model.DefectTypeEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.utils.RestRes;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zheng
 * @since 2021-02-23
 */
public interface DefectTypeService extends IService<DefectTypeEntity> {
    /**
     * 添加、编辑缺陷类型信息
     * @param defectTypeEntity
     * @return
     */
    RestRes saveOrUpdateDefectType(DefectTypeEntity defectTypeEntity);

    /**
     * 获取全部缺陷类型信息
     * @param typeName
     * @return
     */
    RestRes getAllList(String typeName);

    /**
     * 批量软删除
     * @param idList
     * @return
     */
    RestRes deleteDefectType(List<Integer> idList);
}
