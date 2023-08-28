package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import scala.Int;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NhOrderVideoOutDTO {

    private Long total;
    private List<vidoeInfo> infoList;

    @Data
    public static class vidoeInfo {
        private Long id;
        private String name;
        private String alias;
        private String videoUrl;
        private Integer type;
        private Integer recordStatus;
        private Integer missionId;
        private Integer missionRecordsId;
        private String execId;
        private Integer createUserId;
        private LocalDateTime createTime;
        private LocalDateTime modifyTime;
        private boolean deleted;
        private boolean physicalDeleted;
        private Double lat;
        private Double lng;
        private String unitId;
        private String orgCode;
        private Integer tagVersion;
        private String srtJson;
        private String srtUrl;
    }

}
