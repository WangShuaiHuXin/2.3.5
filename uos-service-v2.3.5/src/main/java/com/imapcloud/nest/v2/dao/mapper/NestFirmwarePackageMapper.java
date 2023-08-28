package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.NestFirmwarePackageEntity;
import com.imapcloud.nest.v2.dao.po.NestFirmwarePackageQueryCriteriaPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 基站固件更新记录表 Mapper 接口
 * </p>
 *
 * @author Vastfy
 * @since 2022-07-12
 */
@Mapper
public interface NestFirmwarePackageMapper extends BaseMapper<NestFirmwarePackageEntity>,
        IPageMapper<NestFirmwarePackageEntity, NestFirmwarePackageQueryCriteriaPO, PagingRestrictDo> {

    /**
     * 获取基站各类型固件安装包最新更新记录ID
     */
    List<Long> listNestLatestFprIds(@Param("nestIds") Collection<String> nestIds);

}
