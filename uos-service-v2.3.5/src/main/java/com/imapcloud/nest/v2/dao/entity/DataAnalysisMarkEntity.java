package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_analysis_mark")
public class DataAnalysisMarkEntity extends GenericEntity {

    private Long markId;

    private Long photoId;

    private Long detailId;

    private BigDecimal recX;

    private BigDecimal recY;

    private BigDecimal recWidth;

    private BigDecimal recHeight;

    private BigDecimal relX;

    private BigDecimal relY;

    private BigDecimal cutWidth;

    private BigDecimal cutHeight;

    private BigDecimal picScale;

    private Integer markState;

    private Boolean existMark;

    private Boolean aiMark;

    private String markImagePath;

    private String thumImagePath;

    private String addrImagePath;

    private String addr;

    private Long topicLevelId;

    /**
     * 专题行业id
     * @deprecated 2.1.4，将在后续版本移除，使用industryType替换
     */
    @Deprecated
    private Long topicIndustryId;

    /**
     * 行业类型，取字典`GEOAI_INDUSTRY_TYPE`数据项项
     */
    private Integer industryType;

    private Long topicProblemId;

    private Integer markNo;

    private BigDecimal longitude;

    private BigDecimal latitude;

    /**
     * AI标注时，可能无法匹配上UOS的问题类型，故显示UDA的问题名称
     */
    private String aiProblemName;

}
