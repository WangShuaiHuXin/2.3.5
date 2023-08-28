package com.imapcloud.nest.controller;


import com.alibaba.fastjson.JSONObject;
import com.imapcloud.nest.common.annotation.SysLogIgnoreParam;
import com.imapcloud.nest.service.MissionVideoService;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-08-14
 */
@RestController
@RequestMapping("/missionVideo")
public class MissionVideoController {
    @Autowired
    private MissionVideoService missionVideoService;

    @SysLogIgnoreParam
    @GetMapping("/result/getVideoPage")
    public RestRes getVideoPage(@RequestParam Map<String, Object> params, @RequestParam Integer missionRecordId) {
        PageUtils page = missionVideoService.getVideoPage(params, missionRecordId);
        Map<String, Object> map = new HashMap<>(2);
        map.put("page", page);
        return RestRes.ok(map);
    }

    @GetMapping("/result/update/video/name")
    public RestRes updateVideoName(Long id, String videoName) {
        return missionVideoService.updateVideoName(id, videoName);
    }

    @PostMapping("/create/relay")
    public RestRes createRelay(Integer mode, String inUrl, String outUrl, Boolean disableAudio, String host, String comment) {
        return missionVideoService.createRelay(mode, inUrl, outUrl, disableAudio, host, comment);
    }

    @GetMapping("/updateMissionVideoLatLng")
    public RestRes updateMissionVideoLatLng(Integer videoId){
        missionVideoService.updateMissionVideoLatLng(videoId);
        return RestRes.ok();
    }

    @PostMapping("/delVideo")
    public RestRes delVideo(@RequestBody List<Integer> missionRecordsIds){
        missionVideoService.deleteVideo(missionRecordsIds);
        return RestRes.ok();
    }

    @GetMapping("/delSrt")
    public RestRes delSrt(Integer videoId){
        missionVideoService.delSrt(videoId);
        return RestRes.ok();
    }


    @GetMapping("/getInspectLatLng")
    public void getInspectLatLng(Long videoId){
        missionVideoService.getInspectLatLng(videoId);
    }
}

