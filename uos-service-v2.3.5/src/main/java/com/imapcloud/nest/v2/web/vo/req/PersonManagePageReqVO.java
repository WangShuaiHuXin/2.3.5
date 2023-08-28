package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Classname PersonManagePageReqVO
 * @Description 人员管理请求类
 * @Date 2023/3/28 10:55
 * @Author Carnival
 */

@ApiModel("人员管理信息")
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonManagePageReqVO extends PageInfo implements Serializable {

    @ApiModelProperty(value = "单位编码", position = 1, required = true, example = "000")
    private String orgCode;

    @ApiModelProperty(value = "用户姓名", position = 1, required = true, example = "张三")
    private String name;

}
