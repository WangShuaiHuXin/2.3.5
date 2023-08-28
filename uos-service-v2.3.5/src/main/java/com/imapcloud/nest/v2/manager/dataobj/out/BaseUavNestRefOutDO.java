package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;
import lombok.ToString;

/**
 * 基站无人机关系
 *
 * @author boluo
 * @date 2022-08-25
 */
@ToString
public class BaseUavNestRefOutDO {

    private BaseUavNestRefOutDO() {}

    @Data
    public static class EntityOutDO {
        /**
         * 无人机ID
         */
        private String uavId;

        /**
         * 基站ID
         */
        private String nestId;

        /**
         * 推拉流信息
         */
        private String streamId;
    }
}
