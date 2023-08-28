package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.common.validator.Assert;
import com.imapcloud.nest.enums.DataTypeEnum;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.mapper.DataCenterMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.AirMapDTO;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.reqDto.DataReqDto;
import com.imapcloud.nest.pojo.dto.reqDto.IdenDataDto;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.*;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileStorageOutDO;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.nest.v2.service.DataScenePhotoService;
import com.imapcloud.nest.v2.service.PowerMeterDataService;
import com.imapcloud.nest.v2.service.dto.out.DataScenePhotoOutDTO;
import com.imapcloud.sdk.utils.Base64Util;
import com.imapcloud.sdk.utils.StringUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BadPdfFormatException;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Slf4j
@Service
public class DataCenterServiceImpl extends ServiceImpl<DataCenterMapper, DataCenterEntity> implements DataCenterService {
    @Autowired
    private MissionPhotoService missionPhotoService;
    @Autowired
    private MissionPhotoTagRelService missionPhotoTagRelService;
    @Autowired
    private MissionPhotoTypeRelService missionPhotoTypeRelService;
    @Autowired
    private SysTagService sysTagService;
    @Autowired
    private SysTaskTagService sysTaskTagService;
    @Autowired
    @Lazy
    private MissionVideoService missionVideoService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private MissionService missionService;
    @Autowired
    private MissionRecordsService missionRecordsService;
    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private AirLineService airLineService;
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
    private DataMissionRecordService dataMissionRecordService;
    @Autowired
    private DataTagTypeService dataTagTypeService;
    @Autowired
    private MissionVideoPhotoService missionVideoPhotoService;
    @Autowired
    private FileInfoSrtService fileInfoSrtService;
    @Autowired
    private IllegalPointService illegalPointService;
    @Autowired
    private DataProblemService dataProblemService;
    @Autowired
    private MissionAirService missionAirService;
    @Autowired
    private DataAirService dataAirService;
    @Autowired
    private StationInfraredRecordService stationInfraredRecordService;
    @Autowired
    private DataPollutionGridService dataPollutionGridService;
    @Autowired
    private DataMultispectralService dataMultispectralService;
    @Autowired
    private StationInfraredTiffService stationInfraredTiffService;
    @Autowired
    private SSHHelper sshHelper;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private DataScenePhotoService dataScenePhotoService;

    @Resource
    private PowerMeterDataService powerMeterDataService;

    @Resource
    private FileManager fileManager;

    @Resource
    private UploadManager uploadManager;

    /**
     * 获取标签
     *
     * @param dataType
     * @returnid
     */
    @Override
    public List<SysTagEntity> getTags(Integer dataType, String startTime, String endTime) {

        // 查询用户单位信息
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();

        List<SysTagEntity> tagList = sysTagService.getAllTagsByOrgCode(orgCode);
        if (tagList.size() <= 0) {
            return new ArrayList<>();
        }
        List<SysTaskTagEntity> sysTaskTagEntities = sysTaskTagService.getSysTaskTag();
        if (sysTaskTagEntities.size() > 0) {
            List<MissionRecordsEntity> missionRecordsEntities = getMissionRecords(dataType, startTime, endTime);
            List<Integer> allTaskIds = missionRecordsEntities.stream().map(MissionRecordsEntity::getTaskId).collect(Collectors.toList());
            //根据任务获取标签列表
            sysTaskTagEntities = sysTaskTagEntities.stream().filter(e -> allTaskIds.contains(e.getTaskId())).collect(Collectors.toList());
            Map<Integer, List<Integer>> taskTagMap = sysTaskTagEntities.stream().collect(Collectors.groupingBy(SysTaskTagEntity::getTagId, Collectors.mapping(SysTaskTagEntity::getTaskId, Collectors.toList())));
            List<Integer> tagIds = sysTaskTagEntities.stream().map(SysTaskTagEntity::getTagId).collect(Collectors.toList());
            tagList = tagList.stream().filter(e -> tagIds.contains(e.getId().intValue())).collect(Collectors.toList());
            //标签列表循环，设置missionRecords
            for (SysTagEntity sysTagEntity : tagList) {
                List<Integer> taskIds = taskTagMap.get(sysTagEntity.getId().intValue());
                List<MissionRecordsEntity> collect = missionRecordsEntities.stream().filter(e -> taskIds.contains(e.getTaskId())).collect(Collectors.toList());
                sysTagEntity.setMissionRecordsEntities(collect);
            }

        } else {
            return new ArrayList<>();
        }
        /*if(DataTypeEnum.PHOTO.getValue()==dataType){
            List<MissionPhotoTagRelEntity> missionPhotoTagRelEntities = missionPhotoTagRelService.getMissionPhotoTagList();
            if(missionPhotoTagRelEntities.size()>0) {
                List<Integer> tagIds = missionPhotoTagRelEntities.stream().map(MissionPhotoTagRelEntity::getTagId).collect(Collectors.toList());
                tagList = tagList.stream().filter(e->tagIds.contains(e.getId().intValue())).collect(Collectors.toList());
            }else {
                tagList = new ArrayList<>();
            }
        }*/
        return tagList;
    }

