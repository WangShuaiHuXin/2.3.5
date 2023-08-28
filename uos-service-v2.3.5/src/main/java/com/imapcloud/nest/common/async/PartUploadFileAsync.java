package com.imapcloud.nest.common.async;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.geoai.common.core.constant.SymbolConstants;
import com.imapcloud.nest.common.constant.MissionConstant;
import com.imapcloud.nest.common.validator.Assert;
import com.imapcloud.nest.enums.DataTypeEnum;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.FileInfoDto;
import com.imapcloud.nest.pojo.dto.FlyPoint;
import com.imapcloud.nest.pojo.dto.reqDto.DataReqDto;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.DateUtils;
import com.imapcloud.nest.utils.DistanceUtil;
import com.imapcloud.nest.v2.common.enums.LenTypeEnum;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.utils.DrawImageUtils;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileStorageOutDO;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.nest.v2.service.DataParseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 分片上传的异步操作类
 *
 * @author: zhengxd
 * @create: 2020/12/23
 **/
@Component
@Slf4j
public class PartUploadFileAsync {
    @Autowired
    @Lazy
    private MissionVideoService missionVideoService;
    @Autowired
    private MissionPhotoService missionPhotoService;
    @Autowired
    private MissionVideoPhotoService missionVideoPhotoService;
    @Autowired
    private DataOrthoService dataOrthoService;
    @Autowired
    private DataPointCloudService dataPointCloudService;
    @Autowired
    private DataTiltService dataTiltService;
    @Autowired
    private DataVectorService dataVectorService;
    @Autowired
    private DataPanoramaService dataPanoramaService;
    @Autowired
    private DataMultispectralService dataMultispectralService;
    @Autowired
    private FileInfoSrtService fileInfoSrtService;
    @Autowired
    private StationInfraredTiffService stationInfraredTiffService;
    @Autowired
    private DataCenterService dataCenterService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private UploadService uploadService;

    @Resource
    private UploadManager uploadManager;

    @Resource
    private FileManager fileManager;

    private static FileManager staticFileManager;

    private static UploadManager staticUploadManager;

    @PostConstruct
    public void init() {
        staticFileManager = fileManager;
        staticUploadManager = uploadManager;
    }

    /**
     * 上传srt文件
     * @param filePath 文件绝对路径
     * @deprecated 2.2.3，使用新接口{@link DataParseService#parseFlightTrackData(com.imapcloud.nest.v2.service.dto.in.VideoSrtInDTO)}替代，将在后续版本删除
     */
    @Deprecated
    public FileInfoSrtEntity uploadSrt(String filePath, String originFileName,Integer videoId) {
        FileInfoSrtEntity fileInfoSrtEntity = null;
        String srtJsonFile = convertSrtJson(filePath);
        if (srtJsonFile != null) {
            fileInfoSrtEntity = new FileInfoSrtEntity();
            fileInfoSrtEntity.setSrtUrl(srtJsonFile);
            fileInfoSrtEntity.setFileUrl(filePath);
            fileInfoSrtEntity.setName(originFileName);
            fileInfoSrtService.save(fileInfoSrtEntity);
            log.info("文件读取完毕,该方法结束");
        }
        if (fileInfoSrtEntity == null) {
            Assert.failure("文件上传解析失败");
        }
        if (videoId != null) {
            MissionVideoEntity missionVideoEntity = missionVideoService.getById(videoId);
            missionVideoEntity.setSrtUrl(fileInfoSrtEntity.getSrtUrl());
            missionVideoEntity.setSrtId(fileInfoSrtEntity.getId());

            missionVideoEntity.setSrtFileUrl(null);
            missionVideoService.updateById(missionVideoEntity);
        }
        return fileInfoSrtEntity;
    }

