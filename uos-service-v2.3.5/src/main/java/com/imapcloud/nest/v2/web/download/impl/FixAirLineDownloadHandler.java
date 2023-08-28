package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.imapcloud.nest.service.FixAirLineService;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestAccountOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestAccountManager;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import com.imapcloud.sdk.pojo.entity.Mission;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 系统设置 航线管理-基站航线导出
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "export_fixairline_file")
public class FixAirLineDownloadHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private FixAirLineService fixAirLineService;

    @Resource
    private RedisService redisService;

    @Resource
    private BaseNestAccountManager baseNestAccountManager;

    @Override
    protected boolean check(HandlerIn handlerIn) {
        BaseNestAccountOutDO outDO = baseNestAccountManager.selectByUserId(handlerIn.getAccountId());
        if (ObjectUtils.isEmpty(outDO) || CollectionUtil.isEmpty(outDO.getBaseNestId())) {
            return false;
        }
        Bean bean = toBean(handlerIn.getParam());
        List<String> baseNestIdList = outDO.getBaseNestId();
        return baseNestIdList.contains(bean.nestId);
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        String param = handlerIn.getParam();
        if (StringUtils.isEmpty(param)) {
            return false;
        }
        Bean bean = toBean(param);
        if (ObjectUtils.isEmpty(bean) || StringUtils.isEmpty(bean.missionId) || StringUtils.isEmpty(bean.nestId)) {
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
        Bean bean = toBean(handlerIn.getParam());
        Mission mission = fixAirLineService.exportAirLineStr(bean.nestId, bean.missionId);
        if (mission != null) {
            String fileName = mission.getName() + ".json";
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.reset();
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setHeader("content-type", "application/octet-stream");
            OutputStream out = null;
            try {
                out = response.getOutputStream();
                out.write(JSON.toJSONBytes(mission));
                out.flush();
            } catch (IOException e) {

            } finally {
                try {
                    out.close();
                } catch (IOException e) {

                }
            }
        }

    }

    Bean toBean(String param) {
        return JSONUtil.toBean(param, FixAirLineDownloadHandler.Bean.class);

    }

    @Data
    private static class Bean {
        private String nestId;
        private String missionId;
    }
}
