package com.imapcloud.nest.v2.service.dto.out;

import com.imapcloud.nest.v2.web.vo.resp.DiscernFunctionInfoRespVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DiscernFunctionSettingOutDTO {

    private Integer discernType;

    private String lastModifierId;

    private String lastModifierName;

    private LocalDateTime lastModifiedTime;

    private List<DiscernFunctionInfoOutDTO> discernFunctionInfos;

    @Data
    public static class DiscernFunctionInfoOutDTO {
        private String functionId;

        private String functionName;

        private String version;
    }
}
