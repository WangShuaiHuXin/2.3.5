package com.imapcloud.nest.v2.manager.dataobj;

import lombok.Data;

/**
 * 文件映射信息
 * @author Vastfy
 * @date 2023/4/13 11:58
 * @since 2.3.0.1
 */
@Data
public class FileUrlMappingDO {

    private String originName;

    private String storageUri;

}