    public static String convertSrtJson(String filePath) {
        JSONObject gpsJson = new JSONObject();
        try (InputStream fileInputStream = staticFileManager.getInputSteam(filePath);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream))){
            String line = "";
            int id = 1;
            while (line != null) {
                if(line.contains("GPS")){
                    String gps = getGps(line);
                    if (gps != null) {
                        gpsJson.put(String.valueOf(id), gps);
                    }
                    id++;
                }
                line = bufferedReader.readLine();
            }
            return writeFileContext(gpsJson, filePath);
        } catch (Exception e) {
            log.error("读取文件srt文件时发生错误", e);
            return null;
        }
    }

    /**
     * 将json写入到json文件中
     *
     * @param json
     * @param path
     * @throws Exception
     */
    public static String writeFileContext(JSONObject json, String path) {
        String jsonStrings = JSONUtil.toJsonStr(json);
        CommonFileInDO commonFileInDO = new CommonFileInDO();
        commonFileInDO.setFileName(path);
        commonFileInDO.setInputStream(new ByteArrayInputStream(jsonStrings.getBytes()));
        Optional<FileStorageOutDO> fileStorageOutDO = staticUploadManager.uploadFile(commonFileInDO);
        if (fileStorageOutDO.isPresent()) {
            FileStorageOutDO result = fileStorageOutDO.get();
            return result.getStoragePath() + "/" + result.getFilename();
        }
        return "";
    }

    private static String getGps(String line) {
        if (line == null) {
            return null;
        }
        StringBuilder gps = new StringBuilder();
        String[] strAry = line.split(", ");
        gps.append(strAry[4].substring(5));
        gps.append(", ");
        gps.append(strAry[5]);
        gps.append(", ");
        gps.append(strAry[6], 0, 2);
        return gps.toString();
    }

