package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.mapper.SysTokenMapper;
import com.imapcloud.nest.model.SysTokenEntity;
import com.imapcloud.nest.service.SysTokenService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户token表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Service
public class SysTokenServiceImpl extends ServiceImpl<SysTokenMapper, SysTokenEntity> implements SysTokenService {

}
