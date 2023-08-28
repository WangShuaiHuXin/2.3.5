package com.imapcloud.nest.pojo.dto;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

@Data
public class ParseKmlResDTO {
    private JSONArray jsonArray;
    private Boolean absolute;
}
