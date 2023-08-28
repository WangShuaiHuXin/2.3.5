package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.imapcloud.nest.service.BatteryService;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestAccountOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestAccountManager;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 基站管理 设备维保-导出电池信息
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "battery")
public class BatteryHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private BatteryService batteryService;

    @Resource
    private BaseNestAccountManager baseNestAccountManager;

    @Override
    protected boolean check(HandlerIn handlerIn) {
        BaseNestAccountOutDO outDO = baseNestAccountManager.selectByUserId(handlerIn.getAccountId());
        if (ObjectUtils.isEmpty(outDO) || CollectionUtil.isEmpty(outDO.getBaseNestId())) {
            return false;
        }
        Bean bean = toBean(handlerIn);
        List<String> baseNestIdList = outDO.getBaseNestId();
        return baseNestIdList.contains(bean.nestId);
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn);
        return StringUtil.isNotEmpty(bean.nestId);
    }

    private Bean toBean(HandlerIn handlerIn) {

        return JSONUtil.toBean(handlerIn.getParam(), Bean.class);
    }

    @Data
    private static class Bean {
        private String nestId;
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {

        Bean bean = toBean(handlerIn);
        batteryService.exportBatteryUseNums(bean.nestId, response);
    }
}
