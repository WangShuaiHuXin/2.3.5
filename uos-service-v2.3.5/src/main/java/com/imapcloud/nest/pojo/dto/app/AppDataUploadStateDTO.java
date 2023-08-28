package com.imapcloud.nest.pojo.dto.app;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class AppDataUploadStateDTO {
    private Boolean finish;
    private List<Integer> recordIds;
    private Integer messageType;
    private String token;
}
