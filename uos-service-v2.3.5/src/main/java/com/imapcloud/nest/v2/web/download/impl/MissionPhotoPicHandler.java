package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.json.JSONUtil;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.PowerMeterDataService;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 数据管理
 * 自动巡检-照片打包下载
 * 自动巡检-照片批量下载
 * 网格-任务查看-打包下载
 * 人工上传-照片批量下载
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "mission_photo_pic")
public class MissionPhotoPicHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private MissionPhotoService missionPhotoService;

    @Resource
    private PowerMeterDataService powerMeterDataService;

    @Override
    protected boolean check(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn.getParam());
        String orgCode = powerMeterDataService.getOrgCodeByMissionRecordsId(String.valueOf(bean.missionRecordsId));
        if (orgCode == null) {
            return false;
        }
        return orgCode.startsWith(handlerIn.getOrgCode());
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn.getParam());
        String ids = bean.ids;
        boolean isAll = bean.isAll;
        if (ToolUtil.isEmpty(ids) && !isAll) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_PLEASE_SELECT_THE_CORRECT_PICTURE.getContent()));
        }
        if (ToolUtil.isNotEmpty(ids) && isAll) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_PLEASE_SELECT_THE_CORRECT_PICTURE.getContent()));
        }
        if (bean.getMissionRecordsId() == null) {
            return false;
        }
        return true;
    }

    private Bean toBean(String param) {

        return JSONUtil.toBean(param, Bean.class);
    }

    @Data
    private static class Bean {

        private String ids;
        private boolean isAll;
        private Integer missionRecordsId;
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {

        Bean bean = toBean(handlerIn.getParam());
        missionPhotoService.downlandPic(bean.ids, bean.isAll, bean.missionRecordsId, response);
    }
}
