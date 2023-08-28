package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.pojo.dto.unifyAirLineDto.StationDefectPhotoDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 设备-图片—读数相关字段DTO
 *
 * @author: hc
 * @create: 2021/1/4
 **/
@Data
public class StationDevicePhotoDTO extends TaskAndMissionRecordsDto{
    /**
     * 图片id
     */
    private Integer photoId;
    /**
     * 设备名称
     */
    private String photoName;
    /**
     * 最后一次修改时间
     */
    private LocalDateTime photoModifyTime;
    /**
     * 表计读数的值
     */
    private Double meterNum;
    /**
     * 表计读数的值1
     */
    private Double meterNum1;
    /**
     * 表计读数状态（1-识别成功；2-识别失败）
     */
    private Integer meterStatus;
    /**
     * 识别截图的表计图片
     */
    private String meterPhoto;
    /**
     * 是否有新图片
     */
    private Boolean hasNew;
    /**
     * 是否有缺陷
     */
    private Boolean isDefect;

    /**
     * 表计读数状态（1-识别成功；2-识别失败）
     */
    private Integer defectStatus;

    private List<Map> photoMapList;

    private Double maxTemperature;

    private List<StationDefectPhotoDTO> defectList;

    private String taskName;

    private LocalDateTime taskCreateTime;

    /**
     * 1有问题0没问题
     */
    private Integer flag;
}
