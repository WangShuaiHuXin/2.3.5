package com.imapcloud.nest.v2.manager.dataobj.in;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.InputStream;

/**
 * @Classname CommonFileInDO
 * @Description 通用文件信息
 * @Date 2023/2/16 16:21
 * @Author Carnival
 */
@Data
public class CommonFileInDO {

    /**
     * 文件流
     */
    private InputStream inputStream;

    /**
     * 扩展字段【该字段为调用方扩展信息，会随回调通知返回，长度不超过255】
     */
    private String extParam;

    /**
     * 文件名称[不传时优先从文件对象中获取文件名称]
     */
    private String fileName;

    /**
     * 文件格式[不传时优先从文件对象中获取文件后缀]
     */
    private String fileSuffix;

    /**
     * 文件唯一标识[可以传文件MD5值，不传时默认使用UUID]
     */
    private String fileMd5;

    /**
     * 是否自动解压
     */
    private boolean autoDecompress;

}
