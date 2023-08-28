package com.imapcloud.nest.pojo.dto.app;

import lombok.Data;

/**
 * @author wmin
 * app开始任务结果
 */
@Data
public class AppStartMissionResDTO {
    /**
     * 开启任务结果，1->成功,0->失败
     */
    private Integer startRes;
    /**
     * 架次Id
     */
    private Integer missionId;
    /**
     * 是否录制视频，0-不录制，1-录制
     */
    private Integer gainVideo;

    @Override
    public String toString() {
        return "AppStartMissionResDTO{" +
                "startRes=" + startRes +
                ", missionId=" + missionId +
                ", gainVideo=" + gainVideo +
                '}';
    }
}
