package com.imapcloud.nest.v2.web.vo.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.imapcloud.nest.common.annotation.LimitVal;
import com.imapcloud.nest.common.annotation.NestId;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel("设置RTK信息参数")
@Data
public class SetRtkInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "基站id", position = 1, example = "1", required = true)
    @NotNull(message = "基站id不能为null")
    @NestId
    private String nestId;

    @ApiModelProperty(value = "机位标识，0：G503外其他基站， 1、2、3：标识G503的三个机位", position = 2, example = "1", required = true)
    @LimitVal(values = {"0","1","2","3"}, message = "which值不在范围内")
    @NotNull(message = "基站id不能为null")
    private Integer which;

    /**
     * IP地址或域名
     */
    @ApiModelProperty(value = "地址或域名", position = 3, required = true)
    private String ip;

    /**
     * 端口
     */
    @ApiModelProperty(value = "端口，8001:ITRF2008坐标系，8002:WGS84坐标系，8003:CGCS2000坐标系", position = 4, required = true)
    private Integer port;

    /**
     * 源节点
     */
    @ApiModelProperty(value = "源节点，RTCM32_GGB,RTCM30_GG,AUTO", position = 5, required = true)
    private String mountPoint;

    /**
     * 账号
     */
    @ApiModelProperty(value = "用户名", position = 6, required = true)
    @NotNull(message = "用户名不能为空")
    private String userName;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", position = 7, required = true)
    @NotNull(message = "密码不能为空")
    private String password;
}
