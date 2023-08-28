package com.imapcloud.nest.v2.dao.po.in;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 缺陷识别
 *
 * @author boluo
 * @date 2023-03-10
 */
@ToString
public class PowerMeterFlightDetailDefectInPO {

    private PowerMeterFlightDetailDefectInPO() {}

    @Data
    public static class DefectStateInPO {

        private String paramDetailId;

        /**
         * 参数
         */
        private List<Integer> paramTaskStateList;

        private int deviceState;

        private int defectState;

        private String reason;

        /**
         * 任务状态 可以不更新
         */
        private Integer taskState;
    }
}
