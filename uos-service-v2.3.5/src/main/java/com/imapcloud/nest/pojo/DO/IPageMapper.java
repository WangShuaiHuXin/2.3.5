package com.imapcloud.nest.pojo.DO;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Vastfy
 * @date 2022/04/20 14:24
 * @since 1.8.9
 */
public interface IPageMapper<R, Q extends QueryCriteriaDo<Q>, P extends PagingRestrictDo> {

    /**
     * 根据条件统计数量
     * @param queryCriteria 查询条件
     * @return  数量
     */
    long countByCondition(@Param("criteria") Q queryCriteria);

    /**
     * 根据条件查询数据
     * @param queryCriteria 查询条件
     * @param pagingRestrict    分页限制
     * @return  数据
     */
    List<R> selectByCondition(@Param("criteria") Q queryCriteria, @Param("restrict") P pagingRestrict);

}
