package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 固件版本查询条件
 * @author Vastfy
 * @date 2022/07/08 11:35
 * @since 1.9.7
 */
@ApiModel("固件安装包上传信息")
@Data
public class FirmwarePackageReqVO implements Serializable {

    @ApiModelProperty(value = "安装包类型【1：CPS固件；2：MPS固件】", position = 1, example = "1")
    private Integer type;

    @ApiModelProperty(value = "安装包名称", position = 2, example = "测试9527基站.apk", hidden = true)
    private String fileName;
//
//    @ApiModelProperty(value = "安装包存储路径", position = 3, example = "/data/apk")
//    private String filePath;

    @ApiModelProperty(value = "安装包大小（单位：字节）", position = 4, example = "1024", hidden = true)
    private Long fileSize;

    @ApiModelProperty(value = "安装包版本描述", position = 5, example = "测试9527基站CPS版本")
    private String versionDesc;

}
