package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJICommonResultRespVO.java
 * @Description DJICommonResultRespVO
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@ApiModel("响应信息")
@EqualsAndHashCode(callSuper = false)
public class DJICommonResultRespVO implements Serializable {

    @Data
    public static class CommonResultRespVO {
        @ApiModelProperty(value = "结果码", position = 1,example = "0")
        private Integer result;

        @ApiModelProperty(value = "状态信息", position = 1, example = "")
        private String statusCode;

        @ApiModelProperty(value = "状态描述", position = 1, example = "")
        private String statusStr;
    }

    @Data
    public static class LiveResultRespVO {

        @ApiModelProperty(value = "结果码", position = 1 ,example = "0")
        private Integer result;

        @ApiModelProperty(value = "状态信息", position = 1, example = "")
        private String info;


    }

}
