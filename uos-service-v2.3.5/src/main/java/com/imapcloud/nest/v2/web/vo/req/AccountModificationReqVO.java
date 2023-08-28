package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * 账号修改信息
 * @author Vastfy
 * @date 2022/5/18 17:35
 * @since 1.0.0
 */
@ApiModel("账号修改信息")
@Data
public class AccountModificationReqVO implements Serializable {

    @ApiModelProperty(value = "账号ID", example = "11111")
    @NotBlank(message = "{geoai_uos_cannot_empty_accountid}")
    private String id;

    @ApiModelProperty(value = "姓名", position = 5, example = "老六", required = true)
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,16}$", message = "{geoai_uos_limited_2_16_user_name}")
    @NotBlank(message = "{geoai_uos_cannot_empty_name}")
    private String realName;

    @ApiModelProperty(value = "账号关联手机号", example = "131xxxx8888")
    @NotBlank(message = "{geoai_uos_cannot_empty_phone_number}")
    @Pattern(regexp = "^[1]\\d{10}$", message = "{geoai_uos_wrong_format_phone_number}")
    private String mobile;

    @ApiModelProperty(value = "单位ID", example = "1111")
    @NotBlank(message = "{geoai_uos_cannot_empty_unitid}")
    private String unitId;

    @ApiModelProperty(value = "新增角色ID列表")
    private List<String> incRoleIds;

    @ApiModelProperty(value = "删除角色ID列表")
    private List<String> decRoleIds;

    @ApiModelProperty(value = "基站操作权限【true：可操作；false：不可操作(默认值)】", example = "false")
    private Boolean isOperation = Boolean.FALSE;

    @ApiModelProperty(value = "关联基站列表")
    private List<String> inchargeNestList;

}