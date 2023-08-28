package com.imapcloud.nest.utils.airline;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author wmin
 */
@Data
@Accessors(chain = true)
public class PointCloudWaypoint {
    private Double aircraftLocationAltitude;

    private Double aircraftLocationLatitude;

    private Double aircraftLocationLongitude;
    /**
     * 机头朝向
     */
    private Double aircraftYaw;
    /**
     * 云台俯仰
     */
    private Double gimbalPitch;
    /**
     * 航点类型
     * 0 -> 拍照
     * 1 -> 辅助
     * 2 -> 无任何动作点
     */
    private Integer waypointType;

    private String checkpointuuid;

    private Integer speed;

    private List<PhotoPosition> photoPositionList;

    /**
     * 照片AI识别类型
     */
    private List<Integer> photoPropList;

    /**
     * 航点别名
     */
    private String byname;


    private Double focalLength;

    /**
     * 跟随航线速度
     */
    private Boolean followSpeed;

    /**
     * 跟随航线动作
     */
    private Boolean followAction;


    @Data
    public static class PhotoPosition {
        public Double altitude;
        public String displayName;
        public String feederName;
        public Double focusdist;
        //纬度
        public Double latitude;
        //经度
        public Double longitude;
        public String name;
        public String saveName;
        public String site;
    }
}
