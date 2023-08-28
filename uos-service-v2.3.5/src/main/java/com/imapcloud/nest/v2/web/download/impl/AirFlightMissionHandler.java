package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.imapcloud.nest.service.FlightMissionService;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestAccountOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestAccountManager;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import io.jsonwebtoken.lang.Strings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 基站管理  飞行统计-导出记录
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "air_flight_mission")
public class AirFlightMissionHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private FlightMissionService flightMissionService;

    @Resource
    private BaseNestAccountManager baseNestAccountManager;

    @Override
    protected boolean check(HandlerIn handlerIn) {
        BaseNestAccountOutDO outDO = baseNestAccountManager.selectByUserId(handlerIn.getAccountId());
        if (ObjectUtils.isEmpty(outDO) || CollectionUtil.isEmpty(outDO.getBaseNestId())) {
            return false;
        }
        Bean bean = toBean(handlerIn);
        String[] idsArray = Strings.commaDelimitedListToStringArray(bean.nestIds);
        List<String> nestIdList = outDO.getBaseNestId();
        for (String id : idsArray) {
           if(!nestIdList.contains(id)){
               return false;
           }
        }
        return true;
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        return true;
    }

    private Bean toBean(HandlerIn handlerIn) {
        return JSONUtil.toBean(handlerIn.getParam(), Bean.class);
    }

    @Data
    private static class Bean {
        private String nestIds;
        private String startTime;
        private String endTime;
        private Integer uavWhich;
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {

        Bean bean = toBean(handlerIn);
        flightMissionService.exportFlightMissionRecord(bean.nestIds, bean.startTime, bean.endTime, bean.uavWhich, response);
    }
}
