package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 表计读数修改信息
 * @author Vastfy
 * @date 2022/11/25 18:34
 * @since 2.1.5
 */
@ApiModel("表计读数修改信息")
@Data
public class MeterReadingInfoReqVO implements Serializable {

    @ApiModelProperty(value = "表计读数截图路径", required = true, position = 1)
    private String screenshotPath;

    @ApiModelProperty(value = "表计读数值列表", position = 2)
    @Valid
    @Size(max = 10, message = "{geoai_uos_meterreadinginforeqvo_001}")
    private List<MeterReadingValue> readingValues;

    @ApiModel
    @Data
    public static class MeterReadingValue implements Serializable{

        @ApiModelProperty(value = "读数规则ID", required = true, position = 1)
        @NotBlank(message = "{geoai_uos_meterreadinginforeqvo_002}")
        private String readingRuleId;

        @ApiModelProperty(value = "表计读数值", required = true, position = 2)
        @Digits(integer = 10, fraction = 3, message = "{geoai_uos_meterreadinginforeqvo_003}")
        private String readingValue;

    }

}