//    public static void main(String[] args) {
//        try(FileInputStream fileInputStream = new FileInputStream("D:\\dingTalkDownload\\5845.JPG")){
//            FlyPoint gisFromPic = DistanceUtil.getGisFromPic(fileInputStream);
//            System.out.println(gisFromPic.getTakeTime());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public Long savePhoto(FileInfoDto fileInfoDto) {
        String fileName = fileInfoDto.getFileName();
        String photoUrl = fileInfoDto.getFilePath();
        FlyPoint flyPoint = DistanceUtil.getGisFromPic(fileManager.getInputSteam(photoUrl));
        String tiffStr = fileName.substring(fileName.lastIndexOf(".") + 1);
        if("TIFF".equalsIgnoreCase(tiffStr)) {
            StationInfraredTiffEntity stationInfraredTiffEntity = stationInfraredTiffService.lambdaQuery()
                    .eq(StationInfraredTiffEntity::getMissionId,fileInfoDto.getMissionId())
                    .eq(StationInfraredTiffEntity::getMissionRecordsId,fileInfoDto.getMissionRecordsId())
                    .eq(StationInfraredTiffEntity::getFileName,fileInfoDto.getFileName())
                    .one();
            if(stationInfraredTiffEntity==null){
                stationInfraredTiffEntity = new StationInfraredTiffEntity();
            }
            stationInfraredTiffEntity.setPhotoUrl(photoUrl);
            stationInfraredTiffEntity.setMissionId(fileInfoDto.getMissionId());
            stationInfraredTiffEntity.setMissionRecordsId(fileInfoDto.getMissionRecordsId());
            stationInfraredTiffEntity.setFileName(fileInfoDto.getFileName());
            if (flyPoint != null) {
                stationInfraredTiffEntity.setLatitude(flyPoint.getAircraftLocationLatitude());
                stationInfraredTiffEntity.setLongitude(flyPoint.getAircraftLocationLongitude());
                stationInfraredTiffEntity.setAltitude(flyPoint.getAircraftLocationAltitude());
            }
            stationInfraredTiffService.saveOrUpdate(stationInfraredTiffEntity);
            log.info("tiff合并分片，并解压文件方法结束");
            return stationInfraredTiffEntity.getId().longValue();
        }else {
            MissionPhotoEntity missionPhotoEntity = new MissionPhotoEntity();
            String thumbnailPath = fileManager.generateThumbnail(photoUrl, 0.6D, false);
            missionPhotoEntity.setThumbnailUrl(thumbnailPath);
            try (InputStream inputSteam = fileManager.getInputSteam(photoUrl)){
                // 获取图片类型
                boolean isRedType = DrawImageUtils.imageIsRedType(inputSteam);
                missionPhotoEntity.setPhotoType(isRedType ? 1 : 0);
                missionPhotoEntity.setLenType(isRedType ? LenTypeEnum.THRM_TYPE.getType() : LenTypeEnum.NORMAL_TYPE.getType());
            } catch (Exception e) {
                log.error("#PartUploadFileAsync.savePhoto# fileInfoDto={}", fileInfoDto, e);
            }
            // 图片设备成功，保存入库
            missionPhotoEntity.setName(fileName);
            missionPhotoEntity.setPhotoUrl(photoUrl);
            missionPhotoEntity.setThumbnailName(fileName);
            missionPhotoEntity.setDeviceUuid("124");
            missionPhotoEntity.setTimeCreated(LocalDateTime.now());
            missionPhotoEntity.setSource(2);
            if (flyPoint != null) {
                missionPhotoEntity.setLatitude(flyPoint.getAircraftLocationLatitude());
                missionPhotoEntity.setLongitude(flyPoint.getAircraftLocationLongitude());
                missionPhotoEntity.setAltitude(flyPoint.getAircraftLocationAltitude());
                String takeTime = flyPoint.getTakeTime();
                if(StringUtils.isNotEmpty(takeTime)){
                    LocalDateTime dateTime = null;
                    try {
                        dateTime = LocalDateTime.parse(takeTime, DateUtils.DATE_TIME_FORMATTER_OF_CN);
                    }catch (DateTimeParseException e){
                        log.error("解析图片创建日期失败1 ==> {}", takeTime);
                    }
                    if(Objects.isNull(dateTime)){
                        try {
                            dateTime = LocalDateTime.parse(takeTime, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
                        }catch (DateTimeParseException e){
                            log.error("解析图片创建日期失败1 ==> {}", takeTime);
                        }
                    }
                    missionPhotoEntity.setTimeCreated(dateTime);
                }
            }
            missionPhotoEntity.setMissionId(fileInfoDto.getMissionId());
            missionPhotoEntity.setMissionRecordsId(fileInfoDto.getMissionRecordsId());
            missionPhotoEntity.setOrgCode(fileInfoDto.getUnitId());
            missionPhotoService.save(missionPhotoEntity);
            log.info("图片异步合并分片，并解压文件方法结束");
            // 清除upload
            uploadService.deleteByFilePath(photoUrl);
            return missionPhotoEntity.getId();
        }

    }

    public Long saveVideo(FileInfoDto fileInfoDto) {
        delDataOrigin(fileInfoDto.getMissionRecordsId(), DataTypeEnum.VIDEO.getValue());
        String fileName = fileInfoDto.getFileName();
        String photoUrl = fileInfoDto.getFilePath();
        MissionVideoEntity missionVideoEntity = new MissionVideoEntity();
        missionVideoEntity.setName(fileName);
        missionVideoEntity.setVideoUrl(photoUrl);
        missionVideoEntity.setType(MissionConstant.MissionVideoType.UPLOAD_VIDEO);
        missionVideoEntity.setMissionId(fileInfoDto.getMissionId());
        missionVideoEntity.setMissionRecordsId(fileInfoDto.getMissionRecordsId());
        missionVideoEntity.setOrgCode(fileInfoDto.getUnitId());
        Integer srtId = fileInfoDto.getSrtId();
        if (srtId != null) {
            FileInfoSrtEntity fileInfoSrtEntity = fileInfoSrtService.getById(srtId);
            if (fileInfoSrtEntity != null) {
                missionVideoEntity.setSrtId(srtId);
                missionVideoEntity.setSrtFileUrl(null);
                missionVideoEntity.setSrtUrl(fileInfoSrtEntity.getSrtUrl());
            }
        }
        log.info("即将保存视频数据");
        missionVideoService.save(missionVideoEntity);
        Boolean isExtractSrt = fileInfoDto.getIsExtractSrt();
        if (isExtractSrt != null && isExtractSrt) {
            missionVideoPhotoService.extractSrt(missionVideoEntity.getId().intValue());
        }
        return missionVideoEntity.getId();
    }

    public Long saveOrtho(FileInfoDto fileInfoDto) {
        delDataOrigin(fileInfoDto.getMissionRecordsId(), DataTypeEnum.ORTHO.getValue());
        String fileName = fileInfoDto.getFileName();
        String photoUrl = fileInfoDto.getFilePath();
        String uploadPath = photoUrl.substring(0,photoUrl.lastIndexOf(SymbolConstants.POINT)) + SymbolConstants.SLASH_LEFT;
//        String saveName = photoUrl.substring(photoUrl.lastIndexOf("/")+1);
//        String unPackPath = getUnPackPath(DataTypeEnum.ORTHO.getValue());
//        ZipFile zipFile = getZipFile(uploadPath, saveName,unPackPath);
//        uploadPath += unPackPath;
        DataOrthoEntity dataOrthoEntity = new DataOrthoEntity();
        dataOrthoEntity.setName(fileName);
        dataOrthoEntity.setMissionId(fileInfoDto.getMissionId());
        dataOrthoEntity.setMissionRecordsId(fileInfoDto.getMissionRecordsId());
        dataOrthoEntity.setTagId(fileInfoDto.getTagId());
        dataOrthoEntity.setTaskId(fileInfoDto.getTaskId());
        dataOrthoEntity.setTaskName(fileInfoDto.getTaskName());
        dataOrthoEntity.setMissionRecordTime(fileInfoDto.getMissionRecordTime());
        dataOrthoEntity.setFileInfoId(fileInfoDto.getFileInfoId());
        dataOrthoEntity.setOrgCode(fileInfoDto.getUnitId());
        dataOrthoEntity.setKeyUrl(uploadPath + "/{z}/{x}/{y}.png");
        dataOrthoEntity.setJsonUrl(uploadPath + "meta.json");
        dataOrthoEntity.setTifUrl("");
//        for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); ) {
//            ZipEntry entry = e.nextElement();
//            String entryName = entry.getName();
//            if (entryName.contains("layer.json")) {
//                String keyUrl = entryName.contains("/") ? uploadPath + "/" + entryName.substring(0, entryName.lastIndexOf("/")) + "/{z}/{x}/{y}.png"  : uploadPath + "/{z}/{x}/{y}.png";
//                dataOrthoEntity.setKeyUrl(keyUrl);
////                    log.info("正射的keyUrl: " + dataOrthoEntity.getKeyUrl());
//            }
//            if (entryName.contains("meta.json")) {
//                dataOrthoEntity.setJsonUrl(uploadPath + "/" + entry.getName());
////                    log.info("正射的jsonUrl: " + dataOrthoEntity.getJsonUrl());
//            }
//            if (entryName.endsWith(".tif") || entryName.endsWith(".tiff")) {
//                dataOrthoEntity.setTifUrl(uploadPath + "/" + entry.getName());
////                    log.info("正射的tifUrl: " + dataOrthoEntity.getTifUrl());
//            }
//        }
        dataOrthoService.saveOrUpdate(dataOrthoEntity);
        log.info("正射异步合并分片，并解压文件方法结束");
        return dataOrthoEntity.getId();
    }

