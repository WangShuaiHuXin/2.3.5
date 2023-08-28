package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName MissionPhotoPointOutDTO.java
 * @Description MissionPhotoPointOutDTO
 * @createTime 2022年09月16日 11:23:00
 */
@Data
public class MissionPhotoPointOutDTO implements Serializable {

        /**
         * 照片id
         */
        private String missionPhotoId;

        /**
         * 照片名
         */
        private String missionPhotoName;

        /**
         * 缩略图
         */
        private String thumbnailUrl;

        /**
         * 照片原图
         */
        private String photoUrl;


        /**
         * 序号
         */
        private Integer waypointsIndex;

        /**
         * 镜头类型
         */
        private Integer lenType;

        /**
         * 航点ID
         */
        private String airPointId;

        /**
         * 航点序号
         */
        private String airPointIndex;

        /**
         * 全景名
         */
        private String pointName;

        /**
         * 架次id
         */
        private String missionId;


}
