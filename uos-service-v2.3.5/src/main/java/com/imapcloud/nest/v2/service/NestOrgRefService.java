package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.out.NestOrgRefOutDTO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 基站-单位关系业务接口定义
 *
 * @author Vastfy
 * @date 2022/8/25 15:21
 * @since 2.0.0
 */
public interface NestOrgRefService {

    /**
     * 批量根据基站ID获取基站-单位关系
     * @param nestIds   基站ID
     * @param setOrgName   true：会填充单位名称
     * @return  基站-单位关系
     */
    List<NestOrgRefOutDTO> listNestOrgRefs(Collection<String> nestIds, boolean setOrgName);

    /**
     * 批量根据单位编码获取基站-单位关系
     * @param orgCodes   单位编码
     * @return  基站-单位关系
     */
    List<NestOrgRefOutDTO> fetchNestOrgRefs(Collection<String> orgCodes);

    /**
     * 获取单位下所有可见基站列表
     * @param orgCode   单位编码
     * @return  基站ID列表
     */
    Set<String> listOrgVisibleNestIds(String orgCode);

    /**
     * 兼容旧协议，暂时保留
     * @param orgCode   单位编码
     * @deprecated 2.0.0
     */
    @Deprecated
    List<Object> listOrgNestInfos(String orgCode);

    /**
     * 删除基站-单位关系
     * @param nestId    基站ID
     */
    void deleteNestOrgRefs(String nestId);

    /**
     * 更新基站-单位关系
     * 先删除，后插入最新（如果有）
     * @param nestId    基站ID
     * @param unitIds   单位ID列表
     */
    void updateNestOrgRefs(String nestId, List<String> unitIds);

}
