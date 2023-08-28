package com.imapcloud.nest.v2.service.dto.out;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * AI分析图片结果信息
 * @author Vastfy
 * @date 2022/10/28 13:26
 * @since 2.1.4
 */
@ApiModel("AI分析图片结果信息")
@Data
public class AIAnalysisPicResultDataOutDTO implements Serializable {

    @ApiModelProperty(value = "图片识别记录唯一标识", required = true, position = 1, example = "666")
    private String picId;

    @ApiModelProperty(value = "算法侧（UDA）任务ID", required = true, position = 2, example = "7777777")
    private String taskId;

    @ApiModelProperty(value = "图片识别结果【true：识别成功；false：识别失败】", position = 3, example = "true")
    private boolean result;

    @ApiModelProperty(value = "图片识别标注信息列表", position = 5)
    private List<MarkInfo> markInfos;

    @Data
    public static class MarkInfo implements Serializable {

        @ApiModelProperty(value = "算法侧（UDA）问题类型ID", position = 1, example = "9527666")
        private String problemTypeId;

        @ApiModelProperty(value = "算法侧（UDA）问题类型名称", position = 2, example = "田头棚")
        private String problemTypeName;

        @ApiModelProperty(value = "标注框左上角横坐标值", position = 3, example = "0.123")
        private BigDecimal x;

        @ApiModelProperty(value = "标注框左上角纵坐标值", position = 4, example = "0.345")
        private BigDecimal y;

        @ApiModelProperty(value = "标注框右下角横坐标值", position = 5, example = "0.567")
        private BigDecimal x1;

        @ApiModelProperty(value = "标注框右下角纵坐标值", position = 6, example = "0.678")
        private BigDecimal y1;

        @ApiModelProperty(value = "表计读数值，多个以英文分号分隔", position = 7, example = "45.23;77")
        private String readingValues;

        @ApiModelProperty(value = "表计读数截图URL(内网)", position = 8, example = "http://www.baidu.com/test.jpg")
        private String screenshotUrl;

        @ApiModelProperty(value = "分合状态：kai/he", position = 9, example = "kai")
        private String switchStatus;

    }

}
