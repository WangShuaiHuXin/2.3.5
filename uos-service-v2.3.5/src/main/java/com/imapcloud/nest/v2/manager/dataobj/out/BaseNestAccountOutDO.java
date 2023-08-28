package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.util.List;
@Data
public class BaseNestAccountOutDO {

    private String accountId;


    private List<String> baseNestId;
}
