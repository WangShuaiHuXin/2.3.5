package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件详细信息
 *
 * @author boluo
 * @date 2022-10-27
 */
@Data
public class FileDetailInfoInDO {

    /**
     * 存储桶
     */
    private String bucket;

    /**
     * 存储桶中的对象
     */
    private String object;

    /**
     * 事件类型 1：删除 2：设置tag
     */
    private Integer eventType;

    /**
     * 对象类型：如视频、图片等，业务定义
     */
    private String objectType;

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
     * 对象大小，单位：字节
     */
    private Long objectSize;

    /**
     * 对象创建时间
     */
    private LocalDateTime objectTime;

    /**
     * 标签版本
     */
    private Integer tagVersion;

    /**
     * 同步处理状态-0：未同步 1：已同步
     */
    private int synStatus;

    @Data
    public static class DayReportInDO {

        /**
         * 单位code
         */
        private String orgCode;

        /**
         * 基站id
         */
        private String nestId;

        /**
         * 标签版本
         */
        private Integer tagVersion;

        /**
         * yyyy-MM-dd
         */
        private String reportDay;
    }

    @Data
    public static class SynInDO {
        /**
         * 单位code
         */
        private String orgCode;

        /**
         * 基站id
         */
        private String nestId;

        /**
         * 标签版本
         */
        private Integer tagVersion;

        /**
         * yyyy-MM-dd
         */
        private String reportDay;
    }
}
