package com.imapcloud.nest.controller;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.model.DataAirEntity;
import com.imapcloud.nest.model.SysTagEntity;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.reqDto.DataReqDto;
import com.imapcloud.nest.pojo.dto.reqDto.IdenDataDto;
import com.imapcloud.nest.service.DataCenterService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.PowerInfraredService;
import com.imapcloud.nest.v2.service.dto.in.PowerInfraredInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataScenePhotoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerInfraredOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dataCenter")
public class DataCenterController {
    @Autowired
    private DataCenterService dataCenterService;

    @Resource
    private ExecutorService executorService;

    @Resource
    private PowerInfraredService powerInfraredService;

    @GetMapping("/getTags")
    public RestRes getTags(Integer dataType, String startTime, String endTime) {
        List<SysTagEntity> sysTagEntityList = dataCenterService.getTags(dataType, startTime, endTime);
        Map map = new HashMap();
        map.put("tagList", sysTagEntityList);
        return RestRes.ok(map);
    }

    @GetMapping("/getData")
    public RestRes getData(Integer dataType, Integer missionRecordsId) {
        JSONArray jsonArray = dataCenterService.getData(dataType, missionRecordsId);
        Map map = new HashMap();
        map.put("data", jsonArray);
        map.put("missionRecordsId", missionRecordsId);
        return RestRes.ok(map);
    }

    @GetMapping("/getDataPage")
    public RestRes getDataPage(Integer page, Integer limit, Integer dataType, Integer missionRecordsId, String startTime, String endTime) {
        IPage iPage = dataCenterService.getDataPage(page, limit, dataType, missionRecordsId, startTime, endTime);
        Map map = new HashMap();
        map.put("data", iPage);
        map.put("missionRecordsId", missionRecordsId);
        return RestRes.ok(map);
    }

    @GetMapping("/total")
    public RestRes getTotal(String startTime, String endTime) {
        DataTotalDTO dataTotalDTO = dataCenterService.getTotal(startTime, endTime);
        Map map = new HashMap();
        map.put("dataTotal", dataTotalDTO);
        return RestRes.ok(map);
    }

    @GetMapping("/inspect")
    public RestRes getInspect(String startTime, String endTime) {
        Map map = dataCenterService.getInspect(startTime, endTime);
        return RestRes.ok(map);
    }


    @GetMapping("/getTask")
    public RestRes getTask(@RequestParam Map<String, Object> params, @RequestParam Integer tagId, @RequestParam Integer dataType, @RequestParam String name) {
        IPage<MissionRecordsDto> page = dataCenterService.getTask(params, tagId, dataType, name);
        Map map = new HashMap();
        map.put("page", page);
        return RestRes.ok(map);
    }

    @GetMapping("/getTaskMissions")
    public RestRes getTask(Integer tagId, Integer dataType, String name) {
        List<Map> mapList = dataCenterService.getTaskMissions(tagId, dataType, name);
        Map map = new HashMap();
        map.put("taskMissions", mapList);
        return RestRes.ok(map);
    }

    @GetMapping("/changeName")
    public RestRes changeName(Integer missionRecordsId, String name) {
        dataCenterService.changeName(missionRecordsId, name);
        return RestRes.ok();
    }

    @PostMapping("/saveTask")
    public RestRes saveTask(@RequestBody TaskDataDto taskDataDto) {
        dataCenterService.saveTask(taskDataDto);
        return RestRes.ok();
    }

    @PostMapping(value = "/copyTaskMission")
    public RestRes copyTaskMission(@RequestBody TaskMissionDto taskMissionDto) {
        dataCenterService.copyTaskMission(taskMissionDto);
        return RestRes.ok();
    }

    @PostMapping("/updateMission")
    public RestRes updateMission(@RequestBody TaskDataDto taskDataDto) {
        dataCenterService.updateMission(taskDataDto);
        return RestRes.ok();
    }

