package com.imapcloud.nest.controller;


import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.common.annotation.NestId;
import com.imapcloud.nest.common.annotation.NestUUID;
import com.imapcloud.nest.common.annotation.SysLogIgnoreParam;
import com.imapcloud.nest.enums.ProblemStatusEnum;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.MissionPhotoTypeRelEntity;
import com.imapcloud.nest.pojo.dto.GetPhotoParamDto;
import com.imapcloud.nest.pojo.dto.MeterReadDTO;
import com.imapcloud.nest.pojo.dto.MissionDto;
import com.imapcloud.nest.pojo.dto.reqDto.DefectMonthListReqDto;
import com.imapcloud.nest.pojo.dto.reqDto.MissionPhotosReqDto;
import com.imapcloud.nest.pojo.dto.reqDto.RecordDto;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.service.MissionPhotoTypeRelService;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.sdk.pojo.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Slf4j
@RestController
@ApiSupport(author = "zhongtaigbao@geoai.com", order = 2)
@Api(value = "数据管理-同步", tags = "数据管理-同步")
@RequestMapping("/missionPhoto")
public class MissionPhotoController {

    @Autowired
    private MissionPhotoService missionPhotoService;

    @Autowired
    private MissionPhotoTypeRelService missionPhotoTypeRelService;

    /**
     * Web端 同步数据源f
     */
    @PostMapping("/result/getPhoto")
    @ResponseBody
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "批量数据同步", notes = "批量数据同步")
    public RestRes getPhoto(@RequestBody @Valid GetPhotoParamDto getPhotoParamDto, BindingResult br,HttpServletRequest request) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        String env="zh-CN";
        String header = request.getHeader("Accept-Language");
        if(header.contains("en")){
            env="en-US";
        }
//        return missionPhotoService.batchTranData(getPhotoParamDto.getNestId(), getPhotoParamDto.getRecordIdList());
        return missionPhotoService.synDataList(getPhotoParamDto.getNestId(), getPhotoParamDto.getRecordIdList());
    }

    /**
     * Cps合并后操作，调用回写动作
     */
    @PostMapping("/result/uploadPhotoCps")
    @ResponseBody
    @SysLogIgnoreParam(value = "上传文件接口，参数String mediaFile,MultipartFile fileData")
    public RestRes uploadPhotoCps(@RequestBody String mediaFile) {
        log.info("#MissionPhotoController.uploadPhotoCps# mediaFile={}", mediaFile);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("uploadPhotoCps");
        RestRes restRes = missionPhotoService.uploadPhotoCps(mediaFile);
        stopWatch.stop();
        log.info("uploadPhotoCps -> {} , mediaFile -> {} " ,stopWatch.prettyPrint() , mediaFile);
        return restRes;
    }

    @PostMapping("/result/uploadSrt")
    @ResponseBody
    public RestRes uploadSrt(String execMissionID, @RequestParam MultipartFile fileData) {
        return missionPhotoService.uploadSrt(execMissionID, fileData);
    }

    /**
     * 获取机巢成果的缩略图列表
     *
     * @param params
     * @param missionRecordId
     * @return
     */
    @GetMapping("/result/getThumbnailPage")
    public RestRes getThumbnailPage(@RequestParam Map<String, Object> params, @RequestParam Integer missionRecordId, @RequestParam Integer airLineId) {
        return missionPhotoService.getThumbnailPage(params, missionRecordId, airLineId);
    }

    @NestCodeRecord(Constant.MEDIA_MANAGER_2_C11)
    @GetMapping("/result/stopGetMedia")
    public RestRes stopGetMedia(@RequestParam @NestUUID String nestUuid, @RequestParam Integer missionRecordId) {
        return missionPhotoService.stopGetMedia(nestUuid, missionRecordId);
    }

    @NestCodeRecord(Constant.MEDIA_MANAGER_2_C11)
    @PostMapping("/cancel/batch/tran/data/{nestId}/{recordId}")
    public RestRes cancelBatchTranData(@PathVariable @NestId String nestId, @PathVariable Integer recordId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return missionPhotoService.cancelBatchTranData(nestId, recordId);
    }

    /**
     * 获取移动终端的任务架次记录分页列表
     */
    @PostMapping("/result/app/mission/record/page/{page}/{limit}")
    public RestRes getAppMissionRecordPage(@PathVariable("page") Integer pageNum, @PathVariable("limit") Integer limit, @RequestBody MissionDto missionDto) {
        PageUtils page = missionPhotoService.getAppMissionRecordPage(pageNum, limit, missionDto.getAppId(), missionDto.getTaskName());
        Map<String, Object> map = new HashMap<>(2);
        map.put("page", page);
        return RestRes.ok(map);
    }

    @GetMapping("/result/update/photo/name")
    public RestRes updatePhotoName(Long id, String photoName) {
        return missionPhotoService.updatePhotoName(id, photoName);
    }

    @GetMapping("/result/airline/photo/rename")
    public RestRes updateName(Integer airLineId, Integer missionRecordId) {
        return missionPhotoService.updateAirlinePhotoName(airLineId, missionRecordId, false);
    }

    /**
     * 获取缺陷识别的数目统计
     */
    @GetMapping("/result/getDefectStatistics")
    public RestRes getDefectStatistics(String startTime, String endTime) {
        return missionPhotoService.getDefectStatistics(startTime, endTime);

    }


    @PostMapping("/result/deleteMissionPhoto")
    public RestRes deleteMissionPhoto(@RequestBody List<MissionPhotosReqDto> reqDtoList) {
        return missionPhotoService.deleteMissionPhoto(reqDtoList);
    }

