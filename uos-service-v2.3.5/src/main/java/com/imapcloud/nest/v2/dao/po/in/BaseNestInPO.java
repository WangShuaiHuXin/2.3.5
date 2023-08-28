package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 基站
 *
 * @author boluo
 * @date 2022-08-19
 */
@ToString
public class BaseNestInPO {

    private BaseNestInPO() {

    }

    @SuperBuilder
    public static class ListInPO extends QueryCriteriaDo<ListInPO> {

        /**
         * 用户所在的单位code
         */
        private String userOrgCode;

        /**
         * 单位code
         */
        private String orgCode;

        /**
         * 基站名称
         */
        private String name;

        /**
         * 基站编号
         */
        private String number;

        /**
         * 基站uuid
         */
        private String uuid;

        /**
         * 基站型号
         */
        private Integer type;

        /**
         * 区域id
         */
        private String regionId;

        /**
         * 关键字
         */
        private String keyword;

        /**
         * @since 2.3.2
         */
        private List<Integer> types;

        /**
         * @since 2.3.2
         */
        private Integer showStatus;

        private Integer uavType;

        /**
         * 飞行器型号
         */
        private Integer uavModel;

        /**
         * 飞行器类别
         */
        private Integer uavCate;
    }

}
