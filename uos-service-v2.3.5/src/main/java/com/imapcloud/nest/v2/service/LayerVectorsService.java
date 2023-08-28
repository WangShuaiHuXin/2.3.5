package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.out.LayerVectorsOutDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Classname GridRegionService
 * @Description 网格区域API
 * @Date 2022/12/7 14:25
 * @Author Carnival
 */
public interface LayerVectorsService {


    /**
     * 上传网格矢量
     */
    Boolean uploadVectors(MultipartFile file, String orgCode, String layerVectorName);

    /**
     * 根据单位获取矢量图层
     */
    List<LayerVectorsOutDTO> queryVectors(String orgCode);


    /**
     * 修改网格矢量名称
     */
    Boolean updateVectors(String layerVectorId, String name);

    /**
     * 删除网格矢量
     */
    Boolean deleteVectors(String layerVectorId);
}
