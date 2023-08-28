package com.imapcloud.nest.v2.manager.dataobj.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;

/**
 * 文件统计
 *
 * @author boluo
 * @date 2022-10-31
 */
@Data
public class FileReportInfoInDO {

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
     * 标签版本
     */
    private Integer tagVersion;

    @Data
    public static class ListInDO extends PageInfo {

        private String orgCode;

        private String nestId;

        /**
         * 开始时间yyyy-MM-dd
         */
        private String startTime;

        /**
         * 结束时间 yyyy-MM-dd
         */
        private String endTime;

        /**
         * 排序字段 图片：picture 视频：video 视频抽帧：videoPicture 总计：total
         */
        private String orderBy;

        /**
         * asc 0:降序 1：升序
         */
        private int asc;
    }
}
