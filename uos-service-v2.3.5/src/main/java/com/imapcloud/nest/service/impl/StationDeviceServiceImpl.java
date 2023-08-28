package com.imapcloud.nest.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imapcloud.nest.model.AirLineEntity;
import com.imapcloud.nest.model.StationCheckpointEntity;
import com.imapcloud.nest.model.StationDeviceEntity;
import com.imapcloud.nest.mapper.StationDeviceMapper;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileStorageOutDO;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.sdk.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author daolin
 * @since 2020-11-02
 */
@Service
@Slf4j
public class StationDeviceServiceImpl extends ServiceImpl<StationDeviceMapper, StationDeviceEntity> implements StationDeviceService {

    private static final Logger logger = LoggerFactory.getLogger(StationDeviceServiceImpl.class);

    @Autowired
    StationCheckpointService stationCheckpointService;

    @Autowired
    StationDeviceMapper stationDeviceMapper;

//    @Autowired
//    NestService nestService;

    @Autowired
    private BaseNestService baseNestService;

    @Autowired
    private AirLineService airLineService;

    @Autowired
    private ExportService exportService;

    @Resource
    private UploadManager uploadManager;

    @Override
    public void deleteByNestId(String nestId) {
        stationDeviceMapper.deleteByNestId(nestId);
    }

    @Override
    public RestRes selectByNestId(String nestId) {
        List<StationDeviceEntity> devices = stationDeviceMapper.selectBynestId(nestId);
        Map result = new HashMap(2);
        result.put("list", devices);
        return RestRes.ok(result);
    }

    @Override
    public String getDeviceName(String uuid) {
        return baseMapper.getDeviceName(uuid);
    }

