package com.imapcloud.nest.v2.web;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import com.alibaba.excel.util.ListUtils;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.util.DateUtils;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.common.collect.Lists;
import com.imapcloud.nest.utils.excel.EasyExcelStyleUtils;
import com.imapcloud.nest.utils.excel.EasyExcelUtils;
import com.imapcloud.nest.utils.excel.ProblemResultStyleStrategy;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.config.DataAnalysisConfig;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.service.DataAnalysisResultGroupService;
import com.imapcloud.nest.v2.service.DataAnalysisResultService;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisResultGroupPageInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisResultInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisResultGroupOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisResultOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisTraceSpacetimeOutDTO;
import com.imapcloud.nest.v2.web.transformer.DataAnalysisResultGroupTransformer;
import com.imapcloud.nest.v2.web.vo.req.DataAnalysisResultGroupPageReqVO;
import com.imapcloud.nest.v2.web.vo.req.DataAnalysisResultReqVO;
import com.imapcloud.nest.v2.web.vo.req.DataAnalysisTraceSpacetimeReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataAnalysisResultGroupResqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataAnalysisResultRespVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 数据分析结果
 *
 * @author boluo
 * @date 2022-07-26
 */
@Slf4j
@RequestMapping("v2/dataAnalysis/result/")
@RestController
public class DataAnalysisResultController {

    @Resource
    private DataAnalysisResultService dataAnalysisResultService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;
    @Resource
    private RedisService redisService;

    @Resource
    private DataAnalysisConfig dataAnalysisConfig;

    @Resource
    private DataAnalysisResultGroupTransformer dataAnalysisResultGroupTransformer;

    @Resource
    private DataAnalysisResultGroupService dataAnalysisResultGroupService;

    @GetMapping("collectSum")
    public Result<DataAnalysisResultRespVO.CollectSumRespVO> collectSum(@Valid DataAnalysisResultReqVO.ProblemReq problemReq) {
        DataAnalysisResultInDTO.ProblemIn problemIn = getProblemIn(problemReq);

        DataAnalysisResultOutDTO.CollectSumOut collectSumOut = dataAnalysisResultService.collectSum(problemIn);

        DataAnalysisResultRespVO.CollectSumRespVO collectSumRespVO = new DataAnalysisResultRespVO.CollectSumRespVO();
        List<DataAnalysisResultRespVO.ProblemNumInfoVO> problemNumInfoList = Lists.newLinkedList();
        collectSumRespVO.setProblemSumNum(collectSumOut.getProblemSumNum());
        collectSumRespVO.setProblemNumInfoList(problemNumInfoList);

        for (DataAnalysisResultOutDTO.ProblemNumInfo problemNumInfo : collectSumOut.getProblemNumInfoList()) {
            DataAnalysisResultRespVO.ProblemNumInfoVO vo = new DataAnalysisResultRespVO.ProblemNumInfoVO();
            BeanUtils.copyProperties(problemNumInfo, vo);
            vo.setTopicLevelId(problemNumInfo.getTopicLevelId().toString());
            problemNumInfoList.add(vo);
        }
        return Result.ok(collectSumRespVO);
    }

    @GetMapping("problemTrend")
    public Result<List<DataAnalysisResultRespVO.ProblemTrendRespVO>> problemTrend(@Valid DataAnalysisResultReqVO.ProblemReq problemReq) {
        DataAnalysisResultInDTO.ProblemIn problemIn = getProblemIn(problemReq);

        List<DataAnalysisResultOutDTO.ProblemTrendOut> trendOutList = dataAnalysisResultService.problemTrend(problemIn);

        List<DataAnalysisResultRespVO.ProblemTrendRespVO> result = Lists.newLinkedList();
        for (DataAnalysisResultOutDTO.ProblemTrendOut problemTrendOut : trendOutList) {
            DataAnalysisResultRespVO.ProblemTrendRespVO vo = new DataAnalysisResultRespVO.ProblemTrendRespVO();
            BeanUtils.copyProperties(problemTrendOut, vo);
            result.add(vo);
        }
        return Result.ok(result);
    }

