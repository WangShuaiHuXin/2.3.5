package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class NhOrderPhotoOutDTO {
    private Long total;
    private List<OrderPhotoInfo> records;
    @Data
    public static class OrderPhotoInfo {

        private Long id;
        private String name;
        private String photoUrl;
        private String thumbnailName;
        private String thumbnailUrl;
        private Integer missionRecordsId;
        private String fileId;
        private String fileName;
        private double latitude;
        private double longitude;
        private double altitude;
        private Integer mediaType;
        private LocalDateTime timeCreated;
        private Integer photoType;
        private Integer taskId;
        private LocalDateTime createTime;
    }
}