//    /**
//     * 下载
//     * @deprecated 2.2.3，旧版缺陷识别导出，将在后续版本删除
//     */
//    @Deprecated
//    @GetMapping("/result/downloadDefect")
//    public void downloadDefect(String idList, HttpServletResponse response, HttpServletRequest request) {
//        missionPhotoService.downloadDefect(idList, response, request);
//    }
//
//    /**
//     * 下载
//     * @deprecated 2.2.3，旧版缺陷识别巡检报告导出，将在后续版本删除
//     */
//    @Deprecated
//    @GetMapping("/result/downloadDefectInspection")
//    public void downloadDefectInspection(String idList, HttpServletResponse response, HttpServletRequest request) {
//        missionPhotoService.downloadDefectInspection(idList, response, request);
//    }

    @PostMapping("/result/updatePhotoDefectStatus")
    public RestRes updatePhotoDefectStatus(@RequestBody MissionPhotosReqDto reqDto) {
        List<Integer> photoIds = reqDto.getPhotoIds();
        Integer defectStatus = reqDto.getDefectStatus();
        if (defectStatus == null)
            defectStatus = ProblemStatusEnum.DEFECT_REMOVED.getCode();
        return missionPhotoService.updatePhotoDefectStatus(photoIds, defectStatus);
    }

    @GetMapping("/defect/month/list")
    public RestRes getDefectMonthList(DefectMonthListReqDto defectMonthListReqDto) {
        return missionPhotoService.getDefectMonthList(defectMonthListReqDto);
    }


    /*
        以下是分析中台-表记读数
     */
    @GetMapping("/device/list")
    public RestRes getDeviceList(Integer tagId, String type, Integer defectStatus, String startTime, String endTime, Integer flag) {
        List<Integer> typeList = null;
        if (type != null) {
            typeList = Arrays.asList(type.split(",")).stream().map(e -> Integer.parseInt(e)).collect(Collectors.toList());
        }
        return missionPhotoService.getDeviceList(tagId, typeList, defectStatus, startTime, endTime, flag);
    }

    /*
    	以下是数据分析-变电站-按架次分组
     */
    @GetMapping("/mission/list")
    public RestRes getMissionList(Integer tagId, Integer type, Integer defectStatus, String startTime, String endTime) {
        return missionPhotoService.getMissionList(tagId, type, defectStatus, startTime, endTime);
    }

    @GetMapping("/device/test")
    public RestRes getDeviceTest(Integer tagId, Integer type, String startTime, String endTime, Integer flag) {
        missionPhotoService.getAllStationDefectPhotoDTO(tagId, type, startTime, endTime, flag);
        return RestRes.ok();
    }

    @PostMapping("/meter/photo/list/{page}/{limit}")
    public RestRes getPhotoListPage(Integer tagId, String photoName, @PathVariable("page") Integer pageNum, @PathVariable("limit") Integer pageSize) {
        return missionPhotoService.getPhotoListPage(tagId, photoName, null, null, pageNum, pageSize);
    }

    @GetMapping("/meter/photo/list")
    public RestRes getPhotoListPageMeter(Integer tagId, String name, String missionRecordsId, Integer defectStatus, Integer page, Integer limit) {
        return missionPhotoService.getPhotoListPage(tagId, name, missionRecordsId, defectStatus, page, limit);
    }

    @PostMapping("/meter/number/list")
    public RestRes getNumberList(@RequestBody MeterReadDTO meterReadDTO) {
        return missionPhotoService.getNumberList(meterReadDTO);
    }

    @GetMapping("/getThreshold")
    public RestRes getThreshold() {
        return RestRes.ok(missionPhotoService.getThreshold());
    }

    @GetMapping("/setThreshold")
    public RestRes setThreshold(String value) {
        return missionPhotoService.setThreshold(value);
    }
    /*
        以上是分析中台-表计读数
     */


    /*
        以下是首页
     */
    @GetMapping("/total/photo/and/video")
    public RestRes getTotalPhotoAndVideo(String startTime, String endTime) {
        return missionPhotoService.getTotalPhotoAndVideo(startTime, endTime);
    }

    @GetMapping("/total/nest/photo/by/tag")
    public RestRes getTotalNestPhotoByTag(String startTime, String endTime, String nestId) {
        return missionPhotoService.getTotalNestPhotoByTag(startTime, endTime, nestId);
    }

    @GetMapping("/total/nest/video/by/tag")
    public RestRes getTotalNestVideoByTag(String startTime, String endTime, String nestId) {
        return missionPhotoService.getTotalNestVideoByTag(startTime, endTime, nestId);
    }

    @GetMapping("total/nest/photo/list/{page}/{limit}")
    public RestRes getPhotoByNestPageList(String startTime, String endTime, String nestId, String appId, @PathVariable("page") Integer pageNum, @PathVariable("limit") Integer pageSize) {
        return missionPhotoService.getPhotoByNestPageList(startTime, endTime, nestId, appId, pageNum, pageSize);
    }

    @GetMapping("total/nest/video/list/{page}/{limit}")
    public RestRes getVideoByNestPageList(String startTime, String endTime, String nestId, String appId, @PathVariable("page") Integer pageNum, @PathVariable("limit") Integer pageSize) {
        return missionPhotoService.getVideoByNestPageList(startTime, endTime, nestId, appId, pageNum, pageSize);
    }


    @PostMapping("/resetPhotoRecord")
    public RestRes resetPhotoRecord(@RequestBody RecordDto recordDto) {
        return missionPhotoService.resetPhotoRecord(recordDto);
    }

