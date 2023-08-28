package com.imapcloud.nest.service;

import com.imapcloud.nest.model.DefectInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 缺陷的详细信息表 服务类
 * </p>
 *
 * @author zheng
 * @since 2021-02-23
 */
public interface DefectInfoService extends IService<DefectInfoEntity> {

    /**
     * 通过图片id获取缺陷信息
     * @param photoId
     * @return
     */
    List<DefectInfoEntity> getByPhotoId(Long photoId);


    /**
     * 通过图片id获取问题信息和问题code
     * @param photoId
     * @return
     */
    List<DefectInfoEntity> getDefectInfoList(Long photoId, List<Integer> defectTypeList);
}
