package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.nest.model.AirLineEntity;
import com.imapcloud.nest.model.MissionEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DJIAirLineHandleDO {

    /**
     * 任务类型 -必填
     */
    private Integer taskType;

    /**
     * 任务id - 必填（本地上传保存不传）
     */
    private Integer taskId;

    /**
     * 基站id -必填
     */
    private String nestId;

    /**
     * 是否保存操作 -必填
     */
    private Boolean save;

    /**
     * 只有本地上传保存，需要传这个字段
     */
    private String taskFileId;

    /**
     * 航线架次列表
     */
    private List<AirLineEntity> airLineList;

    /**
     * 架次列表 - 必填
     */
    private List<MissionEntity> missionList;

    /**
     * 航线id跟航线map - 必填
     */
    private Map<Integer , String> djiAirLineMap;

}
