package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.nest.v2.manager.dataobj.BaseInDO;
import lombok.Data;
import lombok.ToString;

/**
 * 基站单位关系
 *
 * @author boluo
 * @date 2022-08-25
 */
@ToString
public class BaseNestOrgRefInDO {

    private BaseNestOrgRefInDO() {}

    @Data
    public static class BaseNestOrgRefEntityInDO extends BaseInDO {

        /**
         * 基站id
         */
        private String nestId;

        /**
         * 单位code
         */
        private String orgCode;
    }
}
