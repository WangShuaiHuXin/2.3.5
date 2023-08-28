package com.imapcloud.nest.v2.service.impl;

import com.imapcloud.nest.v2.service.ThemeImageUploadService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Classname ThemeImageUploadServiceImpl
 * @Description 主题图片上传接口实现类
 * @Date 2022/8/31 15:40
 * @Author Carnival
 */
@Slf4j
//@Service
public class ThemeImageUploadServiceImpl implements ThemeImageUploadService {

//    @Resource
//    private GeoaiUosProperties geoaiUosProperties;

//    @Override
//    public String uploadIcon(MultipartFile fileData) {
//        String newFileName;
//        try (InputStream in = fileData.getInputStream()) {
//            newFileName = String.format("%s.%s", BizIdUtils.snowflakeIdStr(), MinIoUnit.getFileType(in));
//        } catch (Exception e) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_THE_FILE_STREAM.getContent()));
//        }
//
//        String imagePath = String.format("%s%s", UploadTypeEnum.THEME_ICON.getPath(), newFileName);
//        try (InputStream image = fileData.getInputStream()) {
//            if (!MinIoUnit.putObject(imagePath, image)) {
//                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_UPLOAD_FILE.getContent()));
//            }
//        } catch (Exception e) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_THE_FILE_STREAM.getContent()));
//        }
//        DomainConfig domain = geoaiUosProperties.getDomain();
//        String originPath = geoaiUosProperties.getStore().getOriginPath();
//        return domain.getMedia() == null ? "" : originPath + imagePath;
//    }
//
//    @Override
//    public String uploadFavicon(MultipartFile fileData) {
//        String newFileName;
//        try (InputStream in = fileData.getInputStream()) {
//            newFileName = String.format("%s.%s", BizIdUtils.snowflakeIdStr(), MinIoUnit.getFileType(in));
//        } catch (Exception e) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_THE_FILE_STREAM.getContent()));
//        }
//
//        String imagePath = String.format("%s%s", UploadTypeEnum.THEME_FAVICON.getPath(), newFileName);
//        try (InputStream image = fileData.getInputStream()) {
//            if (!MinIoUnit.putObject(imagePath, image)) {
//                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_UPLOAD_FILE.getContent()));
//            }
//        } catch (Exception e) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_THE_FILE_STREAM.getContent()));
//        }
//        DomainConfig domain = geoaiUosProperties.getDomain();
//        String originPath = geoaiUosProperties.getStore().getOriginPath();
//        return domain.getMedia() == null ? "" : originPath + imagePath;
//    }
}
