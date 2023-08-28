package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 专题
 *
 * @author boluo
 * @date 2022-07-14
 */
@ToString
public class TopicProblemConfigInDTO {

    private TopicProblemConfigInDTO() {}

    @Data
    public static class TopicProblemConfigQueryIn extends PageInfo {
        @ApiModelProperty(value = "专题行业ID", example = "1")
        String industryType;
        @ApiModelProperty(value = "来源", example = "-1")
        String source;
        @ApiModelProperty(value = "专题行业问题名称", example = "违停")
        String topicProblemName;
    }

}
