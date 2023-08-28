package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 文件
 *
 * @author boluo
 * @date 2022-11-02
 */
@ToString
public class FileReportRespVO {

    private FileReportRespVO() {}

    @Data
    public static class FileTotalRespVO {
        private Long picture;
        private Long video;
        private Long videoPicture;
        private Long total;
    }

    @Data
    public static class FileTrendRespVO {
        private String time;
        private Long picture;
        private Long video;
        private Long videoPicture;
        private Long total;
    }

    @Data
    public static class FileListRespVO implements Serializable {
        private String nestId;
        private String nestName;
        private Long picture;
        private Long video;
        private Long videoPicture;
        private Long total;
    }
}
