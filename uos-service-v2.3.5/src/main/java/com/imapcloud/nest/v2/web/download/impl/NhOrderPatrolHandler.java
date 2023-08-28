package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.json.JSONUtil;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.service.NhWorkOrderService;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 任务工单 巡检报告-下载
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "nh_order_patrol")
public class NhOrderPatrolHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private NhWorkOrderService nhWorkOrderService;

    @Override
    protected boolean check(HandlerIn handlerIn) {
        return true;
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn);
        return StringUtil.isNotEmpty(bean.reportId);
    }

    private Bean toBean(HandlerIn handlerIn) {
        return JSONUtil.toBean(handlerIn.getParam(), Bean.class);
    }

    @Data
    private static class Bean {
        private String reportId;
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {

        Bean bean = toBean(handlerIn);
        nhWorkOrderService.exportPatrolReport(bean.reportId, response);
    }
}
