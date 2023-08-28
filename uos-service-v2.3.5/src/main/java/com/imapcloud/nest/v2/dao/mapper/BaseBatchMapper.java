package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName BaseBatchMapper.java
 * @Description BaseBatchMapper
 * @createTime 2022年07月19日 17:14:00
 */
public interface BaseBatchMapper<T> extends BaseMapper<T> {

    /**
     * 自定义批量插入
     * 如果要自动填充，@Param(xx) xx参数名必须是 list/collection/array 3个的其中之一
     * @param list
     * @return
     */
    int saveBatch(@Param("list") List<T> list);

    /**
     * 自定义批量新增或更新
     * 如果要自动填充，@Param(xx) xx参数名必须是 list/collection/array 3个的其中之一
     * @param list
     * @return
     */
    int saveOrUpdateBath(@Param("list") List<T> list);

}
