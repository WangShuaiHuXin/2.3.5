package com.imapcloud.nest.v2.dao.mapper;

import com.imapcloud.nest.v2.dao.entity.PowerMeterReadingValueEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 电力-表计读数-数据详情读数表 Mapper 接口
 * </p>
 *
 * @author vastfy
 * @since 2.1.5
 */
@Mapper
public interface PowerMeterReadingValueMapper extends BaseBatchMapper<PowerMeterReadingValueEntity> {


    List<PowerMeterReadingValueEntity> selecByValueIds(@Param("list") List<String> valueIds);

    /**
     * 逻辑删除
     *
     * @param detailIds 详细id
     * @return int
     */
    int deleteByDetailIdList(@Param("detailIdList") List<String> detailIds);
}
