package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DataAnalysisStateSumOutDTO implements Serializable {

    private Long centerBaseId;

    private Long centerDetailId;

    private Long photoId;

    private String sortDate;

    private Integer needAnalyzeSum;

    private Integer needConfirmProblemSum;

    private Integer needConfirmNoProblemSum;

    private Integer problemSum;

    private Integer noProblemSum;

    private Integer hadAnalyzeSum;

    private Integer hadFoundProblemSum;

}
