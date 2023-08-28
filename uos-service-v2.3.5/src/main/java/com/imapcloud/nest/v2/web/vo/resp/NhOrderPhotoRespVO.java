package com.imapcloud.nest.v2.web.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class NhOrderPhotoRespVO implements Serializable {

    /**
     * mission-photo ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
    private int mediaType;
    private LocalDateTime timeCreated;
    private LocalDateTime createTime;
    private int photoType;
}
