package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.utils.RestRes;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RequestMapping("v2/test/")
@Api(value = "API测试", tags = "API测试")
@RestController
public class TestController {

    @Resource
    private MissionPhotoService missionPhotoService;



    @GetMapping("pushAnalysis2")
    public Result<Object> pushAnalysis2(Integer airLineId, Integer missionRecordId) {

        RestRes restRes = missionPhotoService.pushAnalysis2(airLineId, missionRecordId, true);
        return Result.ok(restRes);
    }
}
