package com.imapcloud.nest.v2.service.impl;

import com.alibaba.fastjson.JSON;
import com.imapcloud.nest.v2.common.enums.DJIActionTypeEnum;
import com.imapcloud.nest.v2.common.enums.DJIHeadingTypeEnum;
import com.imapcloud.nest.v2.service.AbstractAirLineService;
import com.imapcloud.nest.v2.service.dto.in.DJIKmlDTO;
import com.imapcloud.nest.v2.service.dto.in.DJIWpmlDTO;
import com.imapcloud.nest.v2.service.dto.in.DjiAirLineDTO;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.libfreenect._freenect_context;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class G900AirLineServiceImpl extends AbstractAirLineService {

    @Override
    public String transformKmzToJsonMainImpl(MultipartFile multipartFile) {

        List<String> list = getKml(multipartFile);
        if (list.isEmpty()) {
            return "";
        }
        String kmlStr = list.get(0), wpmlStr = list.get(1);
        DJIKmlDTO djiKmlDTO = (DJIKmlDTO) super.transformKmlToDTO(kmlStr, DJIKmlDTO.class);
        DJIWpmlDTO djiWpmlDTO = (DJIWpmlDTO) super.transformKmlToDTO(wpmlStr, DJIWpmlDTO.class);
        String djiAirLine = transDTOToJson(djiKmlDTO , djiWpmlDTO);
        return djiAirLine;
    }


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
                    if(StringUtils.hasLength(fileSuffix)) {
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
            } else if (DJIActionTypeEnum.HOVER.getActionType().equals(actionType)) {
                BigDecimal hoverTime = x.getActionActuatorFuncParam().getHoverTime();
                customAction.setValue(hoverTime.multiply(new BigDecimal(1000)));
            } else if (DJIActionTypeEnum.ROTATE_YAW.getActionType().equals(actionType)) {
                BigDecimal aircraftHeading = x.getActionActuatorFuncParam().getAircraftHeading();
                customAction.setValue(aircraftHeading);
                //旧版
            } else if (DJIActionTypeEnum.ACCURATE_SHOOT.getActionType().equals(actionType)) {
                handleAccurateShoot(customActionsList , x.getActionActuatorFuncParam());
                return;
                //新版
            } else if (DJIActionTypeEnum.ORIENTED_SHOOT.getActionType().equals(actionType)) {
                handleOrientedShoot(customActionsList , x.getActionActuatorFuncParam());
                return;
            }
            customActionsList.add(customAction);
        });
        return customActionsList;
    }

    /**
     * 拆分定向拍照
     * @param customActionsList
     * @param param
     */
    private void handleAccurateShoot(List<DjiAirLineDTO.CustomActions> customActionsList , DJIKmlDTO.ActionActuatorFuncParam param){
        //机身偏航动作数值等同于云台偏航角度
        if(param.getGimbalYawRotateAngle() != null){
            DjiAirLineDTO.CustomActions customActions = new DjiAirLineDTO.CustomActions();
            customActions.setValue(param.getGimbalYawRotateAngle());
            customActions.setActionType(DJIActionTypeEnum.ROTATE_YAW.getActionType());
            customActionsList.add(customActions);
        }

        //云台俯仰动作
        if(param.getGimbalPitchRotateAngle() != null){
            DjiAirLineDTO.CustomActions customActions = new DjiAirLineDTO.CustomActions();
            customActions.setValue(param.getGimbalPitchRotateAngle());
            customActions.setActionType(DJIActionTypeEnum.GIMBAL_ROTATE.getActionType());
            customActionsList.add(customActions);
        }

        //变焦动作
        if(param.getFocalLength() != null
                && param.getFocalLength().intValue() != 0){
            DjiAirLineDTO.CustomActions customActions = new DjiAirLineDTO.CustomActions();
            BigDecimal zoomLen = param.getFocalLength();
            customActions.setValue(zoomLen.multiply(new BigDecimal(10)));
            customActions.setActionType(DJIActionTypeEnum.ZOOM.getActionType());
            customActionsList.add(customActions);
        }

        //拍照动作
        DjiAirLineDTO.CustomActions customActions = new DjiAirLineDTO.CustomActions();
        if (Objects.nonNull(param)) {
            String fileSuffix = param.getAccurateFileSuffix();
            if(StringUtils.hasLength(fileSuffix)) {
                customActions.setByname(fileSuffix);
            }
        }
        customActions.setValue(BigDecimal.ZERO);
        customActions.setActionType(DJIActionTypeEnum.TAKE_PHOTO.getActionType());
        customActionsList.add(customActions);
    }

    /**
     * 拆分定向拍照 - 新版
     * @param customActionsList
     * @param param
     */
    private void handleOrientedShoot(List<DjiAirLineDTO.CustomActions> customActionsList , DJIKmlDTO.ActionActuatorFuncParam param){
        //机身偏航动作数值等同于云台偏航角度
        if(param.getGimbalYawRotateAngle() != null){
            DjiAirLineDTO.CustomActions customActions = new DjiAirLineDTO.CustomActions();
            customActions.setValue(param.getGimbalYawRotateAngle());
            customActions.setActionType(DJIActionTypeEnum.ROTATE_YAW.getActionType());
            customActionsList.add(customActions);
        }

        //云台俯仰动作
        if(param.getGimbalPitchRotateAngle() != null){
            DjiAirLineDTO.CustomActions customActions = new DjiAirLineDTO.CustomActions();
            customActions.setValue(param.getGimbalPitchRotateAngle());
            customActions.setActionType(DJIActionTypeEnum.GIMBAL_ROTATE.getActionType());
            customActionsList.add(customActions);
        }

        //变焦动作
        if(param.getFocalLength() != null
                && param.getFocalLength().intValue() != 0){
            DjiAirLineDTO.CustomActions customActions = new DjiAirLineDTO.CustomActions();
            BigDecimal zoomLen = param.getFocalLength();
            customActions.setValue(zoomLen.multiply(new BigDecimal(10)));
            customActions.setActionType(DJIActionTypeEnum.ZOOM.getActionType());
            customActionsList.add(customActions);
        }

        //拍照动作
        DjiAirLineDTO.CustomActions customActions = new DjiAirLineDTO.CustomActions();
        if (Objects.nonNull(param)) {
            String fileSuffix = param.getOrientedFileSuffix();
            if(StringUtils.hasLength(fileSuffix)) {
                customActions.setByname(fileSuffix);
            }
        }
        customActions.setValue(BigDecimal.ZERO);
        customActions.setActionType(DJIActionTypeEnum.TAKE_PHOTO.getActionType());
        customActionsList.add(customActions);
    }

    @Override
    public String transformJsonToKmzMainImpl(String airLineJson , Object... param) {
        return super.transformJsonToKmzMain(airLineJson , param);
    }
}
