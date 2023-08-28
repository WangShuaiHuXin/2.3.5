package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件下载记录
 *
 * @author boluo
 * @date 2023-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_download_record")
public class FileDownloadRecordEntity extends GenericEntity {

    /**
     * 下载业务id
     */
    private String fileDownloadRecordId;

    /**
     * 下载策略key值
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
