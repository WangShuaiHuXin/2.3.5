package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ListDictItemInfoInDO {
    private String dictCode;
    private List<String> itemValues;
}
