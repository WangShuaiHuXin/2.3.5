package com.imapcloud.nest.pojo.vo;

import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.data.entity.FlightMissionPageEntity;
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
public class FlightMissionSyncAggVO {

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
     * 是否成功
     */
    private Boolean success;

    /**
     * 消息
     */
    private String msg;

    /**
     * res 记录
     */
    private MqttResult<FlightMissionPageEntity> res;

    /**
     * page记录
     */
    private List<FlightMissionPageEntity> pageEntityList;

    /**
     * 批量
     */
    private List<FlightMissionVO> flightMissionVOList;

    /**
     * uavWhich
     */
    private Integer uavWhich;

    /**
     * 基站Id
     */
    private String nestId;
}
