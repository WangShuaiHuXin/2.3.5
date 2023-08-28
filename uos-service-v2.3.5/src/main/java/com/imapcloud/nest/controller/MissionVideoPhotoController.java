package com.imapcloud.nest.controller;


import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.MissionVideoPhotoEntity;
import com.imapcloud.nest.service.MissionVideoPhotoService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 视屏抽帧的图片 前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-06-30
 */
@RestController
@RequestMapping("/missionVideoPhoto")
public class MissionVideoPhotoController {
    @Autowired
    private MissionVideoPhotoService missionVideoPhotoService;

    @GetMapping("/extractPhoto")
    public RestRes extractPhoto(Integer id, Integer seconds, String extractTime, String videoName) {
        boolean result = missionVideoPhotoService.extractPhoto(id, seconds, extractTime, videoName);
        return RestRes.ok();
    }

    @GetMapping("/getPhotos")
    public RestRes getAllPhotos(Integer missionRecordsId) {
        List<Map> list = missionVideoPhotoService.getAllPhotos(missionRecordsId);
        Map map = new HashMap<>();
        map.put("list", list);
        return RestRes.ok(map);
    }

    @GetMapping("/cancel")
    public RestRes cancel(Integer id, String extractTime) {
        missionVideoPhotoService.cancel(id, extractTime);
        return RestRes.ok();
    }

    @PostMapping("/del")
    public RestRes del(@RequestBody List<Integer> ids) {
        boolean del = missionVideoPhotoService.del(ids);
        if (del) {
            return RestRes.ok();
        } else {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DELETE_FAILED.getContent()));
        }
    }

    @GetMapping("/extractSrt")
    public RestRes extractSrt(Integer videoId) {
        boolean b = missionVideoPhotoService.extractSrt(videoId);
        if (b) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_EXECUTE_FFMPEG_COMMAND.getContent()));
        } else {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_VIDEO_DOES_NOT_HAVE_A_TRACK_FILE.getContent()));
        }
    }
}

