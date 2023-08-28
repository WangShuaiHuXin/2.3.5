package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件复制信息
 * @author Vastfy
 * @date 2023/03/13 11:12
 * @since 1.0.0
 */
@Data
public class FileCopyInDO implements Serializable {

    /**
     * 文件地址
     */
    private String filePath;

    /**
     * 是否异步，默认为同步
     */
    private Boolean async;

    /**
     * 回调URL
     */
    private String callbackUrl;

}