//    /**
//     * 此方法弃用在v2.2.3后弃用，若后续版本无异常则可以删除，若有异常则去掉该注释并写明方法
//     */
//    @Deprecated
//    public Long saveMultispectral(FileInfoDto fileInfoDto) {
//        delDataOrigin(fileInfoDto.getMissionRecordsId(), DataTypeEnum.MULTISPECTRAL.getValue());
//        String fileName = fileInfoDto.getFileName();
//        String photoUrl = fileInfoDto.getFilePath();
//        String uploadPath = photoUrl.substring(0,photoUrl.lastIndexOf("/"));
//        String saveName = photoUrl.substring(photoUrl.lastIndexOf("/")+1);
//        String unPackPath = getUnPackPath(DataTypeEnum.MULTISPECTRAL.getValue());
//        ZipFile zipFile = getZipFile(uploadPath, saveName,unPackPath);
//        uploadPath += unPackPath;
//        DataMultispectralEntity dataMultispectralEntity = new DataMultispectralEntity();
//        dataMultispectralEntity.setName(fileName);
//        dataMultispectralEntity.setMissionId(fileInfoDto.getMissionId());
//        dataMultispectralEntity.setMissionRecordsId(fileInfoDto.getMissionRecordsId());
//        dataMultispectralEntity.setTagId(fileInfoDto.getTagId());
//        dataMultispectralEntity.setTaskId(fileInfoDto.getTaskId());
//        dataMultispectralEntity.setTaskName(fileInfoDto.getTaskName());
//        dataMultispectralEntity.setMissionRecordTime(fileInfoDto.getMissionRecordTime());
//        dataMultispectralEntity.setFileInfoId(fileInfoDto.getFileInfoId());
//        for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); ) {
//            ZipEntry entry = e.nextElement();
//            String entryName = entry.getName();
//            if (entryName.contains("layer.json")) {
//                String keyUrl = entryName.contains("/") ? uploadPath + "/" + entryName.substring(0, entryName.lastIndexOf("/")) + "/{z}/{x}/{y}.png"  : uploadPath + "/{z}/{x}/{y}.png";
//                dataMultispectralEntity.setKeyUrl(keyUrl);
////                    log.info("正射的keyUrl: " + dataOrthoEntity.getKeyUrl());
//            }
//            if (entryName.contains("meta.json")) {
//                dataMultispectralEntity.setJsonUrl(uploadPath + "/" + entry.getName());
////                    log.info("正射的jsonUrl: " + dataOrthoEntity.getJsonUrl());
//            }
//            if (entryName.endsWith(".tif") || entryName.endsWith(".tiff")) {
//                dataMultispectralEntity.setTifUrl(uploadPath + "/" + entry.getName());
////                    log.info("正射的tifUrl: " + dataOrthoEntity.getTifUrl());
//            }
//        }
//        dataMultispectralService.saveOrUpdate(dataMultispectralEntity);
//        log.info("正射异步合并分片，并解压文件方法结束");
//        return dataMultispectralEntity.getId();
//    }

    public Long savePointCloud(FileInfoDto fileInfoDto) {
        log.info("点云合并分片并解压文件方法开始");
        delDataOrigin(fileInfoDto.getMissionRecordsId(), DataTypeEnum.POINTCLOUD.getValue());
        Map<String, String> map = new HashMap(2);
        String fileName = fileInfoDto.getFileName();
        String photoUrl = fileInfoDto.getFilePath();
        String uploadPath = photoUrl.substring(0,photoUrl.lastIndexOf(SymbolConstants.POINT)) + SymbolConstants.SLASH_LEFT;
//        String saveName = photoUrl.substring(photoUrl.lastIndexOf("/")+1);
//        String unPackPath = getUnPackPath(DataTypeEnum.POINTCLOUD.getValue());
//        ZipFile zipFile = getZipFile(uploadPath, saveName,unPackPath);
//        uploadPath += unPackPath;
//        String lasName;
//        String jsonName;
        DataPointCloudEntity dataPointCloudEntity = new DataPointCloudEntity();
        dataPointCloudEntity.setName(fileName);
        dataPointCloudEntity.setMissionId(fileInfoDto.getMissionId());
        dataPointCloudEntity.setMissionRecordsId(fileInfoDto.getMissionRecordsId());
        dataPointCloudEntity.setTagId(fileInfoDto.getTagId());
        dataPointCloudEntity.setTaskId(fileInfoDto.getTaskId());
        dataPointCloudEntity.setTaskName(fileInfoDto.getTaskName());
        dataPointCloudEntity.setMissionRecordTime(fileInfoDto.getMissionRecordTime());
        dataPointCloudEntity.setFileInfoId(fileInfoDto.getFileInfoId());
        dataPointCloudEntity.setOrgCode(fileInfoDto.getUnitId());
        // 固定为`tileset.json`名称
        dataPointCloudEntity.setKeyUrl(uploadPath + "tileset.json");
//        for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); ) {
//            ZipEntry entry = e.nextElement();
//            if (entry.getName().endsWith(".las")) {
//                lasName = entry.getName();
//                map.put("lasName", lasName);
//                dataPointCloudEntity.setLasUrl(uploadPath + "/" + lasName);
//            }
//            if (entry.getName().endsWith(".json")) {
//                jsonName = entry.getName();
//                map.put("jsonName", jsonName);
//                dataPointCloudEntity.setKeyUrl(uploadPath + "/" + jsonName);
//            }
//        }
        dataPointCloudService.save(dataPointCloudEntity);
        log.info("点云合并分片并解压文件方法结束");
        return dataPointCloudEntity.getId();
    }

    private String getUnPackPath(Integer type) {
        return "/unPack"+type;
    }

    public Long saveTitl(FileInfoDto fileInfoDto) {
        delDataOrigin(fileInfoDto.getMissionRecordsId(), DataTypeEnum.TILT.getValue());
        String fileName = fileInfoDto.getFileName();
        String photoUrl = fileInfoDto.getFilePath();
        String uploadPath = photoUrl.substring(0,photoUrl.lastIndexOf(SymbolConstants.POINT)) + SymbolConstants.SLASH_LEFT;
        String saveName = photoUrl.substring(photoUrl.lastIndexOf("/")+1);
//        String unPackPath = getUnPackPath(DataTypeEnum.TILT.getValue());
//        ZipFile zipFile = getZipFile(uploadPath, saveName,unPackPath);
//        uploadPath += unPackPath;
        DataTiltEntity dataTiltEntity = new DataTiltEntity();
        dataTiltEntity.setKeyUrl(uploadPath + "tileset.json");
//        log.info("倾斜的keyUrl: " + dataTiltEntity.getKeyUrl());
        dataTiltEntity.setName(fileName);
        dataTiltEntity.setMissionId(fileInfoDto.getMissionId());
        dataTiltEntity.setMissionRecordsId(fileInfoDto.getMissionRecordsId());
        dataTiltEntity.setFileInfoId(fileInfoDto.getFileInfoId());
        dataTiltEntity.setOrgCode(fileInfoDto.getUnitId());
        dataTiltService.save(dataTiltEntity);
        log.info("倾斜异步合并分片，并解压文件方法结束");
        return dataTiltEntity.getId();
    }

    public Long saveVector(FileInfoDto fileInfoDto) {
        delDataOrigin(fileInfoDto.getMissionRecordsId(), DataTypeEnum.VECTOR.getValue());
        log.info("矢量异步合并分片，并解压文件方法开始");
        String fileName = fileInfoDto.getFileName();
        String photoUrl = fileInfoDto.getFilePath();
//        String uploadPath = photoUrl.substring(0,photoUrl.lastIndexOf("/"));
//        String saveName = photoUrl.substring(photoUrl.lastIndexOf("/")+1);
//        String unPackPath = getUnPackPath(DataTypeEnum.VECTOR.getValue());
//        ZipFile zipFile = getZipFile(uploadPath, saveName,unPackPath);
//        uploadPath += unPackPath;
        DataVectorEntity dataVectorEntity = new DataVectorEntity();
        dataVectorEntity.setKeyUrl(photoUrl);
//        for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); ) {
//            ZipEntry entry = e.nextElement();
//            if (entry.getName().endsWith(".kml")) {
//                dataVectorEntity.setKeyUrl(uploadPath + "/" + entry.getName());
//                log.info("矢量的keyUrl: " + dataVectorEntity.getKeyUrl());
//            }
//            if (entry.getName().endsWith(".json")) {
//                dataVectorEntity.setKeyUrl(uploadPath + "/" + entry.getName());
//                log.info("矢量的jsonUrl: " + dataVectorEntity.getJsonUrl());
//            }
//        }
        dataVectorEntity.setName(fileName);
        dataVectorEntity.setMissionId(fileInfoDto.getMissionId());
        dataVectorEntity.setMissionRecordsId(fileInfoDto.getMissionRecordsId());
        dataVectorEntity.setFileInfoId(fileInfoDto.getFileInfoId());
        dataVectorEntity.setOrgCode(fileInfoDto.getUnitId());
        dataVectorService.save(dataVectorEntity);
        log.info("矢量异步合并分片，并解压文件方法结束");
        return dataVectorEntity.getId();
    }

