package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Classname AirspacePageReqVO
 * @Description 空域管理分页展示请求类
 * @Date 2023/3/7 16:15
 * @Author Carnival
 */

@ApiModel("空域分页展示")
@Data
@EqualsAndHashCode(callSuper = true)
public class AirspacePageReqVO extends PageInfo implements Serializable {

    @ApiModelProperty(value = "空域名称", example = "北京")
    private String airspaceName;

    @ApiModelProperty(value = "所属单位",  example = "中科云图")
    private String orgCode;
}
