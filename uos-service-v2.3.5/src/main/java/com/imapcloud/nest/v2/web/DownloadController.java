package com.imapcloud.nest.v2.web;

import cn.hutool.json.JSONUtil;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.config.DownloadConfig;
import com.imapcloud.nest.v2.manager.dataobj.in.FileDownloadRecordInDO;
import com.imapcloud.nest.v2.manager.sql.FileDownloadRecordManager;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import com.imapcloud.nest.v2.web.vo.req.DownloadReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DownloadRespVO;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 下载
 *
 * @author boluo
 * @date 2023-05-08
 */
@Controller
@RequestMapping("v2/download/")
public class DownloadController {

    @Resource
    private DownloadConfig downloadConfig;

    @Resource
    private RedisService redisService;

    @Resource
    private ThreadPoolTaskExecutor bizAsyncExecutor;

    @Resource
    private FileDownloadRecordManager fileDownloadRecordManager;

    @ResponseBody
    @PostMapping("getPreSignedKey")
    public Result<Object> getPreSignedKey(@RequestBody @Valid DownloadReqVO.PreSignedReqVO preSignedReqVO) {

        HandlerIn handler = new HandlerIn();
        BeanUtils.copyProperties(preSignedReqVO, handler);
        handler.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        handler.setOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
        DownloadHandler downloadHandler = downloadConfig.getDownloadHandler(handler.getKey());
        String preSignedKey = downloadHandler.getPreSignedKey(handler);
        DownloadRespVO.PreSignedRespVO preSignedRespVO = new DownloadRespVO.PreSignedRespVO();
        preSignedRespVO.setPreSignedKey(preSignedKey);

        bizAsyncExecutor.submit(() -> {

            FileDownloadRecordInDO fileDownloadRecordInDO = new FileDownloadRecordInDO();
            fileDownloadRecordInDO.setFileDownloadRecordId(preSignedKey);
            fileDownloadRecordInDO.setAnnotationKey(handler.getKey());
            fileDownloadRecordInDO.setParam(handler.getParam());
            fileDownloadRecordInDO.setDownloadStatus(0);
            fileDownloadRecordInDO.setAccountId(handler.getAccountId());
            fileDownloadRecordManager.save(fileDownloadRecordInDO);
        });
        return Result.ok(preSignedRespVO);
    }

    @GetMapping("start")
    public void start(HttpServletResponse response, @RequestParam("key") String key) {

        String redisKey = AbstractDownloadHandler.getRedisKey(key);
        Object o = redisService.get(redisKey);
        if (o == null) {
            throw new BizException("key is not valid");
        }
        redisService.del(redisKey);
        HandlerIn handler = JSONUtil.toBean(JSONUtil.parseObj(o), HandlerIn.class);

        DownloadHandler downloadHandler = downloadConfig.getDownloadHandler(handler.getKey());
        downloadHandler.export(handler, response);

        bizAsyncExecutor.submit(() -> {
            fileDownloadRecordManager.updateDownloadStatus(key);
        });
    }
}
