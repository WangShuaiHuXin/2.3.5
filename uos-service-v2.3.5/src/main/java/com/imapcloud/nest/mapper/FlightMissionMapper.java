package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.FlightMissionEntity;
import com.imapcloud.nest.pojo.vo.FlightMissionAggVO;
import com.imapcloud.nest.pojo.vo.FlightMissionExportVO;
import com.imapcloud.nest.pojo.vo.FlightMissionVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author double
 * @since 2022-03-22
 */
public interface FlightMissionMapper extends BaseMapper<FlightMissionEntity> {

    /**
     *  分页查询
     * @param page
     * @param startTimeDate
     * @param endTimeDate
     * @return
     */
    IPage<FlightMissionVO> getFlightMissionPage(@Param("page") IPage<FlightMissionVO> page
                                                , @Param("nestId") String nestId
                                                , @Param("startTimeDate") Date startTimeDate
                                                , @Param("endTimeDate") Date endTimeDate
                                                , @Param("uavWhich") Integer uavWhich);

    /**
     * 获取AGGVO
     * @param nestId
     * @return
     */
    FlightMissionAggVO getTotalFlightMissionVO(@Param("nestId") String nestId);

    /**
     *
     * @param nestIds
     * @param startTimeDate
     * @param endTimeDate
     * @return
     */
    List<FlightMissionExportVO> getExportVO(@Param("nestIds") List<String> nestIds
            , @Param("startTimeDate") Date startTimeDate
            , @Param("endTimeDate") Date endTimeDate
            , @Param("uavWhich") Integer uavWhich);


}
