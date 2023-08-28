package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.BaseNestTypeInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestTypeInfoOutDO;

import java.util.List;

/**
 * 基站类型
 *
 * @author boluo
 * @date 2023-03-31
 */
public interface BaseNestTypeInfoManager {

    /**
     * 编辑
     *
     * @param baseNestTypeInfoInDO 嵌套类型信息在做基础
     */
    void edit(BaseNestTypeInfoInDO baseNestTypeInfoInDO);

    /**
     * 查询
     *
     * @return {@link BaseNestTypeInfoOutDO}
     */
    List<BaseNestTypeInfoOutDO> selectAll();
}
