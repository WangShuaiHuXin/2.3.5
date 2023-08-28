package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Classname AirspaceUploadFileReqVO
 * @Description 空域上传批复文件请求类
 * @Date 2023/3/7 16:15
 * @Author Carnival
 */

@ApiModel("空域上传批复文件请求类")
@Data
public class AirspaceUploadFileReqVO implements Serializable {

    @ApiModelProperty(value = "空域ID", required = true, example = "15202255841151")
    @NotBlank(message = "空域ID不能为空")
    private String airspaceId;

//    /**
//     * @deprecated 2.2.3，将在后续版本删除
//     */
//    @Deprecated
//    @ApiModelProperty(value = "批复文件", required = true)
//    @NotBlank(message = "批复文件不能为空")
//    private MultipartFile file;

    @ApiModelProperty(value = "批复文件存储路径", required = true)
    @NotBlank(message = "批复文件不能为空")
    private String filePath;

}
