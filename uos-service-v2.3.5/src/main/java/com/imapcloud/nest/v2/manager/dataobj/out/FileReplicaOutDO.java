package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件副本信息
 * @author Vastfy
 * @date 2023/01/31 15:12
 * @since 2.2.3
 */
@Data
public class FileReplicaOutDO implements Serializable {

    /**
     * 文件副本存储目录
     */
    private String replicaPath;

    /**
     * 异步任务ID
     */
    private String notifyId;

}
