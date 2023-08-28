package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.nest.v2.manager.dataobj.BaseInDO;
import lombok.Data;
import lombok.ToString;

/**
 * 基站无人机关系
 *
 * @author boluo
 * @date 2022-08-25
 */
@ToString
public class BaseUavNestRefInDO {

    private BaseUavNestRefInDO() {}

    @Data
    public static class EntityInDO extends BaseInDO {
        /**
         * 无人机ID
         */
        private String uavId;

        /**
         * 基站ID
         */
        private String nestId;
    }
}
