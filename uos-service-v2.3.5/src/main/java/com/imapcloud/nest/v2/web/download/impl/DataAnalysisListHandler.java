package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.service.DataAnalysisDetailService;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 综合分析 识别分析-下载照片
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "data_analysis_list")
public class DataAnalysisListHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private DataAnalysisDetailService dataAnalysisDetailService;

    @Override
    protected boolean check(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn.getParam());
        List<String> detailIds = bean.getDetailIds();

        // 校验选择的图片是否存在问题
        dataAnalysisDetailService.downloadData(detailIds.stream()
                .filter(StringUtil::isNotEmpty)
                .map(Long::parseLong)
                .collect(Collectors.toList()), null);

        // 数据权限
        String orgCode = handlerIn.getOrgCode();
        int num = dataAnalysisDetailService.selectNum(detailIds, orgCode);
        return num == detailIds.size();
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        Bean bean = toBean(handlerIn.getParam());
        return CollUtil.isNotEmpty(bean.detailIds);
    }

    private Bean toBean(String param) {
        return JSONUtil.toBean(param, Bean.class);
    }

    @Data
    private static class Bean {
        private List<String> detailIds;
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) {
        Bean bean = toBean(handlerIn.getParam());
        dataAnalysisDetailService.downloadData(bean.detailIds.stream()
                .filter(StringUtil::isNotEmpty)
                .map(Long::parseLong)
                .collect(Collectors.toList()), response);
    }
}
