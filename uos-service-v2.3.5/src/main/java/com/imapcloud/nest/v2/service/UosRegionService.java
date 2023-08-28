package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.UosRegionCreationInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosRegionModificationInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosRegionPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRegionInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRegionQueryInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRegionSimpleOutDTO;

import java.util.List;
import java.util.Map;


/**
 * @Classname UosRegionSerivce
 * @Description 区域服务接口定义
 * @Date 2022/8/11 10:41
 * @Author Carnival
 */
public interface UosRegionService {

    /**
     * 新建区域
     */
    String addRegion(UosRegionCreationInDTO regionCreationInDTO);

    /**
     * 删除区域
     */
    boolean deleteRegion(String regionId);

    /**
     * 删除所有区域
     */
    List<String> deleteBatchRegion(List<String> regionIds);


    /**
     * 修改区域信息
     */
    Boolean modifyRegionInfo(String regionId, UosRegionModificationInDTO regionModificationInDTO);


    /**
     * 查询区域详细信息
     */
    UosRegionQueryInfoOutDTO queryRegionInfo(String regionId);


    /**
     * 分页查询区域列表
     */
    PageResultInfo<UosRegionQueryInfoOutDTO> pageRegionList(UosRegionPageInDTO regionQueryReqVO);


    /**
     * 获取区域简要信息
     */
    List<UosRegionSimpleOutDTO> listRegionSimpleInfo();

    /**
     * 分页获取区域简要信息
     */
    PageResultInfo<UosRegionSimpleOutDTO> pageRegionSimpleInfo(UosRegionPageInDTO uosRegionPageInDTO);


    /**
     * 批量查询
     * @param regionIdList
     * @return
     */
    List<UosRegionInfoOutDTO> listRegionInfos(List<String> regionIdList);

    Map<String, String> getRegionNameMap(List<String> regionIdList);
}
