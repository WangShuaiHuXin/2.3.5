package com.imapcloud.sdk.pojo.entity;

/**
 * 喊话器的状态值
 *
 * @author: zhengxd
 * @create: 2021/4/9
 **/
public class AccessoryStatus {

    private Speaker speaker;

    public Speaker getSpeaker() {
        return speaker;
    }
    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }


    public static class Speaker {
        private Integer downloadProgress;
        private String runningState;
        private Long transmittedDataSize;

        public Integer getDownloadProgress() {
            return downloadProgress;
        }

        public void setDownloadProgress(Integer downloadProgress) {
            this.downloadProgress = downloadProgress;
        }

        public String getRunningState() {
            return runningState;
        }

        public void setRunningState(String runningState) {
            this.runningState = runningState;
        }

        public Long getTransmittedDataSize() {
            return transmittedDataSize;
        }

        public void setTransmittedDataSize(Long transmittedDataSize) {
            this.transmittedDataSize = transmittedDataSize;
        }
    }
}
