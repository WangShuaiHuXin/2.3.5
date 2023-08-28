package com.imapcloud.nest.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName FlightMissionAggVO.java
 * @Description FlightMissionAggVO
 * @createTime 2022年03月24日 10:50:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain=true)
public class FlightMissionAggVO {

    /**
     * 总里程
     */
    private String totalDistance;

    /**
     * 总时长
     */
    private String totalDate;

    /**
     * 总次数
     */
    private String totalNum;

    /**
     * 当前页总里程
     */
    private String pageTotalDistance;

    /**
     * 当前页总时长
     */
    private String pageTotalDate;

    /**
     * 当前页总次数
     */
    private String pageTotalNum;

    /**
     * 批量
     */
    private List<FlightMissionVO> flightMissionVOList;
}
