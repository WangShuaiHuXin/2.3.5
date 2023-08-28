package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.PhotoLocationEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhengxd
 * @since 2021-06-02
 */
public interface PhotoLocationService extends IService<PhotoLocationEntity> {

    /**
     * 获取照片位置列表
     * @param pageNum
     * @param limit
     * @param photoIdList
     * @return
     */
    RestRes getPageList(Integer pageNum, Integer limit, List photoIdList);

    /**
     * 获取同任务架次
     * @param id
     * @return
     */
    List<MissionRecordsEntity> getMissionRecordsIds(Integer id,Integer flag);

    /**
     * 获取同任务上一个架次的照片
     * @param missionRecordsId
     * @return
     */
    IPage<MissionPhotoEntity> getPhoto(Integer page, Integer limit,Integer missionRecordsId,Integer id);

    /**
     * 插入或修改照片位置信息
     * @param photoLocationEntity
     * @param file
     * @return
     */
    RestRes insertOrUpdate(PhotoLocationEntity photoLocationEntity,String fileName,String filePath);

    void exportPhotoWater(String photoIdList, HttpServletResponse response);

    void updatePhoto(Integer id, Integer clearId);
}
