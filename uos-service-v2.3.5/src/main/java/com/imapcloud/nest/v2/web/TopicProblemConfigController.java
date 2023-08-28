package com.imapcloud.nest.v2.web;

import akka.util.Collections;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisAlgoProblemTypeRefEntity;
import com.imapcloud.nest.v2.manager.dataobj.out.UdaAlgorithmRepoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.UdaProblemTypeRepoOutDO;
import com.imapcloud.nest.v2.manager.feign.UdaAnalysisServiceClient;
import com.imapcloud.nest.v2.service.TopicProblemConfigService;
import com.imapcloud.nest.v2.service.dto.in.TopicProblemConfigInDTO;
import com.imapcloud.nest.v2.service.dto.out.TopicProblemConfigOutDTO;
import com.imapcloud.nest.v2.web.vo.req.TopicProblemConfigReqVO;
import com.imapcloud.nest.v2.web.vo.resp.TopicProblemRespVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Api(tags = {"问题管理-相关配置（新）"})
@RestController
@RequestMapping("v2/topic/config4Backstage/")
@Slf4j
public class TopicProblemConfigController {
    @Resource
    private TopicProblemConfigService topicProblemConfigService;

    @ApiOperation(value = "问题管理（获取问题类型列表）")
    @PostMapping("/industryProblemList")
    public Result<PageResultInfo<TopicProblemRespVO>> industryProblemList
            (@Valid @RequestBody TopicProblemConfigReqVO.TopicProblemConfigReq problemConfigReq) {
        TopicProblemConfigInDTO.TopicProblemConfigQueryIn inDto = new TopicProblemConfigInDTO.TopicProblemConfigQueryIn();
        BeanUtil.copyProperties(problemConfigReq, inDto);
        IPage<TopicProblemConfigOutDTO.IndustryProblemListOut> out = topicProblemConfigService.industryProblemList(inDto);
        return Result.ok(new PageResultInfo().of(out.getTotal(), out.getRecords().stream().map(e -> {
            TopicProblemRespVO vo = new TopicProblemRespVO();
            BeanUtil.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList())));
    }

    @ApiOperation(value = "问题管理-问题类型（编辑问题类型）")
    @PostMapping("/editIndustryProblem")
    public Result<Object> editIndustryProblem(@Valid @RequestBody TopicProblemConfigReqVO.EditIndustryProblemReq editIndustryProblemReq) {
        TopicProblemConfigOutDTO.EditIndustryProblemOut ta = new TopicProblemConfigOutDTO.EditIndustryProblemOut();
        List<TopicProblemConfigOutDTO.EditIndustryProblemOut.UDAProblemTypeOut> tauList = new ArrayList<>();
        if (editIndustryProblemReq.getAssociatedFunctionTagList() != null) {
            editIndustryProblemReq.getAssociatedFunctionTagList().stream().forEach(e -> {
                TopicProblemConfigOutDTO.EditIndustryProblemOut.UDAProblemTypeOut tau =
                        new TopicProblemConfigOutDTO.EditIndustryProblemOut.UDAProblemTypeOut();
                BeanUtil.copyProperties(e, tau);
                tauList.add(tau);
            });
            ta.setAssociatedFunctionTagList(tauList);
        }
        BeanUtil.copyProperties(editIndustryProblemReq, ta);

        if (topicProblemConfigService.editIndustryProblem(ta)) {
            return Result.ok();
        }
        return Result.error("执行失败");
    }

    @ApiOperation(value = "问题管理-问题类型（添加问题类型）")
    @PostMapping("/addIndustryProblem")
    public Result<Object> addIndustryProblem(@Valid @RequestBody TopicProblemConfigReqVO.AddIndustryProblemReq addIndustryProblemReq) {
        TopicProblemConfigOutDTO.AddIndustryProblemOut ta = new TopicProblemConfigOutDTO.AddIndustryProblemOut();
        List<TopicProblemConfigOutDTO.AddIndustryProblemOut.UDAProblemTypeOut> tauList = new ArrayList<>();
        if (addIndustryProblemReq.getAssociatedFunctionTagList() != null) {
            addIndustryProblemReq.getAssociatedFunctionTagList().stream().forEach(e -> {
                TopicProblemConfigOutDTO.AddIndustryProblemOut.UDAProblemTypeOut tau =
                        new TopicProblemConfigOutDTO.AddIndustryProblemOut.UDAProblemTypeOut();
                BeanUtil.copyProperties(e, tau);
                tauList.add(tau);
            });
            ta.setAssociatedFunctionTagList(tauList);
        }
        BeanUtil.copyProperties(addIndustryProblemReq, ta);

        if (topicProblemConfigService.addIndustryProblem(ta)) {
            return Result.ok();
        }
        return Result.error("执行失败");
    }

    @ApiOperation(value = "问题管理-问题类型（删除问题类型，传topicProblemIdList数组）")
    @PostMapping("/delProblemTypeList")
    public Result<Object> delProblemTypeList(@RequestBody List<String> topicProblemIdList) {
        if (topicProblemConfigService.delProblemType(topicProblemIdList)) {
            return Result.ok();
        }
        return Result.error("删除失败");
    }

    @ApiOperation(value = "问题管理（获取UDA识别功能列表）")
    @GetMapping("/udaProblemTypeList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "typeString", value = "storage|function|problemType", dataType = "String"),
            @ApiImplicitParam(name = "storageId", value = "返回识别功能信息", dataType = "String"),
            @ApiImplicitParam(name = "functionId", value = "返回UDA问题类型信息", dataType = "String")
    })
    public Result<List<TopicProblemConfigReqVO.UDAProblemTypeReq>> udaProblemTypeList
            (@RequestParam String typeString,
             @RequestParam(required = false) String storageId,
             @RequestParam(required = false) String functionId) {
        return Result.ok(topicProblemConfigService.getUdaProblemTypeReqs(typeString, storageId, functionId));
    }
}