    /**
     * 获取数据
     *
     * @param dataType
     * @param missionRecordsId
     * @return
     */
    @Override
    public JSONArray getData(Integer dataType, Integer missionRecordsId) {
        JSONArray jsonArray = new JSONArray();
        if (DataTypeEnum.PHOTO.getValue() == dataType) {
            List<MissionPhotoEntity> missionPhotoEntities = missionPhotoService.list(new QueryWrapper<MissionPhotoEntity>().eq("deleted", 0).eq("mission_records_id", missionRecordsId));
            if (missionPhotoEntities.size() > 0) {
                jsonArray = JSONArray.parseArray(JSON.toJSONString(missionPhotoEntities));
            }
        } else if (DataTypeEnum.VIDEO.getValue() == dataType) {
            List<MissionVideoEntity> missionVideoEntities = missionVideoService.list(new QueryWrapper<MissionVideoEntity>().eq("deleted", 0).eq("mission_records_id", missionRecordsId));
            if (missionVideoEntities.size() > 0) {
                for (MissionVideoEntity missionVideoEntity : missionVideoEntities) {
                    missionVideoEntity.setVideoUrl(URLEncoder.encode(missionVideoEntity.getVideoUrl()));
                    setSrtJson(missionVideoEntity);
                    setFirstPoint(missionVideoEntity);
                }
                jsonArray = JSONArray.parseArray(JSON.toJSONString(missionVideoEntities));
            }
        } else if (DataTypeEnum.ORTHO.getValue() == dataType) {
            List<DataOrthoEntity> dataOrthoEntities = dataOrthoService.list(new QueryWrapper<DataOrthoEntity>().eq("mission_records_id", missionRecordsId).eq("deleted", 0));
            if (dataOrthoEntities.size() > 0) {
                jsonArray = JSONArray.parseArray(JSON.toJSONString(dataOrthoEntities));
            }
        } else if (DataTypeEnum.POINTCLOUD.getValue() == dataType) {
            List<DataPointCloudEntity> dataPointCloudEntities = dataPointCloudService.list(new QueryWrapper<DataPointCloudEntity>().eq("mission_records_id", missionRecordsId).eq("deleted", 0));
            if (dataPointCloudEntities.size() > 0) {
                jsonArray = JSONArray.parseArray(JSON.toJSONString(dataPointCloudEntities));
            }
        } else if (DataTypeEnum.TILT.getValue() == dataType) {
            List<DataTiltEntity> dataTitlEntities = dataTiltService.list(new QueryWrapper<DataTiltEntity>().eq("mission_records_id", missionRecordsId).eq("deleted", 0));
            if (dataTitlEntities.size() > 0) {
                jsonArray = JSONArray.parseArray(JSON.toJSONString(dataTitlEntities));
            }
        } else if (DataTypeEnum.VECTOR.getValue() == dataType) {
            List<DataVectorEntity> dataVectorEntities = dataVectorService.list(new QueryWrapper<DataVectorEntity>().eq("mission_records_id", missionRecordsId).eq("deleted", 0));
            if (dataVectorEntities.size() > 0) {
                jsonArray = JSONArray.parseArray(JSON.toJSONString(dataVectorEntities));
            }
        } else if (DataTypeEnum.PANORAMA.getValue() == dataType) {
            List<DataPanoramaEntity> dataPanoramaEntities = dataPanoramaService.list(new QueryWrapper<DataPanoramaEntity>().eq("mission_records_id", missionRecordsId).eq("deleted", 0));
            if (dataPanoramaEntities.size() > 0) {
                jsonArray = JSONArray.parseArray(JSON.toJSONString(dataPanoramaEntities));
            }
        } else if (DataTypeEnum.AIR.getValue() == dataType) {
            List<MissionAirEntity> missionAirEntities = missionAirService.list(new QueryWrapper<MissionAirEntity>().eq("mission_records_id", missionRecordsId));
            if (missionAirEntities.size() > 0) {
                jsonArray = JSONArray.parseArray(JSON.toJSONString(missionAirEntities));
            }
        } else if (DataTypeEnum.POLLUTION_GRID.getValue() == dataType) {
            List<DataPollutionGridEntity> dataPollutionGridEntities = dataPollutionGridService.list(new QueryWrapper<DataPollutionGridEntity>().eq("mission_records_id", missionRecordsId));
            if (dataPollutionGridEntities.size() > 0) {
                jsonArray = JSONArray.parseArray(JSON.toJSONString(dataPollutionGridEntities));
            }
        } else if (DataTypeEnum.MULTISPECTRAL.getValue() == dataType) {
            List<DataMultispectralEntity> dataMultispectralEntities = dataMultispectralService.list(new QueryWrapper<DataMultispectralEntity>().eq("mission_records_id", missionRecordsId));
            if (dataMultispectralEntities.size() > 0) {
                jsonArray = JSONArray.parseArray(JSON.toJSONString(dataMultispectralEntities));
            }
        }
        if (jsonArray.size() > 0) {
            List<JSONObject> jsonObjects = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(jsonArray.get(i)));
                jsonObject.put("dataType", dataType);
                jsonObjects.add(jsonObject);
            }
            jsonArray = JSON.parseArray(JSON.toJSONString(jsonObjects));
        }
        return jsonArray;
    }

    /**
     * 设置srt的Json
     *
     * @param missionVideoEntity
     */
    @Override
    public void setSrtJson(MissionVideoEntity missionVideoEntity) {
        String srtUrl = missionVideoEntity.getSrtUrl();
        if(srtUrl == null) {
            srtUrl = missionVideoService.getInspectLatLng(missionVideoEntity);
        }
        if (srtUrl != null) {
            String srtJson = this.getSrtJson(srtUrl);
            if (srtJson != null) {
                missionVideoEntity.setSrtJson(srtJson);
                if (missionVideoEntity.getLat() == null || missionVideoEntity.getLng() == null) {
                    JSONObject jsonObject = JSONObject.parseObject(srtJson);
                    if (jsonObject != null) {
                        String srt = jsonObject.getString(String.valueOf(1));
                        if (srt != null) {
                            String[] split = srt.split(",");
                            double lng = Double.parseDouble(split[0]);
                            double lat = Double.parseDouble(split[1]);
                            missionVideoEntity.setLat(lat);
                            missionVideoEntity.setLng(lng);
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置第一个航点经纬度
     *
     * @param missionVideoEntity
     */
    @Override
    public void setFirstPoint(MissionVideoEntity missionVideoEntity) {
        Double lat = 0D;
        Double lng = 0D;
        if (missionVideoEntity.getLat() == null || missionVideoEntity.getLng() == null) {
            Integer missionId = missionVideoEntity.getMissionId();
            if (missionId != null) {
                MissionEntity missionEntity = missionService.getById(missionId);
                Integer airLineId = missionEntity.getAirLineId();
                if (airLineId != -1) {
                    AirLineEntity airLineEntity = airLineService.getById(airLineId);
                    if (airLineEntity != null) {
                        Integer type = airLineEntity.getType();
                        try {
                            if (type == 1) {
                                String waypoints = airLineEntity.getOriginalWaypoints();
                                JSONArray jsonArray = JSONArray.parseArray(waypoints);
                                JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(0).toString());
                                lat = jsonObject.getDouble("wayPointLatitude");
                                lng = jsonObject.getDouble("wayPointLongitude");
                            } else if (type == 2) {
                                String waypoints = airLineEntity.getWaypoints();
                                JSONArray jsonArray = JSONArray.parseArray(waypoints);
                                JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(0).toString());
                                lat = jsonObject.getDouble("aircraftLocationLatitude");
                                lng = jsonObject.getDouble("aircraftLocationLongitude");
                            } else if (type == 3) {
                                String waypoints = airLineEntity.getWaypoints();
                                JSONArray jsonArray = JSONArray.parseArray(waypoints);
                                JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(0).toString());
                                lat = jsonObject.getDouble("wayPointLatitude");
                                lng = jsonObject.getDouble("wayPointLongitude");
                            }
                        } catch (Exception e) {
                            log.error("转换出错");
                        }
                    }
                }
            }
            missionVideoEntity.setLat(lat);
            missionVideoEntity.setLng(lng);
        }
    }

    @Override
    public List<Map> getTaskMissions(Integer tagId, Integer dataType, String name) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        List<MissionRecordsDto> list = baseMapper.getTaskMissions(tagId, dataType, name, orgCode);
        List<MissionRecordsEntity> missionRecordsEntities = getMissionRecords(dataType, null, null);
        List<Map> mapList = new ArrayList<>();
        if (list.size() > 0) {
            List<Integer> missionRecordsIds = missionRecordsEntities.stream().map(MissionRecordsEntity::getId).collect(Collectors.toList());
            Map<String, List<MissionRecordsDto>> collect = list.stream().collect(Collectors.groupingBy(MissionRecordsDto::getName));
            Iterator<Map.Entry<String, List<MissionRecordsDto>>> iterator = collect.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<MissionRecordsDto>> next = iterator.next();
                String taskName = next.getKey();
                List<MissionRecordsDto> missionRecordsDtos = next.getValue();
                for (MissionRecordsDto missionRecordsDto : missionRecordsDtos) {
                    if (missionRecordsIds.size() > 0 && missionRecordsIds.contains(missionRecordsDto.getMissionRecordsId().intValue())) {
                        missionRecordsDto.setIsData(1);
                    } else {
                        missionRecordsDto.setIsData(0);
                    }
                }
                Map map = new HashMap();
                map.put("taskName", taskName);
                map.put("missionRecordsDtos", missionRecordsDtos);
                mapList.add(map);
            }

        }
        return mapList;
    }

//    @Override
//    public void uploadPointCloudCover(Integer pointCloudId, MultipartFile file) {
//        // 上传封面
//        String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + "-" + file.getOriginalFilename();
//        String uploadPath = "pointCloud/cover/" + pointCloudId + "/";
//        String fileUrl = MinIoUnit.upload(minIoConfig.getBucketName(), file, fileName, uploadPath);
//        // 保存路径
//        dataPointCloudService.lambdaUpdate().set(DataPointCloudEntity::getCoverUrl, globalConfig.getOriginPath() + fileUrl).eq(DataPointCloudEntity::getId, pointCloudId).update();
//    }
//
//    @Override
//    public void uploadOrthoCover(Integer orthoId, MultipartFile file) {
//        // 上传封面
//        String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + "-" + file.getOriginalFilename();
//        String uploadPath = "ortho/cover/" + orthoId + "/";
//        String fileUrl = MinIoUnit.upload(minIoConfig.getBucketName(), file, fileName, uploadPath);
//        // 保存路径
//        dataPointCloudService.lambdaUpdate().set(DataPointCloudEntity::getCoverUrl, globalConfig.getOriginPath() + fileUrl).eq(DataPointCloudEntity::getId, orthoId).update();
//    }

    @Override
    public void relatedMissionRecordIds(Integer dataType, Integer dataId, Integer missionRecordId, List<Integer> photoIdList) {
//        Integer problemSource = DataTypeEnum.ORTHO.getValue() == dataType ? 17 : (DataTypeEnum.POINTCLOUD.getValue() == dataType ? 8 : null);
//        // 删除旧的关联，再增加新的关联
//        missionPhotoTypeRelService.lambdaUpdate().set(MissionPhotoTypeRelEntity::getDeleted, 1).eq(MissionPhotoTypeRelEntity::getType, problemSource)
//                .in(MissionPhotoTypeRelEntity::getMissionPhotoId, photoIdList).update();
//        List<MissionPhotoTypeRelEntity> missionPhotoTypeRelEntityList = new ArrayList<>();
//        photoIdList.forEach(e-> {
//            MissionPhotoTypeRelEntity missionPhotoTypeRelEntity = new MissionPhotoTypeRelEntity();
//            missionPhotoTypeRelEntity.setMissionPhotoId(e);
//            missionPhotoTypeRelEntity.setType(problemSource);
//            missionPhotoTypeRelEntityList.add(missionPhotoTypeRelEntity);
//        });
//        missionPhotoTypeRelService.saveBatch(missionPhotoTypeRelEntityList);
//
//        if (DataTypeEnum.ORTHO.getValue() == dataType) {
//            dataOrthoService.lambdaUpdate().set(DataOrthoEntity::getPhotoRecordId, missionRecordId).eq(DataOrthoEntity::getMissionId, dataId);
//
//        } else if (DataTypeEnum.POINTCLOUD.getValue() == dataType) {
//            dataPointCloudService.lambdaUpdate().set(DataPointCloudEntity::getPhotoRecordId, missionRecordId).eq(DataPointCloudEntity::getMissionId, dataId);
//
//        }

        // 删除旧关联的架次记录
        dataMissionRecordService.lambdaUpdate().set(DataMissionRecordEntity::getDeleted, 1).eq(DataMissionRecordEntity::getDataId, dataId).update();
//        if (missionRecordIdList != null) {
        // 插入新关联的架次记录
//        List<DataMissionRecordEntity> dataMissionRecordEntityList = new ArrayList<>();
//            for (Integer missionRecordId : missionRecordIdList) {
        // 根据架次idList。获取对应的任务信息List
        List<MissionRecordTaskInfoDTO> missionRecordTaskInfoList = baseMapper.getTaskInfoByMissionRecordId(Collections.singletonList(missionRecordId));
        DataMissionRecordEntity dataMissionRecordEntity = new DataMissionRecordEntity();
        dataMissionRecordEntity.setMissionRecordsId(missionRecordId);
        dataMissionRecordEntity.setDataId(dataId);
        dataMissionRecordEntity.setDataType(dataType);
        dataMissionRecordEntity.setTaskId(missionRecordTaskInfoList.get(0).getTaskId());
        dataMissionRecordEntity.setTaskName(missionRecordTaskInfoList.get(0).getTaskName());
        dataMissionRecordEntity.setMissionRecordTime(missionRecordTaskInfoList.get(0).getMissionRecordTime());
//        dataMissionRecordEntityList.add(dataMissionRecordEntity);
//            }
//            dataMissionRecordService.saveBatch(dataMissionRecordEntityList);
//        }
        dataMissionRecordService.save(dataMissionRecordEntity);
    }

    @Override
    public void deletedRelatedMissionRecordIds(Integer dataId, Integer dataType) {
        dataMissionRecordService.lambdaUpdate().set(DataMissionRecordEntity::getDeleted, 1).eq(DataMissionRecordEntity::getDataId, dataId).eq(DataMissionRecordEntity::getDataType, dataType).update();
    }

    /**
     * 统计数据
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public DataTotalDTO getTotal(String startTime, String endTime) {
        List<Integer> missionRecordsIds = getMissionRecordsIds(startTime, endTime);
        DataTotalDTO dataTotalDTO = new DataTotalDTO();
        Integer photoCount = missionPhotoService.count(new QueryWrapper<MissionPhotoEntity>().in("mission_records_id", missionRecordsIds).eq("deleted", 0));
        dataTotalDTO.setPhotos(photoCount);
        Integer videoCount = missionVideoService.count(new QueryWrapper<MissionVideoEntity>().in("mission_records_id", missionRecordsIds).eq("deleted", 0));
        dataTotalDTO.setVideos(videoCount);
        Integer orthoCount = dataOrthoService.count(new QueryWrapper<DataOrthoEntity>().in("mission_records_id", missionRecordsIds).eq("deleted", 0));
        dataTotalDTO.setOrthos(orthoCount);
        Integer pointCloudCount = dataPointCloudService.count(new QueryWrapper<DataPointCloudEntity>().in("mission_records_id", missionRecordsIds).eq("deleted", 0));
        dataTotalDTO.setPointClouds(pointCloudCount);
        Integer tiltCount = dataTiltService.count(new QueryWrapper<DataTiltEntity>().in("mission_records_id", missionRecordsIds).eq("deleted", 0));
        dataTotalDTO.setTilts(tiltCount);
        Integer vectorCount = dataVectorService.count(new QueryWrapper<DataVectorEntity>().in("mission_records_id", missionRecordsIds).eq("deleted", 0));
        dataTotalDTO.setVectors(vectorCount);
        Integer panoramaCount = dataPanoramaService.count(new QueryWrapper<DataPanoramaEntity>().in("mission_records_id", missionRecordsIds).eq("deleted", 0));
        dataTotalDTO.setPanorama(panoramaCount);
        Integer airCount = missionAirService.count(new QueryWrapper<MissionAirEntity>().select("distinct mission_records_id").in("mission_records_id", missionRecordsIds));
        dataTotalDTO.setAirs(airCount);
        Integer multispectralCount = dataMultispectralService.count(new QueryWrapper<DataMultispectralEntity>().in("mission_records_id", missionRecordsIds).eq("deleted", 0));
        dataTotalDTO.setMultispectrals(multispectralCount);
        return dataTotalDTO;
    }

    /**
     * 获取巡检数据
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public Map getInspect(String startTime, String endTime) {
        List<Integer> missionRecordsIds = getMissionRecordsIds(startTime, endTime);
        Map map = new HashMap();
        List<Map> photoMap = missionPhotoService.getInspect(missionRecordsIds);
        map.put("photoMap", photoMap);
        List<Map> videoMap = missionVideoService.getInspect(missionRecordsIds);
        map.put("videoMap", videoMap);
        List<Map> orthoMap = baseMapper.getOrthoInspect(missionRecordsIds);
        map.put("orthoMap", orthoMap);
        List<Map> pointCloudMap = baseMapper.getPointCloudInspect(missionRecordsIds);
        map.put("pointCloudMap", pointCloudMap);
        List<Map> tiltMap = baseMapper.getTiltInspect(missionRecordsIds);
        map.put("tiltMap", tiltMap);
        List<Map> vectorMap = baseMapper.getVectorInspect(missionRecordsIds);
        map.put("vectorMap", vectorMap);
        List<Map> panoramaMap = baseMapper.getPanoramaInspect(missionRecordsIds);
        map.put("panoramaMap", panoramaMap);
        List<Map> multispectralMap = baseMapper.getMultispectralInspect(missionRecordsIds);
        map.put("multispectralMap", multispectralMap);
        return map;
    }

    /**
     * 根据标签获取任务列表
     *
     * @param params
     * @param tagId
     * @param dataType
     * @param name
     * @return
     */
    @Override
    public IPage<MissionRecordsDto> getTask(Map<String, Object> params, Integer tagId, Integer dataType, String name) {
        List<MissionRecordsEntity> missionRecordsEntities = getMissionRecords(dataType, null, null);
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        missionRecordsEntities = missionRecordsEntities.stream()
                .filter(e -> org.springframework.util.StringUtils.hasText(e.getOrgCode()) && e.getOrgCode().startsWith(orgCode))
                .collect(Collectors.toList());
        IPage<MissionRecordsDto> missionRecords = sysTaskTagService.getMissionRecords(params, tagId, dataType, name);
        if (missionRecordsEntities.size() > 0) {
            List<Integer> missionRecordsIds = missionRecordsEntities.stream().map(MissionRecordsEntity::getId).collect(Collectors.toList());
            List<MissionRecordsDto> records = missionRecords.getRecords();
            for (MissionRecordsDto missionRecordsDto : records) {
                if (missionRecordsIds.contains(missionRecordsDto.getMissionRecordsId().intValue())) {
                    missionRecordsDto.setIsData(1);
                } else {
                    missionRecordsDto.setIsData(0);
                }
            }
        }
        return missionRecords;
    }

    /**
     * 保存任务
     *
     * @param taskDataDto
     */
    @Transactional
    @Override
    public void saveTask(TaskDataDto taskDataDto) {
        List<String> names = taskDataDto.getNames();
        for (String name : names) {
            taskDataDto.setName(name);
            saveOrUpdateTask(taskDataDto);
        }
    }

    private void saveOrUpdateTask(TaskDataDto taskDataDto) {
        TaskEntity taskEntity = getTaskEntity(taskDataDto);
        MissionEntity missionEntity = getMissionEntity(taskDataDto, taskEntity);
        //保存记录
        if (taskDataDto.getCreateTime() != null) {
            List<MissionRecordsEntity> missionRecordsEntities = missionRecordsService.list(new QueryWrapper<MissionRecordsEntity>().eq("mission_id", missionEntity.getId()).eq("create_time", taskDataDto.getCreateTime()).eq("deleted", 0));
            if (missionRecordsEntities.size() > 0) {
                return;
                //Assert.failure("该任务下已存在相同时间架次，不予保存");
            }
            MissionRecordsEntity missionRecordsEntity = new MissionRecordsEntity();
            if (taskDataDto.getMissionRecordsId() != null) {
                missionRecordsEntity = missionRecordsService.getById(taskDataDto.getMissionRecordsId());
                updateMissionData(missionEntity.getId(), missionRecordsEntity.getId(), taskDataDto.getDataType());
            }
            missionRecordsEntity.setMissionId(missionEntity.getId());
            LocalDateTime localDateTime = LocalDateTime.parse(taskDataDto.getCreateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            missionRecordsEntity.setCreateTime(localDateTime);
            missionRecordsService.saveOrUpdate(missionRecordsEntity);
        }
    }

    private MissionEntity getMissionEntity(TaskDataDto taskDataDto, TaskEntity taskEntity) {
        MissionEntity missionEntity = missionService.getOne(new QueryWrapper<MissionEntity>().eq("task_id", taskEntity.getId()).eq("deleted", 0));
        //保存架次
        if (missionEntity == null) {
            missionEntity = new MissionEntity();
            missionEntity.setName(taskDataDto.getName());
            missionEntity.setTaskId(taskEntity.getId());
            missionEntity.setAirLineId(-1);
            missionEntity.setMissionParamId(-1);
            missionEntity.setOrgCode(taskEntity.getOrgCode());
            missionService.save(missionEntity);
        }
        return missionEntity;
    }

    private TaskEntity getTaskEntity(TaskDataDto taskDataDto) {
        TaskEntity taskEntity = taskService.getOne(new QueryWrapper<TaskEntity>().eq("name", taskDataDto.getName()).eq("data_type", taskDataDto.getDataType()).eq("tag_id", taskDataDto.getTagId()).eq("deleted", 0));
        if (taskEntity == null) {
            //保存任务
            taskEntity = new TaskEntity();
            taskEntity.setDataType(taskDataDto.getDataType());
            taskEntity.setName(taskDataDto.getName());
            taskEntity.setTagId(taskDataDto.getTagId());

            // 根据标签查询单位ID
            Optional<String> optional = sysTagService.findUnitIdByTagId(taskDataDto.getTagId());
            if(optional.isPresent()){
                taskEntity.setOrgCode(optional.get());
            }else{
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TAG_NOT_ASSOCIATED_WITH_UNIT.getContent()));
            }

            taskService.save(taskEntity);
            SysTaskTagEntity sysTaskTagEntity = new SysTaskTagEntity();
            sysTaskTagEntity.setTagId(taskDataDto.getTagId());
            sysTaskTagEntity.setTaskId(taskEntity.getId());
            sysTaskTagService.save(sysTaskTagEntity);
        }
        return taskEntity;
    }

    private void updateMissionData(Integer missionId, Integer missionRecordsId, Integer dataType) {
        if (DataTypeEnum.PHOTO.getValue() == dataType) {
            missionPhotoService.lambdaUpdate().eq(MissionPhotoEntity::getMissionRecordsId, missionRecordsId).eq(MissionPhotoEntity::getDeleted, 0).set(MissionPhotoEntity::getMissionId, missionId).update();
        } else if (DataTypeEnum.VIDEO.getValue() == dataType) {
            missionVideoService.lambdaUpdate().eq(MissionVideoEntity::getMissionRecordsId, missionRecordsId).eq(MissionVideoEntity::getDeleted, 0).set(MissionVideoEntity::getMissionId, missionId).update();
        } else if (DataTypeEnum.ORTHO.getValue() == dataType) {
            dataOrthoService.lambdaUpdate().eq(DataOrthoEntity::getMissionRecordsId, missionRecordsId).eq(DataOrthoEntity::getDeleted, 0).set(DataOrthoEntity::getMissionId, missionId).update();
        } else if (DataTypeEnum.POINTCLOUD.getValue() == dataType) {
            dataPointCloudService.lambdaUpdate().eq(DataPointCloudEntity::getMissionRecordsId, missionRecordsId).eq(DataPointCloudEntity::getDeleted, 0).set(DataPointCloudEntity::getMissionId, missionId).update();
        } else if (DataTypeEnum.TILT.getValue() == dataType) {
            dataTiltService.lambdaUpdate().eq(DataTiltEntity::getMissionRecordsId, missionRecordsId).eq(DataTiltEntity::getDeleted, 0).set(DataTiltEntity::getMissionId, missionId).update();
        } else if (DataTypeEnum.VECTOR.getValue() == dataType) {
            dataVectorService.lambdaUpdate().eq(DataVectorEntity::getMissionRecordsId, missionRecordsId).eq(DataVectorEntity::getDeleted, 0).set(DataVectorEntity::getMissionId, missionId).update();
        } else if (DataTypeEnum.PANORAMA.getValue() == dataType) {
            dataPanoramaService.lambdaUpdate().eq(DataPanoramaEntity::getMissionRecordsId, missionRecordsId).eq(DataPanoramaEntity::getDeleted, 0).set(DataPanoramaEntity::getMissionId, missionId).update();
        } else if (DataTypeEnum.AIR.getValue() == dataType) {
            missionAirService.lambdaUpdate().eq(MissionAirEntity::getMissionRecordsId, missionRecordsId).set(MissionAirEntity::getMissionId, missionId).update();
        } else if (DataTypeEnum.MULTISPECTRAL.getValue() == dataType) {
            dataMultispectralService.lambdaUpdate().eq(DataMultispectralEntity::getMissionRecordsId, missionRecordsId).eq(DataMultispectralEntity::getDeleted, 0).set(DataMultispectralEntity::getMissionId, missionId).update();
        }

    }


    /**
     * 更新架次
     *
     * @param taskDataDto
     */
    @Transactional
    @Override
    public void updateMission(TaskDataDto taskDataDto) {
        List<String> names = taskDataDto.getNames();
        for (String name : names) {
            taskDataDto.setName(name);
            saveOrUpdateTask(taskDataDto);
        }
    }

    /**
     * 复制架次任务
     *
     * @param taskMissionDto
     */
    @Override
    public void copyTaskMission(TaskMissionDto taskMissionDto) {
        List<Integer> missionRecordsIds = taskMissionDto.getMissionRecordsIds();
        List<Integer> tagIds = taskMissionDto.getTagIds();
        Integer dataType = taskMissionDto.getDataType();
        if (tagIds != null && tagIds.size() > 0) {
            TaskDataDto taskDataDto = new TaskDataDto();
            taskDataDto.setDataType(dataType);
            for (Integer tagId : tagIds) {
                taskDataDto.setTagId(tagId);
                if (missionRecordsIds != null && missionRecordsIds.size() > 0) {
                    List<MissionRecordTaskInfoDTO> missionRecordTaskInfoDTOS = baseMapper.getTaskInfoByMissionRecordId(missionRecordsIds);
                    for (MissionRecordTaskInfoDTO missionRecordTaskInfoDTO : missionRecordTaskInfoDTOS) {
                        taskDataDto.setName(missionRecordTaskInfoDTO.getTaskName());
                        LocalDateTime missionRecordTime = missionRecordTaskInfoDTO.getMissionRecordTime();
                        String format = missionRecordTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        taskDataDto.setCreateTime(format);
                        saveOrUpdateTask(taskDataDto);
                    }
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importAirExcel(MultipartFile multipartFile, Integer missionRecordsId) {
        try {
            List<String> allList = ExcelUtil.importCsv(multipartFile);
            MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(missionRecordsId);
            missionAirService.lambdaUpdate().eq(MissionAirEntity::getMissionRecordsId, missionRecordsId).remove();
            // 读每一行数据
            for (int i = 3; i < allList.size(); i++) {
                // CSV格式文件为逗号分隔符文件，这里根据逗号切分
                String item[] = allList.get(i).split(",");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("VOCs(ppm)", item[7]);
                jsonObject.put("SO2(ppm)", item[8]);
                jsonObject.put("CO(ppm)", item[9]);
                jsonObject.put("NO2(ppm)", item[10]);
                jsonObject.put("O3+NO2(ug/m3)", item[11]);
                jsonObject.put("PM1.0(ug/m3)", item[12]);
                jsonObject.put("PM2.5(ug/m3)", item[13]);
                jsonObject.put("PM10(ug/m3)", item[14]);
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("airData", jsonObject);
                jsonObject1.put("utcTime", item[0]);
                jsonObject1.put("altitude", item[1]);
                jsonObject1.put("longitude", item[2]);
                jsonObject1.put("latitude", item[3]);
                jsonObject1.put("temperature", item[4]);
                jsonObject1.put("humidity", item[5]);
                jsonObject1.put("pressure", item[6]);
                String jsonData = jsonObject1.toJSONString();
                MissionAirEntity missionAirEntity = new MissionAirEntity();
                missionAirEntity.setMissionRecordsId(missionRecordsEntity.getId());
                missionAirEntity.setExecId(missionRecordsEntity.getExecId());
                missionAirEntity.setMissionId(missionRecordsEntity.getMissionId());
                missionAirEntity.setData(jsonData);
                missionAirService.save(missionAirEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否删除标签
     *
     * @param tagId
     * @return
     */
    @Override
    public Boolean delTag(Integer tagId) {
        List<Integer> taskIds = baseMapper.getTaskId(tagId);
        if (taskIds.size() > 0) {
            List<MissionEntity> missionEntities = missionService.list(new QueryWrapper<MissionEntity>().in("task_id", taskIds));
            if (missionEntities.size() > 0) {
                List<Integer> airLineIds = missionEntities.stream().filter(e -> e.getAirLineId() != null && e.getAirLineId() != -1).map(MissionEntity::getAirLineId).collect(Collectors.toList());
                if (airLineIds.size() > 0) {
                    log.info("有航线数据不能删除");
                    return false;
                } else {
                    List<Integer> missionIds = missionEntities.stream().map(MissionEntity::getId).collect(Collectors.toList());
                    //图片
                    List<MissionPhotoEntity> missionPhotoEntities = missionPhotoService.list(new QueryWrapper<MissionPhotoEntity>().in("mission_id", missionIds).eq("deleted", 0));
                    if (missionPhotoEntities.size() > 0) {
                        log.info("有照片数据不能删除");
                        return false;
                    }
                    List<MissionVideoEntity> missionVideoEntities = missionVideoService.list(new QueryWrapper<MissionVideoEntity>().in("mission_id", missionIds).eq("deleted", 0));
                    if (missionVideoEntities.size() > 0) {
                        log.info("有视频数据不能删除");
                        return false;
                    }
                    List<DataPointCloudEntity> dataPointCloudEntities = dataPointCloudService.list(new QueryWrapper<DataPointCloudEntity>().in("mission_id", missionIds).eq("deleted", 0));
                    if (dataPointCloudEntities.size() > 0) {
                        log.info("有点云数据不能删除");
                        return false;
                    }
                    List<DataVectorEntity> dataVectorEntities = dataVectorService.list(new QueryWrapper<DataVectorEntity>().in("mission_id", missionIds).eq("deleted", 0));
                    if (dataVectorEntities.size() > 0) {
                        log.info("有矢量数据不能删除");
                        return false;
                    }
                    List<DataOrthoEntity> dataOrthoEntities = dataOrthoService.list(new QueryWrapper<DataOrthoEntity>().in("mission_id", missionIds).eq("deleted", 0));
                    if (dataOrthoEntities.size() > 0) {
                        log.info("有正射数据不能删除");
                        return false;
                    }
                    List<DataTiltEntity> dataTiltEntities = dataTiltService.list(new QueryWrapper<DataTiltEntity>().in("mission_id", missionIds).eq("deleted", 0));
                    if (dataTiltEntities.size() > 0) {
                        log.info("有倾斜数据不能删除");
                        return false;
                    }
                    List<DataPanoramaEntity> dataPanoramaEntities = dataPanoramaService.list(new QueryWrapper<DataPanoramaEntity>().in("mission_id", missionIds).eq("deleted", 0));
                    if (dataPanoramaEntities.size() > 0) {
                        log.info("有全景数据不能删除");
                        return false;
                    }
                    List<MissionAirEntity> missionAirEntities = missionAirService.list(new QueryWrapper<MissionAirEntity>().in("mission_id", missionIds));
                    if (missionAirEntities.size() > 0) {
                        log.info("有气体数据不能删除");
                        return false;
                    }
                    List<DataMultispectralEntity> dataMultispectralEntities = dataMultispectralService.list(new QueryWrapper<DataMultispectralEntity>().in("mission_id", missionIds).eq("deleted", 0));
                    if (dataMultispectralEntities.size() > 0) {
                        log.info("有正射数据不能删除");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<DataAirEntity> getDataAir(Integer missionRecordsId) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        LambdaQueryWrapper<DataAirEntity> condition = Wrappers.lambdaQuery(DataAirEntity.class)
                .eq(DataAirEntity::getMissionRecordsId, missionRecordsId)
                .likeRight(DataAirEntity::getOrgCode, orgCode)
                .eq(DataAirEntity::getDeleted, false);
        List<DataAirEntity> list = dataAirService.list(condition);
        return list;
    }

    @Override
    public void deletedDataAir(List<Integer> dataAirIdList) {
        dataAirService.removeByIds(dataAirIdList);
    }

    @Override
    public void saveDataAir(DataAirDto dataAirDto) {
        Integer missionRecordsId = dataAirDto.getMissionRecordsId();
        String name = dataAirDto.getName();
        DataAirEntity dataAirEntity = dataAirService.getOne(new QueryWrapper<DataAirEntity>().eq("mission_records_id", missionRecordsId).eq("name", name).eq("deleted", 0));
        if (dataAirEntity == null) {
            dataAirEntity = new DataAirEntity();
            dataAirEntity.setMissionRecordsId(missionRecordsId);
            MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(missionRecordsId);
            dataAirEntity.setMissionId(missionRecordsEntity.getMissionId());
            MissionEntity missionEntity = missionService.getById(missionRecordsEntity.getMissionId());
            dataAirEntity.setOrgCode(missionEntity.getOrgCode());
            dataAirEntity.setName(name);
            dataAirService.save(dataAirEntity);
        }
        //存储在服务器的地址
        String realPath = geoaiUosProperties.getStore().getThumbnailPath() + "dataAir/" + dataAirEntity.getMissionRecordsId() + "/" + name + ".jpg";
        String base64Data = dataAirDto.getBase64Code().split(",")[1];
        byte[] bytes = Base64.getDecoder().decode(base64Data);
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            CommonFileInDO commonFileInDO = new CommonFileInDO();
            commonFileInDO.setInputStream(is);
            commonFileInDO.setFileName(realPath);
            Optional<FileStorageOutDO> fileStorageResult = uploadManager.uploadFile(commonFileInDO);
            if (fileStorageResult.isPresent()) {
                FileStorageOutDO storage = fileStorageResult.get();
                String filename = storage.getFilename();
                String storagePath = storage.getStoragePath();
                dataAirEntity.setRealPath(storagePath + "/" + filename);
            }
        } catch (IOException e) {
            log.error("DataAirImage upload error:" + e);
        }
        dataAirService.updateById(dataAirEntity);
    }

    @SneakyThrows
    @Override
    public void exportDataAir(String dataStr, HttpServletResponse response) {
        JSONObject dataObject = JSONObject.parseObject(dataStr);
        Integer missionRecordsId = dataObject.getInteger("missionRecordsId");
        String avgStr = dataObject.getString("avgStr");
        String areaStr = dataObject.getString("areaStr");
        String lngLatStr = dataObject.getString("lngLatStr");
        String styleName = dataObject.getString("styleName");
        String zipName = dataObject.getString("zipName");
        // 下载文件
        String fileName = null;
        try {
            fileName = URLEncoder.encode(zipName + ".zip", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/force-download");
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);

        //CSV
        String[] headerArr = {"#Created by: Sniffer4DMapper 2.2.06.29", "项目名称 :气体监测总览表格"};
        String[] tableHeaderArr = {"采样点时间", "相对高度 m", "经度", "纬度", "温度 ℃", "湿度 %", "气压 Pa", "VOCs ppm", "SO2 μg/m3", "CO mg/m3", "NO2 μg/m3", "O3+NO2 μg/m3", "PM1.0 μg/m3", "PM2.5 μg/m3", "PM10 μg/m3", "设备序列号"};
        List<String> cellList = new ArrayList();
        List<MissionAirEntity> missionAirEntities = missionAirService.list(new QueryWrapper<MissionAirEntity>().eq("mission_records_id", missionRecordsId));
        if (missionAirEntities.size() <= 0) {
            return;
        }
        for (MissionAirEntity missionAirEntity : missionAirEntities) {
            if (missionAirEntity.getData() != null) {
                JSONObject jsonObject = JSONObject.parseObject(missionAirEntity.getData());
                JSONObject airObject = jsonObject.getJSONObject("airData");
                String airData = jsonObject.getString("utcTime")
                        + "," + jsonObject.getString("altitude")
                        + "," + jsonObject.getString("longitude")
                        + "," + jsonObject.getString("latitude")
                        + "," + jsonObject.getString("temperature")
                        + "," + jsonObject.getString("humidity")
                        + "," + jsonObject.getString("pressure")
                        + "," + airObject.getString("VOCs(ppm)")
                        + "," + airObject.getString("SO2(ppm)")
                        + "," + airObject.getString("NO2(ppm)")
                        + "," + airObject.getString("CO(ppm)")
                        + "," + airObject.getString("O3+NO2(ug/m3)")
                        + "," + airObject.getString("PM1.0(ug/m3)")
                        + "," + airObject.getString("PM2.5(ug/m3)")
                        + "," + airObject.getString("PM10(ug/m3)");
                cellList.add(airData);
            }
        }
        byte[] bytes = ExcelUtil.writeDataAfterToBytes(headerArr, tableHeaderArr, cellList);
        //pdf
        Map<String, Object> data = new HashMap<>();
        missionAirEntities = missionAirEntities.stream().filter(e -> StringUtil.isNotEmpty(e.getData())).collect(Collectors.toList());
        //"温度","湿度","VOCs","SO2","CO","NO2","O3+NO2","PM1.0","PM2.5","PM10"
        String[] typeNames = {"温度", "湿度", "VOCs", "SO2", "CO", "NO2", "O3+NO2", "PM1.0", "PM2.5", "PM10"};
        String num[] = {"二", "三", "四", "五", "六", "七", "八", "九", "十", "十一"};
        //String[] typeNames = {"O3+NO2"};
        data.put("avgStr", avgStr);
        data.put("areaStr", areaStr);
        data.put("lngLatStr", lngLatStr);
        data.put("size", missionAirEntities.size());
        ByteArrayOutputStream baos = null;
        try {
            @Cleanup
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            zos.putNextEntry(new ZipEntry("附件一：气体监测总览表格.CSV"));
            zos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            zos.write(bytes);
            //pdf
            for (int i = 0; i < typeNames.length; i++) {
                String typeName = typeNames[i];
                zos.putNextEntry(new ZipEntry(i + 2 + "、附件" + num[i] + "：气体监测-" + typeName + ".pdf"));
                DataAirEntity dataAirEntity = dataAirService.getOne(new QueryWrapper<DataAirEntity>().eq("mission_records_id", missionRecordsId).eq("name", typeName + "-" + styleName).eq("deleted", 0));
                data.put("num", num[i]);
                data.put("typeName", typeName);
                data.put("maxValue", dataObject.getString(typeName + "-max"));
                data.put("minValue", dataObject.getString(typeName + "-min"));
                String image = "";
                if (dataAirEntity != null) {
                    image = Base64Util.getImageBase64(fileManager.getInputSteam(dataAirEntity.getRealPath()));
                }
                data.put("image", image);
                setDataValue(data, typeName, missionAirEntities);
                baos = PDFTemplateUtil.createPDF(data, "test.ftl", geoaiUosProperties.getFont().getPath() + "simsun.ttc");
                baos.writeTo(zos);
                baos.close();
            }
            zos.closeEntry();
            zos.close();
        } catch (IOException e) {
            log.error("iostream error:{}", e.getMessage(), e);
        } catch (BadPdfFormatException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void pollutionGridRename(Integer pollutionGridId, String name) {
        dataPollutionGridService.lambdaUpdate().eq(DataPollutionGridEntity::getId, pollutionGridId).set(DataPollutionGridEntity::getName, name).update();
    }

    private void setDataValue(Map<String, Object> data, String typeName, List<MissionAirEntity> missionAirEntities) {
        //"温度","湿度","VOCs","SO2","CO","NO2","O3+NO2","PM1.0","PM2.5","PM10"
        if (typeName.equals("温度")) {
            data.put("analyze", "热敏电阻");
            getDataValue(data, missionAirEntities, "temperature", "℃");
        } else if (typeName.equals("湿度")) {
            data.put("analyze", "湿敏电阻");
            getDataValue(data, missionAirEntities, "humidity", "%");
        } else if (typeName.equals("VOCs")) {
            data.put("analyze", "光离子化检测（PID）");
            getDataValue(data, missionAirEntities, "VOCs(ppm)", "ppm");
        } else if (typeName.equals("SO2")) {
            data.put("analyze", "电化学");
            getDataValue(data, missionAirEntities, "SO2(ppm)", "μg/m³");
        } else if (typeName.equals("CO")) {
            data.put("analyze", "电化学");
            getDataValue(data, missionAirEntities, "CO(ppm)", "mg/m³");
        } else if (typeName.equals("NO2")) {
            data.put("analyze", "电化学");
            getDataValue(data, missionAirEntities, "NO2(ppm)", "μg/m³");
        } else if (typeName.equals("O3+NO2")) {
            data.put("analyze", "电化学");
            getDataValue(data, missionAirEntities, "O3+NO2(ug/m3)", "μg/m³");
        } else if (typeName.equals("PM1.0")) {
            data.put("analyze", "激光散射");
            getDataValue(data, missionAirEntities, "PM1.0(ug/m3)", "μg/m³");
        } else if (typeName.equals("PM2.5")) {
            data.put("analyze", "激光散射");
            getDataValue(data, missionAirEntities, "PM2.5(ug/m3)", "μg/m³");
        } else if (typeName.equals("PM10")) {
            data.put("analyze", "激光散射");
            getDataValue(data, missionAirEntities, "PM10(ug/m3)", "μg/m³");
        }
    }

    private void getDataValue(Map<String, Object> data, List<MissionAirEntity> missionAirEntities, String name, String unitName) {
        List<AirMapDTO> airMapDTOS = missionAirEntities.stream().map(e -> {
            AirMapDTO airMapDTO = new AirMapDTO();
            JSONObject jsonObject = JSONObject.parseObject(e.getData());
            airMapDTO.setUtcTime(jsonObject.getString("utcTime"));
            airMapDTO.setLat(jsonObject.getString("latitude"));
            airMapDTO.setLng(jsonObject.getString("longitude"));
            if (name.equals("SO2(ppm)") || name.equals("CO(ppm)") || name.equals("O3+NO2(ug/m3)") || name.equals("PM1.0(ug/m3)") || name.equals("PM2.5(ug/m3)") || name.equals("PM10(ug/m3)") || name.equals("VOCs(ppm)") || name.equals("NO2(ppm)")) {
                JSONObject airObject = jsonObject.getJSONObject("airData");
                if (name.equals("O3+NO2(ug/m3)")) {
                    Double o3 = airObject.getDouble("Ox(ppm)") != null ? airObject.getDouble("Ox(ppm)") : airObject.getDouble("O3+NO2(ug/m3)");
                    airMapDTO.setValue(o3);
                } else {
                    airMapDTO.setValue(airObject.getDouble(name));
                }
            } else {
                airMapDTO.setValue(jsonObject.getDouble(name));
            }
            return airMapDTO;
        }).collect(Collectors.toList());
        data.put("startTime", airMapDTOS.get(0).getUtcTime());
        data.put("endTime", airMapDTOS.get(airMapDTOS.size() - 1).getUtcTime());
        List<AirMapDTO> collect = airMapDTOS.stream().sorted(Comparator.comparing(AirMapDTO::getValue)).collect(Collectors.toList());
        double average = collect.stream().mapToDouble(AirMapDTO::getValue).average().getAsDouble();
        data.put("average", average);
        data.put("min", collect.get(0).getValue() + unitName);
        data.put("minLng", getThree(collect.get(0).getLat()));
        data.put("minLat", getThree(collect.get(0).getLng()));
        data.put("minInspectTime", collect.get(0).getUtcTime());
        data.put("max", collect.get(collect.size() - 1).getValue() + unitName);
        data.put("maxLng", getThree(collect.get(collect.size() - 1).getLat()));
        data.put("maxLat", getThree(collect.get(collect.size() - 1).getLng()));
        data.put("maxInspectTime", collect.get(collect.size() - 1).getUtcTime());
    }

    public double getThree(String str) {
        double d = Double.parseDouble(str);
        BigDecimal b = new BigDecimal(d);
        d = b.setScale(3, BigDecimal.ROUND_DOWN).doubleValue();
        return d;
    }

    /**
     * 推送到分析
     *
     * @param idenDataDto
     */
//    @Transactional(rollbackFor = Exception.class)
    @Override
    public DataScenePhotoOutDTO.PushOut push(IdenDataDto idenDataDto) {
        log.info("#DataCenterServiceImpl.push# idenDataDto={}", idenDataDto);
        Integer tagId = idenDataDto.getTagId();
        List<Integer> photoIds = idenDataDto.getPhotoIds();
        Integer infraredType = idenDataDto.getInfraredType();
        // 推送分析的类型（2-红外； 3-排污监测）
        List<Integer> idenIds = idenDataDto.getIdenIds();

        // 视频的照片
        photoIds = dealVideoPhoto(idenDataDto, photoIds);
        log.info("#DataCenterServiceImpl.push# idenDataDto={}, photoIds={}", idenDataDto, photoIds);
        DataScenePhotoOutDTO.PushOut result = null;
        // 判断是否是分析统计-综合
        if (idenIds.contains(RoleIdenValueEnum.ANALYSIS_STATISTICS_ZH.getIdenValue())) {
            // 推送到数据中心
            try {
                result = dataScenePhotoService.push(photoIds);
            } catch (Exception e) {
                throw new NestException(e.getMessage());
            }
        }
        // 判断是否是分析统计-电力-表计读数
        if (idenIds.contains(RoleIdenValueEnum.ABNORMAL_FIND_DL_BJDS_NEW.getIdenValue())) {
            // 推送到数据中心
            try {
                result = powerMeterDataService.manualPush(photoIds);
            } catch (Exception e) {
                throw new NestException(e.getMessage());
            }
        }

        // 判断是否是分析统计-电力-红外测温
        if (idenIds.contains(RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW.getIdenValue())) {
            // 推送到数据中心
            try {
                result = powerMeterDataService.manualPushInfrared(photoIds);
            } catch (Exception e) {
                throw new NestException(e.getMessage());
            }
        }

        // 判断是否是分析统计-电力-缺陷识别
        if (idenIds.contains(RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW.getIdenValue())) {
            // 推送到数据中心
            try {
                result = powerMeterDataService.manualPushDefect(photoIds);
            } catch (Exception e) {
                throw new NestException(e.getMessage());
            }
        }

        log.info("#DataCenterServiceImpl.push#");
        // 推送到红外的情况
        handlerStationInfraredRecord(tagId, photoIds, idenIds, infraredType);
        log.info("#DataCenterServiceImpl.push#");
        // 推送到排污监测的情况
        handlerolPlutionCheck(photoIds, idenIds, infraredType);
        log.info("#DataCenterServiceImpl.push#");

        // 先将旧的数据删除，再入库，防止重复
        missionPhotoTagRelService.deleteByTagIdAndPhotoIdList(tagId, photoIds);
        if (ToolUtil.isNotEmpty(idenIds)) {
            missionPhotoTypeRelService.deleteByTypeIdAndPhotoIdList(idenIds, photoIds);
        }
        for (Integer photoId : photoIds) {
            MissionPhotoTagRelEntity missionPhotoTagRelEntity = new MissionPhotoTagRelEntity();
            missionPhotoTagRelEntity.setTagId(tagId);
            missionPhotoTagRelEntity.setMissionPhotoId(photoId);
            missionPhotoTagRelService.save(missionPhotoTagRelEntity);
            for (Integer idenId : idenIds) {
                MissionPhotoTypeRelEntity missionPhotoTypeRelEntity = new MissionPhotoTypeRelEntity();
                missionPhotoTypeRelEntity.setMissionPhotoId(photoId);
                missionPhotoTypeRelEntity.setType(idenId);
                missionPhotoTypeRelService.save(missionPhotoTypeRelEntity);
            }
        }
        log.info("#DataCenterServiceImpl.push#");
        if (result == null) {
            result = new DataScenePhotoOutDTO.PushOut();
        }
        return result;
    }

    @Resource
    private RedisService redisService;

    /**
     * 视频的照片推送分析的情况
     *
     * @param idenDataDto
     * @param photoIds
     * @return
     */
    private List<Integer> dealVideoPhoto(IdenDataDto idenDataDto, List<Integer> photoIds) {
        if (DataTypeEnum.VIDEO.getValue() != idenDataDto.getDataType()) {
            return photoIds;
        }
        List<MissionVideoPhotoEntity> entityList = missionVideoPhotoService.list(new QueryWrapper<MissionVideoPhotoEntity>().in("id", photoIds).eq("deleted", 0));
        if (CollUtil.isEmpty(entityList)) {
            return photoIds;
        }
        Integer missionRecordsId = entityList.get(0).getMissionRecordsId();
        String lockKey = String.format("DataCenterServiceImpl:dealVideoPhoto:%s", missionRecordsId);
        String uuid = BizIdUtils.randomUuid();
        try {

            if (!redisService.tryLock(lockKey, uuid, 5, TimeUnit.MINUTES)) {
                throw new NestException("请不要重复推送");
            }
            // missionVideoPhoto转missionPhoto
            List<MissionPhotoEntity> missionPhotoEntityList = missionPhotoService.list(Wrappers.lambdaQuery(MissionPhotoEntity.class)
                    .in(MissionPhotoEntity::getVideoPhotoId, photoIds)
                    .eq(MissionPhotoEntity::getDeleted, 0));
            Map<Integer, MissionPhotoEntity> collect = missionPhotoEntityList.stream().collect(Collectors.toMap(MissionPhotoEntity::getVideoPhotoId, bean -> bean, (key1, key2) -> key1));
            List<Integer> insertList = Lists.newLinkedList();
            List<Integer> resultList = Lists.newLinkedList();
            for (Integer photoId : photoIds) {
                MissionPhotoEntity missionPhotoEntity = collect.get(photoId);
                if (missionPhotoEntity != null) {
                    resultList.add(missionPhotoEntity.getId().intValue());
                } else {
                    insertList.add(photoId);
                }
            }
            if (CollUtil.isEmpty(insertList)) {
                return resultList;
            }
            List<MissionVideoPhotoEntity> missionVideoPhotoEntities = missionVideoPhotoService.list(new QueryWrapper<MissionVideoPhotoEntity>().in("id", insertList).eq("deleted", 0));
            photoIds = missionVideoPhotoEntities.stream().map(e -> {
                MissionPhotoEntity missionPhotoEntity = new MissionPhotoEntity();
                // 图片设备成功，保存入库
                missionPhotoEntity.setName(e.getName());
                missionPhotoEntity.setPhotoUrl(e.getPhotoUrl());
                missionPhotoEntity.setThumbnailName(e.getName());
                missionPhotoEntity.setThumbnailUrl(e.getThumbnailUrl());
                missionPhotoEntity.setDeviceUuid("124");
                missionPhotoEntity.setTimeCreated(LocalDateTime.now());
                missionPhotoEntity.setSource(3);
                missionPhotoEntity.setLatitude(e.getLatitude());
                missionPhotoEntity.setLongitude(e.getLongitude());
                missionPhotoEntity.setMissionId(e.getMissionId());
                missionPhotoEntity.setMissionRecordsId(e.getMissionRecordsId());
                missionPhotoEntity.setVideoPhotoId(e.getId().intValue());
                missionPhotoEntity.setCreateTime(e.getCreateTime());
                missionPhotoEntity.setModifyTime(e.getModifyTime());
                missionPhotoService.save(missionPhotoEntity);
                return missionPhotoEntity.getId().intValue();
            }).collect(Collectors.toList());
            resultList.addAll(photoIds);
            return resultList;
        } finally {
            redisService.releaseLock(lockKey, uuid);
        }
    }

    //推送到红外识别时特殊处理
    private void handlerStationInfraredRecord(Integer tagId, List<Integer> photoIds, List<Integer> idenIds, Integer infraredType) {
        if (!idenIds.contains(2)) {
            return;
        }
        idenIds.remove((Object) 2);
        List<MissionPhotoEntity> missionVideoPhotoEntities = missionPhotoService.list(new QueryWrapper<MissionPhotoEntity>().in("id", photoIds).eq("deleted", 0));
        Map<String, List<MissionPhotoEntity>> maps = new HashMap<>();

        File file = null;
        Metadata metadata = null;
        Directory directory = null;
        List<MissionPhotoEntity> lists = null;
        String strWidth = "", strHeight = "";
        for (MissionPhotoEntity missionVideoPhotoEntitie : missionVideoPhotoEntities) {
            file = fileManager.createTempFile(missionVideoPhotoEntitie.getPhotoUrl());
            try {
                metadata = JpegMetadataReader.readMetadata(file);
                directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                Iterator<Tag> iterator = directory.getTags().iterator();
                missionVideoPhotoEntitie.setDeviceUuid("");
                strWidth = "";
                strHeight = "";
                while (iterator.hasNext()) {
                    Tag tag = iterator.next();
                    if (tag.getTagName().equals("Date/Time Original")) {
                        missionVideoPhotoEntitie.setCreateTimeStr(tag.getDescription().trim());
                    } else if (tag.getTagName().equals("Exif Image Width")) {
                        strWidth = tag.getDescription().replace("pixels", "").trim();
                    } else if (tag.getTagName().equals("Exif Image Height")) {
                        strHeight = tag.getDescription().replace("pixels", "").trim();
                    }
                }
                missionVideoPhotoEntitie.setDeviceUuid("_" + strWidth + "_" + strHeight);
                //大疆
                if (infraredType == 0) {
                    if (missionVideoPhotoEntitie.getCreateTimeStr() == null)
                        continue;
                    lists = maps.get(missionVideoPhotoEntitie.getCreateTimeStr());
                    if (lists == null) {
                        lists = new ArrayList<>();
                        maps.put(missionVideoPhotoEntitie.getCreateTimeStr(), lists);
                    }
                    lists.add(missionVideoPhotoEntitie);
                }
                //道通
                if (infraredType == 1) {
                    if (missionVideoPhotoEntitie.getLatitude() == null)
                        continue;
                    lists = maps.get(missionVideoPhotoEntitie.getLatitude().toString());
                    if (lists == null) {
                        lists = new ArrayList<>();
                        maps.put(missionVideoPhotoEntitie.getLatitude().toString(), lists);
                    }
                    lists.add(missionVideoPhotoEntitie);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        stationInfraredRecordService.lambdaUpdate().set(StationInfraredRecordEntity::getDeleted, true).eq(StationInfraredRecordEntity::getDeleted, false)
                .in(StationInfraredRecordEntity::getPhotoId, missionVideoPhotoEntities.stream().map(MissionPhotoEntity::getId).collect(Collectors.toList())).update();
        List<StationInfraredRecordEntity> stationInfraredRecordEntitys = new ArrayList<>();
        List<Integer> newPhotoIds = new ArrayList<>();
        List<StationInfraredTiffEntity> stationInfraredTiffEntities = stationInfraredTiffService.lambdaQuery().eq(StationInfraredTiffEntity::getDeleted, 0).list();
        Iterator<Map.Entry<String, List<MissionPhotoEntity>>> iterator = maps.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<MissionPhotoEntity>> next = iterator.next();
            String key = next.getKey();
            List<MissionPhotoEntity> value = next.getValue();
            StationInfraredRecordEntity stationInfraredRecordEntity = new StationInfraredRecordEntity();
            for (MissionPhotoEntity missionPhotoEntity : value) {
                //if(missionPhotoEntity.getDeviceUuid().equals("_640_512")){
                if (missionPhotoEntity.getDeviceUuid().startsWith("_640") || missionPhotoEntity.getDeviceUuid().startsWith("_512")) {
                    Long id = missionPhotoEntity.getId();
                    stationInfraredRecordEntity.setPhotoId(id.intValue());
                    newPhotoIds.add(stationInfraredRecordEntity.getPhotoId());
                    //1是道通、0是大疆
                    if (infraredType == 1) {
                        List<StationInfraredTiffEntity> infraredTiffEntityList = stationInfraredTiffEntities.stream().filter(
                                e -> e.getMissionId().equals(missionPhotoEntity.getMissionId())
                                        && e.getMissionRecordsId().equals(missionPhotoEntity.getMissionRecordsId())
                                        && e.getLatitude().equals(missionPhotoEntity.getLatitude())
                        ).collect(Collectors.toList());
                        if (infraredTiffEntityList.size() > 0) {
                            stationInfraredRecordEntity.setTiffUrl(infraredTiffEntityList.get(0).getPhotoUrl());
                        } else {
                            Assert.failure("没有tiff不能推送道通");
                        }
                        stationInfraredRecordEntity.setSunUrl(missionPhotoEntity.getPhotoUrl());
                    }
                } else if (missionPhotoEntity.getDeviceUuid().startsWith("_3840") || missionPhotoEntity.getDeviceUuid().startsWith("_2160")
                        || missionPhotoEntity.getDeviceUuid().startsWith("_8000") || missionPhotoEntity.getDeviceUuid().startsWith("_6000")
                        || missionPhotoEntity.getDeviceUuid().startsWith("_1280") || missionPhotoEntity.getDeviceUuid().startsWith("_720")) {
                    if (infraredType == 0) {
                        stationInfraredRecordEntity.setSunUrl(missionPhotoEntity.getPhotoUrl());
                    }
                    stationInfraredRecordEntity.setPipUrl(missionPhotoEntity.getPhotoUrl());
                }
            }
            if (stationInfraredRecordEntity.getPhotoId() != null) {
                stationInfraredRecordEntitys.add(stationInfraredRecordEntity);
            } else {
                log.info(key);
            }
        }
        if (ToolUtil.isNotEmpty(stationInfraredRecordEntitys)) {
            stationInfraredRecordService.saveBatch(stationInfraredRecordEntitys);
        }
        if (!ToolUtil.isNotEmpty(newPhotoIds))
            return;
        // 先将旧的数据删除，再入库，防止重复
        missionPhotoTagRelService.deleteByTagIdAndPhotoIdList(tagId, newPhotoIds);
        missionPhotoTypeRelService.deleteByTypeIdAndPhotoIdList(Arrays.asList(2), newPhotoIds);
        for (Integer photoId : newPhotoIds) {
            MissionPhotoTypeRelEntity missionPhotoTypeRelEntity = new MissionPhotoTypeRelEntity();
            missionPhotoTypeRelEntity.setMissionPhotoId(photoId);
            missionPhotoTypeRelEntity.setType(2);
            missionPhotoTypeRelService.save(missionPhotoTypeRelEntity);
        }

    }

    /**
     * 推送到排污监测时特处理
     *
     * @param photoIds
     * @param idenIds
     */
    private void handlerolPlutionCheck(List<Integer> photoIds, List<Integer> idenIds, Integer infraredType) {
        if (idenIds.contains(3)) {
            // 解析照片，获取Map：时间-照片
            Map<String, List<MissionPhotoEntity>> maps = getTimePhotoListMap(photoIds, infraredType);

            List<Integer> newPhotoIds = new ArrayList<>();
            List<MissionPhotoEntity> missionPhotoEntityList = new ArrayList<>();
            for (String key : maps.keySet()) {
                int num = maps.get(key).get(0).getId().intValue();
                newPhotoIds.add(num);
                for (MissionPhotoEntity missionPhotoEntity : maps.get(key)) {
                    if (missionPhotoEntity.getDeviceUuid().startsWith("_640")) {
                        // 设置照片类型为红外
                        missionPhotoEntity.setPhotoType(1);
                        missionPhotoEntity.setWaypointIndex(num);
                    } else {
                        // 设置照片类型为红外可见光
                        missionPhotoEntity.setPhotoType(0);
                        missionPhotoEntity.setWaypointIndex(num);
                    }
                    missionPhotoEntityList.add(missionPhotoEntity);
                }
            }
            if (ToolUtil.isNotEmpty(missionPhotoEntityList)) {
                missionPhotoService.updateBatchById(missionPhotoEntityList);
            }
            if (!ToolUtil.isNotEmpty(newPhotoIds)) {
                return;
            }
            // 先将旧的数据删除，再入库，防止重复
            missionPhotoTypeRelService.deleteByTypeIdAndPhotoIdList(Arrays.asList(3), newPhotoIds);
            for (Integer photoId : newPhotoIds) {
                MissionPhotoTypeRelEntity missionPhotoTypeRelEntity = new MissionPhotoTypeRelEntity();
                missionPhotoTypeRelEntity.setMissionPhotoId(photoId);
                missionPhotoTypeRelEntity.setType(3);
                missionPhotoTypeRelService.save(missionPhotoTypeRelEntity);
            }
            idenIds.remove((Object) 3);
        }
    }

    @Override
    public void pushData(IdenDataDto idenDataDto) {
        Integer tagId = idenDataDto.getTagId();
        Integer dataType = idenDataDto.getDataType();
        List<Integer> dataIdList = idenDataDto.getDataIds();
        List<Integer> problemSourceList = idenDataDto.getIdenIds();

        // 删除旧的关联，增加新关联
        dataTagTypeService.lambdaUpdate().set(DataTagTypeEntity::getDeleted, 1).eq(DataTagTypeEntity::getTagId, tagId)
                .in(DataTagTypeEntity::getDataId, dataIdList).in(DataTagTypeEntity::getType, problemSourceList).eq(DataTagTypeEntity::getDataType, dataType).update();
        List<DataTagTypeEntity> dataTagTypeEntityList = new ArrayList<>();

        for (Integer dataId : dataIdList) {
            for (Integer problemSource : problemSourceList) {
                DataTagTypeEntity dataTagTypeEntity = new DataTagTypeEntity();
                dataTagTypeEntity.setDataType(dataType);
                dataTagTypeEntity.setDataId(dataId);
                dataTagTypeEntity.setTagId(tagId);
                dataTagTypeEntity.setType(problemSource);
                dataTagTypeEntityList.add(dataTagTypeEntity);
            }
        }
        dataTagTypeService.saveBatch(dataTagTypeEntityList);
    }

    /**
     * 删除架次
     *
     * @param dataReqDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delTask(DataReqDto dataReqDto) {
        Boolean isDelFile = dataReqDto.getIsDelFile();
        if (isDelFile == null) {
            isDelFile = false;
        }
        List<Integer> missionRecordsIds = dataReqDto.getMissionRecordsIds();
        Integer dataType = dataReqDto.getDataType();
        missionRecordsService.lambdaUpdate().set(MissionRecordsEntity::getDeleted, 1).in(MissionRecordsEntity::getId, missionRecordsIds).update();
        if (DataTypeEnum.PHOTO.getValue() == dataType) {
            List<MissionPhotoEntity> photoEntities = missionPhotoService.list(new QueryWrapper<MissionPhotoEntity>().in("mission_records_id", missionRecordsIds));
            if (ToolUtil.isNotEmpty(photoEntities)) {
                deletePhoto(photoEntities, isDelFile);
            }
        } else if (DataTypeEnum.VIDEO.getValue() == dataType) {
            List<MissionVideoEntity> videoEntityList = missionVideoService.list(new QueryWrapper<MissionVideoEntity>().in("mission_records_id", missionRecordsIds));
            if (ToolUtil.isNotEmpty(videoEntityList)) {
                deleteVideo(videoEntityList, missionRecordsIds, isDelFile);
            }

        } else if (DataTypeEnum.ORTHO.getValue() == dataType) {
            List<DataOrthoEntity> orthoEntityList = dataOrthoService.list(new QueryWrapper<DataOrthoEntity>().in("mission_records_id", missionRecordsIds));
            if (ToolUtil.isNotEmpty(orthoEntityList)) {
                deleteOrtho(orthoEntityList, missionRecordsIds, dataType, isDelFile);
            }
        } else if (DataTypeEnum.POINTCLOUD.getValue() == dataType) {
            List<DataPointCloudEntity> dataPointCloudEntityList = dataPointCloudService.list(new QueryWrapper<DataPointCloudEntity>().in("mission_records_id", missionRecordsIds));
            if (ToolUtil.isNotEmpty(dataPointCloudEntityList)) {
                deletePointCloud(dataPointCloudEntityList, missionRecordsIds, dataType, isDelFile);
            }
        } else if (DataTypeEnum.TILT.getValue() == dataType) {
            List<DataTiltEntity> tiltEntityList = dataTiltService.list(new QueryWrapper<DataTiltEntity>().in("mission_records_id", missionRecordsIds));
            if (ToolUtil.isNotEmpty(tiltEntityList)) {
                deleteTilt(tiltEntityList, isDelFile);
            }
        } else if (DataTypeEnum.VECTOR.getValue() == dataType) {
            List<DataVectorEntity> dataVectorEntityList = dataVectorService.list(new QueryWrapper<DataVectorEntity>().in("mission_records_id", missionRecordsIds));
            if (ToolUtil.isNotEmpty(dataVectorEntityList)) {
                deleteVector(dataVectorEntityList, isDelFile);
            }
        } else if (DataTypeEnum.PANORAMA.getValue() == dataType) {
            List<DataPanoramaEntity> panoramaEntityList = dataPanoramaService.list(new QueryWrapper<DataPanoramaEntity>().in("mission_records_id", missionRecordsIds));
            if (ToolUtil.isNotEmpty(panoramaEntityList)) {
                deletePanorama(panoramaEntityList, isDelFile);
            }
        } else if (DataTypeEnum.AIR.getValue() == dataType) {
            List<MissionAirEntity> missionAirEntities = missionAirService.list(new QueryWrapper<MissionAirEntity>().in("mission_records_id", missionRecordsIds));
            if (ToolUtil.isNotEmpty(missionAirEntities)) {
                List<Integer> missionAirIds = missionAirEntities.stream().map(MissionAirEntity::getId).collect(Collectors.toList());
                missionAirService.removeByIds(missionAirIds);
            }
        } else if (DataTypeEnum.POLLUTION_GRID.getValue() == dataType) {
            List<DataPollutionGridEntity> dataPollutionGridEntityList = dataPollutionGridService.list(new QueryWrapper<DataPollutionGridEntity>().in("mission_records_id", missionRecordsIds));
            if (ToolUtil.isNotEmpty(dataPollutionGridEntityList)) {
                deletePollutionGrid(dataPollutionGridEntityList, isDelFile);
            }
        } else if (DataTypeEnum.MULTISPECTRAL.getValue() == dataType) {
            List<DataMultispectralEntity> dataMultispectralEntities = dataMultispectralService.list(new QueryWrapper<DataMultispectralEntity>().in("mission_records_id", missionRecordsIds));
            if (ToolUtil.isNotEmpty(dataMultispectralEntities)) {
                deleteMultispectral(dataMultispectralEntities, missionRecordsIds, dataType, isDelFile);
            }
        }
    }

    private Map<String, List<MissionPhotoEntity>> getTimePhotoListMap(List<Integer> photoIds, Integer infraredType) {
        File file;
        Metadata metadata;
        Directory directory;
        List<MissionPhotoEntity> lists;
        Map<String, List<MissionPhotoEntity>> maps = new HashMap<>();
        List<MissionPhotoEntity> missionVideoPhotoEntities = missionPhotoService.list(new QueryWrapper<MissionPhotoEntity>().in("id", photoIds).eq("deleted", 0));
        for (MissionPhotoEntity missionVideoPhotoEntity : missionVideoPhotoEntities) {
            file = fileManager.createTempFile(missionVideoPhotoEntity.getPhotoUrl());
            try {
                metadata = JpegMetadataReader.readMetadata(file);
                directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                Iterator<Tag> iterator = directory.getTags().iterator();
                missionVideoPhotoEntity.setDeviceUuid("");
                while (iterator.hasNext()) {
                    Tag tag = iterator.next();
                    if (tag.getTagName().equals("Date/Time Original")) {
                        missionVideoPhotoEntity.setCreateTimeStr(tag.getDescription().trim());
                    } else if (tag.getTagName().equals("Exif Image Width") || tag.getTagName().equals("Exif Image Height")) {
                        missionVideoPhotoEntity.setDeviceUuid("_" + tag.getDescription()
                                .replace("pixels", "").trim());
                    } else if (tag.getTagName().equals("Exif Image Width") || tag.getTagName().equals("Exif Image Height")) {
                        missionVideoPhotoEntity.setDeviceUuid(missionVideoPhotoEntity.getDeviceUuid() + "_" + tag.getDescription()
                                .replace("pixels", "").trim());
                    }
                }
                //大疆
                if (infraredType == 0) {
                    if (missionVideoPhotoEntity.getCreateTimeStr() == null) {
                        continue;
                    }
                    lists = maps.get(missionVideoPhotoEntity.getCreateTimeStr());
                    if (lists == null) {
                        lists = new ArrayList<>();
                        maps.put(missionVideoPhotoEntity.getCreateTimeStr(), lists);
                    }
                    lists.add(missionVideoPhotoEntity);
                }
                //道通
                if (infraredType == 1) {
                    if (missionVideoPhotoEntity.getLatitude() == null) {
                        continue;
                    }
                    lists = maps.get(missionVideoPhotoEntity.getLatitude().toString());
                    if (lists == null) {
                        lists = new ArrayList<>();
                        maps.put(missionVideoPhotoEntity.getLatitude().toString(), lists);
                    }
                    lists.add(missionVideoPhotoEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return maps;
    }

    private void deletePhoto(List<MissionPhotoEntity> photoEntities, boolean isDelFile) {
        List<Long> photoIds = photoEntities.stream().map(MissionPhotoEntity::getId).collect(Collectors.toList());
//        List<Long> noRepeatPhotos = baseMapper.getPhotoNoRePeat();
        missionPhotoService.removeByIds(photoIds);
//        if (isDelFile && noRepeatPhotos.size() > 0) {
//            List<String> noRepeatUrls = photoEntities.stream().filter(e -> noRepeatPhotos.contains(e.getId())).map(MissionPhotoEntity::getPhotoUrl).collect(Collectors.toList());
//            delServerData(noRepeatUrls);
//        }

        //删除问题
        List<DataProblemEntity> dataProblemEntities = dataProblemService.lambdaQuery().in(DataProblemEntity::getDataId, photoIds).eq(DataProblemEntity::getDeleted, 0).list();
        if (dataProblemEntities.size() > 0) {
            List<Integer> problemIds = dataProblemEntities.stream().map(DataProblemEntity::getId).collect(Collectors.toList());
            dataProblemService.removeByIds(problemIds);
        }
        //删除关系
        missionPhotoTagRelService.lambdaUpdate().set(MissionPhotoTagRelEntity::getDeleted, 1).in(MissionPhotoTagRelEntity::getMissionPhotoId, photoIds).update();
        missionPhotoTypeRelService.lambdaUpdate().set(MissionPhotoTypeRelEntity::getDeleted, 1).in(MissionPhotoTypeRelEntity::getMissionPhotoId, photoIds).update();
    }

    private void deleteVideo(List<MissionVideoEntity> videoEntityList, List<Integer> missionRecordsIds, boolean isDelFile) {
//        List<MissionPhotoEntity> photoEntities = missionPhotoService.list(new QueryWrapper<MissionPhotoEntity>().in("mission_records_id", missionRecordsIds));
//        if (ToolUtil.isNotEmpty(photoEntities)) {
//            deletePhoto(photoEntities, isDelFile);
//        }
//        //删除中间表数据
//        List<MissionVideoPhotoEntity> videoPhotoEntities = missionVideoPhotoService.list(new QueryWrapper<MissionVideoPhotoEntity>().in("mission_records_id", missionRecordsIds));
//        if (ToolUtil.isNotEmpty(videoPhotoEntities)) {
//            List<Long> videoPhotoIds = videoPhotoEntities.stream().map(MissionVideoPhotoEntity::getId).collect(Collectors.toList());
//            missionVideoPhotoService.removeByIds(videoPhotoIds);
//        }
        //删除视频
        List<Long> videoIds = videoEntityList.stream().map(MissionVideoEntity::getId).collect(Collectors.toList());
        missionVideoService.removeByIds(videoIds);
//        List<MissionVideoEntity> noRepeatVideos = baseMapper.getVideoNoRePeat();
//        if (isDelFile && noRepeatVideos.size() > 0) {
//            log.info("物理删除");
//            List<Long> noReatVideoIds = noRepeatVideos.stream().map(MissionVideoEntity::getId).collect(Collectors.toList());
//            List<MissionVideoEntity> delVideos = videoEntityList.stream().filter(e -> noReatVideoIds.contains(e.getId())).collect(Collectors.toList());
//            for (MissionVideoEntity missionVideoEntity : delVideos) {
//                baseMapper.updatePhysical(missionVideoEntity.getId());
//            }
//            //List<String> delVideoUrls = delVideos.stream().map(MissionVideoEntity::getVideoUrl).collect(Collectors.toList());
//            //delServerData(delVideoUrls);
//        }
    }

    private void deletePanorama(List<DataPanoramaEntity> panoramaEntityList, boolean isDelFile) {
        List<Long> panoramaIds = panoramaEntityList.stream().map(DataPanoramaEntity::getId).collect(Collectors.toList());
        List<Integer> fileInfoIds = panoramaEntityList.stream().map(DataPanoramaEntity::getFileInfoId).collect(Collectors.toList());
        if (isDelFile) {
            List<Integer> noRepeatFileIds = baseMapper.getPanoramaNoRePeatUrl(panoramaIds);
            delFileUrls(noRepeatFileIds);
        }
        fileInfoService.removeByIds(fileInfoIds);
        dataPanoramaService.removeByIds(panoramaIds);
    }

    //删除文件url
    private void delFileUrls(List<Integer> noRepeatFileIds) {
        if (noRepeatFileIds.size() > 0) {
            List<String> filePaths = fileInfoService.lambdaQuery().in(FileInfoEntity::getId, noRepeatFileIds).list().stream().map(FileInfoEntity::getFilePath).collect(Collectors.toList());
            delServerData(filePaths);
        }
    }

    private void deleteVector(List<DataVectorEntity> dataVectorEntityList, boolean isDelFile) {
        List<Long> vectorIds = dataVectorEntityList.stream().map(DataVectorEntity::getId).collect(Collectors.toList());
        List<Integer> fileInfoIds = dataVectorEntityList.stream().map(DataVectorEntity::getFileInfoId).collect(Collectors.toList());
        if (isDelFile) {
            List<Integer> noRepeatFileIds = baseMapper.getVectorNoRePeatUrl(vectorIds);
            delFileUrls(noRepeatFileIds);
        }
        fileInfoService.removeByIds(fileInfoIds);
        dataVectorService.removeByIds(vectorIds);
    }

    private void deleteTilt(List<DataTiltEntity> tiltEntityList, boolean isDelFile) {
        List<Long> tiltIdList = tiltEntityList.stream().map(DataTiltEntity::getId).collect(Collectors.toList());
        List<Integer> fileInfoIds = tiltEntityList.stream().map(DataTiltEntity::getFileInfoId).collect(Collectors.toList());
        if (isDelFile) {
            List<Integer> noRepeatFileIds = baseMapper.getTiltNoRePeatUrl(tiltIdList);
            delFileUrls(noRepeatFileIds);
        }
        fileInfoService.removeByIds(fileInfoIds);
        dataTiltService.removeByIds(tiltIdList);
    }

    private void deletePollutionGrid(List<DataPollutionGridEntity> pollutionGridEntityList, boolean isDelFile) {
        List<Long> pollutionIds = pollutionGridEntityList.stream().map(DataPollutionGridEntity::getId).collect(Collectors.toList());
        List<Integer> fileInfoIds = pollutionGridEntityList.stream().map(DataPollutionGridEntity::getFileInfoId).collect(Collectors.toList());
        if (isDelFile) {
            List<Integer> noRepeatFileIds = baseMapper.getPollutionGridNoRePeatUrl(pollutionIds);
            delFileUrls(noRepeatFileIds);
        }
        fileInfoService.removeByIds(fileInfoIds);
        dataPollutionGridService.removeByIds(pollutionIds);
    }


    /**
     * 删除点云表，删除fileInfo表，删除illegalPoint表，删除dataProblem表
     *
     * @param dataPointCloudEntityList
     * @param missionRecordsIds
     * @param dataType
     */
    private void deletePointCloud(List<DataPointCloudEntity> dataPointCloudEntityList, List<Integer> missionRecordsIds, Integer dataType, boolean isDelFile) {
        List<Long> pointCloudIdList = dataPointCloudEntityList.stream().map(DataPointCloudEntity::getId).collect(Collectors.toList());
        List<Integer> fileInfoIds = dataPointCloudEntityList.stream().map(DataPointCloudEntity::getFileInfoId).collect(Collectors.toList());
        if (isDelFile) {
            List<Integer> noRepeatFileIds = baseMapper.getPointCloudNoRePeatUrl(pointCloudIdList);
            delFileUrls(noRepeatFileIds);
        }
        fileInfoService.removeByIds(fileInfoIds);
        dataPointCloudService.removeByIds(pointCloudIdList);
        illegalPointService.lambdaUpdate().set(IllegalPointEntity::getDeleted, 1).eq(IllegalPointEntity::getSource, dataType).in(IllegalPointEntity::getAfterFileId, pointCloudIdList).or().in(IllegalPointEntity::getBeforeFileId, pointCloudIdList).update();
        dataProblemService.lambdaUpdate().set(DataProblemEntity::getDeleted, 1).in(DataProblemEntity::getMissionRecordId, missionRecordsIds).update();
    }

    /**
     * 删除正射表，删除fileInfo表，删除illegalPoint表，删除dataProblem表
     *
     * @param orthoEntityList
     * @param missionRecordsIds
     * @param dataType
     */
    private void deleteOrtho(List<DataOrthoEntity> orthoEntityList, List<Integer> missionRecordsIds, Integer dataType, boolean isDelFile) {
        if (ToolUtil.isNotEmpty(orthoEntityList)) {
            List<Long> orthoIdList = orthoEntityList.stream().map(DataOrthoEntity::getId).collect(Collectors.toList());
            List<Integer> fileInfoIds = orthoEntityList.stream().map(DataOrthoEntity::getFileInfoId).collect(Collectors.toList());
            if (isDelFile) {
                List<Integer> noRepeatFileIds = baseMapper.getOrthoNoRePeatUrl(orthoIdList);
                delFileUrls(noRepeatFileIds);
            }
            fileInfoService.removeByIds(fileInfoIds);
            dataOrthoService.removeByIds(orthoIdList);
            illegalPointService.lambdaUpdate().set(IllegalPointEntity::getDeleted, 1).eq(IllegalPointEntity::getSource, dataType).in(IllegalPointEntity::getAfterFileId, orthoIdList).or().in(IllegalPointEntity::getBeforeFileId, orthoIdList).update();
            dataProblemService.lambdaUpdate().set(DataProblemEntity::getDeleted, 1).in(DataProblemEntity::getMissionRecordId, missionRecordsIds).update();
        }
    }

    /**
     * 删除正射表，删除fileInfo表，删除illegalPoint表，删除dataProblem表
     *
     * @param dataMultispectralEntities
     * @param missionRecordsIds
     * @param dataType
     */
    private void deleteMultispectral(List<DataMultispectralEntity> dataMultispectralEntities, List<Integer> missionRecordsIds, Integer dataType, boolean isDelFile) {
        if (ToolUtil.isNotEmpty(dataMultispectralEntities)) {
            List<Long> dataMultispectralIdList = dataMultispectralEntities.stream().map(DataMultispectralEntity::getId).collect(Collectors.toList());
            List<Integer> fileInfoIds = dataMultispectralEntities.stream().map(DataMultispectralEntity::getFileInfoId).collect(Collectors.toList());
            if (isDelFile) {
                List<Integer> noRepeatFileIds = baseMapper.getMultispectralNoRePeatUrl(dataMultispectralIdList);
                delFileUrls(noRepeatFileIds);
            }
            fileInfoService.removeByIds(fileInfoIds);
            dataMultispectralService.removeByIds(dataMultispectralIdList);
            illegalPointService.lambdaUpdate().set(IllegalPointEntity::getDeleted, 1).eq(IllegalPointEntity::getSource, dataType).in(IllegalPointEntity::getAfterFileId, dataMultispectralIdList).or().in(IllegalPointEntity::getBeforeFileId, dataMultispectralIdList).update();
            dataProblemService.lambdaUpdate().set(DataProblemEntity::getDeleted, 1).in(DataProblemEntity::getMissionRecordId, missionRecordsIds).update();
        }
    }

    /**
     * 删除数据
     *
     * @param dataReqDto
     */
    @Override
    public void delData(DataReqDto dataReqDto) {
        Boolean isDelFile = dataReqDto.getIsDelFile();
        if (isDelFile == null) {
            isDelFile = false;
        }
        Integer dataType = dataReqDto.getDataType();
        List<Integer> ids = dataReqDto.getIds();
        if (ToolUtil.isNotEmpty(ids)) {
            if (DataTypeEnum.PHOTO.getValue() == dataType) {
                List<MissionPhotoEntity> photoEntityList = missionPhotoService.listByIds(ids);
                if (ToolUtil.isNotEmpty(photoEntityList)) {
                    deletePhoto(photoEntityList, isDelFile);
                }
            } else if (DataTypeEnum.VIDEO.getValue() == dataType) {
                List<MissionVideoEntity> videoEntityList = missionVideoService.listByIds(ids);
                if (ToolUtil.isNotEmpty(videoEntityList)) {
                    List<Integer> missionRecordsIds = videoEntityList.stream().map(MissionVideoEntity::getMissionRecordsId).collect(Collectors.toList());
                    deleteVideo(videoEntityList, missionRecordsIds, isDelFile);
                }
            } else if (DataTypeEnum.ORTHO.getValue() == dataType) {
                List<DataOrthoEntity> orthoEntityList = dataOrthoService.listByIds(ids);
                if (ToolUtil.isNotEmpty(orthoEntityList)) {
                    List<Integer> missionRecordsIdList = orthoEntityList.stream().map(DataOrthoEntity::getMissionRecordsId).collect(Collectors.toList());
                    deleteOrtho(orthoEntityList, missionRecordsIdList, dataType, isDelFile);
                }
            } else if (DataTypeEnum.POINTCLOUD.getValue() == dataType) {
                List<DataPointCloudEntity> pointCloudEntityList = dataPointCloudService.listByIds(ids);
                if (ToolUtil.isNotEmpty(pointCloudEntityList)) {
                    List<Integer> missionRecordsIdList = pointCloudEntityList.stream().map(DataPointCloudEntity::getMissionRecordsId).collect(Collectors.toList());
                    deletePointCloud(pointCloudEntityList, missionRecordsIdList, dataType, isDelFile);
                }
            } else if (DataTypeEnum.TILT.getValue() == dataType) {
                List<DataTiltEntity> dataTiltEntityList = dataTiltService.listByIds(ids);
                if (ToolUtil.isNotEmpty(dataTiltEntityList)) {
                    deleteTilt(dataTiltEntityList, isDelFile);
                }
            } else if (DataTypeEnum.VECTOR.getValue() == dataType) {
                List<DataVectorEntity> dataVectorEntityList = dataVectorService.listByIds(ids);
                if (ToolUtil.isNotEmpty(dataVectorEntityList)) {
                    deleteVector(dataVectorEntityList, isDelFile);
                }
            } else if (DataTypeEnum.PANORAMA.getValue() == dataType) {
                List<DataPanoramaEntity> panoramaEntityList = dataPanoramaService.listByIds(ids);
                if (ToolUtil.isNotEmpty(panoramaEntityList)) {
                    deletePanorama(panoramaEntityList, isDelFile);
                }
            } else if (DataTypeEnum.AIR.getValue() == dataType) {
                List<MissionAirEntity> missionAirEntityList = missionAirService.listByIds(ids);
                if (ToolUtil.isNotEmpty(missionAirEntityList)) {
                    List<Integer> missionAirIds = missionAirEntityList.stream().map(MissionAirEntity::getId).collect(Collectors.toList());
                    missionAirService.removeByIds(missionAirIds);
                }
            } else if (DataTypeEnum.POLLUTION_GRID.getValue() == dataType) {
                List<DataPollutionGridEntity> dataPollutionGridEntityList = dataPollutionGridService.listByIds(ids);
                if (ToolUtil.isNotEmpty(dataPollutionGridEntityList)) {
                    deletePollutionGrid(dataPollutionGridEntityList, isDelFile);
                }
            } else if (DataTypeEnum.MULTISPECTRAL.getValue() == dataType) {
                List<DataMultispectralEntity> dataMultispectralEntities = dataMultispectralService.listByIds(ids);
                if (ToolUtil.isNotEmpty(dataMultispectralEntities)) {
                    List<Integer> missionRecordsIdList = dataMultispectralEntities.stream().map(DataMultispectralEntity::getMissionRecordsId).collect(Collectors.toList());
                    deleteMultispectral(dataMultispectralEntities, missionRecordsIdList, dataType, isDelFile);
                }
            }
        }
    }

    /**
     * 编辑任务
     *
     * @param missionRecordsId
     * @param name
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changeName(Integer missionRecordsId, String name) {
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(missionRecordsId);
        Integer missionId = missionRecordsEntity.getMissionId();
        missionService.lambdaUpdate().set(MissionEntity::getName, name).eq(MissionEntity::getId, missionId).update();
        MissionEntity missionEntity = missionService.getById(missionId);
        Integer taskId = missionEntity.getTaskId();
        taskService.lambdaUpdate().set(TaskEntity::getName, name).eq(TaskEntity::getId, taskId).update();
        TaskEntity taskEntity = taskService.getById(taskId);
        Integer dataType = taskEntity.getDataType();
        if (DataTypeEnum.ORTHO.getValue() == dataType) {
            // 修改正射表里的taskName
            dataOrthoService.lambdaUpdate().set(DataOrthoEntity::getTaskName, name).eq(DataOrthoEntity::getTaskId, taskId).update();
        } else if (DataTypeEnum.POINTCLOUD.getValue() == dataType) {
            // 修改点云表里的taskName
            dataPointCloudService.lambdaUpdate().set(DataPointCloudEntity::getTaskName, name).eq(DataPointCloudEntity::getTaskId, taskId).update();
        } else if (DataTypeEnum.MULTISPECTRAL.getValue() == dataType) {
            // 修改正射表里的taskName
            dataMultispectralService.lambdaUpdate().set(DataMultispectralEntity::getTaskName, name).eq(DataMultispectralEntity::getTaskId, taskId).update();
        }
    }

    /**
     * 获取人工上传的数据
     *
     * @param page
     * @param limit
     * @param dataType
     * @param missionRecordsId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public IPage getDataPage(Integer page, Integer limit, Integer dataType, Integer missionRecordsId, String startTime, String endTime) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        QueryWrapper queryBase = new QueryWrapper<>()
                .between(startTime != null && endTime != null, "DATE(create_time)", startTime, endTime)
                .eq("mission_records_id", missionRecordsId)
                .eq("deleted", 0);
        if(org.springframework.util.StringUtils.hasText(orgCode)){
            queryBase.likeRight("org_code", orgCode);
        }
        if (DataTypeEnum.PHOTO.getValue() == dataType) {
            IPage<MissionPhotoEntity> missionPhotoEntityIPage = missionPhotoService.page(new Page<>(page, limit), queryBase);
            return missionPhotoEntityIPage;
        } else if (DataTypeEnum.VIDEO.getValue() == dataType) {
            IPage<MissionVideoEntity> missionVideoEntityIPage = missionVideoService.page(new Page<>(page, limit), queryBase);
            missionVideoEntityIPage.getRecords().stream().forEach(e -> {
                setSrtJson(e);
                setFirstPoint(e);
            });
            return missionVideoEntityIPage;
        } else if (DataTypeEnum.ORTHO.getValue() == dataType) {
            Page<DataOrthoEntity> dataOrthoEntityPage = dataOrthoService.page(new Page<>(page, limit), queryBase);
            List<DataOrthoEntity> records = dataOrthoEntityPage.getRecords();
            if (records.size() > 0) {
                DataMissionRecordEntity dataMissionRecordEntity = dataMissionRecordService.getOne(new QueryWrapper<DataMissionRecordEntity>().eq("data_id", records.get(0).getId()).eq("data_type", dataType).eq("deleted", 0));
                if (ToolUtil.isNotEmpty(dataMissionRecordEntity)) {
                    records.get(0).setRelatedTaskName(dataMissionRecordEntity.getTaskName() + "-" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dataMissionRecordEntity.getMissionRecordTime()));
                    dataOrthoEntityPage.setRecords(records);
                }
            }
            return dataOrthoEntityPage;
        } else if (DataTypeEnum.POINTCLOUD.getValue() == dataType) {
            Page<DataPointCloudEntity> dataPointCloudEntityPage = dataPointCloudService.page(new Page<>(page, limit), queryBase);
            List<DataPointCloudEntity> records = dataPointCloudEntityPage.getRecords();
            if (records.size() > 0) {
                DataMissionRecordEntity dataMissionRecordEntity = dataMissionRecordService.getOne(new QueryWrapper<DataMissionRecordEntity>().eq("data_id", records.get(0).getId()).eq("data_type", dataType).eq("deleted", 0));
                if (ToolUtil.isNotEmpty(dataMissionRecordEntity)) {
                    records.get(0).setRelatedTaskName(dataMissionRecordEntity.getTaskName() + "-" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dataMissionRecordEntity.getMissionRecordTime()));
                    dataPointCloudEntityPage.setRecords(records);
                }
            }
            return dataPointCloudEntityPage;
        } else if (DataTypeEnum.TILT.getValue() == dataType) {
            return dataTiltService.page(new Page<>(page, limit), queryBase);
        } else if (DataTypeEnum.VECTOR.getValue() == dataType) {
            return dataVectorService.page(new Page<>(page, limit), queryBase);
        } else if (DataTypeEnum.PANORAMA.getValue() == dataType) {
            return dataPanoramaService.page(new Page<>(page, limit), queryBase);
        } else if (DataTypeEnum.POLLUTION_GRID.getValue() == dataType) {
            return dataPollutionGridService.page(new Page<>(page, limit), queryBase);
        } else if (DataTypeEnum.MULTISPECTRAL.getValue() == dataType) {
            Page<DataMultispectralEntity> dataMultispectralEntityPage = dataMultispectralService.page(new Page<>(page, limit), queryBase);
            List<DataMultispectralEntity> records = dataMultispectralEntityPage.getRecords();
            if (records.size() > 0) {
                DataMissionRecordEntity dataMissionRecordEntity = dataMissionRecordService.getOne(new QueryWrapper<DataMissionRecordEntity>().eq("data_id", records.get(0).getId()).eq("data_type", dataType).eq("deleted", 0));
                if (ToolUtil.isNotEmpty(dataMissionRecordEntity)) {
                    records.get(0).setRelatedTaskName(dataMissionRecordEntity.getTaskName() + "-" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dataMissionRecordEntity.getMissionRecordTime()));
                    dataMultispectralEntityPage.setRecords(records);
                }
            }
            return dataMultispectralEntityPage;
        }
        return null;
    }

    @Override
    public void downloadByRecordIds(Integer dataType, List<Integer> missionRecordIdList, HttpServletResponse response) {
        try {
            // 转换中文否则可能会产生乱码
            String zipName = URLEncoder.encode("数据中心文件.zip", "UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + zipName);

            @Cleanup
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            byte[] buf = new byte[10240];

            // 根据架次idList。获取对应的任务信息List
            List<MissionRecordTaskInfoDTO> missionRecordTaskInfoList = baseMapper.getTaskInfoByMissionRecordId(missionRecordIdList);
            // 根据任务每次分类
            Map<Integer, List<MissionRecordTaskInfoDTO>> taskNameMap = missionRecordTaskInfoList.stream().collect(Collectors.groupingBy(MissionRecordTaskInfoDTO::getTaskId));
            for (Map.Entry<Integer, List<MissionRecordTaskInfoDTO>> m : taskNameMap.entrySet()) {

                List<MissionRecordTaskInfoDTO> missionRecordTaskInfoDTOList = m.getValue();
                if (CollUtil.isEmpty(missionRecordTaskInfoDTOList)) {
                    continue;
                }
                String taskName = missionRecordTaskInfoDTOList.get(0).getTaskName();
                // 根据架次记录id分类
                Map<Integer, List<MissionRecordTaskInfoDTO>> missionRecordIdMap = missionRecordTaskInfoDTOList.stream().collect(Collectors.groupingBy(MissionRecordTaskInfoDTO::getMissionRecordId));
                for (Map.Entry<Integer, List<MissionRecordTaskInfoDTO>> missionRecordMap : missionRecordIdMap.entrySet()) {
                    // 架次记录时间
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    MissionRecordTaskInfoDTO missionRecordTaskInfoDTO = missionRecordMap.getValue().get(0);
                    String missionRecordTimeStr = String.format("%s_%s_%s", missionRecordTaskInfoDTO.getMissionName(),
                            missionRecordTaskInfoDTO.getFlyIndex() == null ? 0 : missionRecordTaskInfoDTO.getFlyIndex(),
                            missionRecordTaskInfoDTO.getMissionRecordTime().format(formatter));
                    log.info("#DataCenterServiceImpl.downloadByRecordIds# taskName={}, missionRecordTimeStr={}", taskName, missionRecordTimeStr);

                    Integer missionRecordId = missionRecordMap.getKey();

                    if (DataTypeEnum.PHOTO.getValue() == dataType) {
                        List<MissionPhotoEntity> missionPhotoList = missionPhotoService.list(new QueryWrapper<MissionPhotoEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
                        if (ToolUtil.isNotEmpty(missionPhotoList)) {
                            for (MissionPhotoEntity missionPhotoEntity : missionPhotoList) {
                                String photoUrl = missionPhotoEntity.getPhotoUrl();
                                String photoName = photoUrl.substring(photoUrl.lastIndexOf("/") + 1);
                                exportDataIntoZip(zos, buf, taskName, missionRecordTimeStr, photoName, photoUrl);
                            }
                        }
                    } else if (DataTypeEnum.VIDEO.getValue() == dataType) {
                        List<MissionVideoEntity> missionVideoList = missionVideoService.list(new QueryWrapper<MissionVideoEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
                        if (ToolUtil.isNotEmpty(missionVideoList)) {
                            for (MissionVideoEntity missionVideoEntity : missionVideoList) {
                                String dataUrl = missionVideoEntity.getVideoUrl();
                                String dataName = dataUrl.substring(dataUrl.lastIndexOf("/") + 1);
                                exportDataIntoZip(zos, buf, taskName, missionRecordTimeStr, dataName, dataUrl);
                            }
                        }
                    } else if (DataTypeEnum.POLLUTION_GRID.getValue() == dataType) {
                        // 根据架次idList批量下载污染网格数据
                        List<DataPollutionGridEntity> pollutionGridEntityList = dataPollutionGridService.list(new QueryWrapper<DataPollutionGridEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
                        if (ToolUtil.isNotEmpty(pollutionGridEntityList)) {
                            for (DataPollutionGridEntity pollutionGridEntity : pollutionGridEntityList) {
                                String fileUrl = pollutionGridEntity.getZipUrl();
                                String fileName = pollutionGridEntity.getName();
                                exportDataIntoZip(zos, buf, taskName, missionRecordTimeStr, fileName, fileUrl);
                            }
                        }
                    }
                }
            }
            zos.closeEntry();
            zos.close();
        } catch (IOException e) {
            log.error("#DataCenterServiceImpl.downloadByRecordIds# missionRecordIdList={} ", missionRecordIdList, e);
        }
    }

    @Override
    public String getOrgCode(Integer dataType, Integer missionRecordId) {

        String orgCode = null;
        if (DataTypeEnum.ORTHO.getValue() == dataType) {
            DataOrthoEntity one = dataOrthoService.getOne(new QueryWrapper<DataOrthoEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
            orgCode = one.getOrgCode();
        } else if (DataTypeEnum.POINTCLOUD.getValue() == dataType) {
            DataPointCloudEntity one = dataPointCloudService.getOne(new QueryWrapper<DataPointCloudEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
            orgCode = one.getOrgCode();
        } else if (DataTypeEnum.TILT.getValue() == dataType) {
            DataTiltEntity one = dataTiltService.getOne(new QueryWrapper<DataTiltEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
            orgCode = one.getOrgCode();
        } else if (DataTypeEnum.VECTOR.getValue() == dataType) {
            DataVectorEntity one = dataVectorService.getOne(new QueryWrapper<DataVectorEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
            orgCode = one.getOrgCode();
        } else if (DataTypeEnum.MULTISPECTRAL.getValue() == dataType) {
//            DataMultispectralEntity one = dataMultispectralService.getOne(new QueryWrapper<DataMultispectralEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
            orgCode = null;
        }else if (DataTypeEnum.PANORAMA.getValue() == dataType) {
            DataPanoramaEntity one = dataPanoramaService.getOne(new QueryWrapper<DataPanoramaEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
            orgCode = one.getOrgCode();
        }
        return orgCode;
    }

    @Override
    public void downloadData(Integer dataType, Integer missionRecordId, HttpServletResponse response) {
        String name = "";
        Integer fileInfoId = null;
        if (DataTypeEnum.ORTHO.getValue() == dataType) {
            DataOrthoEntity one = dataOrthoService.getOne(new QueryWrapper<DataOrthoEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
            fileInfoId = one.getFileInfoId();
            name = "正射";
        } else if (DataTypeEnum.POINTCLOUD.getValue() == dataType) {
            DataPointCloudEntity one = dataPointCloudService.getOne(new QueryWrapper<DataPointCloudEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
            fileInfoId = one.getFileInfoId();
            name = "点云";
        } else if (DataTypeEnum.TILT.getValue() == dataType) {
            DataTiltEntity one = dataTiltService.getOne(new QueryWrapper<DataTiltEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
            fileInfoId = one.getFileInfoId();
            name = "倾斜";
        } else if (DataTypeEnum.VECTOR.getValue() == dataType) {
            DataVectorEntity one = dataVectorService.getOne(new QueryWrapper<DataVectorEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
            fileInfoId = one.getFileInfoId();
            name = "矢量";
        } else if (DataTypeEnum.MULTISPECTRAL.getValue() == dataType) {
            DataMultispectralEntity one = dataMultispectralService.getOne(new QueryWrapper<DataMultispectralEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
            fileInfoId = one.getFileInfoId();
            name = "多光谱";
        }else if (DataTypeEnum.PANORAMA.getValue() == dataType) {
            DataPanoramaEntity one = dataPanoramaService.getOne(new QueryWrapper<DataPanoramaEntity>().eq("mission_records_id", missionRecordId).eq("deleted", false));
            fileInfoId = one.getFileInfoId();
            name = "全景";
        }

        FileInfoEntity fileInfoEntity = fileInfoService.getById(fileInfoId);
        String filePath = fileInfoEntity.getFilePath();
        name += "_" + fileInfoEntity.getFileName();
        DownLoadZipUtil.downloadZip(name, filePath, response);
    }

    @Override
    public void downloadSrt(Integer videoId, HttpServletResponse response) {
        MissionVideoEntity missionVideoEntity = missionVideoService.getById(videoId);
        Integer srtId = missionVideoEntity.getSrtId();
        String srtFileUrl = missionVideoEntity.getSrtFileUrl();
        if (srtId != null) {
            FileInfoSrtEntity fileInfoSrtEntity = fileInfoSrtService.getById(srtId);
            String fileUrl = fileInfoSrtEntity.getFileUrl();
            String name = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            DownLoadZipUtil.downloadZip(name, fileUrl, response);
        }
        if (srtFileUrl != null) {
            String name = srtFileUrl.substring(srtFileUrl.lastIndexOf("/") + 1);
            DownLoadZipUtil.downloadZip(name, srtFileUrl, response);
        }
    }


    /**
     * 根据数据类型获取让任务id
     *
     * @param dataType
     * @param startTime
     * @param endTime
     * @return
     */
    private List<MissionRecordsEntity> getMissionRecords(Integer dataType, String startTime, String endTime) {
        if (DataTypeEnum.PHOTO.getValue() == dataType) {
            return missionPhotoService.getMissionRecords(startTime, endTime);
        } else if (DataTypeEnum.VIDEO.getValue() == dataType) {
            return missionVideoService.getMissionRecords(startTime, endTime);
        } else if (DataTypeEnum.ORTHO.getValue() == dataType) {
            return dataOrthoService.getMissionRecords(startTime, endTime);
        } else if (DataTypeEnum.POINTCLOUD.getValue() == dataType) {
            return dataPointCloudService.getMissionRecords(startTime, endTime);
        } else if (DataTypeEnum.TILT.getValue() == dataType) {
            return dataTiltService.getMissionRecords(startTime, endTime);
        } else if (DataTypeEnum.VECTOR.getValue() == dataType) {
            return dataVectorService.getMissionRecords(startTime, endTime);
        } else if (DataTypeEnum.PANORAMA.getValue() == dataType) {
            return dataPanoramaService.getMissionRecords(startTime, endTime);
        } else if (DataTypeEnum.AIR.getValue() == dataType) {
            return missionAirService.getMissionRecords(startTime, endTime);
        } else if (DataTypeEnum.MULTISPECTRAL.getValue() == dataType) {
            return dataMultispectralService.getMissionRecords(startTime, endTime);
        }
        return new ArrayList<>();
    }

    /**
     * 当前单位获取任务id
     *
     * @return
     */
    private List<Integer> getMissionRecordsIds(String startTime, String endTime) {
        // 查询用户单位信息
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        List<SysTagEntity> tagList = sysTagService.getAllTagsByOrgCode(orgCode);
        List<Long> tagIds = tagList.size() > 0 ? tagList.stream().map(SysTagEntity::getId).collect(Collectors.toList()) : new ArrayList<>();
        List<Integer> missionRecordsIds = sysTaskTagService.getMissionRecordsIds(tagIds, startTime, endTime);
        return missionRecordsIds;
    }

    /**
     * 往zip包插入数据并导出
     *
     * @param zos
     * @param buf
     * @param taskName
     * @param missionRecordTimeStr
     * @param dataName             数据名称
     * @param dataUrl              数据路径
     * @throws IOException
     */
    private void exportDataIntoZip(ZipOutputStream zos, byte[] buf, String taskName, String missionRecordTimeStr, String dataName, String dataUrl) throws IOException {
        zos.putNextEntry(new ZipEntry(taskName + File.separator + missionRecordTimeStr + File.separator + dataName));
        int len;
        try (InputStream in = fileManager.getInputSteam(dataUrl)) {
            if (in == null) {
                return;
            }
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
        } catch (IOException e) {
            log.error("#DataCenterServiceImpl.exportDataIntoZip# missionRecordTimeStr={}", missionRecordTimeStr);
        }
    }

    /**
     * 删除服务器中软删除的数据
     */
    @Override
    public void delServerFile(String startTime, String endTime) {
        List<String> urls = new ArrayList<>();
        if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
            return;
        }
        List<Integer> recordsIds = baseMapper.getRecords(startTime, endTime);
        if (recordsIds.size() <= 0) {
            return;
        }
        //不重复的图片
        urls.addAll(baseMapper.getMissionPhotos(recordsIds));
        //不重复的视频
        urls.addAll(baseMapper.getMissionVideos(recordsIds));
        //不重复的fileInfo
        List<Integer> fileInfoIds = new ArrayList<>();
        fileInfoIds.addAll(baseMapper.getOrthoFileInfoIds(recordsIds));
        fileInfoIds.addAll(baseMapper.getVectorFileInfoIds(recordsIds));
        fileInfoIds.addAll(baseMapper.getTiltFileInfoIds(recordsIds));
        fileInfoIds.addAll(baseMapper.getPointCloudFileInfoIds(recordsIds));
        fileInfoIds.addAll(baseMapper.getPanoramaFileInfoIds(recordsIds));
        if (fileInfoIds.size() > 0) {
            fileInfoService.removeByIds(fileInfoIds);
            List<String> fileUrls = fileInfoService.lambdaQuery().in(FileInfoEntity::getId, fileInfoIds).list().stream().map(FileInfoEntity::getFilePath).collect(Collectors.toList());
            urls.addAll(fileUrls);
        }
        //System.out.println(urls.size());
        delServerData(urls);
    }

    /**
     * 获取服务器资源
     *
     * @return
     */
    @Override
    public Map getServerSize() {
        String osName = System.getenv("OS");
        if(osName == null){
            return Collections.emptyMap();
        }
        if (osName != null && osName.toLowerCase().contains("windows")) {
            return Collections.emptyMap();
        }
        String result = sshHelper.exec("df -h /data");
        String[] s = result.split(" ");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < s.length; i++) {
            if (StringUtil.isNotEmpty(s[i])) {
                list.add(s[i]);
            }
        }
        int size = list.size();
        String sizeStr = list.get(size - 5);
        String usedStr = list.get(size - 4);
        String availStr = list.get(size - 3);
        String useRateStr = list.get(size - 2);
        Map map = new HashMap();
        map.put("size", sizeStr.substring(0, sizeStr.length() - 1));
        map.put("sizeUnit", sizeStr.substring(sizeStr.length() - 1));
        map.put("used", usedStr.substring(0, usedStr.length() - 1));
        map.put("usedUnit", usedStr.substring(usedStr.length() - 1));
        map.put("avail", availStr.substring(0, availStr.length() - 1));
        map.put("availUnit", availStr.substring(availStr.length() - 1));
        map.put("useRate", useRateStr.substring(0, useRateStr.length() - 1));
        map.put("useRateUnit", useRateStr.substring(useRateStr.length() - 1));
        String s1 = list.get(size - 3);
        boolean isAlarm = false;
        if (s1.endsWith("T")) {
            isAlarm = false;
        } else if (s1.endsWith("M")) {
            isAlarm = true;
        } else if (s1.endsWith("K")) {
            isAlarm = true;
        } else if (s1.endsWith("G")) {
            String g = s1.substring(0, s1.lastIndexOf("G"));
            BigDecimal bigDecimal = new BigDecimal(g);
            int i = bigDecimal.intValue();
            if (i < geoaiUosProperties.getDiskUsageThreshold()) {
                isAlarm = true;
            }
        } else {
            isAlarm = false;
        }
        map.put("isAlarm", isAlarm);
        return map;
    }

    //强制删除
    public void delServerData(List<String> urls) {
        if (urls.size() > 0) {
//            List<String> objectList = Lists.newLinkedList();
//            for (String url : urls) {
//                if (MinIoUnit.objectExists(url)) {
//                    objectList.add(url);
//                }
//            }
//            MinIoUnit.rmObjects(objectList);
            fileManager.deleteFiles(urls);
        }
    }

    /**
     * 获取srtJson
     * @param jsonUrl
     * @return
     */
    private String getSrtJson(String jsonUrl) {
        try (InputStream inputSteam = fileManager.getInputSteam(jsonUrl)){
            if(Objects.isNull(inputSteam)){
                return null;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputSteam));
            return bufferedReader.readLine();
        } catch (IOException ioException) {
            log.error("读取srt json文件时发生错误", ioException);
            return null;
        }
    }
}
