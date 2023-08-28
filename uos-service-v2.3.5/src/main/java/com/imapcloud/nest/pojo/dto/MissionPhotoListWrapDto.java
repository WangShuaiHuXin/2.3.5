package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.MissionPhotoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * mission photo的封装类
 * @author kings
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MissionPhotoListWrapDto {

    private String deviceName;
    private Boolean haveNewPhoto;

    private List<MissionPhotoMonthWrapDto> missionPhotoMonthWrapDtoList;

    private List<MissionPhotoEntity> missionPhotoEntities;;

}
