package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.imapcloud.nest.model.StationIdentifyRecordEntity;
import lombok.Data;

import java.util.Date;

/**
 * 缺陷管理表VO类
 *
 * @author: zhengxd
 * @create: 2020/5/28
 **/
@Data
public class StationIdentifyDefectDtO extends StationIdentifyRecordEntity {

    /**
     * 缺陷类型
     */
    ////@ApiModelProperty(value = "缺陷类型")
    private String defectType;

    ////@ApiModelProperty(value = "类别id")
    private Integer typeId;
    /**
     * 消缺状态（0：未消缺；1：已消缺；2：处理中）
     */
    ////@ApiModelProperty(value = "消缺状态（0：未消缺；1：已消缺；2：处理中）")
    //@TypeName(tableName = "sys_dict",dictType = "defectStatus")
    private Integer defectStatus;
    /**
     * 备注
     */
    ////@ApiModelProperty(value = "备注")
    private String note;

    /**
     * 计划编号
     */
    ////@ApiModelProperty(value = "计划编号")
    private String planCode;

    /**
     * 线路名称
     */
    ////@ApiModelProperty(value = "线路名称")
    private String lineName;

    /**
     * 杆塔编号
     */
    ////@ApiModelProperty(value = "杆塔编号")
    private String towerNum;

    /**
     * 资料管理员
     */
    ////@ApiModelProperty(value = "资料管理员")
    private String dataMangerName;

    /**
     * 成果标记
     */
    ////@ApiModelProperty(value = "成果标记")
    //private ResultsTagVO resultsTagVO;
    /**
     * 缩略图路径
     */
    ////@ApiModelProperty(value = "缩略图路径")
    private String picThumbnailPath;

    ////@ApiModelProperty(value = "缺陷等级")
    private String confidenceRate;

    ////@ApiModelProperty(value = "缺陷等级")
    private String defectLevel;

    ////@ApiModelProperty(value = "缺陷内容")
    private String defectContent;

    ////@ApiModelProperty(value = "缺陷类别")
    private String defectName;

    ////@ApiModelProperty(value = "缺陷大类")
    private String typeName;
}
