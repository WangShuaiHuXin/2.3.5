package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.FlightMissionEntity;
import com.imapcloud.nest.pojo.dto.flightMission.FlightMissionDTO;
import com.imapcloud.nest.pojo.vo.FlightMissionSyncAggVO;
import com.imapcloud.nest.utils.RestRes;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author double
 * @since 2022-03-22
 */
public interface FlightMissionService extends IService<FlightMissionEntity> {

    /**
     *  查询分页接口
     * @param nestId
     * @param page
     * @param limit
     * @param asc
     * @param desc
     * @param startTime
     * @param endTime
     * @return
     */
    RestRes queryFlightMissionPage(String nestId , Integer page, Integer limit,
                                  String asc,String desc,String startTime,String endTime , Integer which);

    /**
     *  飞行记录导出
     * @param nestIds
     * @param startTime
     * @param endTime
     * @param response
     */
    void exportFlightMissionRecord(String nestIds
            , String startTime
            , String endTime
            ,Integer uavWhich
            , HttpServletResponse response);

    /**
     *  标记删除飞行记录
     * @param flightId
     * @return
     */
    RestRes deleteBatchRecord(String flightId);

    /**
     *  同步飞行记录
     * @param fightMissionDTO
     * @return
     */
    RestRes syncFlightMission(FlightMissionDTO fightMissionDTO);

    /**
     *  同步主方法
     * @param fightMissionDTO
     * @return
     */
    FlightMissionSyncAggVO syncFlightMissionMain(FlightMissionDTO fightMissionDTO);

}
