package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Classname MultipartFileWrapperInDO
 * @Description 文件元数据信息
 * @Date 2023/2/16 16:43
 * @Author Carnival
 */
@Data
public class MultipartFileWrapperInDO {

    /**
     * 文件信息
     */
    private MultipartFile file;
}
