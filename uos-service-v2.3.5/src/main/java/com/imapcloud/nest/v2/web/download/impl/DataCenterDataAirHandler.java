package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.imapcloud.nest.service.DataCenterService;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.service.PowerMeterDataService;
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
 * 数据管理 人工上传-气体导出报告
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "data_center_data_air")
public class DataCenterDataAirHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private DataCenterService dataCenterService;

    @Resource
    private PowerMeterDataService powerMeterDataService;

    @Override
    protected boolean check(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn.getParam());
        JSONObject dataObject = JSONObject.parseObject(getDataStr(bean));
        Integer missionRecordsId = dataObject.getInteger("missionRecordsId");

        String orgCode = powerMeterDataService.getOrgCodeByMissionRecordsId(String.valueOf(missionRecordsId));
        if (orgCode == null) {
            return false;
        }
        return orgCode.startsWith(handlerIn.getOrgCode());
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn.getParam());
        return StringUtil.isNotEmpty(bean.dataStr);
    }

    private Bean toBean(String param) {
        return JSONUtil.toBean(param, Bean.class);
    }

    @Data
    private static class Bean {

        private String dataStr;
    }

    private String getDataStr(Bean bean) {
        return URLDecoder.decode(bean.dataStr, CharsetUtil.CHARSET_UTF_8);
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {

        Bean bean = toBean(handlerIn.getParam());
        dataCenterService.exportDataAir(getDataStr(bean), response);
    }
}
