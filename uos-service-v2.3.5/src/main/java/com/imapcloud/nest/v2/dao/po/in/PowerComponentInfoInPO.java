package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 部件
 *
 * @author boluo
 * @date 2022-11-28
 */
@ToString
public class PowerComponentInfoInPO {

    private PowerComponentInfoInPO() {}

    @Data
    public static class ListInPO extends PageInfo {

        private String orgCode;

        private String equipmentType;

        private String componentName;

        private LocalDateTime start;

        private LocalDateTime end;

        /**
         * 巡检类型
         */
        private Integer analysisType;

        private int offset;

        private int limit;
    }
}
