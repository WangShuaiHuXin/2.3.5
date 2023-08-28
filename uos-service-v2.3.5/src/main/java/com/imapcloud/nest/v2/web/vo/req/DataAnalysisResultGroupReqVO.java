package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisResultGroupReqVO implements Serializable {

    @Data
    public static class ResultMergeReqVO {
        @NotNull(message = "{geoai_uos_please_select_the_markers_to_be_merged}")
        private Long markId;
        @NotNull(message = "{geoai_uos_please_select_the_groups_to_be_merged}")
        private String groupId;

    }
}
