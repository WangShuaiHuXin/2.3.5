package com.imapcloud.nest.v2.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.constant.SymbolConstants;
import com.imapcloud.nest.enums.AircraftCodeEnum;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.nest.v2.common.enums.*;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.utils.ZipFileUtils;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileStorageOutDO;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.nest.v2.service.converter.DJITaskFileConverter;
import com.imapcloud.nest.v2.service.dto.in.DJIKmlDTO;
import com.imapcloud.nest.v2.service.dto.in.DJIWpmlDTO;
import com.imapcloud.nest.v2.service.dto.in.DjiAirLineDTO;
import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipOutputStream;

public class PubAirLineService {

    /**
     * 默认传的是 param[0] 无人机类型 param[1] 相机型号
     * 从json 转换到 kmz
     *
     * @param djiAirLineJson
     * @param param
     * @return
     */
    public String transformJsonToKmzMain(String djiAirLineJson, Object... param) {
        if (StringUtils.isEmpty(djiAirLineJson)) {
            return "";
        }
        DjiAirLineDTO djiAirLineDTO = JSON.parseObject(djiAirLineJson, DjiAirLineDTO.class);
        //从newJson转化为DTO
        checkDjiAirLine(djiAirLineDTO);
        DJIKmlDTO kmlDTO = transformAirLineToDTO(new DJIKmlDTO(), djiAirLineDTO, param);
        DJIWpmlDTO wpmlDTO = DJITaskFileConverter.INSTANCES.convert(kmlDTO);
        String kmlHeightMode = kmlDTO.getDocument().getFolder().getWaylineCoordinateSysParam().getHeightMode();
        String heightMode = DJIHeightModeEnum.RELATIVE_TO_STARTPOINT.getCode().equals(kmlHeightMode)
                ? DJIHeightModeEnum.RELATIVE_TO_STARTPOINT.getCode()
                : DJIHeightModeEnum.WGS84.getCode();
        wpmlDTO.getDocument().getFolder().setExecuteHeightMode(heightMode);
        //将DTO转化为kml
        String kmlStr = transformDTOToKml(kmlDTO);
        //将DTO转化为wpml
        String wpmlStr = transformDTOToKml(wpmlDTO);
        //格式化
        kmlStr = XmlUtil.format(kmlStr);
        //documents
        kmlStr = kmlStr.replaceAll("<wstxns1:Document xmlns:wstxns1=\"wpml\">", "<Document>");
        kmlStr = kmlStr.replaceAll("</wstxns1:Document>", "</Document>");
        //folder
        kmlStr = kmlStr.replaceAll("<Folder xmlns=\"\">", "<Folder>");
        //kml
        kmlStr = kmlStr.replaceAll("<kml xmlns=\"http://www.opengis.net/kml/2.2\">", "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:wpml=\"http://www.dji.com/wpmz/1.0.2\">");
        //签名前缀
        kmlStr = kmlStr.replaceAll("wstxns1:", "wpml:");

        wpmlStr = XmlUtil.format(wpmlStr);
        //documents
        wpmlStr = wpmlStr.replaceAll("<wstxns1:Document xmlns:wstxns1=\"wpml\">", "<Document>");
        wpmlStr = wpmlStr.replaceAll("</wstxns1:Document>", "</Document>");
        //folder
        wpmlStr = wpmlStr.replaceAll("<Folder xmlns=\"\">", "<Folder>");
        //kml
        wpmlStr = wpmlStr.replaceAll("<kml xmlns=\"http://www.opengis.net/kml/2.2\">", "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:wpml=\"http://www.dji.com/wpmz/1.0.2\">");
        //签名前缀
        wpmlStr = wpmlStr.replaceAll("wstxns1:", "wpml:");

        //生成文件、合并成kmz，并上传
        String urlPath = generalKmzAndUpload(kmlStr, wpmlStr);

        return urlPath;
    }

    /**
     * 检查
     *
     * @param djiAirLineDTO
     */
    public void checkDjiAirLine(DjiAirLineDTO djiAirLineDTO) {
        Optional.ofNullable(djiAirLineDTO).map(DjiAirLineDTO::getLineConfigs)
                .map(DjiAirLineDTO.LineConfigs::getDjiKml)
                .orElseThrow(() -> new BusinessException("djiAirLine缺少航线全局数据"));
        Optional.ofNullable(djiAirLineDTO).map(DjiAirLineDTO::getMapConfigs)
                .map(DjiAirLineDTO.MapConfigs::getPoints)
                .map(List::size)
                .orElseThrow(() -> new BusinessException("djiAirLine缺少航点数据"));
    }

