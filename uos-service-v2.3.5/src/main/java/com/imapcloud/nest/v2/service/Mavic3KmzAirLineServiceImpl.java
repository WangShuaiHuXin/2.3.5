package com.imapcloud.nest.v2.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.enums.AircraftCodeEnum;
import com.imapcloud.nest.v2.common.enums.*;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.service.converter.DJITaskFileConverter;
import com.imapcloud.nest.v2.service.dto.in.DJIKmlDTO;
import com.imapcloud.nest.v2.service.dto.in.DJIWpmlDTO;
import com.imapcloud.nest.v2.service.dto.in.DjiAirLineDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class Mavic3KmzAirLineServiceImpl extends AbstractAirLineService {
    @Override
    public String transformKmzToJsonMainImpl(MultipartFile multipartFile) {
        return super.transformKmzToJsonMain(multipartFile);
    }

    @Override
    public String transformJsonToKmzMainImpl(String airLineJson, Object... param) {

        if (StringUtils.isEmpty(airLineJson)) {
            return "";
        }
        DjiAirLineDTO djiAirLineDTO = JSON.parseObject(airLineJson, DjiAirLineDTO.class);
        //从newJson转化为DTO
        checkDjiAirLine(djiAirLineDTO);
        DJIKmlDTO kmlDTO = this.transformAirLineToDTO(new DJIKmlDTO(), djiAirLineDTO, param);
        DJIWpmlDTO wpmlDTO = DJITaskFileConverter.INSTANCES.convert(kmlDTO);
        String kmlHeightMode = kmlDTO.getDocument().getFolder().getWaylineCoordinateSysParam().getHeightMode();
        String heightMode = DJIHeightModeEnum.RELATIVE_TO_STARTPOINT.getCode().equals(kmlHeightMode)
                ?DJIHeightModeEnum.RELATIVE_TO_STARTPOINT.getCode()
                :DJIHeightModeEnum.WGS84.getCode();
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
        if( DJIHeightModeEnum.RELATIVE_TO_STARTPOINT.getCode().equals(airLineDjiKml.getHeightMode()) ){
            //参考起飞点 - 基站经纬度、高度、海拔
            missionConfig.setTakeOffRefPoint(String.format("%s,%s,%s", airLineDjiKml.getRefPointLat().toString() , airLineDjiKml.getRefPointLng().toString() ,  airLineDjiKml.getRefPointAlt().toString()))
                    //参考起飞点高度 - 对地高度
                    .setTakeOffRefPointAGLHeight(airLineDjiKml.getRefPointAglAlt()) ;
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
                        :DJIHeightModeEnum.RELATIVE_TO_STARTPOINT.getCode());

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
                                .setPayloadLensIndex(finalPayloadLensIndex);
                        if(!StringUtils.isEmpty(customActions.getByname())) {
                            actionActuatorFuncParam.setFileSuffix(customActions.getByname()+String.valueOf(takePhotoIndex[0]++));
                        }else{
                            actionActuatorFuncParam.setFileSuffix("");
                        }
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

}
