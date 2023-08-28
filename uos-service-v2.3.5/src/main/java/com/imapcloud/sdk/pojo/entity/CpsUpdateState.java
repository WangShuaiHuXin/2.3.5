package com.imapcloud.sdk.pojo.entity;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * cps按照状态
 *
 * @author wmin
 */
public class CpsUpdateState extends CpsCommonState {
    /**
     * 下载安装状态
     * start-download,
     * downloading,
     * download-complete,
     * download-fail,
     * start-install,
     * install-success,
     * install-fail
     */
    private String state = "unknown";


    /**
     * 下载进度
     */
    private int progress = 0;

    /**
     * 附加信息
     */
    private String extras = "unknown";


    public enum StateEnum {
        START_DOWNLOAD("start-download", "开始下载", "geoai_uos_StateEnum_01"),
        DOWNLOADING("downloading", "下载中", "geoai_uos_StateEnum_02"),
        DOWNLOAD_COMPLETE("download-complete", "下载完成", "geoai_uos_StateEnum_03"),
        DOWNLOAD_FAIL("download-fail", "下载失败", "geoai_uos_StateEnum_04"),
        START_INSTALL("start-install", "开始安装", "geoai_uos_StateEnum_05"),
        INSTALL_SUCCESS("install-success", "安装完成", "geoai_uos_StateEnum_06"),
        INSTALL_FAIL("install-fail", "安装失败", "geoai_uos_StateEnum_07"),
        UNKNOWN("unknown", "未知", "geoai_uos_StateEnum_08");
        private String value;
        private String express;

        private String key;

        StateEnum(String value, String express, String key) {
            this.value = value;
            this.express = express;
            this.key = key;
        }

        public static StateEnum getInstance(String value) {
            if (value != null) {
                for (StateEnum se : StateEnum.values()) {
                    value = value.trim();
                    if (se.getValue().equals(value)) {
                        return se;
                    }
                }
            }
            return UNKNOWN;
        }

        public static boolean installing(String value) {
            StateEnum state = getInstance(value);
            return START_DOWNLOAD.equals(state) ||
                    DOWNLOADING.equals(state) ||
                    START_INSTALL.equals(state) ||
                    DOWNLOAD_COMPLETE.equals(state)
                    ;
        }

        public static boolean hasInstalling(List<String> values) {
            if (CollectionUtils.isEmpty(values)) {
                return false;
            }
            long count = values.stream().filter(StateEnum::installing).count();
            return count > 0;
        }


        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getExpress() {
            return express;
        }

        public String getKey() {
            return key;
        }

        public void setExpress(String express) {
            this.express = express;
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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }
}
