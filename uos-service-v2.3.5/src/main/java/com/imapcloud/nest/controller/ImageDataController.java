package com.imapcloud.nest.controller;


import com.imapcloud.nest.model.ImageDataEntity;
import com.imapcloud.nest.pojo.dto.DeleteTagDTO;
import com.imapcloud.nest.pojo.dto.AddTagsDTO;
import com.imapcloud.nest.pojo.dto.reqDto.ImageDataReqDto;
import com.imapcloud.nest.service.ImageDataService;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  全景前端控制器
 * </p>
 *
 * @author daolin
 * @since 2020-11-16
 */
@RestController
@RequestMapping("/imageData")
public class ImageDataController {

    @Resource
    ImageDataService imageDataService;

    /**
     *  获取缩略图列表
     * @param imageDataReqDto
     * @return
     */
    @PostMapping("/list")
    public RestRes getThumbnailPage(@RequestBody @Valid ImageDataReqDto imageDataReqDto) {
        PageUtils page = imageDataService.getThumbnailPage(imageDataReqDto);
        Map<String, Object> map = new HashMap<>(2);
        map.put("page", page);
        return  RestRes.ok(map);
    }

    @GetMapping("/info/{id}")
    public RestRes getInfoById(@PathVariable Integer id) {
        ImageDataEntity imageDataEntity = imageDataService.getInfoById(id);
        Map<String, Object> map = new HashMap<>(2);
        map.put("imageData", imageDataEntity);
        return  RestRes.ok(map);
    }

    @PostMapping("/update")
    public RestRes updateImageData(ImageDataEntity imageData) {
        return imageDataService.updateImageData(imageData);
    }

    @DeleteMapping("/delete")
    public RestRes deleteImageData(@RequestBody Integer[] ids) {
        return imageDataService.deleteImageData(Arrays.asList(ids));
    }
}

