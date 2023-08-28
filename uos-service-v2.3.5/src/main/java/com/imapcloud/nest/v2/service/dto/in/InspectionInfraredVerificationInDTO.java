package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.math.BigDecimal;

/*
红外核实画图入参
 */
@Data
public class InspectionInfraredVerificationInDTO {
    private BigDecimal x1;
    private BigDecimal x2;
    private BigDecimal y1;
    private BigDecimal y2;

    private BigDecimal max;
    private BigDecimal min;
    private BigDecimal avg;



    private  BigDecimal maxX;
    private  BigDecimal maxY;


    private BigDecimal minX;
    private BigDecimal minY;
}
