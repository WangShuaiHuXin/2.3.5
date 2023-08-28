package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.service.dto.UnitEntityDTO;
import com.imapcloud.nest.v2.service.dto.UnitNodeDTO;

import java.util.List;

/**
 * <p>
 * 单位信息表 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface SysUnitService {

    IPage<UnitEntityDTO> listSysUnitByPages(Integer pageNo, Integer pageSize, String name);

    /**
     * 获取上级单位ID列表
     * @param orgCode    单位ID
     * @return  上级单位ID列表
     */
    List<String> getSuperiorOrgCodes(String orgCode);

    /**
     * 获取单位
     * @param unitIds    单位ID
     * @return  获取单位节点信息
     */
    List<UnitNodeDTO> getUnitNodeInfos(List<String> unitIds);

    /**
     * 根据单位编码获取单位简要信息
     * @param orgCode   单位便阿门
     * @return  信息
     */
    UnitEntityDTO getById(String orgCode);

}
