package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.DefectInfoEntity;
import lombok.Data;

import java.util.List;

@Data
public class RecordDto {
    //缺陷个数
    int times;
    //所有缺陷内容
    String defectContent;
    //识别后需要标记的信息和位置百分比
    String info;
    //识别后的defectInfo列表
    List<DefectInfoEntity> defectInfoEntityList;
}
