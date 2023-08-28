package com.imapcloud.nest.controller;

import com.alibaba.fastjson.JSON;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.annotation.SysLogIgnoreResult;
import com.imapcloud.nest.pojo.dto.reqDto.MissionRecordsReqDto;
import com.imapcloud.nest.service.MissionRecordsService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.mongo.pojo.AppAirMsgEntity;
import com.imapcloud.nest.utils.mongo.pojo.MongoPage;
import com.imapcloud.nest.utils.mongo.pojo.NestAndAirEntity;
import com.imapcloud.nest.utils.mongo.service.MongoNestAndAirService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 架次记录表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@RestController
@RequestMapping("/missionRecords")
public class MissionRecordsController {


    @Autowired
    private MissionRecordsService missionRecordsService;

    @Autowired
    private MongoNestAndAirService mongoNestAndAirService;

    /*删除掉架次，对应的图片要做对应的处理*/
    @PostMapping("/batchDeleteMissionRecords")
    public RestRes batchDeleteMissionRecords(@RequestBody @Valid MissionRecordsReqDto missionRecordsReqDto) {
        return missionRecordsService.batchUpdateMissionRecordsBy(missionRecordsReqDto);
    }

    @PostMapping("/batchClearMissionRecords")
    public RestRes batchClearMissionRecords(@RequestBody @Valid MissionRecordsReqDto missionRecordsReqDto) {
        if (missionRecordsReqDto.getDataType() == null) {
            return RestRes.errorParam("数据类型不能为空");
        }
        return missionRecordsService.batchClearMissionRecords(missionRecordsReqDto);
    }

    @GetMapping("/total/list/{page}/{limit}")
    public RestRes getTotalRecord(String startTime, String endTime, @PathVariable("page") Integer pageNum, @PathVariable("limit") Integer pageSize) {
        return missionRecordsService.getMissionRecordPageList(startTime, endTime, pageNum, pageSize);
    }

    @GetMapping("/total/miles/and/time")
    public RestRes getTotalMilesAndTime(Integer type) {
        Map totalRecord = missionRecordsService.getTotalMilesAndTime(type);
        return RestRes.ok(totalRecord);
    }

    @GetMapping("/total/inspectTimes")
    public RestRes getTotalInspectTimes(String startTime, String endTime) {
        Map totalRecord = missionRecordsService.getTotalInspectTimes(startTime, endTime);
        return RestRes.ok(totalRecord);
    }


    @GetMapping("/inspectStatistics")
    public RestRes inspectStatistics() {
        return missionRecordsService.missionRecordsStatistics();
    }

    @PostMapping("/getInspectStatisticsBy")
    public RestRes inspectStatistics(@RequestBody MissionRecordsReqDto missionRecordsReqDto) {
        String nestId = missionRecordsReqDto.getNestId();
        String startTime = missionRecordsReqDto.getStartTime();
        String endTime = missionRecordsReqDto.getEndTime();
        return missionRecordsService.getInspectStatisticsBy(nestId, startTime, endTime);
    }

    /**
     * 查询基站和无人机日志
     *
     * @param recordsId
     * @return
     */
    @SysLogIgnoreResult
    @GetMapping("/list/nest/drone/logs/{recordsId}")
    public RestRes listNestAndDroneLogs(@PathVariable Integer recordsId) {
        if (recordsId != null) {
            List<NestAndAirEntity> nestAndAirEntityList = mongoNestAndAirService.findByMissionRecordsId(recordsId);
            Map<String, Object> data = new HashMap<>(2);
            data.put("logs", nestAndAirEntityList);
            return RestRes.ok(data);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETERS_CANNOT_EMPTY.getContent()));
    }

    @SysLogIgnoreResult
    @GetMapping("/list/nest/drone/logs/page/{recordsId}/{currentPage}/{pageSize}")
    public RestRes listNestAndDroneLogsPage(@PathVariable Integer recordsId, @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        if (Objects.nonNull(recordsId) && Objects.nonNull(currentPage) && Objects.nonNull(pageSize)) {
            MongoPage<NestAndAirEntity> page = mongoNestAndAirService.findByMissionRecordsIdPage(recordsId, currentPage, pageSize);
            Map<String, Object> data = new HashMap<>(2);
            data.put("records", page.getRecords());
            data.put("total", page.getTotal());
            data.put("pages", page.getPages());
            data.put("pageSize", page.getPageSize());
            data.put("currentPage", page.getCurrentPage());
            return RestRes.ok(data);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETERS_CANNOT_EMPTY.getContent()));
    }


    @GetMapping("/list/app/air/msg/{recordsId}")
    public RestRes listAppAirMsg(@PathVariable Integer recordsId) {
        if (recordsId != null) {
            List<AppAirMsgEntity> appAirMsgList = mongoNestAndAirService.findAppMsgByMissionRecordIdsId(recordsId);
            Map<String, Object> data = new HashMap<>(2);
            data.put("logs", appAirMsgList);
            return RestRes.ok(data);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }
}

