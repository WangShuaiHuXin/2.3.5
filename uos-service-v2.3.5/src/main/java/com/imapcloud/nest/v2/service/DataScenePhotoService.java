package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.DataScenePhotoDTO;
import com.imapcloud.nest.v2.service.dto.out.DataScenePhotoOutDTO;

import java.util.List;

/**
 * 现场取证
 *
 * @author boluo
 * @date 2022-07-13
 */
public interface DataScenePhotoService {
//    /**
//     * 创建仅含有图片的记录
//     *
//     * @param fileData        源图片
//     * @param fileName  文件名称
//     * @param accountId 用户ID
//     * @return long
//     * @deprecated 2.2.3，使用{@link DataScenePhotoService#update(com.imapcloud.nest.v2.service.dto.in.DataScenePhotoDTO.UpdateInfo)}接口替代，将在后续版本删除
//     */
//    @Deprecated
//    Long create(MultipartFile fileData, String fileName, String accountId);

    /**
     * 更新除图片外的信息
     *
     * @param updateInfo 更新信息
     * @return boolean
     */
    boolean update(DataScenePhotoDTO.UpdateInfo updateInfo);

    /**
     * 推送数据到分析中心 photoID必须是一个架次记录
     *
     * @param photoIds 照片身份证
     * @return int
     */
    DataScenePhotoOutDTO.PushOut push(List<Integer> photoIds);

    /**
     * 根据架次执行ID查询单位
     *
     * @param execMissionId 执行任务id
     * @return {@link String}
     */
    String getOrgIdByExecMissionId(String execMissionId);
}
