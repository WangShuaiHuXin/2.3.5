package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.NestAccountService;
import com.imapcloud.nest.v2.service.UosAccountService;
import com.imapcloud.nest.v2.web.vo.req.AccountBoundNestReqVO;
import com.imapcloud.nest.v2.web.vo.resp.AccountNestInfoRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基站-账号管理API
 * @author Vastfy
 * @date 2022/08/12 11:11
 * @since 2.0.0
 */
@ApiSupport(author = "wumiao@geoai.com", order = 3)
@Api(value = "基站管理API（新）", tags = "基站-账号管理API（新）")
@RequestMapping("v2")
@RestController
public class NestAccountController {

    @Resource
    private NestAccountService nestAccountService;

    @Resource
    private UosAccountService uosAccountService;

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "查询账号拥有基站信息", notes = "")
    @ApiImplicitParam(name = "accountId", value = "账号ID", paramType = "path", required = true)
    @GetMapping("accounts/{accountId}/nests")
    public Result<List<AccountNestInfoRespVO>> listAccountNestInfos(@PathVariable String accountId,
                                                               @RequestParam(required = false) String orgCode){
        List<AccountNestInfoRespVO> data = nestAccountService.listAccountVisibleNestInfos(accountId, orgCode)
                .stream()
                .map(r -> {
                    AccountNestInfoRespVO result = new AccountNestInfoRespVO();
                    result.setNestId(r.getId());
                    result.setName(r.getName());
                    result.setNestUuid(r.getNestUuid());
                    result.setNestNumber(r.getNestNumber());
                    result.setGrantControl(r.isGrantControl());
                    return result;
                }).collect(Collectors.toList());
        return Result.ok(data);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 2)
    @ApiOperation(value = "分配基站给指定账号", notes = "")
    @ApiImplicitParam(name = "accountId", value = "账号ID", paramType = "path", required = true)
    @PutMapping("accounts/{accountId}/nests")
    public Result<Void> updateAccountBoundNests(@PathVariable String accountId,
                                                   @RequestBody AccountBoundNestReqVO body){
        nestAccountService.updateAccountBoundNests(accountId, body.getNestIds(), body.isGrantControl());
        return Result.ok();
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 3)
    @ApiOperation(value = "检测账号是否需要分配基站", notes = "该接口如果检测到时单位管理员，将重新刷新账号基站")
    @ApiImplicitParam(name = "accountId", value = "账号ID", paramType = "path", required = true)
    @PostMapping("accounts/{accountId}/has/org/privilege")
    public Result<Boolean> checkAccountNeedGranted(@PathVariable String accountId){
        Boolean result = uosAccountService.checkAccountNeedGranted(accountId);
        return Result.ok(result);
    }

}