    /**
     * 将json转化为kml航线数据
     *
     * @return
     */
    public DJIKmlDTO transformAirLineToDTO(DJIKmlDTO djiKmlDTO, DjiAirLineDTO djiAirLineDTO, Object... param) {
        //一级
        DJIKmlDTO.Document document = new DJIKmlDTO.Document();
        djiKmlDTO.setDocument(document);
        //二级
        DJIKmlDTO.Folder folder = new DJIKmlDTO.Folder();
        DJIKmlDTO.MissionConfig missionConfig = new DJIKmlDTO.MissionConfig();

        document.setAuthor(TrustedAccessTracerHolder.get().getUsername());
        document.setCreateTime(String.valueOf(System.currentTimeMillis()));
        document.setUpdateTime(String.valueOf(System.currentTimeMillis()));

        document.setMissionConfig(missionConfig);
        document.setFolder(folder);

        DJIKmlDTO.DroneInfo droneInfo = new DJIKmlDTO.DroneInfo();

        DJIKmlDTO.PayloadInfo payloadInfo = new DJIKmlDTO.PayloadInfo();

        DjiAirLineDTO.DJIKml airLineDjiKml = djiAirLineDTO.getLineConfigs().getDjiKml();
        //其他基站涉及基站状态信息
        missionConfig.setFlyToWaylineMode(DJIFlyToWaylineModeEnum.SAFELY.getCode())
                .setFinishAction(DJIFinishActionEnum.GO_HOME.getCode())
                .setExitOnRCLost(DJIExitOnRCLostEnum.GO_CONTINUE.getCode())
                .setExecuteRCLostAction(DJIExecuteRCLostActionEnum.GO_BACK.getCode())
                //安全起飞高度
                .setTakeOffSecurityHeight(BigDecimal.valueOf(airLineDjiKml.getTakeOffLandAlt()))
                //航线速度
                .setGlobalTransitionalSpeed(BigDecimal.valueOf(airLineDjiKml.getAutoFlightSpeed()))
                //返航高度
                .setGlobalRTHHeight(BigDecimal.valueOf(airLineDjiKml.getTakeOffLandAlt()))
                .setDroneInfo(droneInfo)
                .setPayloadInfo(payloadInfo);

        //默认为相对起飞点高度 ， 其他模式，传什么就记录什么格式
        if (DJIHeightModeEnum.RELATIVE_TO_STARTPOINT.getCode().equals(airLineDjiKml.getHeightMode())) {
            //参考起飞点 - 基站经纬度、高度、海拔
            missionConfig.setTakeOffRefPoint(String.format("%s,%s,%s", airLineDjiKml.getRefPointLat().toString(), airLineDjiKml.getRefPointLng().toString(), airLineDjiKml.getRefPointAlt().toString()))
                    //参考起飞点高度 - 对地高度
                    .setTakeOffRefPointAGLHeight(airLineDjiKml.getRefPointAglAlt());
        }

        //无人机信息-默认值M30
        //查询无人机信息
        Integer droneEnumValue = 67, droneSubEnumValue = 0;
        if (Objects.nonNull(param) && StringUtils.hasText(param[0].toString())) {
            if (AircraftCodeEnum.M30T.equals(AircraftCodeEnum.getInstance(param[0].toString()))) {
                droneSubEnumValue = 1;
            }
        }

        droneInfo.setDroneEnumValue(droneEnumValue)
                .setDroneSubEnumValue(droneSubEnumValue);

        DJIKmlDTO.CustomPayloadActionInfo customPayloadActionInfo = new DJIKmlDTO.CustomPayloadActionInfo();
        //负载信息-默认值M30T，不用M30
        Integer payloadEnumValue = DJICameraEnum.M30T_CAMERA.getPayloadEnumValue(), payloadSubEnumValue = DJICameraEnum.M30T_CAMERA.getPayloadSubEnumValue(), payloadPositionIndex = DJICameraEnum.M30T_CAMERA.getPayloadPositionIndex();
        //存储照片信息,默认M30T，可以拍红外
        String payloadLensIndex = "wide,zoom,ir";
        //查询相机型号
        if (Objects.nonNull(param) && StringUtils.hasText(param[1].toString())) {
            DJICameraEnum djiCameraEnum = DJICameraEnum.findMatch(param[1].toString())
                    .orElseGet(() -> DJICameraEnum.M30T_CAMERA);
            //目前只适配M30和M30T
            if (DJICameraEnum.M30_CAMERA.equals(djiCameraEnum)) {
                payloadLensIndex = "wide,zoom";
            } else {
                //后续需要匹配上uos跟大疆的相机对应关联关系，就可以注释该代码
                djiCameraEnum = DJICameraEnum.M30T_CAMERA;
            }
            payloadEnumValue = djiCameraEnum.getPayloadEnumValue();
            payloadSubEnumValue = djiCameraEnum.getPayloadSubEnumValue();
            payloadPositionIndex = djiCameraEnum.getPayloadPositionIndex();
        }
        //默认M30T
        payloadInfo.setPayloadEnumValue(payloadEnumValue)
                .setPayloadSubEnumValue(payloadSubEnumValue)
                .setPayloadPositionIndex(payloadPositionIndex)
                .setCustomPayloadActionInfo(customPayloadActionInfo);


        //自定义负载信息-默认值
        customPayloadActionInfo.setCustomActionType(0)
//                    .setCustomActionName()
                .setCustomActionMinParamValue(0)
                .setCustomActionMaxParamValue(0)
                .setCustomActionIndex(0);

        //坐标系参数，默认值
        DJIKmlDTO.WaylineCoordinateSysParam waylineCoordinateSysParam = new DJIKmlDTO.WaylineCoordinateSysParam();
        waylineCoordinateSysParam.setCoordinateMode("WGS84")
                //相对基站高度 0215- 将相对地面高度 改为 传什么高度模式，就是什么高度模式
                .setHeightMode(StringUtils.hasText(airLineDjiKml.getHeightMode()) ? airLineDjiKml.getHeightMode()
                        : DJIHeightModeEnum.RELATIVE_TO_STARTPOINT.getCode());

        //全局偏航角模式
        DJIKmlDTO.GlobalWaypointHeadingParam globalWaypointHeadingParam = new DJIKmlDTO.GlobalWaypointHeadingParam();
        String headingMode = DJIHeadingTypeEnum.findMatchDjiActionType(airLineDjiKml
                .getHeadingMode())
                .map(DJIHeadingTypeEnum::getDjiHeadingType)
                .orElseThrow(() -> new BusinessException("查询不到对应的偏航角模式"));
        globalWaypointHeadingParam.setWaypointHeadingMode(headingMode)
                .setWaypointHeadingAngle(0)
                .setWaypointPoiPoint("0.000000,0.000000,0.000000")
                .setWaypointHeadingPathMode("followBadArc");

        //航点信息
        List<DJIKmlDTO.Placemark> placemarks = new ArrayList<>();

        List<DjiAirLineDTO.Points> pointsList = djiAirLineDTO.getMapConfigs().getPoints();
        BigDecimal distance = airLineDjiKml.getDistance(), duration = airLineDjiKml.getDuration();

        AtomicInteger index = new AtomicInteger(0), actionGroupIndex = new AtomicInteger(0);
        String finalPayloadLensIndex = payloadLensIndex;

        int[] takePhotoIndex = {0};
        pointsList.stream().forEach(x -> {
            DJIKmlDTO.Placemark placemark = new DJIKmlDTO.Placemark();
            DJIKmlDTO.Point point = new DJIKmlDTO.Point();
            point.setCoordinates(x.getLocation().getLng() + "," + x.getLocation().getLat());

            Integer useStraightLine = 1, useGlobalHeight = 0, useGlobalSpeed = 0, useGlobalTurnParam = 1, useGlobalHeadingParam = 1;

            //判断是否锁定下一偏航角模式，如果是，需要采用航点方式
            DJIKmlDTO.WaypointHeadingParam waypointHeadingParam = new DJIKmlDTO.WaypointHeadingParam();
            waypointHeadingParam.setWaypointHeadingMode(headingMode)
                    .setWaypointHeadingAngle(BigDecimal.ZERO)
                    .setWaypointPoiPoint("0.000000,0.000000,0.000000")
                    .setWaypointHeadingPathMode("followBadArc");


            //转弯模式
            DJIKmlDTO.WaypointTurnParam waypointTurnParam = new DJIKmlDTO.WaypointTurnParam();
            waypointTurnParam.setWaypointTurnMode("toPointAndStopWithDiscontinuityCurvature")
                    .setWaypointTurnDampingDist(new BigDecimal("0.2"));


            placemark.setPoint(point)
                    .setIndex(index.get())
                    // 椭球高度，暂时取绝对高度
                    .setEllipsoidHeight(x.getLocation().getEllipsoidHeight() == null
                            ? x.getLocation().getAlt()
                            : x.getLocation().getEllipsoidHeight())
                    .setHeight(x.getLocation().getAlt())
                    //偏航角
                    .setWaypointHeadingParam(waypointHeadingParam)
                    .setWaypointTurnParam(waypointTurnParam)
                    .setUseStraightLine(useStraightLine)
                    .setUseGlobalHeight(useGlobalHeight)
                    .setUseGlobalSpeed(useGlobalSpeed)
                    .setWaypointSpeed(x.getSpeed() == null
                            ? BigDecimal.valueOf(airLineDjiKml.getSpeed())
                            : BigDecimal.valueOf(x.getSpeed()))
                    .setUseGlobalHeadingParam(useGlobalHeadingParam)
                    .setUseGlobalTurnParam(useGlobalTurnParam);
//                        .setGimbalPitchAngle()
//                        .setActionGroup();
            //如果是锁定下一航点，则需要设置heading值，对应于司空平滑模式
            //偏航角模式 - 指定下一偏航角
            if (DJIHeadingTypeEnum.USING_WAYPOINT_HEADING.getDjiHeadingType().equals(headingMode)) {
                waypointHeadingParam.setWaypointHeadingMode(headingMode)
                        .setWaypointHeadingAngle(x.getHeading());
                placemark.setUseGlobalHeadingParam(0);
            }


            List<DjiAirLineDTO.CustomActions> customActionsList = x.getCustomActions();
            if (CollectionUtil.isNotEmpty(customActionsList)) {
                DJIKmlDTO.ActionGroup actionGroup = new DJIKmlDTO.ActionGroup();
                placemark.setActionGroup(actionGroup);
                List<DJIKmlDTO.Action> actionList = new ArrayList<>();
                DJIKmlDTO.ActionTrigger actionTrigger = new DJIKmlDTO.ActionTrigger();
                actionTrigger.setActionTriggerType("reachPoint");
                actionGroup.setActionGroupId(actionGroupIndex.get())
                        .setActionGroupStartIndex(index.get())
                        .setActionGroupEndIndex(index.get())
                        .setActionGroupMode("sequence")
                        .setActionTrigger(actionTrigger)
                        .setAction(actionList);
                actionGroupIndex.set(actionGroupIndex.get() + 1);
                int actionIndexId = 0;


                for (DjiAirLineDTO.CustomActions customActions : customActionsList) {
                    DJIKmlDTO.Action action = new DJIKmlDTO.Action();
                    DJIKmlDTO.ActionActuatorFuncParam actionActuatorFuncParam = new DJIKmlDTO.ActionActuatorFuncParam();

//                    actionActuatorFuncParam.setPayloadPositionIndex(0)
//                            .setUseGlobalPayloadLensIndex(1);
                    String djiActionType = DJIActionTypeEnum.findMatchDjiActionType(customActions.getActionType())
                            .map(DJIActionTypeEnum::getDjiActionType)
                            .orElseGet(() -> "");
                    action.setActionId(actionIndexId++)
                            .setActionActuatorFunc(djiActionType)
                            .setActionActuatorFuncParam(actionActuatorFuncParam);
                    if (DJIActionTypeEnum.GIMBAL_ROTATE.getDjiActionType().equals(djiActionType)) {
                        actionActuatorFuncParam.setGimbalRotateMode("absoluteAngle")
                                .setGimbalHeadingYawBase("north")
                                .setGimbalPitchRotateEnable(1)
                                .setGimbalPitchRotateAngle(customActions.getValue())
                                .setGimbalRollRotateEnable(0)
                                .setGimbalRollRotateAngle(BigDecimal.ZERO)
                                .setGimbalYawRotateEnable(0)
                                .setGimbalYawRotateAngle(BigDecimal.ZERO)
                                .setGimbalRotateTimeEnable(0)
                                .setGimbalRotateTime(BigDecimal.ZERO)
                                .setPayloadPositionIndex(0);
                    } else if (DJIActionTypeEnum.TAKE_PHOTO.getDjiActionType().equals(djiActionType)) {
                        actionActuatorFuncParam.setPayloadPositionIndex(0)
                                .setUseGlobalPayloadLensIndex(1)
                                .setPayloadLensIndex(finalPayloadLensIndex)
                                .setFileSuffix(String.valueOf(takePhotoIndex[0]++));

                    } else if (DJIActionTypeEnum.START_RECORD.getDjiActionType().equals(djiActionType)) {
                        actionActuatorFuncParam.setPayloadPositionIndex(0);
                    } else if (DJIActionTypeEnum.STOP_RECORD.getDjiActionType().equals(djiActionType)) {
                        actionActuatorFuncParam.setPayloadPositionIndex(0);
                    } else if (DJIActionTypeEnum.FOCUS.getDjiActionType().equals(djiActionType)) {
                        actionActuatorFuncParam.setPayloadPositionIndex(0)
                                .setUseGlobalPayloadLensIndex(1);
                    } else if (DJIActionTypeEnum.ZOOM.getDjiActionType().equals(djiActionType)) {
                        //前端计算逻辑是*10/240算出倍率 ， 然后给后端的时候，会记录算出实际值，后端自行乘10
                        actionActuatorFuncParam.setFocalLength(customActions.getValue().divide(new BigDecimal(10)));
                    } else if (DJIActionTypeEnum.ROTATE_YAW.getDjiActionType().equals(djiActionType)) {
                        actionActuatorFuncParam.setAircraftHeading(customActions.getValue())
                                .setAircraftPathMode("counterClockwise");
                    } else if (DJIActionTypeEnum.HOVER.getDjiActionType().equals(djiActionType)) {
                        actionActuatorFuncParam.setHoverTime(customActions.getValue() == null
                                ? BigDecimal.ONE
                                : customActions.getValue().divide(new BigDecimal(1000), 1, RoundingMode.HALF_UP));
                    }

                    actionList.add(action);
                }

            }


            index.set(index.get() + 1);
            placemarks.add(placemark);

        });

        //模版信息
        folder.setTemplateType("waypoint")
                .setUseGlobalTransitionalSpeed(BigDecimal.valueOf(airLineDjiKml.getAutoFlightSpeed()))
                .setTemplateId(0)
                .setWaylineCoordinateSysParam(waylineCoordinateSysParam)
                //全局速度
                .setAutoFlightSpeed(BigDecimal.valueOf(airLineDjiKml.getSpeed()))
                //全局高度
                .setGlobalHeight(BigDecimal.valueOf(airLineDjiKml.getGlobalHeight()))
                //是否开启标定飞行
                .setCaliFlightEnable(0)
//                    .setTransitionalSpeed()
                .setGimbalPitchMode("manual")
                .setGlobalWaypointHeadingParam(globalWaypointHeadingParam)
                .setGlobalWaypointTurnMode("toPointAndStopWithDiscontinuityCurvature")
                .setGlobalUseStraightLine(1)
                .setPlacemark(placemarks)
                .setDistance(distance)
                .setDuration(duration);

        //负载信息
        DJIKmlDTO.PayloadParam payloadParam = new DJIKmlDTO.PayloadParam();
        payloadParam.setPayloadPositionIndex(0)
                .setFocusMode("firstPoint")
                .setReturnMode("singleReturnFirst")
                .setMeteringMode("average")
                .setSamplingRate(240000)
                .setScanningMode("repetitive")
                .setImageFormat(payloadLensIndex);
        folder.setPayloadParam(payloadParam);


        return djiKmlDTO;
    }

