package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;

import java.util.List;
@Data
public class NhQueryVectorsOutPO {
    private String vectorId;

    private String name;

    private int index;

    private List<String> points;

    private Integer type;
}
