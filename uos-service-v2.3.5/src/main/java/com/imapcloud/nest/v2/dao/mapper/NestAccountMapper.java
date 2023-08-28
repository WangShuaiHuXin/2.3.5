package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.NestAccountEntity;
import com.imapcloud.nest.v2.dao.po.NestAccountQueryCriteriaPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 基站账户映射器
 *
 * @author boluo
 * @date 2022-05-23
 */
@Mapper
public interface NestAccountMapper extends BaseMapper<NestAccountEntity>, IPageMapper<NestAccountEntity, NestAccountQueryCriteriaPO, PagingRestrictDo> {

    int insertBatch(@Param("records") List<NestAccountEntity> entities);

    List<String> selectNestUuidByAccountId(Long accountId);

}
