//package com.imapcloud.nest.v2.web;
//
//import com.geoai.common.core.util.BizIdUtils;
//import com.geoai.common.web.rest.Result;
//import com.imapcloud.nest.enums.UploadTypeEnum;
//import com.imapcloud.nest.utils.MinIoUnit;
//import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
//import com.imapcloud.nest.v2.web.vo.resp.MinioRespVO;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.Resource;
//import java.io.InputStream;
//
///**
// * minio相关操作
// *
// * @author boluo
// * @date 2022-11-25
// */
//@Slf4j
//@RequestMapping("v2/minio/")
//@RestController
//public class MinioController {
//
//    @Resource
//    private GeoaiUosProperties geoaiUosProperties;
//
//    /**
//     * @deprecated 2.2.3，将在后续版本删除
//     */
//    @Deprecated
//    @PostMapping("picture/upload")
//    public Result<Object> pictureUpload(MultipartFile file) {
//
//        try (InputStream io = file.getInputStream()) {
//            String url = String.format("%s%s.%s", UploadTypeEnum.MINIO_COMMON_PICTURE.getPath(), BizIdUtils.snowflakeIdStr(), MinIoUnit.getFileType(file.getInputStream()));
//            if(MinIoUnit.putObject(url, io)) {
//
//                String format = String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), url);
//
//                MinioRespVO.PictureRespVO pictureRespVO = new MinioRespVO.PictureRespVO();
//                pictureRespVO.setObject(format);
//                return Result.ok(pictureRespVO);
//            }
//        } catch (Exception e) {
//            log.error("#MinioController.pictureUpload# error:", e);
//        }
//        return Result.error("上传失败");
//    }
//}
