package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;
import lombok.ToString;

/**
 * 基站
 *
 * @author boluo
 * @date 2022-08-19
 */
@ToString
public class BaseNestOutPO {

    private BaseNestOutPO() {

    }

    @Data
    public static class ListOutPO {

        /**
         * 基站业务ID
         */
        private String nestId;

        /**
         * 机巢uuid
         */
        private String uuid;

        /**
         * 机巢名称
         */
        private String name;

        /**
         * 机巢编号
         */
        private String number;

        /**
         * 类型(0-P4R固定基站；1-mini1代；2-M300；3-简易基站；4-车载基站；5-mini2代)
         */
        private Integer type;

        /**
         * 区域id
         */
        private String regionId;

        /**
         * 维度
         */
        private Double latitude;

        /**
         * 经度
         */
        private Double longitude;

        /**
         * 高度
         */
        private Double altitude;
    }
}
