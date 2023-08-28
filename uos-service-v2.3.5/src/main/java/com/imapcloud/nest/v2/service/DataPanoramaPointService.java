package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.DataPanoramaPointInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaPointOutDTO;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaPointService.java
 * @Description DataPanoramaPointService
 * @createTime 2022年09月16日 11:56:00
 */
public interface DataPanoramaPointService {

    /**
     * 新增全景点
     * @param addPointInDTO
     * @return
     */
    Boolean addPoint(DataPanoramaPointInDTO.AddPointInDTO addPointInDTO);

    /**
     * 删除全景点
     * @param pointIds
     * @return
     */
    Boolean deletePoints(List<String> pointIds);

    /**
     * 更新全景点
     * @param updatePointInDTO
     * @return
     */
    Boolean updatePoint(DataPanoramaPointInDTO.UpdatePointInDTO updatePointInDTO);

    /**
     * 查询分页
     * @param queryPageInDTO
     * @return
     */
    PageResultInfo<DataPanoramaPointOutDTO.QueryPageOutDTO> queryPointPage(DataPanoramaPointInDTO.QueryPageInDTO queryPageInDTO);

    /**
     *查询全量
     * @param queryInDTO
     * @return
     */
    List<DataPanoramaPointOutDTO.QueryLessOutDTO> queryAllPoints(DataPanoramaPointInDTO.QueryInDTO queryInDTO);

    /**
     *
     * @param queryOneInDTO
     * @return
     */
    List<DataPanoramaPointOutDTO.QueryOutDTO> queryPoint(DataPanoramaPointInDTO.QueryOneInDTO queryOneInDTO);
}
