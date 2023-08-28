package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.pojo.dto.MiniAircraftInfoDto;
import com.imapcloud.nest.pojo.dto.MiniNestInfoDto;
import lombok.Data;

/**
 * @author wmin
 */
@Data
public class MiniNestAircraftInfoDto {
    private MiniNestInfoDto miniNestInfoDto;
    private MiniAircraftInfoDto miniAircraftInfoDto;
}
