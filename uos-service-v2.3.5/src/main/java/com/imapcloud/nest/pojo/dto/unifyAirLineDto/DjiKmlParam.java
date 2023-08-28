package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import com.imapcloud.sdk.pojo.constant.HeadingModeEnum;
import lombok.Data;

/**
 * @author wmin
 */
@Data
public class DjiKmlParam {
    private Boolean absolute;
    private Integer autoFlightSpeed;
    private Integer speed;
    private HeadingModeEnum headingMode;
    private Double takeOffLandAlt;

}
