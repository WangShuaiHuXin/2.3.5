package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * @Classname UosMqttCreationReqVO
 * @Description Mqtt代理地址新增信息
 * @Date 2022/8/16 11:52
 * @Author Carnival
 */
@ApiModel("Mqtt代理地址查询信息")
@Data
public class UosMqttPageReqVO extends PageInfo implements Serializable {

    @ApiModelProperty(value = "管理账号", example = "中科云图")
    @Length(min = 1, max = 80, message = "geoai_uos_cannot_exceed_80_useraccount")
    private String mqttName;

}
