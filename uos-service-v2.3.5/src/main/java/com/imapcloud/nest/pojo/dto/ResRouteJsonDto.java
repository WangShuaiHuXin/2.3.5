package com.imapcloud.nest.pojo.dto;
import lombok.Data;
import java.util.List;

@Data
public class ResRouteJsonDto {
    public String code;
    public String message;
    public Integer missnum;
    public ResData data;

    public static class ResData {
        public String requestId;
        public String airRouteType;
        public Integer missionType;
        /**
         * -1 错误航线 0 正常航线 1 低危航线
         */
        public Integer routeLineType;
        public String company;
        public String aircraftName;
        public List<List<FlyPoint>> missions;

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public String getAirRouteType() {
            return airRouteType;
        }

        public void setAirRouteType(String airRouteType) {
            this.airRouteType = airRouteType;
        }

        public Integer getMissionType() {
            return missionType;
        }

        public void setMissionType(Integer missionType) {
            this.missionType = missionType;
        }

        public Integer getRouteLineType() {
            return routeLineType;
        }

        public void setRouteLineType(Integer routeLineType) {
            this.routeLineType = routeLineType;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getAircraftName() {
            return aircraftName;
        }

        public void setAircraftName(String aircraftName) {
            this.aircraftName = aircraftName;
        }

        public List<List<FlyPoint>> getMissions() {
            return missions;
        }

        public void setMissions(List<List<FlyPoint>> missions) {
            this.missions = missions;
        }
    }
}
