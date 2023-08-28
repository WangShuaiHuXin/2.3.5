package com.imapcloud.nest.v2.service.dto.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imapcloud.nest.v2.web.vo.resp.DataAnalysisHisResultRespVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DataAnalysisHisResultDTO {

    @Data
    public static class HisResultDTO {
        private String addr;

        private String topicLevelId;

        private Integer industryType;

        private String topicProblemId;

        private String topicLevelName;

        private String topicIndustryName;

        private String topicProblemName;

        private BigDecimal longitude;

        private BigDecimal latitude;

        private String resultGroupId;

        private List<DataAnalysisHisResultDTO.GroupicResultDTO> groupPics;

        private LocalDateTime createTime;

    }

    @Data
    @Builder
    public static class GroupicResultDTO {
        // private String markImagePath;

        private String thumImagePath;

        private Long resultId;

        private String resultImgPath;

        private Long markId;

    }
}
