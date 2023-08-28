package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * dto文件
 *
 * @author boluo
 * @date 2022-11-02
 */
@ToString
public class FileReportOutDTO {

    private FileReportOutDTO() {}

    @Data
    public static class FileInfoOutDTO {

        private Long picture;
        private Long video;
        private Long videoPicture;
        private Long total;
    }

    @Data
    public static class FileTrendOutDTO {

        private String time;
        private Long picture;
        private Long video;
        private Long videoPicture;
        private Long total;
    }

    @Data
    public static class FileListOutDTO implements Serializable {

        private String nestId;
        private String nestName;
        private Long picture;
        private Long video;
        private Long videoPicture;
        private Long total;
    }
}
