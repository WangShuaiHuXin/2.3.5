package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class UavInfoOutDTO extends BaseUavInfoOutDTO {

    private NestBasicOutDTO nestInfo;

}
