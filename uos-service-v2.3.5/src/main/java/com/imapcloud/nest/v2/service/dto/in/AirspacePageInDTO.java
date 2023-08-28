package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Classname AirspacePageInDTO
 * @Description 空域分页展示
 * @Date 2023/3/8 18:06
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AirspacePageInDTO extends PageInfo implements Serializable {

    private String airspaceName;

    private String orgCode;
}
