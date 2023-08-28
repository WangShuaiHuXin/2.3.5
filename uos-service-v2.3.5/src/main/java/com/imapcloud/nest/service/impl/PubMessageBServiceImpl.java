package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.mapper.PubMessageBMapper;
import com.imapcloud.nest.model.PubMessageBodyEntity;
import com.imapcloud.nest.service.PubMessageBService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author double
 * @since 2022-03-22
 */
@Service
public class PubMessageBServiceImpl extends ServiceImpl<PubMessageBMapper, PubMessageBodyEntity> implements PubMessageBService {

}
