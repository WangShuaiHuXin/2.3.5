package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;
import scala.Int;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class NhOrderInfoReqVO implements Serializable {

    private String orderId;
    @NotEmpty(message = "title can't no be empty")
    @Size(max = 50, min = 1, message = "length 【1-50】")
    private String title;
    @NotEmpty(message = "orgCode can't no be empty")
    private String orgCode;
    @NotEmpty(message = "degree can't no be empty")
    private String degree;
    @NotEmpty(message = "orderType can't no be empty")
    private String orderType;
    @NotNull(message = "beginTime can't no be empty")
    private String beginTime;
    @NotNull(message = "endTime can't no be empty")
    private String endTime;
    @NotEmpty(message = "verificationMethod can't no be empty")
    private String verificationMethod;
    private String frequency;
    @NotEmpty(message = "desc can't no be empty")
    private String desc;
    private String orderStatus;
    private List<NhOrderInfoReqVO.VectorsInfoReq> vectors;

    @Data
    public static class VectorsInfoReq {
        private String name;
        private List<String> points;
        private Integer order;
        private Integer type;
    }
}

