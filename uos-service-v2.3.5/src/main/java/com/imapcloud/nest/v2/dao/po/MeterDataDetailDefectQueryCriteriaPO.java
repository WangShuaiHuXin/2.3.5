package com.imapcloud.nest.v2.dao.po;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 缺陷识别分页
 *
 * @author boluo
 * @date 2023-03-07
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MeterDataDetailDefectQueryCriteriaPO extends QueryCriteriaDo<MeterDataDetailDefectQueryCriteriaPO> {

    private String dataId;

    private Integer deviceState;

    /**
     * 缺陷识别状态
     */
    private Integer defectState;

    private Integer verificationStatus;


    private List<String> detailIds;
}
