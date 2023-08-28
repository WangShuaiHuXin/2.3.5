package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.json.JSONUtil;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.service.AirspaceManageService;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 系统设置 空域管理-下载批复文件
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "download_airspace_approval")
public class AirspaceApprovalFileDownloadHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private AirspaceManageService airspaceManageService;

    @Override
    protected boolean check(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn.getParam());
        int num = airspaceManageService.selectNum(bean.airspaceId, handlerIn.getOrgCode());
        return num == 1;
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        String param = handlerIn.getParam();
        if (StringUtils.isEmpty(param)) {
            return false;
        }
        Bean bean = toBean(param);
        if (ObjectUtils.isEmpty(bean) || StringUtils.isEmpty(bean.airspaceId)) {
            return false;
        }
        return true;
    }

    private AirspaceApprovalFileDownloadHandler.Bean toBean(String param) {
        return JSONUtil.toBean(param, AirspaceApprovalFileDownloadHandler.Bean.class);
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {
        Bean bean = toBean(handlerIn.getParam());
        airspaceManageService.exportApprovalFile(response, bean.airspaceId);
    }

    @Data
    private static class Bean {
        private String airspaceId;
    }
}