//    /**
//     * 此方法弃用在v2.2.3后弃用，若后续版本无异常则可以删除，若有异常则去掉该注释并写明方法
//     */
//    @Deprecated
//    public Long savePanorama(FileInfoDto fileInfoDto) {
//        log.info("全景异步合并分片，并解压文件方法开始");
//        String fileName = fileInfoDto.getFileName();
//        String photoUrl = fileInfoDto.getFilePath();
//        String uploadPath = photoUrl.substring(0,photoUrl.lastIndexOf("/"));
//        String saveName = photoUrl.substring(photoUrl.lastIndexOf("/")+1);
//        String unPackPath = "/"+DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
//        ZipFile zipFile = getZipFile(uploadPath, saveName,unPackPath);
//        uploadPath += unPackPath;
//        String xmlName = "";
//        String picName = "";
//        DataPanoramaEntity dataPanoramaEntity = new DataPanoramaEntity();
//        dataPanoramaEntity.setName(fileName);
//        dataPanoramaEntity.setMissionId(fileInfoDto.getMissionId());
//        dataPanoramaEntity.setMissionRecordsId(fileInfoDto.getMissionRecordsId());
//        dataPanoramaEntity.setFileInfoId(fileInfoDto.getFileInfoId());
//        dataPanoramaEntity.setOrgCode(fileInfoDto.getUnitId());
//        for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); ) {
//            ZipEntry entry = e.nextElement();
//            if (entry.getName().endsWith(DataConstant.XML)) {
//                xmlName = entry.getName();
//                // 解析xml文件获取经纬度
//                InputStream inputStream;
//                try {
//                    String filePath = uploadPath + "/" + xmlName;
//                    inputStream = fileManager.getInputSteam(filePath);
//                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                    String line = "";
//                    while ((line = bufferedReader.readLine()) != null) {
//                        if (line.contains("lat=\"") || line.contains("lng=\"")) {
//                            String lat = line.substring(line.indexOf("lat=\"") + 5, line.indexOf("\" lng=\""));
//                            String lng = line.substring(line.indexOf("lng=\"") + 5, line.indexOf("\" heading="));
//                            dataPanoramaEntity.setLat(Double.valueOf(lat));
//                            dataPanoramaEntity.setLog(Double.valueOf(lng));
//                        }
//                    }
//                }catch (Exception e1){
//                    e1.printStackTrace();
//                }
//                dataPanoramaEntity.setKeyUrl(uploadPath + "/" + xmlName);
//                log.info("全景的keyUrl: " + dataPanoramaEntity.getKeyUrl());
//            }
//            if (entry.getName().endsWith("thumb.jpg")) {
//                picName = entry.getName();
//                dataPanoramaEntity.setThumbnailUrl(uploadPath + "/" + picName);
//            }
//            if (xmlName.length() > 0 && picName.length() > 0) {
//                dataPanoramaService.save(dataPanoramaEntity);
//                break;
//            }
//        }
//        log.info("全景异步合并分片，并解压文件方法结束");
//        return dataPanoramaEntity.getId();
//    }

