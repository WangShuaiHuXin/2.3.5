package com.imapcloud.nest.v2.web;

import cn.hutool.core.collection.CollUtil;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.DataScenePhotoService;
import com.imapcloud.nest.v2.service.TopicService;
import com.imapcloud.nest.v2.service.dto.in.TopicInDTO;
import com.imapcloud.nest.v2.service.dto.out.TopicOutDTO;
import com.imapcloud.nest.v2.web.vo.SelectVo;
import com.imapcloud.nest.v2.web.vo.req.TopicReqVO;
import com.imapcloud.nest.v2.web.vo.resp.TopicRespVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 专题
 *
 * @author boluo
 * @date 2022-07-14
 */
@RequestMapping("v2/topic/config/")
@RestController
public class TopicController {

    @Resource
    private TopicService topicService;

    @Resource
    private DataScenePhotoService dataScenePhotoService;

    /**
     * 专题级别
     *
     * @return {@link Result}<{@link Object}>
     */
    @GetMapping("levelList")
    public Result<List<TopicRespVO.LevelInfoResp>> levelList(String topicKey) {
        if (StringUtils.isBlank(topicKey)) {
            return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_TOPICVALUE.getContent()));
        }
        List<TopicOutDTO.LevelInfoOut> levelInfoList = topicService.levelList(topicKey);
        List<TopicRespVO.LevelInfoResp> result = Lists.newLinkedList();
        for (TopicOutDTO.LevelInfoOut levelInfo : levelInfoList) {
            TopicRespVO.LevelInfoResp levelInfoVO = new TopicRespVO.LevelInfoResp();
            BeanUtils.copyProperties(levelInfo, levelInfoVO);
            levelInfoVO.setTopicLevelId(levelInfo.getTopicLevelId().toString());
            result.add(levelInfoVO);
        }
        return Result.ok(result);
    }

    @PostMapping("editIndustry")
    public Result<Object> editIndustry(@Valid @RequestBody TopicReqVO.EditIndustryReq editIndustryReq) {
        // 参数转换
        TopicInDTO.EditIndustryIn insertIndustryIn = new TopicInDTO.EditIndustryIn();
        insertIndustryIn.setOrgId(editIndustryReq.getOrgId());
        insertIndustryIn.setTopicKey(editIndustryReq.getTopicKey());
        insertIndustryIn.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        List<Integer> industryTypes = Lists.newLinkedList();
        insertIndustryIn.setIndustryTypes(industryTypes);
        if (CollUtil.isNotEmpty(editIndustryReq.getIndustryInfoReqList())) {
            for (String industryType : editIndustryReq.getIndustryInfoReqList()) {
                industryTypes.add(Integer.parseInt(industryType));
            }
        }
        topicService.editIndustry(insertIndustryIn);
        return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_IMPLEMENTATION.getContent()));
    }

    @GetMapping("industryList")
    public Result<List<SelectVo>> industryList(@Valid TopicReqVO.IndustryListReq industryListReq) {

        String orgId = industryListReq.getOrgId();
        if (StringUtils.isBlank(orgId)) {
            // 现场取证会通过架次ID查询单位，查询该单位下的行业
            if (!StringUtils.isBlank(industryListReq.getExecMissionId())) {
                orgId = dataScenePhotoService.getOrgIdByExecMissionId(industryListReq.getExecMissionId());
            }
        }

        List<TopicOutDTO.IndustryListOut> industryListOutList = topicService.industryList(orgId, industryListReq.getTopicKey());

        List<SelectVo> selectVoList = Lists.newLinkedList();
        for (TopicOutDTO.IndustryListOut industryListOut : industryListOutList) {
            SelectVo selectVo = new SelectVo();
            selectVo.setValue(industryListOut.getIndustryType().toString());
            selectVo.setLabel(industryListOut.getTopicIndustryName());
            selectVoList.add(selectVo);
        }
        return Result.ok(selectVoList);
    }

    @PostMapping("editIndustryProblem")
    public Result<Object> editIndustryProblem(@Valid @RequestBody TopicReqVO.EditIndustryProblemReq editIndustryProblemReq) {
        // 参数转换
        TopicInDTO.EditIndustryProblemIn editIndustryProblemIn = new TopicInDTO.EditIndustryProblemIn();
        editIndustryProblemIn.setIndustryType(editIndustryProblemReq.getIndustryType());
        editIndustryProblemIn.setOrgId(editIndustryProblemReq.getOrgId());
        editIndustryProblemIn.setTopicKey(editIndustryProblemReq.getTopicKey());
        editIndustryProblemIn.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        List<TopicInDTO.IndustryProblemIn> industryProblemInList = Lists.newLinkedList();
        editIndustryProblemIn.setIndustryProblemInList(industryProblemInList);
        if (CollUtil.isNotEmpty(editIndustryProblemReq.getIndustryProblemInfoReqList())) {
            for (TopicReqVO.IndustryProblemInfoReq industryProblemInfoReq : editIndustryProblemReq.getIndustryProblemInfoReqList()) {
                TopicInDTO.IndustryProblemIn industryProblemIn = new TopicInDTO.IndustryProblemIn();
                industryProblemIn.setTopicProblemId(industryProblemInfoReq.getTopicProblemId() == null ? null : Long.parseLong(industryProblemInfoReq.getTopicProblemId()));
                industryProblemIn.setTopicProblemName(industryProblemInfoReq.getTopicProblemName().trim());
                industryProblemInList.add(industryProblemIn);
            }
        }
        topicService.editIndustryProblem(editIndustryProblemIn);
        return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_IMPLEMENTATION.getContent()));
    }

    @GetMapping("industryProblemList")
    public Result<List<SelectVo>> industryProblemList(@Valid TopicReqVO.IndustryProblemListReq industryProblemListReq) {

        // 忽略，不进行报错
        if(Objects.isNull(industryProblemListReq.getIndustryType()) || StringUtils.isBlank(industryProblemListReq.getTopicKey())){
            return Result.ok(Collections.emptyList());
        }

        List<TopicOutDTO.IndustryProblemListOut> industryProblemListOutList = topicService.industryProblemList(industryProblemListReq.getOrgId()
                , industryProblemListReq.getTopicKey(), industryProblemListReq.getIndustryType());

        List<SelectVo> selectVoList = Lists.newLinkedList();
        for (TopicOutDTO.IndustryProblemListOut industryListOut : industryProblemListOutList) {
            SelectVo selectVo = new SelectVo();
            selectVo.setValue(industryListOut.getTopicProblemId().toString());
            selectVo.setLabel(industryListOut.getTopicProblemName());
            selectVo.setSource(industryListOut.getSource());
            selectVoList.add(selectVo);
        }
        return Result.ok(selectVoList);
    }
}