    @Override
    public Integer initDevice(MultipartFile deviceJson, MultipartFile pointJson, MultipartFile mapsJson, MultipartFile wholeUnitJson, String nestId) {
        //2.解析文本，导入台账
        Gson gson = new Gson();
        try {
            DeviceDTO dto = gson.fromJson(new InputStreamReader(deviceJson.getInputStream(), "utf-8"), DeviceDTO.class);
            //1.清空原有数据
            stationCheckpointService.deleteByNestId(nestId);
            stationDeviceMapper.deleteByNestId(nestId);
//            NestEntity nest = nestService.getById(nestId);
            //2.获取规划编号，保存到机巢信息，存储文件
            CheckPointJson checkPointJson = new Gson().fromJson(new InputStreamReader(pointJson.getInputStream(), "utf-8"), CheckPointJson.class);
            String code = checkPointJson.getSUBSTATIONGUID();
//            nest.setPlanCode(code);
//            nestService.saveOrUpdate(nest);
            baseNestService.setPlanCodeByNestId(nestId, code);
            String path = "/nest/Data/" + code;
            //先校验文件名称，再保存
            if (deviceJson != null && deviceJson.getOriginalFilename().equals("substationTree.json")) {
                storeFile(path, deviceJson);
            }
            if (pointJson != null && pointJson.getOriginalFilename().equals("substationRouteList.json")) {
                storeFile(path, pointJson);
            }
            if (mapsJson != null && mapsJson.getOriginalFilename().equals("maps.json")) {
                storeFile(path, mapsJson);
            }
            if (wholeUnitJson != null && wholeUnitJson.getOriginalFilename().equals("WholeUnit.json")) {
                storeFile(path, wholeUnitJson);
            }
            /** 解析台账 */
            //第0级，变电站/机巢
            String name0 = dto.name;
//            String number0 = nest.getUuid();
            String number0 = baseNestService.getNestUuidByNestIdInCache(nestId);
            StationDeviceEntity dv0 = buildDevice(name0, number0, nestId, 0, "", "", "", "", "", "", "");
            stationDeviceMapper.insert(dv0);
            Integer id0 = dv0.getId();
            if (dto.List != null && dto.List.size() != 0) {
                for (Map item1 : dto.List) {
                    //第一级,区域
                    String name1 = (String) item1.get("name");
                    //匹配设备区域
                    String deviceArea = getDeviceArea(name1);
                    String number1 = (String) item1.get("number");
                    List<Map> list1 = (List<Map>) item1.get("List");
                    StationDeviceEntity dv1 = buildDevice(name1, number1, nestId, id0, deviceArea, "", "", "", "", "", "");
                    stationDeviceMapper.insert(dv1);
                    Integer id1 = dv1.getId();
//                    logger.info("一级节点 name:{}, uuid:{}, id:{}, pid:{}, createTime:{}, modifyTime:{}", name1, number1, id1, dv1.getParentId(), dv1.getCreateTime(), dv1.getModifyTime());
                    if (list1 != null && list1.size() != 0) {
                        for (Map item2 : list1) {
                            //第二级，间隔
                            String name2 = (String) item2.get("name");
                            String number2 = (String) item2.get("number");
                            List<Map> list2 = (List<Map>) item2.get("List");
                            StationDeviceEntity dv2 = buildDevice(name2, number2, nestId, id1, deviceArea, "", "", "", "", "", "");
                            stationDeviceMapper.insert(dv2);
                            Integer id2 = dv2.getId();
//                            logger.info("二级节点 name:{}, uuid:{}, deviceArea:{}, id:{}, pid:{}", name2, number2, deviceArea, id2, dv2.getParentId());
                            if (list2 != null && list2.size() != 0) {
                                for (Map item3 : list2) {
                                    //第三级，单元
                                    String name3 = (String) item3.get("name");
                                    //为候选池则跳过
                                    if (name3.trim().equals("候选池")) {
                                        continue;
                                    }
                                    //获取低中高区
//                                    String space = getSpaceByCode(Convert.toInt(item3.get("heightSpace")));
                                    String space = getSpace(name3);
                                    String number3 = (String) item3.get("number");
                                    String entryPoint3 = (String) item3.get("entryPoint");
                                    String entryAssistPoint3 = (String) item3.get("entryAssistPoint");
                                    List<Map> list3 = (List<Map>) item3.get("List");
                                    StationDeviceEntity dv3 = buildDevice(name3, number3, nestId, id2, deviceArea, "", space, "", entryAssistPoint3, entryPoint3, "");
                                    stationDeviceMapper.insert(dv3);
                                    Integer id3 = dv3.getId();
//                                    logger.info("三级节点 name:{}, uuid:{}, space:{}, deviceArea:{}, id:{}, pid:{}", name3, number3, space, deviceArea, id3, dv3.getParentId());
                                    if (list3 != null && list3.size() != 0) {
                                        for (Map item4 : list3) {
                                            //第四级,设备
                                            String name4 = (String) item4.get("name");
                                            String number4 = (String) item4.get("number");
                                            //暂时对目标点不做处理
//                                            List<Map> alginpoints4 = (List<Map>) item3.get("alginpoints");
//                                            String alginpoint4 = "";
//                                            if (alginpoints4 != null) {
//                                                alginpoint4 = (String) alginpoints4.get(0).get("P01");
//                                            }
                                            Map<String, String> photolist = (Map<String, String>) item4.get("photolist");
                                            Map props = (Map) item4.get("props");
                                            String tag0 = "";
                                            if (props != null) {
                                                tag0 = (String) props.get("tag0");
                                            }
                                            String photoJson = "";
                                            if (photolist != null) {
                                                photoJson = gson.toJson(photolist);
                                            }
                                            StationDeviceEntity dv4 = buildDevice(name4, number4, nestId, id3, deviceArea, "", space, photoJson, "", "", tag0);
                                            stationDeviceMapper.insert(dv4);
                                            Integer id4 = dv4.getId();
//                                            logger.info("四级节点 name:{}, uuid:{}, space:{}, deviceArea{}, id:{}, pid:{}, createTime:{}, modifyTime:{}", name4, number4, space, deviceArea, id4, dv4.getParentId(), dv4.getCreateTime(), dv4.getModifyTime());
//                                            logger.info("初始化巡检点数据, deviceId:{}", id4);
                                            if (photolist != null) {
                                                Set<Map.Entry<String, String>> entrySet = photolist.entrySet();
                                                Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
                                                while (iterator.hasNext()) {
                                                    Map.Entry<String, String> entry = iterator.next();
                                                    String photoName = name4 + "_" + entry.getKey();
                                                    String photoUuid = entry.getValue();
                                                    StationCheckpointEntity point = buildCheckpoint(photoName, photoUuid, id4, number4, nestId);
                                                    stationCheckpointService.insert(point);
                                                }
                                            }
                                            logger.info("初始化巡检点数据完成, deviceId:{}", id4);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
            //更新拍照点坐标信息
            if (pointJson != null) {
                updateCheckpoint(pointJson);
            }
            List<StationDeviceEntity> devices = stationDeviceMapper.selectBynestId(nestId);
            logger.info("device size:{}", devices.size());
            return devices.size();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }


    @Override
    public Integer checkPointDetail(Integer airLineStartId, Integer airLineEndId, HttpServletResponse response) {
        if (airLineStartId == null || airLineEndId == null) {
            return null;
        }

        try {
            List<AirLineAndDeviceDto> airLineAndDeviceDtos = new ArrayList<>();
            AtomicReference<Integer> airLineInfoCount2028 = new AtomicReference<>(2028);
            AtomicReference<Integer> infoCount = new AtomicReference<>(3760);
            for (int airLineId = airLineStartId; airLineId <= airLineEndId; airLineId++) {
                AirLineEntity airLineEntity = airLineService.getById(airLineId);
                AtomicReference<Integer> checkPointCount = new AtomicReference<>(0);

                if (!Objects.isNull(airLineEntity)) {
                    String airLineEntityName = airLineEntity.getName();
                    log.info("航线名称:" + airLineEntityName);

                    List<WayPointsDto> wayPointsListDtos = JSONUtil.parseArray(airLineEntity.getWaypoints(), WayPointsDto.class);

                    if (!CollectionUtils.isEmpty(wayPointsListDtos)) {
                        airLineInfoCount2028.getAndSet(airLineInfoCount2028.get() + 1);
                        wayPointsListDtos.forEach(it -> {
                            if (it.getWaypointType().equals("0")) {
                                StationCheckpointEntity checkpointEntity = stationCheckpointService.getOne(new QueryWrapper<StationCheckpointEntity>().eq("uuid", it.getCheckpointuuid()));
                                if (checkpointEntity == null) {
                                    log.info("checkpointEntity为null：" + JSON.toJSONString(it));
                                }
                                StationDeviceEntity stationDeviceEntity = stationDeviceMapper.selectOne(new QueryWrapper<StationDeviceEntity>().eq("uuid", checkpointEntity.getDeviceUuid()));
                                String photoName = checkpointEntity.getName();
                                String[] s = photoName.split("_");
                                String photoNum = s[4].replace("photo", "拍照点");
                                String deviceEntityName = stationDeviceEntity.getName();
                                //log.info("拍照设备：" + deviceEntityName + "--照片名字：" + s[4]);
                                //log.info("拍照设备：" + deviceEntityName);
                                AirLineAndDeviceDto airLineAndDeviceDto = new AirLineAndDeviceDto();
                                int airLineGangIndex = airLineEntityName.indexOf("-");
                                airLineAndDeviceDto.setAirLineName(airLineEntityName.substring(0, airLineGangIndex));
                                airLineAndDeviceDto.setAirLineInfoCount(airLineInfoCount2028.get());
                                String deviceName = deviceEntityName;
                                log.info("设备名字" + deviceEntityName);
                                if (deviceEntityName.contains("相")) {
                                    int capturePointIndex = deviceEntityName.indexOf("相");
                                    log.info("有拍有相" + deviceEntityName);
                                    airLineAndDeviceDto.setDeviceName(deviceEntityName.substring(0, capturePointIndex - 1));
                                    deviceName = deviceEntityName.substring(0, capturePointIndex - 3);
                                } else {
                                    airLineAndDeviceDto.setDeviceName(deviceEntityName);
                                }
                                airLineAndDeviceDto.setCapturePointName(deviceEntityName + photoNum);
                                airLineAndDeviceDto.setInfoCount(infoCount.getAndSet(infoCount.get() + 1));
                                airLineAndDeviceDtos.add(airLineAndDeviceDto);

                                log.info("拍照点全称：" + deviceEntityName + photoNum);
                                checkPointCount.getAndSet(checkPointCount.get() + 1);
                            }
                        });


                        log.info("当前的拍照点数为" + checkPointCount);
                        log.info("------------分割线--------------------");
                    }
                }
            }
            String test = "设备名称,拍照点";
            String[] title = test.split(",");
            exportService.exportByPOI(airLineAndDeviceDtos, title, "台账", "台账名称", response);

            return null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private Boolean storeFile(String path, MultipartFile file) {
        if (file.isEmpty()) {
            return false;
        }
        try {
            String fileName = file.getOriginalFilename();
            String filePath = path + "/" + fileName;
            CommonFileInDO commonFileInDO = new CommonFileInDO();
            commonFileInDO.setFileName(filePath);
            commonFileInDO.setInputStream(file.getInputStream());
            Optional<FileStorageOutDO> result = uploadManager.uploadFile(commonFileInDO);
            return result.isPresent();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 构建设备实体
     *
     * @param name
     * @param number
     * @param nestId
     * @param parentId
     * @param deviceArea
     * @param deviceType
     * @param spatialAttr
     * @param photoList
     * @param alginpoints
     * @param tag
     * @return
     */
    private StationDeviceEntity buildDevice(String name, String number, String nestId, Integer parentId, String deviceArea, String deviceType, String spatialAttr, String photoList, String entryAssistPoint, String alginpoints, String tag) {
        StationDeviceEntity device = new StationDeviceEntity();
        device.setName(name);
        device.setUuid(number);
        device.setParentId(parentId);
        device.setDeviceArea(deviceArea);
        device.setDeviceType(deviceType);
        device.setSpatialAttr(spatialAttr);
        device.setPhotoList(photoList);
        device.setBaseNestId(nestId);
        device.setEntryAssistPoint(entryAssistPoint);
        device.setAlginpoints(alginpoints);
        device.setTag(tag);
        return device;
    }

    /**
     * 获取设备空间属性
     *
     * @param spaceStr
     * @return
     */
    private String getSpace(String spaceStr) {
        if (spaceStr.trim().length() < 2) {
            return "L";
        }
        String space = spaceStr.trim().substring(0, 2);
        switch (space) {
            case "中空":
                return "M";
            case "高空":
                return "H";
            default:
                return "L";
        }
    }

    /**
     * 根据code获取设备空间属性
     *
     * @param code
     * @return
     */
    private String getSpaceByCode(Integer code) {
        switch (code) {
            case 2:
                return "M";
            case 3:
                return "H";
            default:
                return "L";
        }
    }

    /**
     * 设备区域：
     * "zb": "主变区域",
     * "10": "10kV区域",
     * "35": "35kV区域",
     * "11": "110kV区域",
     * "22": "220kV区域",
     * "33": "330kV区域",
     * "50": "500kV区域",
     * "80": "800kV区域",
     * "ff": "其他区域"
     *
     * @param deviceStr
     * @return
     */
    private String getDeviceArea(String deviceStr) {
        String deviceSub = deviceStr.trim().substring(0, 2);
        switch (deviceSub) {
            case "10":
                return "10";
            case "35":
                return "35";
            case "11":
                return "110";
            case "22":
                return "220";
            case "33":
                return "330";
            case "50":
                return "500";
            case "主变":
                return "zb";
            default:
                return "ff";
        }
    }

    /**
     * 创建巡检点实体
     *
     * @param name
     * @param uuid
     * @param deviceId
     * @param deviceUuid
     * @param nestId
     * @return
     */
    private StationCheckpointEntity buildCheckpoint(String name, String uuid, Integer deviceId, String deviceUuid, String nestId) {
        StationCheckpointEntity p = new StationCheckpointEntity();
        p.setName(name);
        p.setUuid(uuid);
        p.setDeviceId(deviceId);
        p.setDeviceUuid(deviceUuid);
        p.setBaseNestId(nestId);
        p.setLongitude(new BigDecimal(0d));
        p.setLatitude(new BigDecimal(0d));
        p.setAltitude(new BigDecimal(0d));
        return p;
    }

    /**
     * 更新巡检点数据
     *
     * @param json
     */
    private void updateCheckpoint(MultipartFile json) {
        Type type = new TypeToken<CheckPointJson>() {
        }.getType();

        try {
            CheckPointJson result = new Gson().fromJson(new InputStreamReader(json.getInputStream(), "utf-8"), type);
            logger.info("更新巡检点坐标");
            if (result != null) {
                List<CheckpointDTO> checkpointDTOList = result.getROUTELIST();
                if (!CollectionUtils.isEmpty(checkpointDTOList)) {
                    for (CheckpointDTO dto : checkpointDTOList) {
                        StationCheckpointEntity checkPoint = new StationCheckpointEntity();
                        checkPoint.setUuid(dto.getGuid_id());
                        checkPoint.setJobAttr(dto.getStyle());
                        checkPoint.setName(dto.getByname());
                        if (StringUtils.hasText(dto.getGeopos())) {
                            String[] geoposArr = dto.getGeopos().trim().split(",");
                            BigDecimal longitude = new BigDecimal(geoposArr[1].trim());
                            BigDecimal latitude = new BigDecimal(geoposArr[0].trim());
                            BigDecimal altitude = new BigDecimal(geoposArr[2].trim());
                            checkPoint.setLongitude(longitude);
                            checkPoint.setLatitude(latitude);
                            checkPoint.setAltitude(altitude);
                            stationCheckpointService.updateByUuid(checkPoint);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        logger.info("更新巡检点坐标完成");

    }

}
