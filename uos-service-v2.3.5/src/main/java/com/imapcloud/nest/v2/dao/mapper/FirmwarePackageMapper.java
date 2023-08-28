package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.FirmwarePackageEntity;
import com.imapcloud.nest.v2.dao.po.FirmwarePackageQueryCriteriaPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author Vastfy
 * @since 2022-07-12
 */
@Mapper
public interface FirmwarePackageMapper extends BaseMapper<FirmwarePackageEntity>,
        IPageMapper<FirmwarePackageEntity, FirmwarePackageQueryCriteriaPO, PagingRestrictDo> {

    /**
     * 批量逻辑删除
     * @param packageIds 安装包ID
     * @param operatorId 操作人ID
     * @return  影像记录数
     */
    int logicDeleteBatch(@Param("packageIds") List<String> packageIds, @Param("operatorId") Long operatorId);
}
