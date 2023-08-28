package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;

/**
 * 文件
 *
 * @author boluo
 * @date 2022-11-03
 */
public class FileReportInfoOutPO {

    @Data
    public static class FileTrendOutPO {

        private String reportDay;
        private String reportMonth;

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
    }

    @Data
    public static class FileListOutPO {

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
    }
}
