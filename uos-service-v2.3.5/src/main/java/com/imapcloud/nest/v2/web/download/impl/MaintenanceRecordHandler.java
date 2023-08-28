package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.imapcloud.nest.service.NestMaintenanceService;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestAccountOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestAccountManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import com.imapcloud.sdk.utils.StringUtil;
import io.jsonwebtoken.lang.Strings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 基站管理 飞行统计-导出记录
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "maintenance_record")
public class MaintenanceRecordHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private NestMaintenanceService nestMaintenanceService;

    @Resource
    private BaseNestAccountManager baseNestAccountManager;

    @Override
    protected boolean check(HandlerIn handlerIn) {
        BaseNestAccountOutDO outDO = baseNestAccountManager.selectByUserId(handlerIn.getAccountId());
        if (ObjectUtils.isEmpty(outDO) || CollectionUtil.isEmpty(outDO.getBaseNestId())) {
            return false;
        }
        Bean bean = toBean(handlerIn);
        List<String> baseNestIdList = outDO.getBaseNestId();
        String[] idsArray = Strings.commaDelimitedListToStringArray(bean.nestIds);
        for (String id : idsArray) {
            if (!baseNestIdList.contains(id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {

        Bean bean = toBean(handlerIn);
        if (StringUtil.isEmpty(bean.nestIds)) {
            return false;
        }
        if (StringUtil.isEmpty(bean.startTime)) {
            return false;
        }
        if (StringUtil.isEmpty(bean.endTime)) {
            return false;
        }
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
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {

        Bean bean = toBean(handlerIn);
        nestMaintenanceService.exportNestMaintenanceRecord(bean.nestIds, bean.startTime, bean.endTime, response);
    }
}
