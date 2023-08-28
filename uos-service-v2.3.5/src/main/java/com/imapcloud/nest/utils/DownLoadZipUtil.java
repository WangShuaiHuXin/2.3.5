package com.imapcloud.nest.utils;

import com.geoai.common.core.util.DateUtils;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.model.MissionVideoPhotoEntity;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.manager.dataobj.FileUrlMappingDO;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 下载成果的图片、视频zip包
 *
 * @author: zhengxd
 * @create: 2020/10/30
 **/
@Slf4j
@Component
public class DownLoadZipUtil {

    @Resource
    private FileManager fileManager;

    private static FileManager staticFileManager;

    @PostConstruct
    public void init() {
        staticFileManager = fileManager;
    }

    /**
     * 下载图片\视频：将图片\视频从MinIO下载，再下载42服务器上的录屏视频,并打包到压缩包里
     * @param fileName      压缩包名称
     * @param minIoNameList 原图\原视频的名称List
     * @param nmsNameList   录屏的名称List
     *
     */
    public void downloadFileAndZip(String fileName, List<FileUrlMappingDO> minIoNameList, List<FileUrlMappingDO> nmsNameList, HttpServletResponse response) {
        try(ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())){
            // 输出到浏览器
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            // 压缩原图或原视频
            if (ToolUtil.isNotEmpty(minIoNameList)) {
                log.info("----------开始压缩原图或原视频----------");
                for (int i = 0; i < minIoNameList.size(); i++) {
                    FileUrlMappingDO originVideo = minIoNameList.get(i);
                    log.info("#DownLoadZipUtil=======> originVideo:{}", originVideo);
                    String filename = StringUtils.hasText(originVideo.getOriginName()) ? originVideo.getOriginName() : "unknown_"+ i + StringUtils.getFilenameExtension(originVideo.getStorageUri());
                    fileManager.handleInputSteam(originVideo.getStorageUri(), inputStream -> tryZipOutInput(filename, inputStream, zipOut));
                }
                log.info("----------结束压缩原图或原视频----------");
            }

            // 压缩录屏的视频
            if (ToolUtil.isNotEmpty(nmsNameList)) {
                log.info("----------开始压缩录屏的视频----------");
                for (int i = 0; i < nmsNameList.size(); i++) {
                    FileUrlMappingDO recordVideo = nmsNameList.get(i);
                    log.info("#DownLoadZipUtil=======> recordVideo:{}", recordVideo);
                    String filename = StringUtils.hasText(recordVideo.getOriginName()) ? recordVideo.getOriginName() : "unknown_"+ i + StringUtils.getFilenameExtension(recordVideo.getStorageUri());
                    fileManager.handleInputSteam(recordVideo.getStorageUri(), inputStream -> tryZipOutInput(filename, inputStream, zipOut));
                }
                log.info("----------结束压缩录屏的视频----------");
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_FILE_COMPRESSION_ERROR.getContent()));
        }

    }

    /**
     * 下载zip包通用接口,直接从服务器上下载zip包到浏览器）
     * @param zipName zip包名称
     * @param zipPath zip包存放路径
     * @param response
     */
    public static void downloadZip(String zipName, String zipPath, HttpServletResponse response) {
        try (InputStream inputSteam = staticFileManager.getInputSteam(zipPath)){
            @Cleanup
            OutputStream out = response.getOutputStream();
            zipName = URLEncoder.encode(zipName, "UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + zipName);

            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputSteam);
            int temp = 0;
            while ((temp = bufferedInputStream.read()) != -1) {
                out.write(temp);
            }

        } catch (IOException e) {
            log.info(e.getMessage());
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOADING_THE_COMPRESSED_PACKAGE_FAILED.getContent()));
        }
    }

    private static void tryZipOutInput(String name, InputStream input, ZipOutputStream zipOutLocal){
        try {
            zipOutInput(name, input, zipOutLocal);
        }catch (IOException e){
            log.error("#DownLoadZipUtil.tryZipOutInput# error:", e);
        }
    }

    /**
     * 压缩文件到压缩包里
     * @throws IOException
     */
    private static void zipOutInput(String name,InputStream input,ZipOutputStream zipOutLocal) throws IOException {
        String downloadName = name.substring(name.lastIndexOf("/") + 1);
        zipOutLocal.putNextEntry(new ZipEntry(downloadName));
        int len = 0;
        byte[] buf = new byte[10240];
        while ((len = input.read(buf)) != -1) {
            zipOutLocal.write(buf, 0, len);
        }
    }

    public static void downloadUploadPhotoOrVideo(String zipName, List<FileUrlMappingDO> fileUrls, HttpServletResponse response) {
        try(ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())){
            // 输出到浏览器

            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipName, "UTF-8"));

            // 压缩原图或原视频
            if (ToolUtil.isNotEmpty(fileUrls)) {
                for (int i=0; i < fileUrls.size(); i++) {
                    FileUrlMappingDO mapping = fileUrls.get(i);
                    String filename = StringUtils.hasText(mapping.getOriginName()) ? mapping.getOriginName() : "unknown_"+ i + StringUtils.getFilenameExtension(mapping.getStorageUri());
                    staticFileManager.handleInputSteam(mapping.getStorageUri(), inputStream -> tryZipOutInput(filename, inputStream, zipOut));
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_FILE_COMPRESSION_ERROR.getContent()));
        }

    }

    public static void downloadUploadVideoPhoto(String fileName, List<MissionVideoPhotoEntity> missionVideoPhotoEntityList, HttpServletResponse response) {
        try {
            // 输出到浏览器
            @Cleanup
            ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());

            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            Map<LocalDateTime, List<MissionVideoPhotoEntity>> timeListMap = missionVideoPhotoEntityList.stream()
                    .collect(Collectors.groupingBy(MissionVideoPhotoEntity::getExtractTime));
            timeListMap.forEach((extractTime, entityList) -> {
                String timeName = extractTime.format(DateUtils.DATE_TIME_FORMATTER_OF_CN);
                for (MissionVideoPhotoEntity missionVideoPhotoEntity : entityList) {
                    String fileUrl = missionVideoPhotoEntity.getPhotoUrl();
                    try(InputStream stream = staticFileManager.getInputSteam(fileUrl)){
                        if (stream == null) {
                            continue;
                        }
                        String name = fileUrl.lastIndexOf("/") == -1 ? fileUrl.substring(fileUrl.lastIndexOf("\\")) : fileUrl.substring(fileUrl.lastIndexOf("/"));
                        if (name.startsWith("/")) {
                            name = name.substring(1);
                        }
                        zipOut.putNextEntry(new ZipEntry(timeName + File.separator + name));
                        int len;
                        byte[] buf = new byte[10240];
                        while ((len = stream.read(buf)) != -1) {
                            zipOut.write(buf, 0, len);
                        }
                    } catch (Exception e) {
                        log.error("#DownLoadZipUtil.downloadUploadVideoPhoto# fileUrl={} error:", fileUrl, e);
                    }
                }
            });
        } catch (Exception e) {
            log.error("#DownLoadZipUtil.downloadUploadVideoPhoto# error:", e);
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOADING_THE_COMPRESSED_PACKAGE_FAILED.getContent()));
        }
    }
}
