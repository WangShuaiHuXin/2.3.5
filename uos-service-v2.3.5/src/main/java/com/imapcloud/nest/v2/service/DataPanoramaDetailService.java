package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.DataPanoramaDetailInDTO;
import com.imapcloud.nest.v2.service.dto.in.PanoramaDataDetailInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaDetailOutDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaDetailService.java
 * @Description DataPanoramaDetailService
 * @createTime 2022年09月16日 11:56:00
 */
public interface DataPanoramaDetailService {

    /**
     * 新增全景明细
     * @param addDetailInDTO
     * @return
     */
    String addDetail(DataPanoramaDetailInDTO.AddDetailInDTO addDetailInDTO);


    /**
     * 上传全景明细
     */
    DataPanoramaDetailOutDTO.QueryOutDTO uploadDetail(DataPanoramaDetailInDTO.DetailUploadInDTO detailUploadInDTO, MultipartFile fileData);

    /**
     * 保存全景明细数据
     * @param detailData    全景明细数据
     * @return  明细ID
     */
    DataPanoramaDetailOutDTO.QueryOutDTO savePanoramaDetail(PanoramaDataDetailInDTO detailData);

    /**
     * 删除全景明细
     * @param detailIds
     * @return
     */
    Boolean deleteDetails(List<String> detailIds);

    /**
     * 查询分页
     * @param queryPageInDTO
     * @return
     */
    PageResultInfo<DataPanoramaDetailOutDTO.QueryPageOutDTO> queryDetailPage(DataPanoramaDetailInDTO.QueryPageInDTO queryPageInDTO);

    /**
     *查询全量
     * @param queryInDTO
     * @return
     */
    List<DataPanoramaDetailOutDTO.QueryLessOutDTO> queryAllDetails(DataPanoramaDetailInDTO.QueryInDTO queryInDTO);

    /**
     *
     * @param queryOneInDTO
     * @return
     */
    List<DataPanoramaDetailOutDTO.QueryOutDTO> queryDetail(DataPanoramaDetailInDTO.QueryOneInDTO queryOneInDTO);

    /**
     * 根据pointId获取detailURL
     * @param pointIdList
     * @param missionId
     * @param missionRecordsId
     * @return
     */
    Map<String,DataPanoramaDetailOutDTO.QueryOutDTO> getPointToURL(List<String> pointIdList,String missionId , String missionRecordsId);

}
