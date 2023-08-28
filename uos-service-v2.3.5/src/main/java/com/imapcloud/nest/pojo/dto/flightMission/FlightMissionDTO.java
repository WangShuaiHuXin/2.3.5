package com.imapcloud.nest.pojo.dto.flightMission;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName FlightMissionDTO.java
 * @Description FightMissionDTO
 * @createTime 2022年03月24日 10:30:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightMissionDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 基站Id
     */
    @ApiModelProperty(value = "基站id", position = 1, example = "")
    @NotNull(message = "基站Id，不能为空")
    private String nestId;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", position = 1, example = "")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @NotNull(message = "开始时间，不能为空")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", position = 1, example = "")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @NotNull(message = "结束时间，不能为空")
    private LocalDateTime endTime;

    /**
     * 切换无人机
     */
    @ApiModelProperty(value = "无人机标识", position = 1, example = "")
    @NotNull(message = "无人机序号，不能为空")
    private Integer uavWhich;


}
