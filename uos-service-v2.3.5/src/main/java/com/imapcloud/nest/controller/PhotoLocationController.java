package com.imapcloud.nest.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.PhotoLocationEntity;
import com.imapcloud.nest.service.PhotoLocationService;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhengxd
 * @since 2021-06-02
 */
@RestController
@RequestMapping("/photoLocation")
public class PhotoLocationController {

    @Autowired
    private PhotoLocationService photoLocationService;

    @GetMapping("/list/{page}/{limit}")
    public RestRes getPageList(@PathVariable("page") Integer pageNum, @PathVariable("limit") Integer limit, String ids) {
        List<String> photoIdList = Arrays.asList(ids.split(","));
        return photoLocationService.getPageList(pageNum, limit, photoIdList);
    }

    @PostMapping("/upload/photo/location")
    public RestRes insertOrUpdate(PhotoLocationEntity photoLocationEntity,String fileName,String filePath) {
        return photoLocationService.insertOrUpdate(photoLocationEntity,fileName,filePath);
    }

    /**
     * 导出水务报告
     *
     * @return
     */
    @GetMapping("/export/photo/water")
    public void exportPhotoWater(String photoIdList, HttpServletResponse response){
        photoLocationService.exportPhotoWater(photoIdList, response);
    }

    /**
     * 获取同任务上一个架次
     *
     * @return
     */
    @GetMapping("/get/records/water")
    public RestRes getMissionRecordsIds(Integer id){
        List<MissionRecordsEntity> missionRecordsEntities = photoLocationService.getMissionRecordsIds(id,0);
        Map<String, Object> map = new HashMap<>(2);
        map.put("records", missionRecordsEntities);
        return RestRes.ok(map);
    }

    /**
     * 获取同任务上一个架次的照片
     *
     * @return
     */
    @GetMapping("/get/photo/water")
    public RestRes getPhoto(Integer page, Integer limit,Integer missionRecordsId,Integer id){
        IPage<MissionPhotoEntity> photo = photoLocationService.getPhoto(page,limit,missionRecordsId,id);
        Map<String, Object> map = new HashMap<>(2);
        map.put("photos", photo);
        return RestRes.ok(map);
    }

    /**
     * 更改默认的同任务上一个架次的照片
     *
     * @return
     */
    @GetMapping("/update/photo/water")
    public RestRes updatePhoto(Integer id,Integer clearId){
        photoLocationService.updatePhoto(id,clearId);
        return RestRes.ok();
    }


    /*@PostMapping("/upload/photo/water")
    public RestRes insertOrUpdate(PhotoLocationEntity photoLocationEntity, MultipartFile file) {
        return photoLocationService.insertOrUpdate(photoLocationEntity, file);
    }*/

}
