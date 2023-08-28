package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("台账设备更新/新增入参")
public class PowerEquipmentEditReqVO implements Serializable {

    private String equipmentId;
    @NotNull(message = "设备名称不能为空")
    @Length(max = 50,min = 1,message = "最大长度为50字符")
    private String equipmentName;
    @NotNull(message = "变电站名称不能为空")
    @Length(max = 50,min = 1,message = "最大长度为50字符")
    private String stationName;
    @NotNull(message = "间隔单元不能为空")
    @Length(max = 50,min = 1,message = "最大长度为50字符")
    private String spacingUnit;
    @NotNull(message = "设备类型不能为空")
    @Length(max = 50,min = 1,message = "最大长度为50字符")
    private String equipmentType;
    @NotNull(message = "电压等级不能为空")
    @Length(max = 50,min = 1,message = "最大长度为50字符")
    private String voltageLevel;
    @NotNull(message = "PMS_ID不能为空")
    @Length(max = 50,min = 1,message = "最大长度为50字符")
    private String equipmentPmsId;

    private String orgCode;
}
