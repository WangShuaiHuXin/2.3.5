package com.imapcloud.nest.pojo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * app上传图片
 *
 * @author wmin
 */
@Data
public class AppUploadPhotoDto {
    @NotNull
    private Integer missionIndex;
    @NotNull
    private Integer recordId;
    @NotNull
    private String unitId;
    @NotNull
    private String deviceId;
    @NotNull
    private MultipartFile file;
}
