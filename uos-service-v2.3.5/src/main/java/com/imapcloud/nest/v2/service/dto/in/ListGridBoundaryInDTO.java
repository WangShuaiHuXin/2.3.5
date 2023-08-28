package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wmin
 */

@Data
public class ListGridBoundaryInDTO {

    private BigDecimal maxLng;
    private BigDecimal minLng;
    private BigDecimal maxLat;
    private BigDecimal minLat;
}