    private DataAnalysisResultInDTO.ProblemIn getProblemIn(DataAnalysisResultReqVO.ProblemReq problemReq) {
        LocalDateTime startTime, endTime;
        try {
            LocalDate start = DateUtils.toLocalDate(problemReq.getStartTime(), DateUtils.DATE_FORMATTER_OF_CN);
            LocalDate end = DateUtils.toLocalDate(problemReq.getEndTime(), DateUtils.DATE_FORMATTER_OF_CN);
            startTime = LocalDateTime.of(start, LocalTime.of(0, 0, 0));
            endTime = LocalDateTime.of(end, LocalTime.of(23, 59, 59));
            problemReq.setStart(startTime);
            problemReq.setEnd(endTime);
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PROBLEM_WITH_THE_DATE_FORMAT.getContent()));
        }
        // 日期最长366天
        long until = problemReq.getStart().until(problemReq.getEnd(), ChronoUnit.DAYS) + 1;
        if (until > 366) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DATE_SEARCH_UP_TO_366_DAYS.getContent()));
        }
        DataAnalysisResultInDTO.ProblemIn problemIn = new DataAnalysisResultInDTO.ProblemIn();
        problemIn.setStartTime(problemReq.getStart());
        problemIn.setEndTime(problemReq.getEnd());
        problemIn.setTopicKey(problemReq.getTopicKey());
        if (StringUtils.isNotBlank(problemReq.getOrgId())) {
            problemIn.setOrgCode(problemReq.getOrgId());
        }
        problemIn.setIndustryType(problemReq.getIndustryType());
        problemIn.setTopicLevelId(problemReq.getTopicLevelId());
        problemIn.setTagName(problemReq.getTagName());
        problemIn.setTopicProblemId(problemReq.getTopicProblemId());
        return problemIn;
    }

    @GetMapping("problemList")
    public Result<PageResultInfo<DataAnalysisResultRespVO.ProblemRespVO>> problemList(@Valid DataAnalysisResultReqVO.ProblemReq problemReq) {

        DataAnalysisResultInDTO.ProblemIn problemIn = getProblemIn(problemReq);
        problemIn.setPageNo(problemReq.getPageNo());
        if (problemReq.isMap()) {
            problemIn.setPageSize(geoaiUosProperties.getAnalysis().getMapMaxNum());
        } else {
            problemIn.setPageSize(problemReq.getPageSize());
        }

        PageResultInfo<DataAnalysisResultOutDTO.ProblemOut> pageResultInfo = dataAnalysisResultService.problemList(problemIn);

        List<DataAnalysisResultRespVO.ProblemRespVO> problemRespVOList = Lists.newLinkedList();
        for (DataAnalysisResultOutDTO.ProblemOut record : pageResultInfo.getRecords()) {
            DataAnalysisResultRespVO.ProblemRespVO problemRespVO = new DataAnalysisResultRespVO.ProblemRespVO();
            BeanUtils.copyProperties(record, problemRespVO);
            problemRespVO.setCreateTime(record.getPhotoCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            problemRespVO.setResultId(record.getResultId().toString());
            problemRespVO.setTopicLevelId(record.getTopicLevelId().toString());
            problemRespVO.setIndustryType(record.getIndustryType());
            problemRespVO.setTopicProblemId(record.getTopicProblemId().toString());
            if (record.getMissionRecordsTime() != null) {
                problemRespVO.setMissionRecordTime(record.getMissionRecordsTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
            problemRespVO.setNestId(record.getBaseNestId() == null ? "" : record.getBaseNestId());
            problemRespVOList.add(problemRespVO);
        }
        return Result.ok(PageResultInfo.of(pageResultInfo.getTotal(), problemRespVOList));
    }


    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 1)
    @ApiOperation(value = "问题统计结果分组列表分页查询")
    @GetMapping("pageProblemResult")
    public Result<PageResultInfo<DataAnalysisResultGroupResqVO.ResultGroupRespVO>> pageResultGroupList(DataAnalysisResultGroupPageReqVO pageReqVO) {
        // 兼容旧接口
        if (!StringUtils.isEmpty(pageReqVO.getOrgId())) {
            pageReqVO.setOrgCode(pageReqVO.getOrgId());
        }
        DataAnalysisResultGroupPageInDTO inDTO = dataAnalysisResultGroupTransformer.transform(pageReqVO);
        PageResultInfo<DataAnalysisResultGroupOutDTO.ResultGroupOutDTO> pageResult = dataAnalysisResultGroupService.pageResultGroupList(inDTO);
        PageResultInfo<DataAnalysisResultGroupResqVO.ResultGroupRespVO> res = pageResult.map(r -> {
            DataAnalysisResultGroupResqVO.ResultGroupRespVO vo = dataAnalysisResultGroupTransformer.transform(r);
            vo.setTopicLevelName(MessageUtils.getMessage(vo.getTopicLevelName()));
            vo.setMissionId(String.valueOf(r.getMissionId()));
            vo.setTopicProblemId(String.valueOf(r.getTopicProblemId()));
            vo.setEarliestTime(timeFormat(r.getEarliestTime()));
            vo.setLatestTime(timeFormat(r.getLatestTime()));
            // 兼容旧接口
            vo.setNestId(vo.getBaseNestId());
            vo.setOrgId(vo.getOrgCode());
            return vo;
        });

        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 1)
    @ApiOperation(value = "全量问题统计结果列表分页查询")
    @GetMapping("listProblemResult")
    public Result<List<DataAnalysisResultGroupResqVO.ResultGroupRespVO>> listResultGroupList(DataAnalysisResultGroupPageReqVO pageReqVO) {
        // 兼容旧接口
        if (!StringUtils.isEmpty(pageReqVO.getOrgId())) {
            pageReqVO.setOrgCode(pageReqVO.getOrgId());
        }
        DataAnalysisResultGroupPageInDTO inDTO = dataAnalysisResultGroupTransformer.transform(pageReqVO);
        List<DataAnalysisResultGroupOutDTO.ResultGroupOutDTO> dtoList = dataAnalysisResultGroupService.listResultGroupList(inDTO);
        List<DataAnalysisResultGroupResqVO.ResultGroupRespVO> res = Lists.newLinkedList();

        for (DataAnalysisResultGroupOutDTO.ResultGroupOutDTO resultGroupOutDTO : dtoList) {
            DataAnalysisResultGroupResqVO.ResultGroupRespVO vo = new DataAnalysisResultGroupResqVO.ResultGroupRespVO();
            BeanUtils.copyProperties(resultGroupOutDTO, vo);
            vo.setTopicLevelName(MessageUtils.getMessage(vo.getTopicLevelName()));
            vo.setMissionId(String.valueOf(resultGroupOutDTO.getMissionId()));
            vo.setTopicProblemId(String.valueOf(resultGroupOutDTO.getTopicProblemId()));
            vo.setEarliestTime(timeFormat(resultGroupOutDTO.getEarliestTime()));
            vo.setLatestTime(timeFormat(resultGroupOutDTO.getLatestTime()));
            vo.setNestId(vo.getBaseNestId());
            // 兼容旧接口
            vo.setOrgId(vo.getOrgCode());
            res.add(vo);
        }
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 1)
    @ApiOperation(value = "问题统计结果分组详情")
    @GetMapping("group/{resultGroupId}")
    public Result<Object> pageResultGroupList(@PathVariable("resultGroupId") String resultGroupId) {

        List<DataAnalysisResultOutDTO.ProblemOut> problemOutList = dataAnalysisResultService.selectResultByGroupId(resultGroupId);
        List<DataAnalysisResultRespVO.ProblemRespVO> problemRespVOList = Lists.newLinkedList();
        for (DataAnalysisResultOutDTO.ProblemOut record : problemOutList) {
            DataAnalysisResultRespVO.ProblemRespVO problemRespVO = new DataAnalysisResultRespVO.ProblemRespVO();
            BeanUtils.copyProperties(record, problemRespVO);
            problemRespVO.setCreateTime(timeFormat(record.getPhotoCreateTime()));
            problemRespVO.setResultId(record.getResultId().toString());
            problemRespVO.setTopicLevelId(record.getTopicLevelId().toString());
            problemRespVO.setIndustryType(record.getIndustryType());
            problemRespVO.setTopicProblemId(record.getTopicProblemId().toString());
            if (record.getMissionRecordsTime() != null) {
                problemRespVO.setMissionRecordTime(timeFormat(record.getMissionRecordsTime()));
            }
            problemRespVO.setNestId(record.getBaseNestId() == null ? "" : record.getBaseNestId());
            problemRespVOList.add(problemRespVO);
        }
        return Result.ok(problemRespVOList);
    }

    private String timeFormat(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 2)
    @ApiOperation(value = "删除问题统计结果分组")
    @DeleteMapping("delProblemResult")
    public Result<Boolean> deleteResultGroup(@RequestBody List<String> problemResultList) {
        Boolean res = dataAnalysisResultGroupService.deleteResultGroup(problemResultList);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 4)
    @ApiOperation(value = "时空追溯")
    @PostMapping("traceSpacetime")
    public Result<Object> traceSpacetime(@Validated @RequestBody DataAnalysisTraceSpacetimeReqVO vo) {
        Map<String, List<DataAnalysisTraceSpacetimeOutDTO>> resMap = dataAnalysisResultGroupService.traceSpacetime(dataAnalysisResultGroupTransformer.transformTraceSpacetime(vo));

        List<DataAnalysisResultGroupResqVO.TraceSpacetimeRespVO> traceSpacetimeRespVOList = Lists.newArrayList();
        resMap.forEach((k, v) -> {

            DataAnalysisResultGroupResqVO.TraceSpacetimeRespVO traceSpacetimeRespVO = new DataAnalysisResultGroupResqVO.TraceSpacetimeRespVO();
            traceSpacetimeRespVO.setTime(k);
            List<DataAnalysisResultGroupResqVO.TraceInfoRespVO> list = Lists.newLinkedList();
            traceSpacetimeRespVO.setList(list);

            for (DataAnalysisTraceSpacetimeOutDTO dataAnalysisTraceSpacetimeOutDTO : v) {

                DataAnalysisResultGroupResqVO.TraceInfoRespVO traceInfoRespVO = new DataAnalysisResultGroupResqVO.TraceInfoRespVO();
                BeanUtils.copyProperties(dataAnalysisTraceSpacetimeOutDTO, traceInfoRespVO);
                list.add(traceInfoRespVO);
            }
            traceSpacetimeRespVOList.add(traceSpacetimeRespVO);
        });
        traceSpacetimeRespVOList.sort(Comparator.comparing(DataAnalysisResultGroupResqVO.TraceSpacetimeRespVO::getTime).reversed());
        return Result.ok(traceSpacetimeRespVOList);
    }




}
