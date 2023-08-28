package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel("首页根据设备类型查询")
@Validated
@Data
public class InspectionEquipmentReqVO implements Serializable {

    @NotNull(message = "参数异常")
    private Integer pageNo;
    @NotNull(message = "参数异常")
    private Integer pageSize;
    @NotNull(message = "参数异常")
    private String equipmentType;
    @NotNull(message = "参数异常")
    private String beginTime;
    @NotNull(message = "参数异常")
    private String endTime;


}
