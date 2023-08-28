package com.imapcloud.nest.v2.dao.po.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataAnalysisMarkUpdateInPO {
    /**
     * 标注ID
     */
    private String markId;
    /**
     * 地址缩略图
     */
    private String addrImagePath;

    /**
     * 地址信息
     */
    private String addr;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 标注等级
     */
    private Long topicLevelId;

}
