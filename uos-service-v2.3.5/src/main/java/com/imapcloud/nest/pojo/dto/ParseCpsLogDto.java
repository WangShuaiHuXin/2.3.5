package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ParseCpsLogDto {

    @NotNull
    private String nestId;
    @NotNull
    private List<String> pathList;
}
