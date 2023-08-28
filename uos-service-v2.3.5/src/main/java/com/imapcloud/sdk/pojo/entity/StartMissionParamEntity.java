package com.imapcloud.sdk.pojo.entity;

import com.imapcloud.nest.v2.common.properties.OssConfig;
import lombok.Getter;
import lombok.Setter;

public class StartMissionParamEntity {
    private String missionUuid;
    private Integer mode;
    /**
     * @deprecated 2.2.3, 使用{@link StartMissionParamEntity#ossConfig}替代
     */
    @Deprecated
    private String uploadUrl;
    /**
     * @deprecated 2.2.3, 使用{@link StartMissionParamEntity#ossConfig}替代
     */
    @Deprecated
    private String chunkInitUrl;
    /**
     * @deprecated 2.2.3, 使用{@link StartMissionParamEntity#ossConfig}替代
     */
    @Deprecated
    private String chunkCombineUrl;
    private String chunkSyncUrl;
    private Boolean uploadByChunks;
    @Getter
    @Setter
    private OssConfig ossConfig;
    private Boolean disableRtkInMission;

    public String getMissionUuid() {
        return missionUuid;
    }

    public void setMissionUuid(String missionUuid) {
        this.missionUuid = missionUuid;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getChunkInitUrl() {
        return chunkInitUrl;
    }

    public void setChunkInitUrl(String chunkInitUrl) {
        this.chunkInitUrl = chunkInitUrl;
    }

    public String getChunkCombineUrl() {
        return chunkCombineUrl;
    }

    public void setChunkCombineUrl(String chunkCombineUrl) {
        this.chunkCombineUrl = chunkCombineUrl;
    }

    public String getChunkSyncUrl() {
        return chunkSyncUrl;
    }

    public void setChunkSyncUrl(String chunkSyncUrl) {
        this.chunkSyncUrl = chunkSyncUrl;
    }

    public Boolean getUploadByChunks() {
        return uploadByChunks;
    }

    public void setUploadByChunks(Boolean uploadByChunks) {
        this.uploadByChunks = uploadByChunks;
    }

    public Boolean getDisableRtkInMission() {
        return disableRtkInMission;
    }

    public void setDisableRtkInMission(Boolean disableRtkInMission) {
        this.disableRtkInMission = disableRtkInMission;
    }
}
