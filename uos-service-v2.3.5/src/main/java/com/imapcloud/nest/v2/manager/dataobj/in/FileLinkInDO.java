package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

/**
 * 文件下载链接参数
 * @author Vastfy
 * @date 2023/5/9 10:13
 * @since 2.3.3.1
 */
@Data
public class FileLinkInDO {

    private String filePath;

    private Boolean onlyIntranet;

}
