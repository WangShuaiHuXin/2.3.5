package com.imapcloud.nest.pojo.vo;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ElectricResultDefectCount {
    //@ApiModelProperty("缺陷总数")
    private Integer defectTotal;
    //@ApiModelProperty("未消缺总数")
    private Integer defectNotEliminatedTotal;
    //@ApiModelProperty("已消缺总数")
    private Integer defectEliminatedTotal;
    //@ApiModelProperty("成果总数")
    private Integer resultTotal;
    //@ApiModelProperty("未查看总数")
    private Integer resultNotViewedTotal;
    //@ApiModelProperty("有缺陷总数")
    private Integer resultDefectTotal;
    //@ApiModelProperty("无缺陷总数")
    private Integer resultNotDefectTotal;
    //@ApiModelProperty("缺陷率")
    private double defectRate;
    //@ApiModelProperty("月均发现缺陷个数")
    private double defectMonthTotal;
    //@ApiModelProperty("月均发现缺陷个数")
    private List<Map> defectMonthTotals;
    //@ApiModelProperty("一般缺陷总数")
    private int commonlyDefectNum;
    //@ApiModelProperty("嚴重缺陷总数")
    private int seriousDefectNum;
    //@ApiModelProperty("緊急缺陷总数")
    private int urgentDefectNum;
}
