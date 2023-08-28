package com.imapcloud.nest.v2.web.download;

import cn.hutool.json.JSONUtil;
import com.geoai.common.core.bean.DefaultTrustedAccessInformation;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 文摘下载
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
public abstract class AbstractDownloadHandler implements DownloadHandler {

    public static String getRedisKey(String key) {

        return String.format("download:%s", key);
    }

    @Override
    public String getPreSignedKey(HandlerIn handlerIn) {
        try {
            if (!checkParam(handlerIn)) {
                throw new BusinessException("param is not valid");
            }
            if(!check(handlerIn)) {
                throw new BusinessException("Data permission exceeded");
            }
            String key = BizIdUtils.snowflakeIdStr();

            // 缓存2分钟
            getRedisService().set(getRedisKey(key), JSONUtil.toJsonStr(handlerIn), 2, TimeUnit.MINUTES);
            return key;
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            log.error("#AbstractDownloadHandler.getPreSignedKey# error:", e);
            throw new BusinessException("param is not valid.");
        }
    }

    @Override
    public void export(HandlerIn handlerIn, HttpServletResponse response) {

        try {
            DefaultTrustedAccessInformation information = new DefaultTrustedAccessInformation();
            information.setUsername("SYSTEM");
            information.setAccountId(handlerIn.getAccountId());
            information.setOrgCode(handlerIn.getOrgCode());
            TrustedAccessTracerHolder.set(information);
            realExportImpl(handlerIn, response);
        } catch (Exception e) {
            log.error("#AbstractDownloadHandler.export#", e);
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_DOWNLOADING.getContent()));
        } finally {
            TrustedAccessTracerHolder.clear();
        }
    }

    /**
     * 检查数据权限
     *
     * @param handlerIn 处理程序在
     * @return boolean
     */
    protected abstract boolean check(HandlerIn handlerIn);

    /**
     * 检查参数
     *
     * @param handlerIn 处理程序在
     * @return boolean
     */
    protected abstract boolean checkParam(HandlerIn handlerIn);

    /**
     * 获取redis
     *
     * @return RedisService
     */
    protected abstract RedisService getRedisService();

    /**
     * 导出实现
     *
     * @param handlerIn 处理程序在
     * @param response  响应
     * @throws Exception 异常
     */
    protected abstract void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception;
}
