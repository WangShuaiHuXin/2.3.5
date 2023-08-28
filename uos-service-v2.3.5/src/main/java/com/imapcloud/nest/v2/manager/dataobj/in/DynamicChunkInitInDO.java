package com.imapcloud.nest.v2.manager.dataobj.in;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Classname DynamicChunkInitInDO
 * @Description 大文件约定切片上传初始化信息
 * @Date 2023/2/16 15:49
 * @Author Carnival
 */
@Data
public class DynamicChunkInitInDO {

    /**
     * 源文件格式
     */
    private String srcFileSuffix;

    /**
     * 源文件名称
     */
    private String srcFileName;

    /**
     * 源文件大小【单位：字节】，最小值5M
     */
    private Long srcFileSize;

    /**
     * 切片文件大小，最小值5M
     */
    private Integer chunkSize;

    /**
     * 源文件MD5值
     */
    private String srcFileMd5;

    /**
     * 合并结果回调URL【不为空时会在文件合并完成后推送合并结果】
     */
    private String callbackUrl;
}
