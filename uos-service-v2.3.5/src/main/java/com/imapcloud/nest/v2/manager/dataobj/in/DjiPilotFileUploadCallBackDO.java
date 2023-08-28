package com.imapcloud.nest.v2.manager.dataobj.in;

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
public class DjiPilotFileUploadCallBackDO {

    private FileExtensionInDTO ext;

    private String fingerprint;

    private String name;

    private String path;

    private FileMetadataInDTO metadata;

    private String objectKey;

    private Integer subFileType;

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class FileExtensionInDTO {

        private String droneModelKey;

        private Boolean isOriginal;

        private String payloadModelKey;

        private String tinnyFingerprint;

        private String sn;

        private String flightId;

        private String fileGroupId;

    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class FileMetadataInDTO {

        private Double absoluteAltitude;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
        private Date createdTime;

        private Double gimbalYawDegree;

        private PositionInDTO photoedPosition;

        private PositionInDTO shootPosition;

        private Double relativeAltitude;
    }

    @Data
    public static class PositionInDTO {

        private Double lat;

        private Double lng;
    }
}
