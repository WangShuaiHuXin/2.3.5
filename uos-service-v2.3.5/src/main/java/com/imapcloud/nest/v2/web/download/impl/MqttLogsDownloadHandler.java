package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.imapcloud.nest.service.MqttLogParseService;
import com.imapcloud.nest.utils.JsonUtil;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.regexp.RE;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * 系统设置 日志解析-导出
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "download_mqtt_logs")
public class MqttLogsDownloadHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private MqttLogParseService mqttLogParseService;

    @Resource
    private RedisService redisService;

    @Override
    protected boolean check(HandlerIn handlerIn) {
        return true;
    }

    private MqttLogsDownloadHandler.LogsBean toBean(String param) {
        return JSONUtil.toBean(param, MqttLogsDownloadHandler.LogsBean.class);
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        String param = handlerIn.getParam();
        LogsBean bean = toBean(param);
        if (StringUtils.isEmpty(param)) {
            return false;
        }
        if (ObjectUtils.isEmpty(bean.startTime) || ObjectUtils.isEmpty(bean.endTime)) {
            return false;
        }
        return true;
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {
        LogsBean bean = toBean(handlerIn.getParam());
        mqttLogParseService.exportMqttLogs(bean.getStartTime(), bean.getEndTime(), response);
    }

    @Data
    private static class LogsBean {
        private Long startTime;
        private Long endTime;
    }

}
