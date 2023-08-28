package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

/**
 * @Classname AirspaceUploadFileInDTO
 * @Description 空域上传批复文件信息类
 * @Date 2023/3/8 18:09
 * @Author Carnival
 */
@Data
public class AirspaceUploadFileInDTO {

    private String airspaceId;

//    private MultipartFile file;

    private String filePath;

}
