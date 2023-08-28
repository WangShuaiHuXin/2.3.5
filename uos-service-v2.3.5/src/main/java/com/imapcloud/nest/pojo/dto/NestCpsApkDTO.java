package com.imapcloud.nest.pojo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @author wmin
 */
@Data
@Accessors(chain = true)
public class NestCpsApkDTO {
    private Integer nestId;
    private String nestUuid;
    private String nestName;
    private Integer nestType;
    private String cpsVersion;
    private Integer cpsUpdateState;
    private List<Record> updateRecords;

    @Data
    @Accessors(chain = true)
    public static class Record {
        private String record;
        private String time;
        @JsonIgnore
        private String version;
        private String state;
    }
}
