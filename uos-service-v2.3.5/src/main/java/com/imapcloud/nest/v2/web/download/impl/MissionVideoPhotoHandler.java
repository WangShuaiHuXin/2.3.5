package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.json.JSONUtil;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.service.MissionVideoPhotoService;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import io.jsonwebtoken.lang.Strings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 数据管理 视频抽帧-下载照片
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "mission_video_photo")
public class MissionVideoPhotoHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private MissionVideoPhotoService missionVideoPhotoService;

    @Override
    protected boolean check(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn.getParam());
        String[] ids = Strings.commaDelimitedListToStringArray(bean.ids);
        int num = missionVideoPhotoService.selectNum(ids, handlerIn.getOrgCode());
        return num == ids.length;
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn.getParam());
        if (ToolUtil.isEmpty(bean.ids)) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_PLEASE_SELECT_THE_CORRECT_PICTURE.getContent()));
        }
        return true;
    }

    private Bean toBean(String param) {

        return JSONUtil.toBean(param, Bean.class);
    }

    @Data
    private static class Bean {
        private String ids;
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {

        Bean bean = toBean(handlerIn.getParam());
        missionVideoPhotoService.downlandPic(bean.ids, response);
    }
}
