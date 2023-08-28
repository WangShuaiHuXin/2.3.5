package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 视频飞行轨迹信息
 * @author Vastfy
 * @date 2023/02/21 09:52
 * @since 2.2.3
 */
@ApiModel("视频飞行轨迹信息")
@Data
public class VideoSrtReqVO implements Serializable {

    @ApiModelProperty(value = "视频ID", position = 1, required = true, example = "666")
    @NotNull(message = "视频ID不能为空")
    private Integer videoId;

    @ApiModelProperty(value = "飞行轨迹文件", position = 2, required = true)
    @NotNull(message = "飞行轨迹文件不能为空")
    private MultipartFile file;

}
