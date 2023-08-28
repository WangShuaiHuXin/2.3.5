package com.imapcloud.nest.v2.service.impl;

import com.alibaba.fastjson.JSON;
import com.geoai.common.core.exception.BizException;
import com.imapcloud.nest.enums.TaskModeEnum;
import com.imapcloud.nest.v2.common.enums.DJIOldActionTypeEnum;
import com.imapcloud.nest.v2.common.enums.DJIOldHeadingTypeEnum;
import com.imapcloud.nest.v2.service.AbstractAirLineService;
import com.imapcloud.nest.v2.service.dto.in.DjiAirLineDTO;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wmin
 */
@Slf4j
@Component
public class DjiOldAirLineServiceImpl extends AbstractAirLineService {
    @Override
    public String transformKmzToJsonMainImpl(MultipartFile multipartFile) {
        //1.kml -> objJson
        Document document = inputStream2Doc(multipartFile);
        if (Objects.isNull(document)) {
            return null;
        }

        Element rootElementEle = document.getRootElement();
        Element documentEle = rootElementEle.element("Document");
        Element placemarkEle = documentEle.element("Placemark");
        Element folderEle = documentEle.element("Folder");

        //2.设置航线参数
        DjiAirLineDTO djiAirLineDTO = new DjiAirLineDTO();
        DjiAirLineDTO.DJIKml djiKml = parseAirLineParam(placemarkEle);
        DjiAirLineDTO.LineConfigs lineConfigs = new DjiAirLineDTO.LineConfigs();
        lineConfigs.setDjiKml(djiKml);
        djiAirLineDTO.setLineConfigs(lineConfigs);

        //3.设置航点参数
        DjiAirLineDTO.MapConfigs mapConfigs = new DjiAirLineDTO.MapConfigs();
        Double takeOffHeight = addTakeOffHeight(placemarkEle);
        mapConfigs.setPoints(parsePoint(folderEle, takeOffHeight));
        djiAirLineDTO.setMapConfigs(mapConfigs);
        //4.设置mode
        djiAirLineDTO.setMode(TaskModeEnum.DJI_KML.name());

        return JSON.toJSONString(djiAirLineDTO);
    }

    @Override
    public String transformJsonToKmzMainImpl(String airLineJson, Object... param) {
        return null;
    }

