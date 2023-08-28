package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.MissionPhotoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * mission photo的封装类
 * @author kings
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MissionPhotoMonthWrapDto {

    private String month;
    /**
     * 航线拍视频的数量
     */
    private List<MissionPhotoEntity> missionPhotoEntities;

}
