package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.dao.entity.PowerDiscernSettingEntity;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerDiscernFunSettingInfosOutDO;

import java.util.List;

public interface PowerDataManager {
    List<PowerDiscernFunSettingInfosOutDO> queryOrgFunctionSettings(String orgCode, String discernType);

    PowerDiscernSettingEntity queryOrgDiscernFunctionSettings(String orgCode);

    int updateOrgDiscernFunctionSettings(String orgCode,boolean flag);

    void deleteOrgFunctionSettings(String orgCode, String valueOf);

    void saveOrgFunctionSettings(String orgCode, Integer discernType, List<String> discernFunctionIds);

    void saveOrgDiscernSettings(String orgCode, boolean b);

    /**
     * 单位开启自动识别判定
     *
     * @param orgCode 组织代码
     * @return boolean
     */
    boolean autoDiscern(String orgCode);
}
