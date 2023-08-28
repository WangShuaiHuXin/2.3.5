package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.SysAppEntity;

/**
 * <p>
 * 终端信息表 Mapper 接口
 * </p>
 *
 * @author kings
 * @since 2020-10-26
 */
public interface SysAppMapper extends BaseMapper<SysAppEntity> {

    SysAppEntity getInfoById(Integer appId);

}
