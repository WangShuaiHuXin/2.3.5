package com.imapcloud.nest.pojo.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务航线的航点对象
 *
 * @author daolin
 */
public class FlyPoint {

    //经纬度、海拔
    private Double aircraftLocationAltitude;

    private Double aircraftLocationLatitude;

    private Double aircraftLocationLongitude;

    //图片拍摄时间
    private String takeTime;
    /**
     * 航点速度
     */
    private Double wayPointSpeed;
    //机头朝向
    private Double aircraftYaw;
    //云台角度
    private Double gimbalPitch;
    /**
     * 是否拍照, 0 执行, 1不执行
     */
    private Integer waypointType;

    private String checkpointuuid;
    //
    private String routename;
    /**
     * 别名
     */
    private String byname;

    private List<PhotoPosition> photoPositionList;

    private List<Integer> photoPropList;

    public FlyPoint(){
    }

    public FlyPoint(Double altitude, Double latitude, Double longitude, Double speed){
        aircraftLocationAltitude=altitude;
        aircraftLocationLatitude=latitude;
        aircraftLocationLongitude=longitude;
        wayPointSpeed=speed;
    }

    public Double getAircraftLocationAltitude() {
        return aircraftLocationAltitude;
    }

    public void setAircraftLocationAltitude(Double aircraftLocationAltitude) {
        this.aircraftLocationAltitude = aircraftLocationAltitude;
    }

    public Double getAircraftLocationLatitude() {
        return aircraftLocationLatitude;
    }

    public void setAircraftLocationLatitude(Double aircraftLocationLatitude) {
        this.aircraftLocationLatitude = aircraftLocationLatitude;
    }

    public Double getAircraftLocationLongitude() {
        return aircraftLocationLongitude;
    }

    public void setAircraftLocationLongitude(Double aircraftLocationLongitude) {
        this.aircraftLocationLongitude = aircraftLocationLongitude;
    }

    public Double getAircraftYaw() {
        return aircraftYaw;
    }

    public void setAircraftYaw(Double aircraftYaw) {
        this.aircraftYaw = aircraftYaw;
    }

    public Double getGimbalPitch() {
        return gimbalPitch;
    }

    public void setGimbalPitch(Double gimbalPitch) {
        this.gimbalPitch = gimbalPitch;
    }

    public Integer getWaypointType() {
        return waypointType;
    }

    public void setWaypointType(Integer waypointType) {
        this.waypointType = waypointType;
    }

    public String getCheckpointuuid() {
        return checkpointuuid;
    }

    public void setCheckpointuuid(String checkpointuuid) {
        this.checkpointuuid = checkpointuuid;
    }

    public String getRoutename() {
        return routename;
    }

    public void setRoutename(String routename) {
        this.routename = routename;
    }

    public String getByname() {
        return byname;
    }

    public void setByname(String byname) {
        this.byname = byname;
    }

    public String getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public List<PhotoPosition> getPhotoPositionList() {
        return photoPositionList;
    }

    public void setPhotoPositionList(List<PhotoPosition> photoPositionList) {
        this.photoPositionList = photoPositionList;
    }

	public List<Integer> getPhotoPropList() {
		return photoPropList;
	}

	public void setPhotoPropList(List<Integer> photoPropList) {
		this.photoPropList = photoPropList;
	}

	@Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        final FlyPoint flyPoint = (FlyPoint) obj;
        if (this == flyPoint) {
            return true;
        } else {
            return (this.aircraftLocationAltitude.equals(flyPoint.aircraftLocationAltitude) && this.aircraftLocationLatitude.equals(flyPoint.aircraftLocationLatitude)) && this.aircraftLocationLongitude.equals(flyPoint.aircraftLocationLongitude);
        }
    }

    @Override
    public int hashCode() {
        int hashno = 7;
        hashno = (13 * hashno + (aircraftLocationLongitude == null ? 0 : aircraftLocationLongitude.hashCode()) + (aircraftLocationLatitude == null ? 0 : aircraftLocationLongitude.hashCode()) + (aircraftLocationAltitude == null ? 0 : aircraftLocationAltitude.hashCode()));
        return hashno;
    }

    public Double getWayPointSpeed() {
        return wayPointSpeed;
    }

    public void setWayPointSpeed(Double wayPointSpeed) {
        this.wayPointSpeed = wayPointSpeed;
    }


    public static class PhotoPosition {

        public String displayName;
        public String feederName;
        //距离拍照点距离
        public Double focusdist;
        //拍照焦距
        public Double focuslength;
        //纬度
        public Double latitude;
        //经度
        public Double longitude;
        public Double altitude;
        public String name;
        public String saveName;
        public String site;

    }


}