    private Document inputStream2Doc(MultipartFile multipartFile) {
        try {
            SAXReader reader = new SAXReader();
            return reader.read(multipartFile.getInputStream());
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private DjiAirLineDTO.DJIKml parseAirLineParam(Element placemark) {
        DjiAirLineDTO.DJIKml djiKml = new DjiAirLineDTO.DJIKml();
        djiKml.setAbsolute(absolute(placemark));

        Element extendedData = placemark.element("ExtendedData");
        Element autoFlightSpeedEle = extendedData.element("autoFlightSpeed");
        if (Objects.nonNull(autoFlightSpeedEle)) {
            double autoFlightSpeed = Double.parseDouble(autoFlightSpeedEle.getStringValue());
            djiKml.setAutoFlightSpeed((int) Math.round(autoFlightSpeed));
            djiKml.setSpeed((int) Math.round(autoFlightSpeed));
        }

        Element headingModeEle = extendedData.element("headingMode");
        if (Objects.nonNull(headingModeEle)) {
            djiKml.setHeadingMode(DJIOldHeadingTypeEnum.getEnumHeadingName(headingModeEle.getStringValue()));
        }

        Element speedEle = extendedData.element("speed");
        if (Objects.nonNull(speedEle)) {
            double speed = Double.parseDouble(speedEle.getStringValue());
            djiKml.setSpeed((int) Math.round(speed));
        }

        Element altitudeEle = extendedData.element("altitude");
        if (Objects.nonNull(altitudeEle)) {
            double altitude = Double.parseDouble(altitudeEle.getStringValue());
            djiKml.setTakeOffLandAlt((int) Math.round(altitude) + 10);
            djiKml.setGlobalHeight((int) Math.round(altitude));
        }

        return djiKml;
    }

    private List<DjiAirLineDTO.Points> parsePoint(Element folder, Double takeOffHeight) {
        List<Element> placemarkList = folder.elements("Placemark");
        return placemarkList.stream().map(placeMarkEle -> {
            Element point = placeMarkEle.element("Point");

            DjiAirLineDTO.Points points = new DjiAirLineDTO.Points();
            //设置heading值
            Element extendedData = placeMarkEle.element("ExtendedData");
            points.setHeading(BigDecimal.valueOf(heading(extendedData)));
            //设置速度
            points.setSpeed(speed(extendedData));
            //设置位置
            points.setLocation(point2location(point, takeOffHeight));
            //设置自定义动作
            points.setCustomActions(extendedData2actions(extendedData));
            return points;
        }).collect(Collectors.toList());
    }

    /**
     * 1、如果有droneInfoEle，则使用里面的droneHeight.useAbsolute值来进行判断
     * 2、如果没有droneInfoEle，则使用LineString.altitudeMode值来进行判断
     *
     * @param placemark
     * @return
     */
    private boolean absolute(Element placemark) {
        Element extendedDataEle = placemark.element("ExtendedData");
        Element droneInfoEle = extendedDataEle.element("droneInfo");
        if (Objects.nonNull(droneInfoEle)) {
            Element droneHeightEle = droneInfoEle.element("droneHeight");
            if (Objects.nonNull(droneHeightEle)) {
                Element useAbsoluteEle = droneHeightEle.element("useAbsolute");
                if (Objects.nonNull(useAbsoluteEle)) {
                    String absolute = useAbsoluteEle.getStringValue();
                    if ("true".equals(absolute)) {
                        return true;
                    }
                    if ("false".equals(absolute)) {
                        return false;
                    }
                }
            }
        }

        Element lineStringEle = placemark.element("LineString");
        Element altitudeModeEle = lineStringEle.element("altitudeMode");
        String altitudeMode = altitudeModeEle.getStringValue();
        if (Objects.nonNull(altitudeMode)) {
            if ("relativeToGround".equals(altitudeMode) || altitudeMode.contains("relative")) {
                return false;
            }
            if ("absolute".equals(altitudeMode)) {
                return true;
            }
        }
        throw new BizException("无法判断航线是绝对还是相对值");
    }

    private DjiAirLineDTO.Location point2location(Element pointEle, Double takeOffHeight) {
        Element coordinates = pointEle.element("coordinates");
        String stringValue = coordinates.getStringValue();
        if (Objects.isNull(stringValue)) {
            return null;
        }
        DjiAirLineDTO.Location location = new DjiAirLineDTO.Location();
        String[] split = stringValue.split(",");
        location.setLng(BigDecimal.valueOf(Double.parseDouble(split[0])));
        location.setLat(BigDecimal.valueOf(Double.parseDouble(split[1])));
        location.setAlt(BigDecimal.valueOf(Double.parseDouble(split[2]) + takeOffHeight));
        return location;
    }

    private List<DjiAirLineDTO.CustomActions> extendedData2actions(Element extendedDataEle) {
        List<Element> elements = extendedDataEle.elements();
        List<DjiAirLineDTO.CustomActions> actionList = elements.stream().filter(e -> !Objects.equals("actions", e.getName())).map(e -> {
            String name = e.getName();
            String actionTypeName = DJIOldActionTypeEnum.getByOldDjiActionType(name);
            if (Objects.isNull(actionTypeName)) {
                return null;
            }
            DjiAirLineDTO.CustomActions customActions = new DjiAirLineDTO.CustomActions();
            customActions.setActionType(actionTypeName);
            if ("gimbalPitch".equals(name)) {
                Element gimbalPitchEle = extendedDataEle.element("gimbalPitch");
                String stringValue = gimbalPitchEle.getStringValue();
                double pitch = Double.parseDouble(stringValue);
                pitch = pitch >= -90 && pitch <= 30 ? pitch : pitch / 10;
                customActions.setValue(BigDecimal.valueOf(pitch));
            }
            if ("heading".equals(name)) {
                Element headingEle = extendedDataEle.element("heading");
                customActions.setValue(BigDecimal.valueOf(Double.parseDouble(headingEle.getStringValue())));
            }
            return customActions;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        //航点动作
        List<Element> actions = extendedDataEle.elements("actions");
        if (!CollectionUtils.isEmpty(actions)) {
            List<DjiAirLineDTO.CustomActions> actionList2 = actions.stream().map(this::kmlAction2customAction).collect(Collectors.toList());
            actionList.addAll(actionList2);
        }
        return actionList;
    }

    private DjiAirLineDTO.CustomActions kmlAction2customAction(Element actionEle) {
        String kmlActionName = actionEle.getStringValue();
        String param = actionEle.attributeValue("param");
        DjiAirLineDTO.CustomActions customActions = new DjiAirLineDTO.CustomActions();
        String actionTypeName = DJIOldActionTypeEnum.getByOldDjiActionType(kmlActionName);
        customActions.setActionType(actionTypeName);
        switch (kmlActionName) {
            case "GimbalPitch":
                double pitch = Double.parseDouble(param);
                pitch = pitch >= -90 && pitch <= 30 ? pitch : pitch / 10;
                customActions.setValue(BigDecimal.valueOf(pitch));
                break;
            case "Hovering":
                customActions.setValue(BigDecimal.valueOf(Integer.parseInt(param)));
                break;
            case "AircraftYaw":
                customActions.setValue(BigDecimal.valueOf(Integer.parseInt(param)));
                break;
            default:
                break;
        }
        return customActions;
    }

    private Integer speed(Element extendedDataEle) {
        //航点速度
        Element speedEle = extendedDataEle.element("speed");
        return Objects.isNull(speedEle) ? 0 : (int) Math.round(Double.parseDouble(speedEle.getStringValue()));
    }

    private Double heading(Element extendedDataEle) {
        //航点速度
        Element headingEle = extendedDataEle.element("heading");
        return Objects.isNull(headingEle) ? 0 : Double.parseDouble(headingEle.getStringValue());
    }

    private Double addTakeOffHeight(Element placemark) {
        Element extendedDataEle = placemark.element("ExtendedData");
        Element droneInfoEle = extendedDataEle.element("droneInfo");
        if (Objects.nonNull(droneInfoEle)) {
            Element droneHeightEle = droneInfoEle.element("droneHeight");
            if (Objects.nonNull(droneHeightEle)) {
                Element hasTakeoffHeightEle = droneHeightEle.element("hasTakeoffHeight");
                Element useAbsoluteEle = droneHeightEle.element("useAbsolute");
                if (Objects.nonNull(hasTakeoffHeightEle) && Objects.nonNull(useAbsoluteEle)) {
                    String absolute = useAbsoluteEle.getStringValue();
                    String hasTakeoffHeight = hasTakeoffHeightEle.getStringValue();
                    if ("true".equals(hasTakeoffHeight) && "true".equals(absolute)) {
                        Element takeoffHeightEle = droneHeightEle.element("takeoffHeight");
                        String takeoffHeight = takeoffHeightEle.getStringValue();
                        try {
                            return Double.parseDouble(takeoffHeight);
                        } catch (NumberFormatException e) {
                            return 0.0;
                        }
                    }
                }
            }
        }
        return 0.0;
    }

}
