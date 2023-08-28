package com.imapcloud.nest.v2.service.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;
@Data
public class NhQueryVectorOutDTO {

    private String vectorId;

    private String name;

    private int index;

    private List<String> points;

    private Integer type;
}
