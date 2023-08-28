package com.imapcloud.nest.schedule;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.imapcloud.nest.pojo.dto.flightMission.FlightMissionDTO;
import com.imapcloud.nest.pojo.vo.FlightMissionSyncAggVO;
import com.imapcloud.nest.service.FlightMissionService;
import com.imapcloud.nest.utils.LocalDateUtil;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.dto.out.NestSimpleOutDTO;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName FlightMissionSchedule.java
 * @Description FlightMissionSchedule
 * @createTime 2022年03月24日 14:22:00
 */
@Slf4j
@Component
@EnableScheduling
public class FlightMissionSchedule {


    @Resource
    private BaseNestService baseNestService;

    @Resource
    private FlightMissionService flightMissionService;

    @Scheduled(cron = "0 0 0 * * ? ")
    public void scheduleSyncFlightMission() {
        //查询所有基站。
        log.info("【scheduleSyncFlightMission】定时任务执行开始:{}", DateUtil.now());
        List<NestSimpleOutDTO> nestSimpleOutDTOS = baseNestService.listAllNestInfos();

        DateTime endTime = DateUtil.date(), startTime = DateUtil.offsetDay(endTime, -1);

        log.info("【scheduleSyncFlightMission】定时任务执行，开始遍历基站，一共:{}台", nestSimpleOutDTOS.size());
        for(NestSimpleOutDTO nestSimpleOutDTO : nestSimpleOutDTOS){
            String nestId = nestSimpleOutDTO.getId();
            Integer type = nestSimpleOutDTO.getType();
            //G503, 一巢三机，需要遍历3次
            FlightMissionDTO flightMissionDTO = FlightMissionDTO.builder()
                    .nestId(nestId)
                    .startTime(LocalDateUtil.dateTimeToLocalDateTime(startTime))
                    .endTime(LocalDateUtil.dateTimeToLocalDateTime(endTime))
                    .build();
            if(NestTypeEnum.G503.getValue() == type){
                for (int i = 1; i <= 3 ; i++){
                    flightMissionDTO.setUavWhich(i);
                    this.syncFlightMission(flightMissionDTO,nestId);
                }
            }else{
                this.syncFlightMission(flightMissionDTO,nestId);
            }
        }
    }

    public void syncFlightMission(FlightMissionDTO flightMissionDTO , String nestId){
        log.info("【scheduleSyncFlightMission】遍历nestId->{}",nestId);
        FlightMissionSyncAggVO flightMissionSyncAggVO = this.flightMissionService.syncFlightMissionMain(flightMissionDTO);
        log.info("【scheduleSyncFlightMission】遍历nestId->{}结束，返回结果：{},{}",nestId,flightMissionSyncAggVO.getSuccess(),flightMissionSyncAggVO.getMsg());
    }
}
