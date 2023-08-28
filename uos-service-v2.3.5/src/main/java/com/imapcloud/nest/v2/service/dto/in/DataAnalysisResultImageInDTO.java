package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

/**
 * @Classname DataAnalysisTraceSpacetimeImageInDTO
 * @Description 标注图片InDTO
 * @Date 2022/10/24 13:55
 * @Author Carnival
 */
@Data
public class DataAnalysisResultImageInDTO {

    private Long photoId;

    private Long missionRecordsId;

    private String resultImagePath;

    private String thumImagePath;
}
