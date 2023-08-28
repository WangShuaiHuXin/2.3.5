package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.DataPanoramaRecordsInDTO;
import com.imapcloud.nest.v2.service.dto.in.PanoramaLocationDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaRecordsOutDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaPointService.java
 * @Description DataPanoramaPointService
 * @createTime 2022年09月16日 11:56:00
 */
public interface DataPanoramaRecordsService {

    /**
     *  任务列表-分页查询（当前用户拥有的基站下面的巡检记录）
     * @param recordsInDTO
     * @return
     */
    PageResultInfo<DataPanoramaRecordsOutDTO.RecordsPageOutDTO> queryPointRecordsPage(DataPanoramaRecordsInDTO.RecordsInDTO recordsInDTO);


    /**
     *  任务列表- 根据架次记录查询航点信息
     * @param missionRecordsId
     * @return
     */
    List<DataPanoramaRecordsOutDTO.AirPointOutDTO> queryAirPoint(String missionRecordsId,String taskId);

    /**
     *  任务列表- 根据架次记录以及航点查找照片源数据 全量
     * @param picInDTO
     * @return
     */
    List<DataPanoramaRecordsOutDTO.PicOutDTO> queryPointRecordsPic(DataPanoramaRecordsInDTO.PicInDTO picInDTO);

    /**
     *  任务列表-根据架次记录以及全景点打包下载照片源数据
     * @param picInDTO
     * @param response
     * @return
     */
    void downloadPointRecordsPic(DataPanoramaRecordsInDTO.PicInDTO picInDTO, HttpServletResponse response);

    /**
     *  任务列表- 查询航线信息
     * @return
     */
    List<DataPanoramaRecordsOutDTO.TaskOutDTO> queryPanoramaTask();


    /**
     * 根据航线中json字段解析出航点信息
     * @param waypointJson
     * @return
     */
    List<PanoramaLocationDTO> getLocationByWaypoints(String waypointJson);

}
