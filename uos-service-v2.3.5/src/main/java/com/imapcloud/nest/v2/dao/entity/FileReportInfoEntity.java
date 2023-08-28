package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件统计
 *
 * @author boluo
 * @date 2022-10-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_report_info")
public class FileReportInfoEntity extends GenericEntity {
    /**
     * 应用名称 uos uda
     */
    private String app;

    /**
     * 单位code
     */
    private String orgCode;

    /**
     * 基站id
     */
    private String nestId;

    /**
     * 图片大小，单位：字节
     */
    private Long pictureSize;

    /**
     * 视频大小，单位：字节
     */
    private Long videoSize;

    /**
     * 视频抽帧大小，单位：字节
     */
    private Long videoPictureSize;

    /**
     * 总计大小，单位：字节
     */
    private Long totalSize;

    /**
     * 按天 yyyy-mm-dd
     */
    private String reportDay;

    /**
     * 按月 yyyy-mm
     */
    private String reportMonth;

    /**
     * 标签版本
     */
    private Integer tagVersion;
}
