package com.imapcloud.nest.v2.web.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("查询历史同类问题返回值")
public class DataAnalysisHisResultRespVO implements Serializable {

    private DataAnalysisHisResultRespVO() {

    }

    @Data
    public static class HisResultRespVO {
        @ApiModelProperty(value = "地址描述信息", position = 1, example = "")
        private String addr;

        @ApiModelProperty(value = "问题等级", position = 2, example = "")
        private String topicLevelId;

        @ApiModelProperty(value = "行业", position = 3, example = "")
        private Integer industryType;

        @ApiModelProperty(value = "问题类型", position = 4, example = "")
        private String topicProblemId;

        @ApiModelProperty(value = "问题等级", position = 5, example = "")
        private String topicLevelName;

        @ApiModelProperty(value = "行业", position = 6, example = "")
        private String topicIndustryName;

        @ApiModelProperty(value = "问题类型", position = 7, example = "")
        private String topicProblemName;

        @ApiModelProperty(value = "经度", position = 8, example = "")
        private BigDecimal longitude;

        @ApiModelProperty(value = "纬度", position = 9, example = "")
        private BigDecimal latitude;

        @ApiModelProperty(value = "问题分组id", position = 10, example = "")
        private String resultGroupId;

        @ApiModelProperty(value = "分组图片信息", position = 11, example = "")
        private List<GroupicRespVo> groupPics;

        @ApiModelProperty(value = "问题创建时间", position = 12, example = "")
        private LocalDateTime createTime;
    }

    @Data
    @Builder
    public static class GroupicRespVo {
       // private String markImagePath;

        private String thumImagePath;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Long resultId;

        private String resultImgPath;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Long markId;

    }
}
