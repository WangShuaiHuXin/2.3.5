package com.imapcloud.nest.v2.service.dto.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 专题dto
 *
 * @author boluo
 * @date 2022-07-14
 */

@Data
public class TopicUDAProblemTypeConfigOutDTO {
    @ApiModelProperty(value = "uda场景ID/识别功能ID/问题类型ID")
    public String id;

    @ApiModelProperty(value = "uda场景名称/uda场景名称/识别功能名称/问题类型名称")
    public String name;

    @ApiModelProperty(value = "问题类型是否可用（0：不可选；1可选）")
    public Integer status;
}
