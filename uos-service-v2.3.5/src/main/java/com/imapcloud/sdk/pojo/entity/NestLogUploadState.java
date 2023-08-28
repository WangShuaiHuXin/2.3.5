package com.imapcloud.sdk.pojo.entity;


import com.geoai.common.web.util.MessageUtils;

/**
 * 基站日志上传状态
 *
 * @author wmin
 */
public class NestLogUploadState {
    private String extras = "";
    private Integer progress = 0;
    private String state = "";

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        if (extras != null) {
            this.extras = extras;
        }
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        if (progress != null) {
            this.progress = progress;
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if (state != null) {
            this.state = state.trim();
        }
    }


    public enum StateEnum {
        START_ZIP("start-zip", "开始压缩", "geoai_uos_StateEnum_START_ZIP"),
        ZIPPING("zipping", "正在压缩", "geoai_uos_StateEnum_ZIPPING"),
        ZIP_COMPLETE("zip-complete", "压缩完成", "geoai_uos_StateEnum_ZIP_COMPLETE"),
        START_UPLOAD("start-upload", "开始上传", "geoai_uos_StateEnum_START_UPLOAD"),
        UPLOADING("uploading", "正在上传", "geoai_uos_StateEnum_START_UPLOADING"),
        UPLOAD_COMPLETE("upload-complete", "上传完成", "geoai_uos_StateEnum_UPLOAD_COMPLETE"),
        UPLOAD_FAIL("upload-fail", "上传失败", "geoai_uos_StateEnum_UPLOAD_FAIL"),

        /**
         * 由于minio上传过程比较长，所以自定义一下两个类型
         */
        MINIO_SAVING("minio-saving", "正在文件保存", "geoai_uos_StateEnum_MINIO_SAVING"),
        MINIO_SAVE_COMPLETE("minio-save-complete", "文件保存完成", "geoai_uos_StateEnum_MINIO_SAVE_COMPLETE"),
        UNKNOWN("unknown", "未知", "geoai_uos_StateEnum_UNKNOWN");
        private String value;
        private String express;

        private String key;

        StateEnum(String value, String express, String key) {
            this.value = value;
            this.express = express;
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public String getExpress() {
            return MessageUtils.getMessage(key);
        }

        public String getKey() {return key;}

        public static NestLogUploadState.StateEnum getInstance(String value) {
            if (value != null) {
                for (NestLogUploadState.StateEnum se : NestLogUploadState.StateEnum.values()) {
                    value = value.trim();
                    if (se.getValue().equals(value)) {
                        return se;
                    }
                }
            }
            return UNKNOWN;
        }
    }
}
