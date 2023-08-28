package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * app任务航线DTO
 *
 * @author: zhengxd
 * @create: 2020/12/9
 **/
@Data
public class
AppMissionAircraftDTO {
    private Integer missionId;
    private String missionName;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
    private Integer deleted;
}
