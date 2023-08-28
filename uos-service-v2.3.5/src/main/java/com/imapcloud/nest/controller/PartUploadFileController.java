package com.imapcloud.nest.controller;

import com.imapcloud.nest.common.annotation.SysLogIgnoreParam;
import com.imapcloud.nest.model.UploadEntity;
import com.imapcloud.nest.pojo.dto.FileInfoDto;
import com.imapcloud.nest.service.FileInfoService;
import com.imapcloud.nest.service.UploadService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.web.UosDataParseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 大文件分片上传
 *
 *
 * @author: zhengxd
 * @create: 2020/12/15
 **/
@RestController
@RequestMapping("/part/upload/file")
@Slf4j
public class PartUploadFileController {
    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private UploadService uploadService;


    @PostMapping("/save")
    public RestRes save(@RequestBody FileInfoDto fileInfoDto){
        Long id = fileInfoService.save(fileInfoDto);
        Map map = new HashMap();
        map.put("id",id);
        return RestRes.ok(map);
    }

//    /**
//     * 此方法弃用在v2.2.3后弃用，若后续版本无异常则可以删除，若有异常则去掉该注释并写明方法
//     */
//    @Deprecated
//    @PostMapping("/unPack")
//    public RestRes unPack(String uploadPath, String fileName, String unPackPath){
//        fileInfoService.unPack(uploadPath,fileName,unPackPath);
//        return RestRes.ok();
//    }

    /**
     * @deprecated 2.2.3，使用新接口{@link UosDataParseController#parseFlightTrackData(com.imapcloud.nest.v2.web.vo.req.VideoSrtReqVO)}替代，将在后续版本删除
     */
    @Deprecated
    @SysLogIgnoreParam("合并文件接口，参数MultipartFile multipartFile,Integer vedioId")
    @PostMapping("/uploadSrt")
    public RestRes uploadSrt(String fileName,String filePath,Integer videoId){
        Integer id = fileInfoService.uploadSrt(fileName,filePath,videoId);
        Map map = new HashMap(2);
        map.put("id", id);
        return RestRes.ok(map);
    }

//    /**
//     * 分片初始化
//     * @deprecated 2.2.3，新版文件上传初始化接口已迁移至文件服务，将在后续版本删除该接口
//     */
//    @Deprecated
//    @PostMapping("/init")
//    public RestRes initMultiPartUpload(@RequestBody UploadEntity uploadEntity) {
//        System.out.println("------------");
//        return uploadService.initMultiPartUpload(uploadEntity);
//    }

    /**
     * 分片初始化-Cps
     * @deprecated 2.2.3，新版文件上传初始化接口已迁移至文件服务，将在后续版本删除该接口
     */
    @Deprecated
    @PostMapping("/initCps")
    public RestRes initMultiPartUpload4Cps(@RequestBody UploadEntity uploadEntity) {
        log.info("打印:initCps -> {} - {} ","Cps分片初始化",uploadEntity.toString());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("initCps");
        RestRes restRes = uploadService.initMultiPartUploadCps(uploadEntity);
        stopWatch.stop();
        log.info("initCps->{} ， uploadEntity -> {} ",stopWatch.prettyPrint() ,uploadEntity.toString());
        return restRes;
    }

//    /**
//     * 完成上传
//     * @deprecated 2.2.3，新版文件上传无需手动合并，将在后续版本删除该接口
//     */
//    @Deprecated
//    @PostMapping("/complete")
//    public RestRes completeMultiPartUpload(@RequestBody UploadEntity uploadEntity) {
//        return uploadService.mergeMultipartUpload(uploadEntity);
//    }

    /**
     * 完成上传-Cps
     * @deprecated 2.2.3，新版文件上传无需手动合并，将在后续版本删除该接口
     */
    @Deprecated
    @PostMapping("/completeCps")
    public RestRes completeMultiPartUpload4Cps(@RequestBody UploadEntity uploadEntity) {
        log.info("打印:{}-{}","Cps调用完成上传",uploadEntity.toString());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("completeCps");
        RestRes restRes = uploadService.mergeMultipartUploadCps(uploadEntity);
        stopWatch.stop();
        log.info("completeCps -> {}" ,stopWatch.prettyPrint());
        return restRes;
    }
}