    @PostMapping("/delTask")
    public RestRes delTask(@RequestBody DataReqDto dataReqDto) {
        dataCenterService.delTask(dataReqDto);
        return RestRes.ok();
    }

    @PostMapping("/delData")
    public RestRes delData(@RequestBody DataReqDto dataReqDto) {
        dataCenterService.delData(dataReqDto);
        return RestRes.ok();
    }

    /**
     * 照片的推送分析
     * @param idenDataDto
     * @return
     */
    @PostMapping("/push")
    public RestRes push(@RequestBody IdenDataDto idenDataDto) {
        DataScenePhotoOutDTO.PushOut pair = dataCenterService.push(idenDataDto);
        Map<String, Object> map = Maps.newHashMap();
        map.put("failedList", pair.getFailedList());
        map.put("successNum", pair.getSuccessNum());
        map.put("ignoredNum", pair.getIgnoredNum());
        return RestRes.ok(map);
    }

    /**
     * 点云正射的推送分析
     * @param idenDataDto
     * @return
     */
    @PostMapping("/pushData")
    public RestRes pushData(@RequestBody IdenDataDto idenDataDto) {
        dataCenterService.pushData(idenDataDto);
        return RestRes.ok();
    }

    /**
     * 气体截图
     *
     * @param dataAirDto
     * @return
     */
    @PostMapping("/saveDataAir")
    public RestRes saveDataAir(@RequestBody DataAirDto dataAirDto) {
        dataCenterService.saveDataAir(dataAirDto);
        return RestRes.ok();
    }

    @GetMapping("/relatedDataRecordIds")
    public RestRes relatedMissionRecordIds(Integer dataType, Integer dataId, Integer missionRecordId, String photoIds) {
        List<Integer> photoIdList = null;
        if (photoIds != null) {
            photoIdList = Arrays.asList(photoIds.split(",")).stream().map(e -> Integer.parseInt(e)).collect(Collectors.toList());
        }
        dataCenterService.relatedMissionRecordIds(dataType, dataId, missionRecordId, photoIdList);
        return RestRes.ok();
    }

    @GetMapping("/deletedRelated")
    public RestRes deletedRelatedMissionRecordIds(Integer dataId, Integer dataType) {
        dataCenterService.deletedRelatedMissionRecordIds(dataId, dataType);
        return RestRes.ok();
    }

    @PostMapping("/importAirExcel")
    public RestRes importAirExcel(MultipartFile multipartFile, Integer missionRecordsId) {
        dataCenterService.importAirExcel(multipartFile, missionRecordsId);
        return RestRes.ok();
    }

    @GetMapping("/delTag")
    public RestRes delTag(Integer tagId) {
        Boolean b = dataCenterService.delTag(tagId);
        return RestRes.ok("isTag", b);
    }

    @GetMapping("/getDataAir")
    public RestRes getDataAir(Integer missionRecordsId) {
        List<DataAirEntity> list = dataCenterService.getDataAir(missionRecordsId);
        Map map = new HashMap() {
            {
                put("list", list);
            }
        };
        return RestRes.ok(map);
    }

    @PostMapping("/deletedDataAir")
    public RestRes deletedDataAir(@RequestBody List<Integer> dataAirIdList) {
        dataCenterService.deletedDataAir(dataAirIdList);
        return RestRes.ok();

    }

    /**
     * 污染网格重命名
     */
    @GetMapping("/pollutionGridRename")
    public RestRes pollutionGridRename(Integer pollutionGridId, String name) {
        dataCenterService.pollutionGridRename(pollutionGridId, name);
        return RestRes.ok();
    }

    @GetMapping("/delServerFile")
    public RestRes delServerFile(String startTime,String endTime){
        dataCenterService.delServerFile(startTime,endTime);
        return RestRes.ok();
    }

    @GetMapping("/getServerSize")
    public RestRes getServerSize(){
        Map map = dataCenterService.getServerSize();
        return RestRes.ok(map);
    }
}
