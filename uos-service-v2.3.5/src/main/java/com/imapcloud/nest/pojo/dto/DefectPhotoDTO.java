package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.StationIdentifyRecordEntity;
import lombok.Data;

import java.time.LocalDate;

/**
 * 统计分析中台全部缺陷DTO
 *
 * @author: zhengxd
 * @create: 2021/3/30
 **/
@Data
public class DefectPhotoDTO extends StationIdentifyRecordEntity {

    private Integer type;

    private Integer nestId;

    private Integer tagId;

    private String photoTime;

    private Integer num;

    private Long photoId;
    
    private Integer id;
    
    private Integer source;

    private Double lat;
    private Double lng;
}
