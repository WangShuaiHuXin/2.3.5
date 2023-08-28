package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.RegionEntity;
import com.imapcloud.nest.utils.RestRes;

import java.util.List;

/**
 * <p>
 * 架次表 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
public interface RegionService extends IService<RegionEntity> {

    /**
     * 通过用userName查询SysUser
     *
     * @param userName
     * @return
     */
    RegionEntity queryByName(String userName);

    IPage<RegionEntity> listRegionByPages(Integer pageNo, Integer pageSize, String name);

    /**
     * 批量删除区域，设置为默认分组id
     * @param idList
     * @return
     */
    RestRes batchDeleteRegions (List<Integer> idList);

    /**
     * 查询所以的区域
     *
     * @return
     */
    List<RegionEntity> getAllRegion();

    /**
     * 查询id和名字
     *
     * @param idList
     * @return
     */
    List<RegionEntity> listIdAndNameByIdList(List<Integer> idList);
}
