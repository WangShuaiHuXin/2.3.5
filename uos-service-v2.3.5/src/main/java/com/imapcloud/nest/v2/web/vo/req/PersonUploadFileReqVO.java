package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Classname PersonUploadFileReqVO
 * @Description 人员上传信息
 * @Date 2023/3/28 11:55
 * @Author Carnival
 */
@Data
public class PersonUploadFileReqVO {

    @ApiModelProperty(value = "上传类型", notes = "类型：0-身份证正面、1-身份证反面、2-驾驶证", required = true, example = "1")
    @NotNull(message = "上传类型不能为空")
    private Integer uploadType;


    @ApiModelProperty(value = "批复文件", required = true)
    @NotNull(message = "批复文件不能为空")
    private MultipartFile file;
}
