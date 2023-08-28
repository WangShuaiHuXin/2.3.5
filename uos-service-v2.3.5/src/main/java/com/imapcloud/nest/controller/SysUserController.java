package com.imapcloud.nest.controller;


import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.utils.PageConverterUtils;
import com.imapcloud.nest.v2.service.UosAccountService;
import com.imapcloud.nest.v2.service.dto.out.AccountDetailOutDTO;
import com.imapcloud.nest.v2.service.dto.out.AccountInfoOutDTO;
import com.imapcloud.nest.v2.web.transformer.AccountTransformer;
import com.imapcloud.nest.v2.web.vo.req.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.xml.core.Validate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@RestController
@RequestMapping("/sysUser")
@Slf4j
public class SysUserController {

    @Resource
    private RedisService redisService;

    @Resource
    private UosAccountService uosAccountService;

    @Resource
    private AccountTransformer accountTransformer;

    /*添加用户*/
    @ApiOperation("添加用户")
    @PostMapping("/addSysUser")
    public RestRes addSysUser(@RequestBody @Valid AccountCreationReqVO body) {
        boolean success = uosAccountService.createNewUser(accountTransformer.transform(body));
        if(success){
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ADD_USER_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ADDING_USER_FAILED.getContent()));
    }

    /*修改用户信息*/
    @ApiOperation("修改用户信息")
    @PostMapping("/updateSysUser")
    public RestRes updateSysUser(@RequestBody @Validate AccountModificationReqVO body) {
        boolean success = uosAccountService.updateUosUser(accountTransformer.transform(body));
        if(success){
            AccountDetailOutDTO accountDetails = uosAccountService.getAccountDetails(body.getId());
            if(Objects.nonNull(accountDetails)){
                String account = accountDetails.getAccount();
                clearAccountNestCache(account);
            }
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_USER_INFORMATION_MODIFICATION_SUCCESS.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_USER_INFORMATION_MODIFICATION_FAILED.getContent()));
    }

    /*获取用户详情*/
    @ApiOperation("获取用户详情")
    @PostMapping("/getSysUser")
    public RestRes getSysUser(@RequestBody AccountDetailReqVO body) {
        AccountDetailOutDTO accountDetails = uosAccountService.getAccountDetails(body.getId());
        return RestRes.ok(Collections.singletonMap("sysUserEntity", accountDetails));
    }

    /*获取用户信息*/
    @ApiOperation("用户分页检索列表")
    @PostMapping("/listSysUserBy")
    public RestRes listSysUserBy(@RequestBody @Valid AccountInfoReqVO accountInfoReqVO) {
        PageResultInfo<AccountInfoOutDTO> pageResultInfo = uosAccountService.queryAccountInfo(accountTransformer.transform(accountInfoReqVO));
        Map<String, Object> map = new HashMap<>(1);
        map.put("sysUserDtoIPage", PageConverterUtils.convertToOld(pageResultInfo));
        return RestRes.ok(map);
    }

    /*删除用户*/
    @ApiOperation("删除用户")
    @PostMapping("/deleteSysUser")
    public RestRes deleteSysUser(@RequestBody @Valid AccountCommonReqVO body) {
        boolean success = uosAccountService.deleteAccount(body.getId());
        if(success){
            return RestRes.ok();
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_USER_DELETION_FAILED.getContent()));
    }

    /*重置用户密码*/
    @PostMapping("/resetSysUserPassword")
    public RestRes resetSysUserPassword(@RequestBody PasswordResetReqVO body) {
        if(uosAccountService.resetPassword(body.getId(), body.getNewPwd())){
            AccountDetailOutDTO accountDetails = uosAccountService.getAccountDetails(body.getId());
            if(Objects.nonNull(accountDetails)){
                String account = accountDetails.getAccount();
                clearAccountNestCache(account);
            }
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ACCOUNT_PASSWORD_HAS_BEEN_RESET.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PASSWORD_RESET_FAILED_PLEASE_CONTACT_THE_ADMINISTRATOR.getContent()));
    }

    /*启用当前用户*/
    @Deprecated
    @ApiOperation("账号启用")
    @PostMapping("/enableSysUserStatus")
    public RestRes enableSysUserStatus(@RequestBody AccountCommonReqVO body) {
        return uosAccountService.settingAccountStatus(body.getId(), "on") ? RestRes.ok() : RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_STATUS_CHANGE_FAILED_PLEASE_CONTACT_ADMINISTRATOR.getContent()));
    }

    /*禁用当前用户*/
    @Deprecated
    @ApiOperation("账号停用")
    @PostMapping("/disableSysUserStatus")
    public RestRes disableSysUserStatus(@RequestBody AccountCommonReqVO body) {
        boolean off = uosAccountService.settingAccountStatus(body.getId(), "off");
        if(off){
            AccountDetailOutDTO accountDetails = uosAccountService.getAccountDetails(body.getId());
            if(Objects.nonNull(accountDetails)){
                String account = accountDetails.getAccount();
                clearAccountNestCache(account);
            }
            return RestRes.ok();
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ACCOUNT_DEACTIVATION_FAILED_PLEASE_CONTACT_THE_ADMINISTRATOR.getContent()));
    }

    /**
     * 更改当前登录人的信息
     */
    @ApiOperation(value = "修改账号基本信息")
    @PostMapping("/updateUserInfo")
    public RestRes updateUserInfo(@RequestBody @Validated AccountInfoModificationReqVO info) {
        if(uosAccountService.changeInformation(accountTransformer.transform(info))){
            clearAccountNestCache(TrustedAccessTracerHolder.get().getUsername());
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_USER_INFORMATION_MODIFICATION_SUCCESS.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_USER_INFORMATION_MODIFICATION_FAILED.getContent()));
    }

    /*重置用户密码*/
    @ApiOperation(value = "修改账号密码")
    @PostMapping("/updateUserPassword")
    public RestRes updateUserPassword(@RequestBody @Validated PasswordModificationReqVO info) {
        if(uosAccountService.changePassword(accountTransformer.transform(info))){
            clearAccountNestCache(TrustedAccessTracerHolder.get().getUsername());
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PASSWORD_CHANGED_SUCCESSFULLY_PLEASE_USE_NEW_PASSWORD_FOR_NEXT_LOGIN.getContent()));
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PASSWORD_CHANGE_FAILED.getContent()));
    }

    private void clearAccountNestCache(String account) {
        redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_NEST_LIST_KEY, account));
        redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_REGION_LIST_KEY, account));
        redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_NEST_LIST_DTO_KEY, account));
    }

}

