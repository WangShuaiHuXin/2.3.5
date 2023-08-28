package com.imapcloud.nest.v2.service.impl;

import com.imapcloud.nest.v2.manager.cps.MotorManager;
import com.imapcloud.nest.v2.service.CpsMotorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * cps motor指令服务
 *
 * @author boluo
 * @date 2023-03-27
 */
@Service
public class CpsMotorServiceImpl implements CpsMotorService {

    @Resource
    private MotorManager motorManager;
}
