package com.imapcloud.nest.controller;


import com.baomidou.mybatisplus.extension.api.R;
import com.imapcloud.nest.model.StationInfraredRecordRectangleEntity;
import com.imapcloud.nest.pojo.dto.AIRedRecognitionDto;
import com.imapcloud.nest.pojo.dto.reqDto.RecordDto;
import com.imapcloud.nest.pojo.vo.StationInfraredRecordRectangleVO;
import com.imapcloud.nest.service.StationInfraredRecordService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 变电站的设备出现的红外测温记录 前端控制器
 * </p>
 *
 * @author hc
 * @since 2020-12-29
 */
@RestController
@RequestMapping("/stationInfraredRecord")
public class StationInfraredRecordController {

    @Autowired
    private StationInfraredRecordService stationInfraredRecordService;


    /**
     * 根据标签id获取分组图片列表
     * @param tagId
     * @return
     */
    @GetMapping("/getRecordByTagId")
    public RestRes getRecordByTagId(Integer tagId){
        return stationInfraredRecordService.queryRecordByTagId(tagId);
    }

    /**
     * 根据标签id获取所有图片列表
     * @return
     */
    @GetMapping("/getPhotosByPhotoName")
    public RestRes getPhotosByDeviceId(@RequestParam Map<String, Object> params){
        return stationInfraredRecordService.queryPhotosByPhotoName(params);
    }


    /**
     * 获取一个点的温度或者矩形温度
     * @return
     */
    @PostMapping("/getTem")
    public RestRes getTem(@RequestBody StationInfraredRecordRectangleEntity stationInfraredRecordRectangleEntity){
        return stationInfraredRecordService.getTem(stationInfraredRecordRectangleEntity);
    }

    /**
     * 红外手动标记保存
     * @return
     */
    @PostMapping("/saveInfrared")
    public RestRes save(@RequestBody StationInfraredRecordRectangleVO stationInfraredRecordRectangleVO){
        return stationInfraredRecordService.saveInfrared(stationInfraredRecordRectangleVO);
    }

    /**
     * 红外修改状态（0/null未识别，1无人问题，2有问题，3已消缺）
     * @return
     */
    @PostMapping("/updateInfraredState")
    public RestRes save(@RequestBody RecordDto recordDto){
        return stationInfraredRecordService.updateInfraredState(recordDto);
    }

    /**
     * 曲线图
     * @param tagId
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/getPicByMonth")
    public RestRes getPicByMonth(Integer tagId,String photoName,String startTime,String endTime){
        return stationInfraredRecordService.queryPicByMonth(tagId,photoName,startTime,endTime);
    }

    /**
     * 获取ai识别图片
     * @return
     */
    @PostMapping("/getAiRecognitionPic")
    public RestRes getAiRecognitionPic(@RequestBody AIRedRecognitionDto aiRedRecognitionDto){
        return stationInfraredRecordService.getAiRecognitionPic(aiRedRecognitionDto);
    }

    /**
     * 获取列表
     * @return
     */
    @GetMapping("/getThreshold")
    public RestRes getThreshold(){
        Map map = stationInfraredRecordService.getThreshold();
        return RestRes.ok(map);
    }

    @GetMapping("/setThreshold")
    public RestRes setThreshold(String value){
        return stationInfraredRecordService.setThreshold(value);
    }
}

