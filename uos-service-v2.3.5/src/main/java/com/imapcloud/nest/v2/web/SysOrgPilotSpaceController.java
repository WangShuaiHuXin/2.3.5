package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.SysOrgPilotSpaceService;
import com.imapcloud.nest.v2.service.dto.out.SysOrgPilotSpaceOutDTO;
import com.imapcloud.nest.v2.web.transformer.SysOrgPilotSpaceTransformer;
import com.imapcloud.nest.v2.web.vo.req.SysOrgPilotSpaceReqVO;
import com.imapcloud.nest.v2.web.vo.resp.SysOrgPilotSpaceRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 大疆pilot媒体控制
 * @author zhongtb
 * @version 1.0.0
 * @ClassName SysOrgPilotSpaceController.java
 * @Description SysOrgPilotSpaceController
 * @createTime 2022年08月16日 11:57:00
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "", tags = "大疆pilot空间信息")
@RequestMapping("v2/dji/pilot/sys")
@RestController
@Slf4j
public class SysOrgPilotSpaceController {

    @Resource
    private SysOrgPilotSpaceService sysOrgPilotSpaceService;

    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "查询pilot组织对应空间信息", notes = "查询pilot组织对应空间信息")
    @GetMapping("/org/space/get")
    public Result<SysOrgPilotSpaceRespVO> getPilotOrgSpace(SysOrgPilotSpaceReqVO reqVO) {
        SysOrgPilotSpaceOutDTO sysOrgPilotSpace = this.sysOrgPilotSpaceService.getSysOrgPilotSpace(reqVO.getOrgCode(),reqVO.getWorkSpaceId());
        SysOrgPilotSpaceRespVO sysOrgPilotSpaceRespVO = SysOrgPilotSpaceTransformer.INSTANCES.transform(sysOrgPilotSpace);
        return Result.ok(sysOrgPilotSpaceRespVO);
    }

}
