package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class NestCodeOperationOutDTO implements Serializable {
    private static final long serialVersionUID = 8097987825963930816L;
    private String nestCode;
    private String creatorId;
    private LocalDateTime creatorTime;
}
