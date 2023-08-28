package com.imapcloud.nest.v2.service.dto.in;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

//  <Document>
//
//  <!-- Step 1: Implement File Creation Information -->
//  <author>Name</author>
//  <createTime>1637600807044</createTime>
//  <updateTime>1637600875837</updateTime>
//
//  <!-- Step 2: Setup Mission Configuration -->
//  <missionConfig>
//    <flyToWaylineMode>safely</flyToWaylineMode>
//    <finishAction>goHome</finishAction>
//    <exitOnRCLost>goContinue</exitOnRCLost>
//    <executeRCLostAction>hover</executeRCLostAction>
//    <takeOffSecurityHeight>20</takeOffSecurityHeight>
//    <takeOffRefPoint>23.98057,115.987663,100</takeOffRefPoint>
//    <takeOffRefPointAGLHeight>35</takeOffRefPointAGLHeight>
//    <globalTransitionalSpeed>8</globalTransitionalSpeed>
//    <droneInfo>
//      <!-- Declare drone model with M30 -->
//      <droneEnumValue>67</droneEnumValue>
//      <droneSubEnumValue>0</droneSubEnumValue>
//    </droneInfo>
//    <payloadInfo>
//      <!-- Declare payload model with M30 -->
//      <payloadEnumValue>52</payloadEnumValue>
//      <payloadSubEnumValue>0</payloadSubEnumValue>
//      <payloadPositionIndex>0</payloadPositionIndex>
//    </payloadInfo>
//  </missionConfig>
//</Document>
//<Document xmlns:wstxns1="wpml" xmlns="" wstxns1:author="Name">
//<wstxns1:author xmlns:wstxns1="wpml">Name</wstxns1:author>
@Data
@JacksonXmlRootElement(namespace = "http://www.opengis.net/kml/2.2", localName = "kml")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DJIKmlDTO{

    @JacksonXmlProperty(namespace = "wpml", localName = "Document")
    private Document document;

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class Folder {
        @JacksonXmlProperty(namespace = "wpml", localName = "templateType")
        private String templateType;
        @JacksonXmlProperty(namespace = "wpml", localName = "useGlobalTransitionalSpeed")
        private BigDecimal useGlobalTransitionalSpeed;
        @JacksonXmlProperty(namespace = "wpml", localName = "templateId")
        private Integer templateId;
        @JacksonXmlProperty(namespace = "wpml", localName = "waylineCoordinateSysParam")
        private WaylineCoordinateSysParam waylineCoordinateSysParam;
        @JacksonXmlProperty(namespace = "wpml", localName = "autoFlightSpeed")
        private BigDecimal autoFlightSpeed;
        @JacksonXmlProperty(namespace = "wpml", localName = "globalHeight")
        private BigDecimal globalHeight;
        @JacksonXmlProperty(namespace = "wpml", localName = "caliFlightEnable")
        private Integer caliFlightEnable;
        @JacksonXmlProperty(namespace = "wpml", localName = "distance")
        private BigDecimal distance;
        @JacksonXmlProperty(namespace = "wpml", localName = "duration")
        private BigDecimal duration;
        @JacksonXmlProperty(namespace = "wpml", localName = "transitionalSpeed")
        private BigDecimal transitionalSpeed;
        @JacksonXmlProperty(namespace = "wpml", localName = "gimbalPitchMode")
        private String gimbalPitchMode;
        @JacksonXmlProperty(namespace = "wpml", localName = "globalWaypointHeadingParam")
        private GlobalWaypointHeadingParam globalWaypointHeadingParam;
        @JacksonXmlProperty(namespace = "wpml", localName = "globalWaypointTurnMode")
        private String globalWaypointTurnMode;
        @JacksonXmlProperty(namespace = "wpml", localName = "globalUseStraightLine")
        private Integer globalUseStraightLine;
        @JacksonXmlElementWrapper( useWrapping = false)
        @JacksonXmlProperty(localName = "Placemark")
        private List<Placemark> placemark;
        @JacksonXmlProperty(namespace = "wpml",localName = "payloadParam")
        private PayloadParam payloadParam;


    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayloadParam{
        @JacksonXmlProperty(namespace = "wpml",localName = "payloadPositionIndex")
        private Integer payloadPositionIndex;
        @JacksonXmlProperty(namespace = "wpml",localName = "focusMode")
        private String focusMode;
        @JacksonXmlProperty(namespace = "wpml",localName = "returnMode")
        private String returnMode;
        @JacksonXmlProperty(namespace = "wpml",localName = "samplingRate")
        private Integer samplingRate;
        @JacksonXmlProperty(namespace = "wpml",localName = "scanningMode")
        private String scanningMode;
        @JacksonXmlProperty(namespace = "wpml",localName = "imageFormat")
        private String imageFormat;
        @JacksonXmlProperty(namespace = "wpml",localName = "meteringMode")
        private String meteringMode;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class WaylineCoordinateSysParam {
        @JacksonXmlProperty(namespace = "wpml", localName = "coordinateMode")
        private String coordinateMode;
        @JacksonXmlProperty(namespace = "wpml", localName = "heightMode")
        private String heightMode;
        @JacksonXmlProperty(namespace = "wpml", localName = "globalShootHeight")
        private BigDecimal globalShootHeight;
        @JacksonXmlProperty(namespace = "wpml", localName = "positioningType")
        private String positioningType;
        @JacksonXmlProperty(namespace = "wpml", localName = "surfaceFollowModeEnable")
        private Integer surfaceFollowModeEnable;
        @JacksonXmlProperty(namespace = "wpml", localName = "surfaceRelativeHeight")
        private BigDecimal surfaceRelativeHeight;

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class GlobalWaypointHeadingParam {
        @JacksonXmlProperty(namespace = "wpml", localName = "waypointHeadingMode")
        private String waypointHeadingMode;
        @JacksonXmlProperty(namespace = "wpml", localName = "waypointHeadingAngle")
        private Integer waypointHeadingAngle;
        @JacksonXmlProperty(namespace = "wpml", localName = "waypointPoiPoint")
        private String waypointPoiPoint;//>24.323345,116.324532,31.000000
        @JacksonXmlProperty(namespace = "wpml", localName = "waypointHeadingPathMode")
        private String waypointHeadingPathMode;//>clockwise</wpml:waypointHeadingPathMode>
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class WaypointHeadingParam {
        @JacksonXmlProperty(namespace = "wpml", localName = "waypointHeadingMode")
        private String waypointHeadingMode;
        @JacksonXmlProperty(namespace = "wpml", localName = "waypointHeadingAngle")
        private BigDecimal waypointHeadingAngle;
        @JacksonXmlProperty(namespace = "wpml", localName = "waypointPoiPoint")
        private String waypointPoiPoint;//>24.323345,116.324532,31.000000
        @JacksonXmlProperty(namespace = "wpml", localName = "waypointHeadingPathMode")
        private String waypointHeadingPathMode;//>clockwise</wpml:waypointHeadingPathMode>
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class Placemark {
        @JacksonXmlProperty( localName = "Point")
        private Point point;
        @JacksonXmlProperty(namespace = "wpml", localName = "index")
        private Integer index;
        @JacksonXmlProperty(namespace = "wpml", localName = "ellipsoidHeight")
        private BigDecimal ellipsoidHeight;
        @JacksonXmlProperty(namespace = "wpml", localName = "height")
        private BigDecimal height;
        @JacksonXmlProperty(namespace = "wpml", localName = "waypointHeadingParam")
        private WaypointHeadingParam waypointHeadingParam;
        @JacksonXmlProperty(namespace = "wpml", localName = "waypointTurnParam")
        private WaypointTurnParam waypointTurnParam;
        @JacksonXmlProperty(namespace = "wpml", localName = "useStraightLine")
        private Integer useStraightLine;
        @JacksonXmlProperty(namespace = "wpml", localName = "useGlobalHeight")
        private Integer useGlobalHeight;
        @JacksonXmlProperty(namespace = "wpml", localName = "useGlobalSpeed")
        private Integer useGlobalSpeed;
        @JacksonXmlProperty(namespace = "wpml", localName = "waypointSpeed")
        private BigDecimal waypointSpeed;
        @JacksonXmlProperty(namespace = "wpml", localName = "useGlobalHeadingParam")
        private Integer useGlobalHeadingParam;
        @JacksonXmlProperty(namespace = "wpml", localName = "useGlobalTurnParam")
        private Integer useGlobalTurnParam;
        @JacksonXmlProperty(namespace = "wpml", localName = "gimbalPitchAngle")
        private Integer gimbalPitchAngle;
        @JacksonXmlProperty(namespace = "wpml", localName = "actionGroup")
        private ActionGroup actionGroup;


    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class WaypointTurnParam{
        @JacksonXmlProperty(namespace = "wpml", localName = "waypointTurnMode")
        private String waypointTurnMode;
        @JacksonXmlProperty(namespace = "wpml", localName = "waypointTurnDampingDist")
        private BigDecimal waypointTurnDampingDist;
    }


    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Point {
        @JacksonXmlProperty( localName = "coordinates")
        private String coordinates;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ActionGroup {
        @JacksonXmlProperty(namespace = "wpml", localName = "actionGroupId")
        private Integer actionGroupId;
        @JacksonXmlProperty(namespace = "wpml", localName = "actionGroupStartIndex")
        private Integer actionGroupStartIndex;
        @JacksonXmlProperty(namespace = "wpml", localName = "actionGroupEndIndex")
        private Integer actionGroupEndIndex;
        @JacksonXmlProperty(namespace = "wpml", localName = "actionGroupMode")
        private String actionGroupMode;
        @JacksonXmlProperty(namespace = "wpml", localName = "actionTrigger")
        private ActionTrigger actionTrigger;
        @JacksonXmlElementWrapper( useWrapping = false)
        @JacksonXmlProperty(namespace = "wpml",localName = "action")
        private List<Action> action;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ActionTrigger {
        @JacksonXmlProperty(namespace = "wpml", localName = "actionTriggerType")
        private String actionTriggerType;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class Action {
        @JacksonXmlProperty(namespace = "wpml", localName = "actionId")
        private Integer actionId;
        @JacksonXmlProperty(namespace = "wpml", localName = "actionActuatorFunc")
        private String actionActuatorFunc;
        @JacksonXmlProperty(namespace = "wpml", localName = "actionActuatorFuncParam")
        private ActionActuatorFuncParam actionActuatorFuncParam;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ActionActuatorFuncParam {
        //gimbal
        @JacksonXmlProperty(namespace = "wpml", localName = "gimbalRotateMode")
        private String gimbalRotateMode;
        @JacksonXmlProperty(namespace = "wpml", localName = "gimbalPitchRotateEnable")
        private Integer gimbalPitchRotateEnable;
        @JacksonXmlProperty(namespace = "wpml", localName = "gimbalPitchRotateAngle")
        private BigDecimal gimbalPitchRotateAngle;
        @JacksonXmlProperty(namespace = "wpml", localName = "gimbalRollRotateEnable")
        private Integer gimbalRollRotateEnable;
        @JacksonXmlProperty(namespace = "wpml", localName = "gimbalRollRotateAngle")
        private BigDecimal gimbalRollRotateAngle;
        @JacksonXmlProperty(namespace = "wpml", localName = "gimbalYawRotateEnable")
        private Integer gimbalYawRotateEnable;
        @JacksonXmlProperty(namespace = "wpml", localName = "gimbalYawRotateAngle")
        private BigDecimal gimbalYawRotateAngle;
        @JacksonXmlProperty(namespace = "wpml", localName = "gimbalRotateTimeEnable")
        private Integer gimbalRotateTimeEnable;
        @JacksonXmlProperty(namespace = "wpml", localName = "gimbalRotateTime")
        private BigDecimal gimbalRotateTime;
        @JacksonXmlProperty(namespace = "wpml", localName = "gimbalHeadingYawBase")
        private String gimbalHeadingYawBase;
        @JacksonXmlProperty(namespace = "wpml", localName = "payloadPositionIndex")
        private Integer payloadPositionIndex;
        @JacksonXmlProperty(namespace = "wpml", localName = "useGlobalPayloadLensIndex")
        private Integer useGlobalPayloadLensIndex;

        //take photo \ start record
        @JacksonXmlProperty(namespace = "wpml", localName = "fileSuffix")
        private String fileSuffix;
        @JacksonXmlProperty(namespace = "wpml", localName = "payloadLensIndex")
        private String payloadLensIndex;

        //focus 区域对焦、点对焦
        @JacksonXmlProperty(namespace = "wpml", localName = "isPointFocus")
        private Integer isPointFocus;

        //对焦点位置
        @JacksonXmlProperty(namespace = "wpml", localName = "focusX")
        private BigDecimal focusX;
        @JacksonXmlProperty(namespace = "wpml", localName = "focusY")
        private BigDecimal focusY;
        @JacksonXmlProperty(namespace = "wpml", localName = "focusRegionWidth")
        private BigDecimal focusRegionWidth;
        @JacksonXmlProperty(namespace = "wpml", localName = "focusRegionHeight")
        private BigDecimal focusRegionHeight;
        @JacksonXmlProperty(namespace = "wpml", localName = "isInfiniteFocus")
        private BigDecimal isInfiniteFocus;

        //变焦
        @JacksonXmlProperty(namespace = "wpml", localName = "focalLength")
        private BigDecimal focalLength;

        //创建文件夹
        @JacksonXmlProperty(namespace = "wpml", localName = "directoryName")
        private String directoryName;

        //偏航角
        @JacksonXmlProperty(namespace = "wpml", localName = "aircraftHeading")
        private BigDecimal aircraftHeading;
        //转动模式
        @JacksonXmlProperty(namespace = "wpml", localName = "aircraftPathMode")
        private String aircraftPathMode;

        //accurateShoot
        @JacksonXmlProperty(namespace = "wpml", localName = "accurateFrameValid")
        private Integer accurateFrameValid;

        @JacksonXmlProperty(namespace = "wpml", localName = "targetAngle")
        private BigDecimal targetAngle;

        @JacksonXmlProperty(namespace = "wpml", localName = "imageWidth")
        private Integer imageWidth;

        @JacksonXmlProperty(namespace = "wpml", localName = "imageHeight")
        private Integer imageHeight;

        @JacksonXmlProperty(namespace = "wpml", localName = "AFPos")
        private Integer AFPos;

        @JacksonXmlProperty(namespace = "wpml", localName = "gimbalPort")
        private Integer gimbalPort;

        @JacksonXmlProperty(namespace = "wpml", localName = "accurateCameraType")
        private Integer accurateCameraType;

        @JacksonXmlProperty(namespace = "wpml", localName = "accurateFilePath")
        private String accurateFilePath;

        @JacksonXmlProperty(namespace = "wpml", localName = "accurateFileMD5")
        private String accurateFileMD5;

        @JacksonXmlProperty(namespace = "wpml", localName = "accurateFileSize")
        private Integer accurateFileSize;

        @JacksonXmlProperty(namespace = "wpml", localName = "accurateFileSuffix")
        private String accurateFileSuffix;

        @JacksonXmlProperty(namespace = "wpml", localName = "accurateCameraApertue")
        private Integer accurateCameraApertue;

        @JacksonXmlProperty(namespace = "wpml", localName = "accurateCameraLuminance")
        private Integer accurateCameraLuminance;

        @JacksonXmlProperty(namespace = "wpml", localName = "accurateCameraShutterTime")
        private BigDecimal accurateCameraShutterTime;

        @JacksonXmlProperty(namespace = "wpml", localName = "accurateCameraISO")
        private Integer accurateCameraISO;


        //orientedShoot
        @JacksonXmlProperty(namespace = "wpml", localName = "actionUUID")
        private String actionUUID;

        @JacksonXmlProperty(namespace = "wpml", localName = "orientedCameraType")
        private Integer orientedCameraType;

        @JacksonXmlProperty(namespace = "wpml", localName = "orientedFilePath")
        private String orientedFilePath;

        @JacksonXmlProperty(namespace = "wpml", localName = "orientedFileMD5")
        private String orientedFileMD5;

        @JacksonXmlProperty(namespace = "wpml", localName = "orientedFileSize")
        private Integer orientedFileSize;

        @JacksonXmlProperty(namespace = "wpml", localName = "orientedFileSuffix")
        private String orientedFileSuffix;

        @JacksonXmlProperty(namespace = "wpml", localName = "orientedCameraApertue")
        private Integer orientedCameraApertue;

        @JacksonXmlProperty(namespace = "wpml", localName = "orientedCameraLuminance")
        private Integer orientedCameraLuminance;

        @JacksonXmlProperty(namespace = "wpml", localName = "orientedCameraShutterTime")
        private BigDecimal orientedCameraShutterTime;

        @JacksonXmlProperty(namespace = "wpml", localName = "orientedCameraISO")
        private Integer orientedCameraISO;

        @JacksonXmlProperty(namespace = "wpml", localName = "orientedPhotoMode")
        private String orientedPhotoMode;

        //悬停
        @JacksonXmlProperty(namespace = "wpml", localName = "hoverTime")
        private BigDecimal hoverTime;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class Document {
        @JacksonXmlProperty(namespace = "wpml", localName = "author")
        private String author;
        @JacksonXmlProperty(namespace = "wpml", localName = "createTime")
        private String createTime;
        @JacksonXmlProperty(namespace = "wpml", localName = "updateTime")
        private String updateTime;
        @JacksonXmlProperty(namespace = "wpml", localName = "missionConfig")
        private MissionConfig missionConfig;
        @JacksonXmlProperty(localName = "Folder")
        private Folder folder;

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class MissionConfig {
        @JacksonXmlProperty(namespace = "wpml", localName = "flyToWaylineMode")
        private String flyToWaylineMode;
        @JacksonXmlProperty(namespace = "wpml", localName = "finishAction")
        private String finishAction;
        @JacksonXmlProperty(namespace = "wpml", localName = "exitOnRCLost")
        private String exitOnRCLost;
        @JacksonXmlProperty(namespace = "wpml", localName = "executeRCLostAction")
        private String executeRCLostAction;
        @JacksonXmlProperty(namespace = "wpml", localName = "takeOffSecurityHeight")
        private BigDecimal takeOffSecurityHeight;
        @JacksonXmlProperty(namespace = "wpml", localName = "takeOffRefPoint")
        private String takeOffRefPoint;
        @JacksonXmlProperty(namespace = "wpml", localName = "takeOffRefPointAGLHeight")
        private BigDecimal takeOffRefPointAGLHeight;
        @JacksonXmlProperty(namespace = "wpml", localName = "globalTransitionalSpeed")
        private BigDecimal globalTransitionalSpeed;
        @JacksonXmlProperty(namespace = "wpml", localName = "globalRTHHeight")
        private BigDecimal globalRTHHeight;
        @JacksonXmlProperty(namespace = "wpml", localName = "droneInfo")
        private DroneInfo droneInfo;
        @JacksonXmlProperty(namespace = "wpml", localName = "payloadInfo")
        private PayloadInfo payloadInfo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class DroneInfo {
        @JacksonXmlProperty(namespace = "wpml", localName = "droneEnumValue")
        private Integer droneEnumValue;
        @JacksonXmlProperty(namespace = "wpml", localName = "droneSubEnumValue")
        private Integer droneSubEnumValue;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class PayloadInfo {
        @JacksonXmlProperty(namespace = "wpml", localName = "payloadEnumValue")
        private Integer payloadEnumValue;
        @JacksonXmlProperty(namespace = "wpml", localName = "payloadSubEnumValue")
        private Integer payloadSubEnumValue;
        @JacksonXmlProperty(namespace = "wpml", localName = "payloadPositionIndex")
        private Integer payloadPositionIndex;
        @JacksonXmlProperty(namespace = "wpml", localName = "customPayloadActionInfo")
        private CustomPayloadActionInfo customPayloadActionInfo;

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class CustomPayloadActionInfo{
        @JacksonXmlProperty(namespace = "wpml", localName = "customActionType")
        private Integer customActionType;
        @JacksonXmlProperty(namespace = "wpml", localName = "customActionName")
        private String customActionName;
        @JacksonXmlProperty(namespace = "wpml", localName = "customActionMinParamValue")
        private Integer customActionMinParamValue;
        @JacksonXmlProperty(namespace = "wpml", localName = "customActionMaxParamValue")
        private Integer customActionMaxParamValue;
        @JacksonXmlProperty(namespace = "wpml", localName = "customActionIndex")
        private Integer customActionIndex;
    }

}
