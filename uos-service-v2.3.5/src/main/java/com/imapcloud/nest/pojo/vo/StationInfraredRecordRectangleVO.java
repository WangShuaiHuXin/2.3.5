package com.imapcloud.nest.pojo.vo;

import com.imapcloud.nest.model.StationInfraredRecordRectangleEntity;
import lombok.Data;

import java.util.List;

@Data
public class StationInfraredRecordRectangleVO {
    private List<StationInfraredRecordRectangleEntity> list;
    private Integer photoId;
}
