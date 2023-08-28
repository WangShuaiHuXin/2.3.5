package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.util.Map;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJITaskFileOutDTO.java
 * @Description DJITaskFileOutDTO
 * @createTime 2022年07月25日 16:08:00
 */
public class DJITaskOutDTO {

    @Data
    public static class DJITaskFileQueryOutDTO {

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
    public static class DJITaskInfoOutDTO {

        /**
         * key -> 航线架次id
         * val -> 航线的JSON
         */
        private Map<Integer, String> airLineMap;

        /**
         * 机巢id
         */
        private String nestId;

        private Integer tagId;

        /**
         * 显示参数
         */
        private String showInfo;

        /**
         * key -> 航线架次id
         * val -> 航线的JSON
         */
        private Map<Integer,String> djiAirLineMap;

    }


    @Data
    public static class DJITaskFileInfoOutDTO {

        private String taskFileId;

        private String djiAirLine;

    }


}
