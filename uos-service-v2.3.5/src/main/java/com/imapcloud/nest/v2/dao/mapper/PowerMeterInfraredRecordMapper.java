package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.PowerMeterInfraredRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 红外测温-测温详情表
 *
 * @author boluo
 * @date 2022-12-28
 */
@Mapper
public interface PowerMeterInfraredRecordMapper extends BaseMapper<PowerMeterInfraredRecordEntity> {

    /**
     * 逻辑删除
     *
     * @param detailIdList  详细id
     * @param accountId 帐户id
     * @return int
     */
    int deleteByDetailId(@Param("detailId") String detailId, @Param("accountId") String accountId);

    List<PowerMeterInfraredRecordEntity> selectInfraredValueByValueIds(@Param("list") List<String> valueIds);
    int deleteByDetailIdList(@Param("detailIdList") List<String> detailIdList, @Param("accountId") String accountId);


    List<PowerMeterInfraredRecordEntity> selectInfraredValueByValueIdsNotDelete(List<String> valueIds);

    /**
     * 批量保存
     *
     * @param powerMeterInfraredRecordEntityList 功率计红外记录实体列表
     * @return int
     */
    int batchSave(@Param("entityList") List<PowerMeterInfraredRecordEntity> powerMeterInfraredRecordEntityList);

    /**
     * 红外记录id删除
     *
     * @param infraredRecordId 红外记录id
     */
    int deleteByInfraredRecordId(@Param("infraredRecordId") String infraredRecordId);
}
