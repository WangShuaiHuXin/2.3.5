package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.json.JSONUtil;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.service.DataPanoramaRecordsService;
import com.imapcloud.nest.v2.service.PowerMeterDataService;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import com.imapcloud.nest.v2.web.transformer.DataPanoramaRecordsTransformer;
import com.imapcloud.nest.v2.web.vo.req.DataPanoramaRecordsReqVO;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 数据管理 全景-数据处理-打包下载
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "panorama_point_pic")
public class PanoramaPointPicHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private DataPanoramaRecordsService dataPanoramaRecordsService;

    @Resource
    private PowerMeterDataService powerMeterDataService;

    @Override
    protected boolean check(HandlerIn handlerIn) {
        DataPanoramaRecordsReqVO.PicReqVO picReqVO = toPicReqVO(handlerIn.getParam());
        String orgCode = powerMeterDataService.getOrgCodeByMissionRecordsId(picReqVO.getMissionRecordsId());
        if (orgCode == null) {
            return false;
        }
        return orgCode.startsWith(handlerIn.getOrgCode());
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {

        DataPanoramaRecordsReqVO.PicReqVO picReqVO = toPicReqVO(handlerIn.getParam());
        if (StringUtil.isEmpty(picReqVO.getMissionRecordsId())) {
            return false;
        }
        if (picReqVO.getAirPointIndex() == null) {
            return false;
        }
        return true;
    }

    private DataPanoramaRecordsReqVO.PicReqVO toPicReqVO(String param) {

        return JSONUtil.toBean(param, DataPanoramaRecordsReqVO.PicReqVO.class);
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {

        DataPanoramaRecordsReqVO.PicReqVO picReqVO = toPicReqVO(handlerIn.getParam());
        dataPanoramaRecordsService.downloadPointRecordsPic(DataPanoramaRecordsTransformer.INSTANCES.transform(picReqVO), response);
    }
}
