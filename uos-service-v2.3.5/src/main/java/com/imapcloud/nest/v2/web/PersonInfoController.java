package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.service.PersonInfoService;
import com.imapcloud.nest.v2.service.dto.in.PersonInfoInDTO;
import com.imapcloud.nest.v2.service.dto.out.PersonInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.req.PersonInfoReqVO;
import com.imapcloud.nest.v2.web.vo.resp.PersonInfoRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Classname PersonInfoController
 * @Description 个人信息
 * @Date 2023/3/8 16:55
 * @Author Carnival
 */
@ApiSupport(author = "liujiahua@geoai.com", order = 1)
@Api(value = "个人信息", tags = "个人信息")
@RequestMapping("v2/personInfo")
@RestController
public class PersonInfoController {

    @Resource
    private PersonInfoService personInfoService;

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 1)
    @ApiOperation(value = "新增或修改个人信息")
    @PostMapping()
    public Result<Boolean> savaOrUpdatePersonInfo(@RequestBody PersonInfoReqVO personInfoReqVO) {
        PersonInfoInDTO inDTO = new PersonInfoInDTO();
        BeanUtils.copyProperties(personInfoReqVO, inDTO);
        Boolean res = personInfoService.savaOrUpdatePersonInfo(inDTO);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 2)
    @ApiOperation(value = "查看个人信息")
    @GetMapping()
    public Result<PersonInfoRespVO> queryPersonInfo(@RequestParam String accountId) {
        PersonInfoOutDTO outDTO = personInfoService.queryPersonInfo(accountId);
        PersonInfoRespVO vo = new PersonInfoRespVO();
        BeanUtils.copyProperties(outDTO, vo);
        return Result.ok(vo);
    }
}
