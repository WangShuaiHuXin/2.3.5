package com.imapcloud.nest.controller;


import com.imapcloud.nest.service.MissionAirService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-04-21
 */
@RestController
@RequestMapping("/missionAir")
public class MissionAirController {
    @Autowired
    private MissionAirService missionAirService;

    @GetMapping("/getDataByMissionId")
    public RestRes getDataByMissionId(Integer missionRecordsId){
        Map map = missionAirService.getDataByMissionId(missionRecordsId);
        return RestRes.ok(map);
    }
}

