package com.imapcloud.nest.v2.manager.dataobj.in;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Classname ThumbnailRuleInDO
 * @Description 缩略图存储信息
 * @Date 2023/2/17 11:25
 * @Author Carnival
 */
@Data
public class ThumbnailRuleInDO {
    /**
     * 原图文件路径
     */
    private String srcImagePath;

    /**
     * 宽度缩放比例【取值范围为(0, 1)，精度限定为小数点后2位】
     */
    private Double scaleWidth;

    /**
     * 高度缩放比例【取值范围为(0, 1)，精度限定为小数点后2位，为空时将使用scaleWidth参数值】
     */
    private Double scaleHeight;

    /**
     * 是否异步生成缩略图，默认为同步生成，该值为true时回调URL不能为空
     */
    private Boolean async;

    /**
     * 回调URL
     */
    private String callbackUrl;
}
