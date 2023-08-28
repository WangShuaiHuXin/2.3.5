package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class DataAnalysisStateSumOutPO implements Serializable {

    private Long centerBaseId;

    private Long centerDetailId;

    private Long photoId;

    private String sortDate;

    private Integer needAnalyzeSum;

    private Integer needConfirmProblemSum;

    private Integer needConfirmNoProblemSum;

    private Integer problemSum;

    private Integer noProblemSum;

}
