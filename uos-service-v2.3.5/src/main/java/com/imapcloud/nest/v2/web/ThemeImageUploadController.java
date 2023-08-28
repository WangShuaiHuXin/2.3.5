package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.enums.UploadTypeEnum;
import com.imapcloud.nest.v2.common.properties.DomainConfig;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Classname ThemeImageUploadController
 * @Description 主题图片上传Api
 * @Date 2022/8/31 15:26
 * @Author Carnival
 */
@ApiSupport(author = "jiahua@geoai.com", order = 1)
@Api(value = "主题", tags = "主题图片上传")
@RequestMapping("v2/theme/upload")
@RestController
public class ThemeImageUploadController {

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

//    @Resource
//    private ThemeImageUploadService themeImageUploadService;

//    /**
//     * @deprecated 2.2.3，将在后续版本删除
//     */
//    @Deprecated
//    @ApiOperationSupport(author = "jiahua@geoai.com", order = 1)
//    @ApiOperation(value = "上传主题logo", notes = "")
//    @PostMapping("icon")
//    public Result<String> uploadIcon(MultipartFile fileData) {
//        if (fileData == null) {
//            return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLEASE_SELECT_THE_FILE.getContent()));
//        }
//        try (InputStream is = fileData.getInputStream()) {
//            String fileType = MinIoUnit.getFileType(is);
//            if (!DataConstant.IMAGE_TYPE_LIST.contains(fileType)) {
//                return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FILE_TYPE_IS_NOT_AN_IMAGE.getContent()));
//            }
//            if (checkFileSize(fileData.getSize(),5,"M")) {
//                return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FILE_SHOULD_NOT_EXCEED_5M.getContent()));
//            }
//        } catch (Exception e) {
//            return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_THE_FILE_STREAM.getContent()));
//        }
//        String path = themeImageUploadService.uploadIcon(fileData);
//
//        return Result.ok(path);
//    }
//
//    /**
//     * @deprecated 2.2.3，将在后续版本删除
//     */
//    @Deprecated
//    @ApiOperationSupport(author = "jiahua@geoai.com", order = 2)
//    @ApiOperation(value = "上传主题图标", notes = "")
//    @PostMapping("favicon")
//    public Result<String> uploadFavIcon(MultipartFile fileData) {
//        if (fileData == null) {
//            return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLEASE_SELECT_THE_FILE.getContent()));
//        }
//        try (InputStream is = fileData.getInputStream()) {
//            String fileType = MinIoUnit.getFileType(is);
//            if (!DataConstant.IMAGE_TYPE_LIST.contains(fileType)) {
//                return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FILE_TYPE_IS_NOT_AN_IMAGE.getContent()));
//            }
//            if (checkFileSize(fileData.getSize(),5,"M")) {
//                return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FILE_SHOULD_NOT_EXCEED_5M.getContent()));
//            }
//        } catch (Exception e) {
//            return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_THE_FILE_STREAM.getContent()));
//        }
//        String path = themeImageUploadService.uploadFavicon(fileData);
//
//        return Result.ok(path);
//    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 3)
    @ApiOperation(value = "恢复默认Logo")
    @GetMapping("default/icon")
    public Result<String> recoverDefaultIcon() {
        String originPath = geoaiUosProperties.getStore().getOriginPath();
        return Result.ok(originPath + UploadTypeEnum.DEFAULT_ICON.getPath());
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 4)
    @ApiOperation(value = "恢复默认图标")
    @GetMapping("default/favicon")
    public Result<String> recoverDefaultFavicon() {
        String originPath = geoaiUosProperties.getStore().getOriginPath();
        return Result.ok(originPath + UploadTypeEnum.DEFAULT_FAVICON.getPath());
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 5)
    @ApiOperation(value = "获取媒体域名")
    @GetMapping("domain")
    public Result<String> getMediaDomain() {
        DomainConfig domain = geoaiUosProperties.getDomain();
        return Result.ok(domain.getMedia() == null ? "" : domain.getMedia());
    }


    private boolean checkFileSize(Long len, int size, String unit) {
        double fileSize = 0;
        switch (unit.toUpperCase()) {
            case "B":
                fileSize = (double) len;
                break;
            case "K":
                fileSize = (double) len / 1024;
                break;
            case "M":
                fileSize = (double) len / 1048576;
                break;
            case "G":
                fileSize = (double) len / 1073741824;
                break;
        }
        return fileSize > size;
    }
}
