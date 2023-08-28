package com.imapcloud.nest.common.aspect;

import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

@Data
public class TestNestAnno {

    @NestId
    private String nestIdDD;
}