    /**
     * 转换为KmlDTO、WpmlDTO
     *
     * @return
     */
    public String transformDTOToKml(Object objDTO) {
        String str = "";
        try {
            ObjectMapper mapper = new XmlMapper();
            //忽略实体类中不含有的字段方法
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //xml转kml对象
            str = mapper.writeValueAsString(objDTO);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        return str;
    }


//    /**
//     * 转化成kmz，并上传
//     *
//     * @param kmlStr
//     * @param wpmlStr
//     * @return
//     */
//    public String generalKmzAndUpload(String kmlStr, String wpmlStr) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//        UUID uuid = UUID.randomUUID();
//        String path = String.format("%s%s"
//                , UploadTypeEnum.TASK_FILE_PATH.getPath()
//                , uuid + ".kmz");
//        try {
//            String wpmz = String.format("data/combine/%s/wpmz", uuid);
//            String kmlPath = String.format("%s/template.kml", wpmz), wpmlPath = String.format("%s/waylines.wpml", wpmz);
//            File kmlFile = FileUtil.newFile(kmlPath), wpmlFile = FileUtil.newFile(wpmlPath);
//            //生成文件
//            FileUtil.writeUtf8String(kmlStr, kmlFile);
//            FileUtil.writeUtf8String(wpmlStr, wpmlFile);
//
//            this.zipFile(wpmz, byteArrayOutputStream);
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//            MinIoUnit.putObject(path, inputStream, "application/octet-stream");
//        } finally {
//            try {
//                byteArrayOutputStream.close();
//                //删除文件
//                File filePath = Paths.get(String.format("data/combine/%s", uuid)).toFile();
//                if (filePath != null && filePath.exists()) {
//                    FileUtil.del(filePath);
//                }
//            } catch (IOException e) {
//                throw new BusinessException(e.getMessage());
//            }
//        }
//
//        return path;
//    }

    /**
     * 转化成kmz，并上传
     *
     * @param kmlStr
     * @param wpmlStr
     * @return
     */
    public String generalKmzAndUpload(String kmlStr, String wpmlStr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        UUID uuid = UUID.randomUUID();
        try {
            String wpmz = String.format("data/combine/%s/wpmz", uuid);
            String kmlPath = String.format("%s/template.kml", wpmz), wpmlPath = String.format("%s/waylines.wpml", wpmz);
            File kmlFile = FileUtil.newFile(kmlPath), wpmlFile = FileUtil.newFile(wpmlPath);
            //生成文件
            FileUtil.writeUtf8String(kmlStr, kmlFile);
            FileUtil.writeUtf8String(wpmlStr, wpmlFile);

            this.zipFile(wpmz, byteArrayOutputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//            MinIoUnit.putObject(path, inputStream, "application/octet-stream");
            UploadManager uploadManager = SpringContextUtils.getBean(UploadManager.class);
            CommonFileInDO commonFileInDO = new CommonFileInDO();
            commonFileInDO.setFileName(UUID.randomUUID() + ".kmz");
            commonFileInDO.setInputStream(inputStream);
            Optional<FileStorageOutDO> outDO = uploadManager.uploadFile(commonFileInDO);
            if (outDO.isPresent()) {
                return outDO.get().getStoragePath() + SymbolConstants.SLASH_LEFT + outDO.get().getFilename();
            }
        } finally {
            try {
                byteArrayOutputStream.close();
                //删除文件
                File filePath = Paths.get(String.format("data/combine/%s", uuid)).toFile();
                if (filePath.exists()) {
                    FileUtil.del(filePath);
                }
            } catch (IOException e) {
                throw new BusinessException(e.getMessage());
            }
        }

        return "";
    }


    /**
     * 将文件压缩
     */
    public void zipFile(String path, ByteArrayOutputStream byteArrayOutputStream) {
        ZipOutputStream zipOutputStream = null;
        try {
            File zipFile = FileUtil.newFile(path);
            zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            ZipFileUtils.zipFile(zipFile, zipFile.getName(), zipOutputStream);
        } finally {
            try {
                zipOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 从 kmz 转换成 json
     *
     * @param multipartFile
     * @return
     */
    public String transformKmzToJsonMain(MultipartFile multipartFile) {
        //解压kmz，获取kml、wpml的Str内容
        List<String> list = getKml(multipartFile);
        if (list.isEmpty()) {
            return "";
        }
        String kmlStr = list.get(0), wpmlStr = list.get(1);
        DJIKmlDTO djiKmlDTO = (DJIKmlDTO) transformKmlToDTO(kmlStr, DJIKmlDTO.class);
        DJIWpmlDTO djiWpmlDTO = (DJIWpmlDTO) transformKmlToDTO(wpmlStr, DJIWpmlDTO.class);
        String djiAirLine = transDTOToJson(djiKmlDTO, djiWpmlDTO);

//        log.info("tranTo -> {}", djiAirLine);
        return djiAirLine;
    }

    /**
     * @param djiKmlDTO
     * @param djiWpmlDTO
     * @return
     */
    public String transDTOToJson(DJIKmlDTO djiKmlDTO, DJIWpmlDTO djiWpmlDTO) {
        String djiAirLine = "";
        //一级
        DjiAirLineDTO djiAirLineDTO = new DjiAirLineDTO();
        //二级
        DjiAirLineDTO.LineConfigs lineConfigs = new DjiAirLineDTO.LineConfigs();
        DjiAirLineDTO.MapConfigs mapConfigs = new DjiAirLineDTO.MapConfigs();
        //三级
        DjiAirLineDTO.DJIKml djiKml = new DjiAirLineDTO.DJIKml();
        List<DjiAirLineDTO.Points> pointsList = new ArrayList<>();
        //四级 - 待定
//        DjiAirLineDTO.Location location = new DjiAirLineDTO.Location();
//        List<DjiAirLineDTO.CustomActions> customActions = new ArrayList<>();

        //一级
        djiAirLineDTO.setMode("DJI_KML");
        djiAirLineDTO.setMapConfigs(mapConfigs);
        djiAirLineDTO.setLineConfigs(lineConfigs);
        //二级
        mapConfigs.setPoints(pointsList);

        DJIKmlDTO.Folder folder = Optional.ofNullable(djiKmlDTO)
                .map(DJIKmlDTO::getDocument)
                .map(DJIKmlDTO.Document::getFolder)
                .orElseGet(() -> new DJIKmlDTO.Folder());

        //航点信息
        List<DJIKmlDTO.Placemark> placeMarkList = Optional.ofNullable(djiKmlDTO)
                .map(DJIKmlDTO::getDocument)
                .map(DJIKmlDTO.Document::getFolder)
                .map(DJIKmlDTO.Folder::getPlacemark).orElseGet(() -> new ArrayList<>());

        placeMarkList.stream().forEach(x -> {
            DjiAirLineDTO.Points point = new DjiAirLineDTO.Points();
            DjiAirLineDTO.Location location = new DjiAirLineDTO.Location();

            String coordinates = x.getPoint().getCoordinates();
            //经纬度-dji
            String[] coordinate = coordinates.split(",");

            point.setLocation(location.setLng(new BigDecimal(coordinate[0].trim()))
                    .setLat(new BigDecimal(coordinate[1].trim()))
                    .setAlt(x.getHeight())
                    .setEllipsoidHeight(x.getEllipsoidHeight())
            );

            point.setSpeed( (x.getUseGlobalSpeed()!=null && x.getUseGlobalSpeed() == 1)
                    ? folder.getAutoFlightSpeed().setScale(0, RoundingMode.HALF_UP).intValue()
                    : x.getWaypointSpeed().setScale(0, RoundingMode.HALF_UP).intValue());
            point.setHeading(BigDecimal.ZERO);

            DJIKmlDTO.ActionGroup actionGroup = x.getActionGroup();
            if (Objects.nonNull(actionGroup)) {
                List<DJIKmlDTO.Action> actionList = actionGroup.getAction();
                point.setCustomActions(handleActionList(actionList));
            }

            pointsList.add(point);
        });

        DJIKmlDTO.MissionConfig missionConfig = Optional.ofNullable(djiKmlDTO)
                .map(DJIKmlDTO::getDocument)
                .map(DJIKmlDTO.Document::getMissionConfig)
                .orElseGet(() -> new DJIKmlDTO.MissionConfig());

        lineConfigs.setDjiKml(djiKml);
        //全局速度
        djiKml.setSpeed(folder.getAutoFlightSpeed().setScale(0, RoundingMode.HALF_UP).intValue());
        djiKml.setTakeOffLandAlt(missionConfig.getTakeOffSecurityHeight().setScale(0, RoundingMode.HALF_UP).intValue());
        //过渡速度
        djiKml.setAutoFlightSpeed(missionConfig.getGlobalTransitionalSpeed().setScale(0, RoundingMode.HALF_UP).intValue());
        djiKml.setGlobalHeight(folder.getGlobalHeight().setScale(0, RoundingMode.HALF_UP).intValue());
        //如果是除了沿航线、锁定当前、锁定下一航线之外的模式，都归为沿航线；
        //只有锁定下一航线，才需要设heading值；由于大疆司空平台没有平滑模式可以选，故这里返回前台都是0度
        djiKml.setHeadingMode(DJIHeadingTypeEnum
                .findMatchActionType(folder.getGlobalWaypointHeadingParam().getWaypointHeadingMode())
                .map(DJIHeadingTypeEnum::getHeadingType)
                .filter(x -> DJIHeadingTypeEnum.AUTO.getHeadingType().equals(x)
                        || DJIHeadingTypeEnum.USING_INITIAL_DIRECTION.getHeadingType().equals(x)
                        || DJIHeadingTypeEnum.USING_WAYPOINT_HEADING.getHeadingType().equals(x))
                .orElseGet(() -> DJIHeadingTypeEnum.AUTO.getHeadingType()));
        //将高度模式返回
        djiKml.setHeightMode(folder.getWaylineCoordinateSysParam().getHeightMode());
        djiAirLine = JSON.toJSONString(djiAirLineDTO);
        return djiAirLine;
    }

    /**
     * 处理动作列表
     *
     * @param actionList
     * @return
     */
    public List<DjiAirLineDTO.CustomActions> handleActionList(List<DJIKmlDTO.Action> actionList) {
        List<DjiAirLineDTO.CustomActions> customActionsList = new ArrayList<>();
        actionList.stream().forEach(x -> {
            DjiAirLineDTO.CustomActions customAction = new DjiAirLineDTO.CustomActions();
            String actionType = DJIActionTypeEnum.findMatchActionType(x.getActionActuatorFunc())
                    .map(DJIActionTypeEnum::getActionType)
                    .orElseGet(() -> "");
            customAction.setActionType(actionType)
                    //未找到参数
                    .setValue(BigDecimal.ZERO);
            if (DJIActionTypeEnum.GIMBAL_ROTATE.getActionType().equals(actionType)) {
                BigDecimal gimbalPitchRotateAngle = x.getActionActuatorFuncParam().getGimbalPitchRotateAngle();
                if (gimbalPitchRotateAngle == null || x.getActionActuatorFuncParam().getGimbalPitchRotateEnable() == 0) {
                    return;
                }
                customAction.setValue(gimbalPitchRotateAngle);
            } else if (DJIActionTypeEnum.TAKE_PHOTO.getActionType().equals(actionType)) {
                //将司空重命名解析到平台
                DJIKmlDTO.ActionActuatorFuncParam actionActuatorFuncParam = x.getActionActuatorFuncParam();
                if (Objects.nonNull(actionActuatorFuncParam)) {
                    String fileSuffix = actionActuatorFuncParam.getFileSuffix();
                    if (StringUtils.hasLength(fileSuffix)) {
                        customAction.setByname(fileSuffix);
                    }
                }
                customAction.setValue(BigDecimal.ZERO);
            } else if (DJIActionTypeEnum.START_RECORD.getActionType().equals(actionType)) {
                customAction.setValue(BigDecimal.ZERO);
            } else if (DJIActionTypeEnum.STOP_RECORD.getActionType().equals(actionType)) {
                customAction.setValue(BigDecimal.ZERO);
            } else if (DJIActionTypeEnum.FOCUS.getActionType().equals(actionType)) {
                customAction.setValue(BigDecimal.ZERO);
            } else if (DJIActionTypeEnum.ZOOM.getActionType().equals(actionType)) {
                BigDecimal zoomLen = x.getActionActuatorFuncParam().getFocalLength();
                customAction.setValue(zoomLen.multiply(new BigDecimal(10)));
            } else if (DJIActionTypeEnum.ROTATE_YAW.getActionType().equals(actionType)) {
                BigDecimal aircraftHeading = x.getActionActuatorFuncParam().getAircraftHeading();
                customAction.setValue(aircraftHeading);
            } else if (DJIActionTypeEnum.HOVER.getActionType().equals(actionType)) {
                BigDecimal hoverTime = x.getActionActuatorFuncParam().getHoverTime();
                customAction.setValue(hoverTime.multiply(new BigDecimal(1000)));
            }
            customActionsList.add(customAction);
        });
        return customActionsList;
    }

    /**
     * 解压kmz，
     *
     * @param multipartFile
     * @return
     */
    public List<String> getKml(MultipartFile multipartFile) {
        List<String> list = new ArrayList<>();
        File file = null, zipFilePath = null;
        if (multipartFile.isEmpty() || !multipartFile.getOriginalFilename().endsWith(".kmz")) {
            //TODO 国际化
            throw new BusinessException("上传的kmz包不符合大疆航线包格式，请检查！");
        }
        try {
            //
            file = FileUtil.file("data/" + UUID.randomUUID() + ".zip");
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
            zipFilePath = ZipUtil.unzip(file);
            checkFile(zipFilePath);
            String kmlStr = "", wpmlStr = "", wpmzFilePath = String.format("%s/wpmz", zipFilePath.getPath());
            File wpmzFile = FileUtil.file(wpmzFilePath);
            for (File f : wpmzFile.listFiles()) {
                if (StringUtils.isEmpty(f.getName())) {
                    continue;
                }
                if (f.getName().endsWith(".kml")) {
                    kmlStr = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
                }
                if (f.getName().endsWith(".wpml")) {
                    wpmlStr = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
                }

            }
            list.add(kmlStr);
            list.add(wpmlStr);
        } catch (IOException e) {
            throw new BusinessException(e.getMessage());
        } finally {
            //删除文件
            if (file != null && file.exists()) {
                FileUtil.del(file);
            }
            if (zipFilePath != null && zipFilePath.exists()) {
                FileUtil.del(zipFilePath.getPath());
            }

        }
        return list;
    }

    /**
     * 检查文件是否符合格式
     *
     * @param zipFilePath
     * @return
     */
    public void checkFile(File zipFilePath) {
        boolean bol = false;
        if (zipFilePath.exists()) {
            for (File f : zipFilePath.listFiles()) {
                if ("wpmz".equals(f.getName())) {
                    bol = true;
                    break;
                }
            }
        }
        if (!bol) {
            throw new BusinessException("上传的kmz包不符合大疆航线包格式，请检查！");
        }
    }

    /**
     * 转换为KmlDTO、WpmlDTO
     *
     * @return
     */
    public Object transformKmlToDTO(String transStr, Class clazz) {
        Object dto = null;
        try {
            ObjectMapper mapper = new XmlMapper();
            //忽略实体类中不含有的字段方法
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //xml转kml对象
            dto = mapper.readValue(transStr, clazz);

        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        return dto;
    }

}
