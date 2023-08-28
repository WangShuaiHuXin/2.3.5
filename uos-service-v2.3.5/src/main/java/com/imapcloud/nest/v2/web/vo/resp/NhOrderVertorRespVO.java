package com.imapcloud.nest.v2.web.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("南海工单-查询矢量信息")
public class NhOrderVertorRespVO implements Serializable {

    private String vectorId;

    private String name;

    private int index;

    private List<String> pointList;

    private Integer type;
}
