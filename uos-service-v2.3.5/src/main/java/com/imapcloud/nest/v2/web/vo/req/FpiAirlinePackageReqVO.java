package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 精细巡检航线数据包信息
 * @author Vastfy
 * @date 2023/02/21 09:52
 * @since 2.2.3
 */
@ApiModel("精细巡检航线数据包信息")
@Data
public class FpiAirlinePackageReqVO implements Serializable {

    @ApiModelProperty(value = "精细巡检航线数据包文件", position = 2, required = true)
    @NotNull(message = "压缩包文件不能为空")
    private MultipartFile file;

}
