package com.imapcloud.sdk.pojo.entity;

import cn.hutool.core.util.StrUtil;
import com.imapcloud.sdk.pojo.constant.MediaStatusV2Enum;
import lombok.Data;

import java.util.Collections;
import java.util.Map;

/**
 * 多媒体状态
 *
 * @author: zhengxd
 * @create: 2020/9/8
 **/
public class MediaStateV2 {
    /**
     * 当前媒体状态
     */
    private String currentState;
    /**
     * SD卡状态
     */
    private String storageState;
    /**
     * 下载状态
     */
    private Map downloadState;
    /**
     * 上传状态
     */
    private Map uploadState;

    /**
     * 机身存储信息
     */
    private StorageInfo internalStorageInfo;

    /**
     * sd卡存储信息
     */
    private StorageInfo sdcardStorageInfo;

    /**
     * 基站剩余可用空间
     */
    private Long systemRemainSpace;

    /**
     * 基站总存储空间
     */
    private Long systemTotalSpace;

    /**
     * 是否电池替换中
     */
    private Boolean isReplacingBattery;


    public MediaStateV2() {
        this.currentState = "";
        this.storageState = "";
        this.downloadState = Collections.emptyMap();
        this.uploadState = Collections.emptyMap();
        this.internalStorageInfo = new StorageInfo();
        this.sdcardStorageInfo = new StorageInfo();
        this.systemRemainSpace = -1L;
        this.systemTotalSpace = -1L;
        this.isReplacingBattery = false;
    }

    public String getCurrentState() {
        return this.currentState;
    }

    public void setCurrentState(String currentState) {
        if (currentState != null) {
            this.currentState = currentState;
        }

    }

    public Map getDownloadState() {
        return this.downloadState;
    }

    public void setDownloadState(Map downloadState) {
        if (downloadState != null) {
            this.downloadState = downloadState;
        }

    }

    public Map getUploadState() {
        return this.uploadState;
    }

    public void setUploadState(Map uploadState) {
        if (uploadState != null) {
            this.uploadState = uploadState;
        }

    }

    public String getStorageState() {
        return storageState;
    }

    public void setStorageState(String storageState) {
        if (storageState != null) {
            this.storageState = storageState;
        }
    }

    public StorageInfo getInternalStorageInfo() {
        return internalStorageInfo;
    }

    public void setInternalStorageInfo(StorageInfo internalStorageInfo) {
        if (internalStorageInfo != null) {
            this.internalStorageInfo = internalStorageInfo;
        }
    }

    public StorageInfo getSdcardStorageInfo() {
        return sdcardStorageInfo;
    }

    public void setSdcardStorageInfo(StorageInfo sdcardStorageInfo) {
        if (sdcardStorageInfo != null) {
            this.sdcardStorageInfo = sdcardStorageInfo;
        }
    }

    public Long getSystemRemainSpace() {
        return systemRemainSpace;
    }

    public void setSystemRemainSpace(Long systemRemainSpace) {
        if (systemRemainSpace != null) {
            this.systemRemainSpace = systemRemainSpace;
        }
    }

    public Long getSystemTotalSpace() {
        return systemTotalSpace;
    }

    public void setSystemTotalSpace(Long systemTotalSpace) {
        if (systemTotalSpace != null) {
            this.systemTotalSpace = systemTotalSpace;
        }
    }

    public Boolean getIsReplacingBattery() {
        return isReplacingBattery;
    }

    public void setIsReplacingBattery(Boolean isReplacingBattery) {
        if(isReplacingBattery != null) {
            isReplacingBattery = isReplacingBattery;
        }
    }

    public static class StorageInfo {
        /**
         * 可拍摄照片数量
         */
        private Long availableCaptureCount = -1L;
        /**
         * 可录制视频时长
         */
        private Long availableRecordingTimeInSecond = -1L;

        /**
         * 缓存更新时间戳
         */
        private Long cacheTime = -1L;

        /**
         * 剩余空间
         */
        private Long remainingSpaceInMB = -1L;

        /**
         * 状态信息，同storageState
         */
        private String state = "";

        /**
         * 存储位置
         */
        private String storageLocation = "";

        /**
         * 存储总空间
         */
        private Long totalSpaceInMB = -1L;

        public Long getAvailableCaptureCount() {
            return availableCaptureCount;
        }

        public void setAvailableCaptureCount(Long availableCaptureCount) {
            if (availableCaptureCount != null) {
                this.availableCaptureCount = availableCaptureCount;
            }
        }

        public Long getAvailableRecordingTimeInSecond() {
            return availableRecordingTimeInSecond;
        }

        public void setAvailableRecordingTimeInSecond(Long availableRecordingTimeInSecond) {
            if (availableRecordingTimeInSecond != null) {
                this.availableRecordingTimeInSecond = availableRecordingTimeInSecond;
            }
        }

        public Long getCacheTime() {
            return cacheTime;
        }

        public void setCacheTime(Long cacheTime) {
            if (cacheTime != null) {
                this.cacheTime = cacheTime;
            }
        }

        public Long getRemainingSpaceInMB() {
            return remainingSpaceInMB;
        }

        public void setRemainingSpaceInMB(Long remainingSpaceInMB) {
            if (remainingSpaceInMB != null) {
                this.remainingSpaceInMB = remainingSpaceInMB;
            }
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            if (state != null) {
                this.state = state;
            }
        }

        public String getStorageLocation() {
            return storageLocation;
        }

        public void setStorageLocation(String storageLocation) {
            if (storageLocation != null) {
                this.storageLocation = storageLocation;
            }
        }

        public Long getTotalSpaceInMB() {
            return totalSpaceInMB;
        }

        public void setTotalSpaceInMB(Long totalSpaceInMB) {
            if (totalSpaceInMB != null) {
                this.totalSpaceInMB = totalSpaceInMB;
            }
        }
    }

    public enum StorageStateEnum {
        WORKING("WORKING", "正常"),
        UNFORMATTED("UNFORMATTED", "未格式化"),
        FORMATTING("FORMATTING", "正在格式化"),
        FULL("FULL", "已满"),
        INITIALIZING("INITIALIZING", "正在初始化"),
        NOT_INSERTED("NOT_INSERTED", "未插入"),
        INVALID_FORMAT("INVALID_FORMAT", "格式无效"),
        READ_ONLY("READ_ONLY", "只读"),
        UNVERIFIED("UNVERIFIED", "认证不通过"),
        UNKNOWN_ERROR("UNKNOWN_ERROR", "未知错误"),
        UNSUPPORTED("UNSUPPORTED", "不支持"),
        UNKNOWN("UNKNOWN", "未知");
        private String value;
        private String express;

        StorageStateEnum(String value, String express) {
            this.value = value;
            this.express = express;
        }

        public String getValue() {
            return value;
        }

        public String getExpress() {
            return express;
        }

        public static StorageStateEnum getInstance(String value) {
            if (value != null) {
                for (StorageStateEnum e : StorageStateEnum.values()) {
                    if (e.getValue().equals(value)) {
                        return e;
                    }
                }
            }
            return UNKNOWN;
        }
    }
}