//    /**
//     * 项目上使用，后续会移除
//     * @deprecated 2.2.3
//     */
//    @Deprecated
//    public Long savePollution(FileInfoDto fileInfoDto) {
//        log.info("污染网格异步合并分片，并解压文件方法开始");
//        String fileName = fileInfoDto.getFileName();
//        String photoUrl = fileInfoDto.getFilePath();
//        String uploadPath = photoUrl.substring(0, photoUrl.lastIndexOf(SymbolConstants.POINT));
////        String saveName = photoUrl.substring(photoUrl.lastIndexOf("/")+1);
////        解压zip压缩包
////        String unPackPath = getUnPackPath(DataTypeEnum.POLLUTION_GRID.getValue());
////        UnPackUtil.unPackZip(saveName, uploadPath, null,unPackPath);
////        uploadPath += unPackPath;
////        log.info("污染网格upPath：" + uploadPath);
//        DataPollutionGridEntity dataPollutionGridEntity = new DataPollutionGridEntity();
//        dataPollutionGridEntity.setName(fileName);
//        //dataPollutionGridEntity.setSize(FileUtil.formatFileSize(fileInfo.getFileSize()));
//        dataPollutionGridEntity.setZipUrl(photoUrl);
//        dataPollutionGridEntity.setKeyUrl(uploadPath);
//        dataPollutionGridEntity.setType(fileInfoDto.getPollutionType());
//        dataPollutionGridEntity.setTagId(fileInfoDto.getTagId());
//        dataPollutionGridEntity.setMissionId(fileInfoDto.getMissionId());
//        dataPollutionGridEntity.setMissionRecordsId(fileInfoDto.getMissionRecordsId());
//        dataPollutionGridEntity.setMissionRecordTime(fileInfoDto.getMissionRecordTime());
//        dataPollutionGridEntity.setTaskId(fileInfoDto.getTaskId());
//        dataPollutionGridEntity.setTaskName(fileInfoDto.getTaskName());
//        dataPollutionGridEntity.setFileInfoId(fileInfoDto.getFileInfoId());
//        dataPollutionGridEntity.setOrgCode(fileInfoDto.getUnitId());
//        return dataPollutionGridEntity.getId();
//    }

    public void delDataOrigin(Integer missionRecordsId,Integer dataType){
        List<Integer> ids;
        if(DataTypeEnum.VIDEO.getValue() == dataType){
            ids = missionVideoService.lambdaQuery().eq(MissionVideoEntity::getMissionRecordsId, missionRecordsId).list().stream().map(e->e.getId().intValue()).collect(Collectors.toList());
        }else if(DataTypeEnum.ORTHO.getValue() == dataType){
            ids = dataOrthoService.lambdaQuery().eq(DataOrthoEntity::getMissionRecordsId, missionRecordsId).list().stream().map(e->e.getId().intValue()).collect(Collectors.toList());
        }else if(DataTypeEnum.POINTCLOUD.getValue() == dataType){
            ids = dataPointCloudService.lambdaQuery().eq(DataPointCloudEntity::getMissionRecordsId, missionRecordsId).list().stream().map(e->e.getId().intValue()).collect(Collectors.toList());
        }else if(DataTypeEnum.TILT.getValue() == dataType){
            ids = dataTiltService.lambdaQuery().eq(DataTiltEntity::getMissionRecordsId, missionRecordsId).list().stream().map(e->e.getId().intValue()).collect(Collectors.toList());
        }else if(DataTypeEnum.MULTISPECTRAL.getValue() == dataType){
            ids = dataMultispectralService.lambdaQuery().eq(DataMultispectralEntity::getMissionRecordsId, missionRecordsId).list().stream().map(e->e.getId().intValue()).collect(Collectors.toList());
        }else{
            ids = dataVectorService.lambdaQuery().eq(DataVectorEntity::getMissionRecordsId, missionRecordsId).list().stream().map(e->e.getId().intValue()).collect(Collectors.toList());
        }
        DataReqDto dataReqDto = new DataReqDto();
        dataReqDto.setDataType(dataType);
        dataReqDto.setIds(ids);
        dataReqDto.setIsDelFile(false);
        dataCenterService.delData(dataReqDto);
    }

//    //解压缩
//    public ZipFile getZipFile(String uploadPath, String fileName,String unPackPath) {
//        //解压zip压缩包
////        String savePath = uploadPath + unPackPath;
////        File file=new File(savePath);
////        //不存在
////        if(!file.exists()) {
////            //创建文件夹
////            file.mkdirs();
////        }else {
////            file.delete();
////            file.mkdirs();
////        }
//        UnPackUtil.unPackZip(fileName, uploadPath, null,unPackPath);
//        ZipFile zipFile = null;
//        try {
//            zipFile = new ZipFile(uploadPath + "/" + fileName, Charset.forName(UnPackUtil.codeString(uploadPath + "/" + fileName)));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return zipFile;
//    }
}
