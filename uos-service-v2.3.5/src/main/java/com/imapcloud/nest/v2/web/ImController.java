package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.service.ImService;
import com.imapcloud.nest.v2.service.dto.in.ImInDTO;
import com.imapcloud.nest.v2.web.vo.req.ImReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 即时通讯
 *
 * @author boluo
 * @date 2023-02-13
 */
@Slf4j
@RequestMapping("v2/im/")
@RestController
public class ImController {

    @Resource
    private ImService imService;

    @PostMapping("callback")
    public Result<Object> callback(@RequestBody ImReqVO.CallbackReqVO callbackReqVO) {

        ImInDTO.CallbackInDTO callbackInDTO = new ImInDTO.CallbackInDTO();
        BeanUtils.copyProperties(callbackReqVO, callbackInDTO);
        imService.callback(callbackInDTO);
        return Result.ok();
    }

    /**
     * 设置指定channel接收的消息类型
     */
    @PostMapping("changePage")
    public Result<Object> changePage(@Valid @RequestBody ImReqVO.PageReqVO pageReqVO) {

        ImInDTO.PageInDTO pageInDTO = new ImInDTO.PageInDTO();
        BeanUtils.copyProperties(pageReqVO, pageInDTO);
        pageInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        pageInDTO.setOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
        imService.changePage(pageInDTO);
        return Result.ok();
    }
}
