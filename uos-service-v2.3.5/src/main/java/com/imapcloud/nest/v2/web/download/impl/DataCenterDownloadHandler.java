package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.json.JSONUtil;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据管理
 * 自动巡检-照片、视频选择任务下载
 * 人工上传-照片、视频选择任务下载
 * 人工上传-照片打包下载
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "data_center_download")
public class DataCenterDownloadHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private DataCenterService dataCenterService;

    @Resource
    private PowerMeterDataService powerMeterDataService;

    @Override
    protected boolean check(HandlerIn handlerIn) {

        Bean bean = toBean(handlerIn.getParam());

        List<Integer> missionRecordIdList = Arrays.stream(bean.getMissionRecordIds().split(","))
                .map(Integer::parseInt).collect(Collectors.toList());
        for (Integer integer : missionRecordIdList) {
            String orgCode = powerMeterDataService.getOrgCodeByMissionRecordsId(String.valueOf(integer));
            if (orgCode == null || !orgCode.startsWith(handlerIn.getOrgCode())) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn.getParam());
        if (bean.getDataType() == null) {
            return false;
        }
        if (StringUtil.isEmpty(bean.getMissionRecordIds())) {
            return false;
        }
        return true;
    }

    private Bean toBean(String param) {

        return JSONUtil.toBean(param, Bean.class);
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Data
    private static class Bean {
        private Integer dataType;
        private String missionRecordIds;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {

        Bean bean = toBean(handlerIn.getParam());
        List<Integer> missionRecordIdList = Arrays.stream(bean.getMissionRecordIds().split(",")).map(Integer::parseInt).collect(Collectors.toList());
        dataCenterService.downloadByRecordIds(bean.getDataType(), missionRecordIdList, response);
    }
}
