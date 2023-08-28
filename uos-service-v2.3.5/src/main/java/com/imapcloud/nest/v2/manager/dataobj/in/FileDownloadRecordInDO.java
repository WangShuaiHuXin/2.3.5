package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.nest.v2.manager.dataobj.BaseInDO;
import lombok.Data;

/**
 * 文件下载记录
 *
 * @author boluo
 * @date 2023-05-09
 */
@Data
public class FileDownloadRecordInDO extends BaseInDO {

    /**
     * 下载业务id
     */
    private String fileDownloadRecordId;

    /**
     * 下载的注解key值
     */
    private String annotationKey;

    /**
     * 下载参数
     */
    private String param;

    /**
     * 下载状态【0：未下载；1：已下载】
     */
    private int downloadStatus;
}
