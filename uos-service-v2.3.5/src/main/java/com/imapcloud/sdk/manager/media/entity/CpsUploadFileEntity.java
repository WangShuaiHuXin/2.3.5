package com.imapcloud.sdk.manager.media.entity;


import com.imapcloud.nest.v2.common.properties.OssConfig;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CpsUploadFileEntity {
    private String execId;
    private Boolean autoStartUp;
    private List<String> fileIdList;
    /**
     * 无人机标识
     */
    private Integer uavWhich;
    /**
     * @deprecated 2.2.3，使用{@link CpsUploadFileEntity#ossConfig}替代
     */
    @Deprecated
    private String url;
    /**
     * @deprecated 2.2.3，使用{@link CpsUploadFileEntity#ossConfig}替代
     */
    @Deprecated
    private String chunkInitUrl;
    /**
     * @deprecated 2.2.3，使用{@link CpsUploadFileEntity#ossConfig}替代
     */
    @Deprecated
    private String chunkCombineUrl;
    private Boolean uploadByChunks;
    private String chunkSyncUrl;
    /**
     * @since 2.2.3，cps新版上传分片接口
     */
    @Setter
    @Getter
    private OssConfig ossConfig;

    public Integer getUavWhich() {
        return uavWhich;
    }

    public void setUavWhich(Integer uavWhich) {
        this.uavWhich = uavWhich;
    }

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getAutoStartUp() {
        return autoStartUp;
    }

    public void setAutoStartUp(Boolean autoStartUp) {
        this.autoStartUp = autoStartUp;
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

    public List<String> getFileIdList() {
        return fileIdList;
    }

    public void setFileIdList(List<String> fileIdList) {
        this.fileIdList = fileIdList;
    }
}
