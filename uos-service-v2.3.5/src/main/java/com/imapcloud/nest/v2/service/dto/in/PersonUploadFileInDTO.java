package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Classname PersonUploadFileInDTO
 * @Description 人员管理信息类
 * @Date 2023/3/28 14:03
 * @Author Carnival
 */
@Data
public class PersonUploadFileInDTO {

    private Integer uploadType;

    private MultipartFile file;
}
