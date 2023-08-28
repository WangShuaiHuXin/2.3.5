package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJITaskFileInDTO.java
 * @Description DJITaskFileInDTO
 * @createTime 2022年07月25日 16:08:00
 */
public class DJITaskFileInDTO {

    @Data
    @Accessors(chain = true)
    public static class DJITaskFileAddInDTO {

        /**
         * 大疆航线主键
         */
        private String taskFileId;

        /**
         * 大疆航线url
         */
        private String fileUrl;

        /**
         * 文件名
         */
        private String fileName;

        /**
         * 文件md5
         */
        private String fileMd5;

        /**
         * 航线id
         */
        private String taskId;

        /**
         * 架次id
         */
        private String missionId;

    }

    @Data
    public static class DJITaskFileUpdateInDTO {

        /**
         * 大疆航线主键
         */
        private String taskFileId;

        /**
         * 大疆航线url
         */
        private String fileUrl;

        /**
         * 文件名
         */
        private String fileName;

        /**
         * 文件md5
         */
        private String fileMd5;

        /**
         * 航线id
         */
        private String taskId;

        /**
         * 架次id
         */
        private String missionId;

    }

    @Data
    @Accessors(chain = true)
    public static class DJITaskFileQueryInDTO {

        /**
         * 大疆航线主键
         */
        private String taskFileId;

        /**
         * 航线id
         */
        private String taskId;

        /**
         * 架次id
         */
        private String missionId;

    }

}
