package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.json.JSONUtil;
import com.imapcloud.nest.service.DataCenterService;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
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
 * 数据管理
 * 人工上传-正射、点云、倾斜、矢量选择任务下载
 * 人工上传-倾斜、矢量打包下载
 * 人工上传-倾斜、矢量下载
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "data_center_data")
public class DataCenterDataHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private DataCenterService dataCenterService;

    @Override
    protected boolean check(HandlerIn handlerIn) {

        Bean bean = toBean(handlerIn.getParam());
        String orgCode = dataCenterService.getOrgCode(bean.dataType, bean.missionRecordId);
        if (orgCode == null) {
            return false;
        }
        return orgCode.startsWith(handlerIn.getOrgCode());
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn.getParam());
        if (bean.getDataType() == null) {
            return false;
        }
        if (bean.getMissionRecordId() == null) {
            return false;
        }
        return true;
    }

    private Bean toBean(String param) {

        return JSONUtil.toBean(param, Bean.class);
    }

    @Data
    private static class Bean {
        private Integer dataType;
        private Integer missionRecordId;
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {

        Bean bean = toBean(handlerIn.getParam());
        dataCenterService.downloadData(bean.dataType, bean.missionRecordId, response);
    }
}
