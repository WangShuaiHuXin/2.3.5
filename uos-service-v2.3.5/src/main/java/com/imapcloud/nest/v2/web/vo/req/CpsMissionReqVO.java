package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import java.io.Serializable;

@Data
@ApiModel("cps-mission指令请求参数类")
public class CpsMissionReqVO implements Serializable {
    @Data
    public static class CpsMissionLandingReqVO {
        @ApiModelProperty(value = "latitude", name = "纬度", required = true, example = "23.1111")
        @Max(value = 90L, message = "设置备降点坐标,纬度不能大于90°")
        private Double latitude;
        @ApiModelProperty(value = "longitude", name = "经度", required = true, example = "123.1111")
        @Max(value = 180L, message = "设置备降点坐标,经度不能大于180°")
        private Double longitude;
        @ApiModelProperty(value = "longitude", name = "经度", required = true, example = "123.1111")
        private Double altitude;

        private Boolean enable;
    }

    @Data
    public static class CpsMissionLand4ReqVO {
        private Boolean enable;
    }

    @Data
    public static class CpsMissionLandAltitudeReqVO {
        private Double altitude;
    }
}
