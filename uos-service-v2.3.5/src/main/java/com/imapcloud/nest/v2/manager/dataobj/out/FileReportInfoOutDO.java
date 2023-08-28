package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;
import lombok.ToString;

/**
 * 信息文件
 *
 * @author boluo
 * @date 2022-11-02
 */
@ToString
public class FileReportInfoOutDO {

    private FileReportInfoOutDO() {}

    @Data
    public static class FileOutDO {

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
    public static class FileTrendOutDO {

        private String time;

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
    public static class ListOutDO {

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
