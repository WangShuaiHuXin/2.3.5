package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.json.JSONUtil;
import com.imapcloud.nest.service.DataCenterService;
import com.imapcloud.nest.service.MissionVideoService;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 数据管理 人工上传-视频下载轨迹
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "data_center_data_srt")
public class DataCenterDataSrtHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private DataCenterService dataCenterService;

    @Resource
    private MissionVideoService missionVideoServicel;

    @Override
    protected boolean check(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn.getParam());
        String orgCode = handlerIn.getOrgCode();
        int num = missionVideoServicel.selectNum(bean.videoId, orgCode);
        return num > 0;
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn.getParam());
        return bean.getVideoId() != null;
    }

    private Bean toBean(String param) {
        return JSONUtil.toBean(param, Bean.class);
    }

    @Data
    private static class Bean {

        private Integer videoId;
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {

        Bean bean = toBean(handlerIn.getParam());
        dataCenterService.downloadSrt(bean.videoId, response);
    }
}
