package com.imapcloud.sdk.manager.data.entity;

import java.util.List;

/**
 * @author wmin
 * 飞行架次信息
 */
public class FlightMissionPageEntity {
    private Long currentPage;
    private Integer pageSize;
    private Integer totalCount;
    private Integer totalPage;
    private List<MissionMsg> list;

    public Long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<MissionMsg> getList() {
        return list;
    }

    public void setList(List<MissionMsg> list) {
        this.list = list;
    }

    public static class MissionMsg{
        private String execMissionId;
        private String missionId;
        private String name;
        private Long startTime;
        private Long endTime;
        private Double distance;
        private String date;

        public String getExecMissionId() {
            return execMissionId;
        }

        public void setExecMissionId(String execMissionId) {
            this.execMissionId = execMissionId;
        }

        public String getMissionId() {
            return missionId;
        }

        public void setMissionId(String missionId) {
            this.missionId = missionId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getStartTime() {
            return startTime;
        }

        public void setStartTime(Long startTime) {
            this.startTime = startTime;
        }

        public Long getEndTime() {
            return endTime;
        }

        public void setEndTime(Long endTime) {
            this.endTime = endTime;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
