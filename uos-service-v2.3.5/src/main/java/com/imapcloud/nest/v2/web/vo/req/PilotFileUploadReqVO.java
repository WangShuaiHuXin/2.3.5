package com.imapcloud.nest.v2.web.vo.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Date;

/**
 * @author sean
 * @version 0.2
 * @date 2021/12/7
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PilotFileUploadReqVO {

    private FileExtensionReqVO ext;

    private String fingerprint;

    private String name;

    private String path;

    private FileMetadataReqVO metadata;

    private String objectKey;

    private Integer subFileType;

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class FileExtensionReqVO {

        private String droneModelKey;

        private Boolean isOriginal;

        private String payloadModelKey;

        private String tinnyFingerprint;

        private String sn;

        private String flightId;

    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class FileMetadataReqVO {

        private Double absoluteAltitude;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
        private Date createdTime;

        private Double gimbalYawDegree;

        private PositionReqVO photoedPosition;

        private PositionReqVO shootPosition;

        private Double relativeAltitude;
    }

    @Data
    public static class PositionReqVO {

        private Double lat;

        private Double lng;
    }
}
