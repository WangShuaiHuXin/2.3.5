package com.imapcloud.nest.v2.web;

import cn.hutool.core.lang.Pair;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.service.AdminBaseNestService;
import com.imapcloud.nest.v2.web.vo.resp.TaskRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

/**
 * 任务
 *
 * @author boluo
 * @date 2022-09-29
 */
@Slf4j
@ApiSupport(author = "boluo", order = 1)
@Api(value = "UOS前台-航线规划", tags = "UOS前台-航线规划")
@RequestMapping("v2/task/")
@RestController
public class TaskV2Controller {

    @Resource
    private AdminBaseNestService adminBaseNestService;

    @ApiOperationSupport(author = "boluo", order = 1)
    @ApiOperation(value = "创建任务选择单位列表", notes = "单位列表")
    @GetMapping("condition/{nestId}/orgList")
    public Result<Object> taskConditionOrgList(@PathVariable("nestId") String nestId) {

        List<TaskRespVO.ConditionOrgInfoRespVO> conditionOrgInfoRespVOList = Lists.newLinkedList();

        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        List<Pair<String, String>> pairList = adminBaseNestService.queryOrgInfoByNestId(nestId);
        for (Pair<String, String> stringStringPair : pairList) {

            if (stringStringPair.getKey().startsWith(orgCode)) {
                TaskRespVO.ConditionOrgInfoRespVO conditionOrgInfoRespVO = new TaskRespVO.ConditionOrgInfoRespVO();
                conditionOrgInfoRespVO.setOrgCode(stringStringPair.getKey());
                conditionOrgInfoRespVO.setOrgName(stringStringPair.getValue());
                conditionOrgInfoRespVOList.add(conditionOrgInfoRespVO);
            }
        }
        conditionOrgInfoRespVOList.sort((bean1, bean2) -> bean2.getOrgCode().length() - bean1.getOrgCode().length());
        return Result.ok(conditionOrgInfoRespVOList);
    }
}