//    /**
//     * @deprecated 2.2.3，旧版电力分析缺陷识别图片下载，已废弃将在后续版本删除
//     */
//    @Deprecated
//    @GetMapping("/downloadPhoto")
//    public void downloadPhoto(Integer recordType, String photoIds, HttpServletResponse response) {
//        missionPhotoService.downloadPhoto(recordType, photoIds, response);
//    }

    @PostMapping("/handleMissionTagAndMissionType")
    public void handleAirLineJsonNoIdentificationType() {
        List<MissionPhotoTypeRelEntity> list = missionPhotoTypeRelService.list();
        List<Integer> idList = list.stream().map(MissionPhotoTypeRelEntity::getId).collect(Collectors.toList());
        List<MissionPhotoTypeRelEntity> distinctList = list.stream().distinct().collect(Collectors.toList());
        List<Integer> distinctIdList = distinctList.stream().map(MissionPhotoTypeRelEntity::getId).collect(Collectors.toList());
        idList.removeAll(distinctIdList);

        List<MissionPhotoTypeRelEntity> collect = list.stream().filter(mp -> idList.contains(mp.getId())).map(mp -> {
            mp.setDeleted(false);
            return mp;
        }).collect(Collectors.toList());
//        missionPhotoTypeRelService.updateBatchById(collect);
    }


    @GetMapping("/getAllPhotoByRecordsId")
    public RestRes getAllPhotoByMissionRecordsId(Integer missionRecordsId) {
        List<MissionPhotoEntity> photoEntityList = missionPhotoService.getAllPhotoByMissionRecordsId(missionRecordsId);
        Map map = new HashMap(2);
        map.put("list", photoEntityList);
        return RestRes.ok(map);
    }

    @PostMapping("/check/media/status/{nestId}")
    public RestRes checkNestMediaStatus(@PathVariable String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return missionPhotoService.checkNestMediaStatus(nestId);
    }

    @PostMapping("/tran/data/check/{nestId}/{uavWhich}")
    public RestRes tranDataCheck(@PathVariable String nestId,@PathVariable Integer uavWhich) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return missionPhotoService.tranDataCheck(nestId,uavWhich);
    }

}

