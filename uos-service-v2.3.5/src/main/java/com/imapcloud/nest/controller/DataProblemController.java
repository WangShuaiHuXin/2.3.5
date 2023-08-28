package com.imapcloud.nest.controller;


import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.DeleteDataDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TaskPhotoDTO;
import com.imapcloud.nest.service.DataProblemService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据-问题Controller
 *
 * @author zhengxd
 * @since 2021-06-16
 */
@RestController
@RequestMapping("/dataProblem")
public class DataProblemController {

    @Resource
    private DataProblemService dataProblemService;

    /**
     * 获取分析应用的 任务-有问题数据List
     * @param tagId 标签id
     * @param problemSourceStr 问题来源
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/task/problem/photo/list")
    public RestRes getTaskProblemPhotoList(Integer tagId, String problemSourceStr, String startTime, String endTime) {
        List<Integer> problemSourceList = null;
        if(problemSourceStr != null) {
            problemSourceList = Arrays.asList(problemSourceStr.split(",")).stream().map(e -> Integer.parseInt(e)).collect(Collectors.toList());
        }
        return dataProblemService.getTaskProblemPhotoList(tagId, problemSourceList, startTime, endTime);
    }

    @GetMapping("/hz/problem/photo/list")
    public RestRes getHZTaskProblemPhotoList(Integer problemSource, String startTime, String endTime) {
        return dataProblemService.getHZTaskProblemPhotoList(problemSource, startTime, endTime);
    }
    
    /**
     * 获取分析应用的 任务-有问题数据List
     * @param tagId 标签id
     * @param problemSourceStr 问题来源
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/task/problem/photo/list/electric")
    public RestRes getTaskProblemPhotoListElectric(Integer tagId, String problemSourceStr, String startTime, String endTime) {
        List<Integer> problemSourceList = null;
        if(problemSourceStr != null) {
            problemSourceList = Arrays.asList(problemSourceStr.split(",")).stream().map(e -> Integer.parseInt(e)).collect(Collectors.toList());
        }
        return dataProblemService.getTaskProblemPhotoListElectric(tagId, problemSourceList, startTime, endTime);
    }

    /**
     * 获取分析中台的 任务-问题数据List
     * @param tagId 标签id
     * @param problemSource 问题来源
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/task/photo/list")
    public RestRes getTaskPhotoList(Integer tagId, Integer problemSource, String startTime, String endTime) {
        return dataProblemService.getTaskPhotoList(tagId, problemSource, startTime, endTime);
    }

    /**
     * 获取缩图展示照片列表
     * @param missionRecordIds
     * @param tagIds
     * @param problemSource
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/photo/problem/list")
    public RestRes getPhotoProblemList(String missionRecordIds, String tagIds, Integer problemSource, String startTime, String endTime) {
        List<Integer> missionRecordIdList = changeStringToList(missionRecordIds);
        List<Integer> tagIdList = changeStringToList(tagIds);
        return dataProblemService.getPhotoProblemList(missionRecordIdList, tagIdList, problemSource, startTime, endTime);
    }

    /**
     * 下载、删除、重置、AI识别时获取照片id、名称、标签id使用
     * @param missionRecordIds
     * @param tagIds
     * @param problemSource
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/photoIdNameTagIdList")
    public RestRes getPhotoIdNameTagIdList(String missionRecordIds, String tagIds, Integer problemSource, String startTime, String endTime) {
        List<Integer> missionRecordIdList = changeStringToList(missionRecordIds);
        List<Integer> tagIdList = changeStringToList(tagIds);
        return dataProblemService.getPhotoIdNameTagIdList(missionRecordIdList, tagIdList, problemSource, startTime, endTime);
    }

    /**
     * 获取列表展示列表
     * @param pageNum
     * @param limit
     * @param missionRecordIds
     * @param tagIds
     * @param problemSource
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/photo/problem/page/list/{page}/{limit}")
    public RestRes getPhotoProblemPageList(@PathVariable("page") Integer pageNum, @PathVariable("limit") Integer limit,
                                           String missionRecordIds, String tagIds, Integer problemSource, String startTime, String endTime) {
        List<Integer> missionRecordIdList = changeStringToList(missionRecordIds);
        List<Integer> tagIdList = changeStringToList(tagIds);
        return dataProblemService.getPhotoProblemPageList(pageNum, limit, missionRecordIdList, tagIdList, problemSource, startTime, endTime);
    }

    /**
     * 批量解决问题
     * @param problemIdList
     * @return
     */
    @PostMapping("/solve/problem")
    public RestRes solveProblem(@RequestBody List<Integer> problemIdList) {
        return dataProblemService.solveProblem(problemIdList);
    }

    /**
     * 删除分析中台照片
     * @param deleteDataDTO
     * @return
     */
    @PostMapping("/delete")
    public RestRes deleteDataProblem(@RequestBody DeleteDataDTO deleteDataDTO) {
        return dataProblemService.deleteDataProblem(deleteDataDTO);
    }

    /**
     * 重置分析中台照片
     * @param deleteDataDTO
     * @return
     */
    @PostMapping("/reset")
    public RestRes reset(@RequestBody DeleteDataDTO deleteDataDTO) {
        return dataProblemService.resetDataProblem(deleteDataDTO);
    }

    /**
     * 获取历史照片
     * @param problemSource
     * @param photoId
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/get/history/photo")
    public RestRes getHistoryPhoto(Integer problemSource, Long photoId, Double range, String startTime, String endTime) {
        return dataProblemService.getHistoryPhotoInfo(problemSource, photoId, range, startTime, endTime);
    }


    /**
     * 将字符串id转化给List
     * @param ids
     * @return
     */
    private List<Integer> changeStringToList(String ids) {
        List<Integer> idList = null;
        if(ids != null) {
            idList = Arrays.asList(ids.split(",")).stream().map(e -> Integer.parseInt(e)).collect(Collectors.toList());
        }
        return idList;
    }

}

