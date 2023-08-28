package com.imapcloud.nest.v2.web;

import cn.hutool.core.collection.CollectionUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.web.rest.PilotResult;
import com.imapcloud.nest.v2.service.DJIPilotCommonService;
import com.imapcloud.nest.v2.service.DJIPilotMediaService;
import com.imapcloud.nest.v2.service.dto.out.PilotStsCredentialsOutDTO;
import com.imapcloud.nest.v2.web.transformer.DJIPilotMediaTransformer;
import com.imapcloud.nest.v2.web.vo.req.PilotFileUploadReqVO;
import com.imapcloud.nest.v2.web.vo.req.PilotMissionFinishCallBackInputReqVO;
import com.imapcloud.nest.v2.web.vo.resp.PilotFileUploadCallbackRespVO;
import com.imapcloud.nest.v2.web.vo.resp.PilotGetTinyFingerprintsRespVO;
import com.imapcloud.nest.v2.web.vo.resp.PilotStsCredentialsRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 大疆pilot媒体控制
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIPilotMediaController.java
 * @Description DJIPilotMediaController
 * @createTime 2022年08月16日 11:57:00
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "", tags = "大疆pilot媒体控制")
@RequestMapping("v2/dji/pilot/media")
@RestController
@Slf4j
public class DJIPilotMediaController {

    @Resource
    private DJIPilotCommonService djiPilotCommonService;

    @Resource
    private DJIPilotMediaService djiPilotMediaService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    /**
     * 文件快传
     *
     * @param
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "文件快传", notes = "文件快传")
    @PostMapping("/fastUpload")
    public PilotResult<String> fastUpload(@RequestBody PilotFileUploadReqVO pilotFileUploadReqVO) {
        log.info("fastUpload - > {} ", pilotFileUploadReqVO.toString());
        return PilotResult.error("");
    }

    /**
     * 获取已存在文件的精简指纹
     *
     * @param
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "获取已存在文件的精简指纹", notes = "获取已存在文件的精简指纹")
    @PostMapping("/tinyFingerprints")
    public PilotResult<PilotGetTinyFingerprintsRespVO> tinyFingerprints(@RequestBody Map<String, List<String>> tinyFingerprints) {
        return PilotResult.ok(PilotGetTinyFingerprintsRespVO.builder().tinyFingerprints(CollectionUtil.newArrayList()).build());
    }

    /**
     * 获取文件上传临时凭证
     *
     * @param
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "获取文件上传临时凭证", notes = "获取文件上传临时凭证")
    @PostMapping("/sts")
    public PilotResult<PilotStsCredentialsRespVO> sts() {
        PilotStsCredentialsOutDTO outDTO = this.djiPilotMediaService.getSts();
        PilotStsCredentialsRespVO respVO = DJIPilotMediaTransformer.INSTANCES.transform(outDTO);
        return PilotResult.ok(respVO);
    }

    /**
     * 媒体文件上传结果上报
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "媒体文件上传结果上报", notes = "媒体文件上传结果上报")
    @PostMapping("/uploadCallback")
    public PilotResult<PilotFileUploadCallbackRespVO> uploadCallback(@RequestBody PilotFileUploadReqVO pilotFileUploadReqVO) {
        String objectKey = this.djiPilotMediaService.uploadCallback(DJIPilotMediaTransformer.INSTANCES.transform(pilotFileUploadReqVO));
        return PilotResult.ok(PilotFileUploadCallbackRespVO.builder().objectKey(objectKey).build());
    }

    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "文件组上传回调", notes = "文件组上传回调")
    @PostMapping("/groupUploadCallback")
    public PilotResult<String> groupUploadCallback(@RequestBody PilotMissionFinishCallBackInputReqVO reqVO) {
        log.info("groupUploadCallback - > {} ", reqVO.toString());
        return PilotResult.ok("");
    }

}
