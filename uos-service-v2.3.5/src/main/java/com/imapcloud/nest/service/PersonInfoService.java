package com.imapcloud.nest.service;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.imapcloud.nest.v2.service.dto.in.PersonInfoInDTO;
import com.imapcloud.nest.v2.service.dto.out.PersonInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.req.PersonInfoReqVO;
import com.imapcloud.nest.v2.web.vo.resp.PersonInfoRespVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Classname PersonInfoService
 * @Description 个人信息API
 * @Date 2023/3/10 9:02
 * @Author Carnival
 */
public interface PersonInfoService {


    /**
     * 新增或修改个人信息
     */
    Boolean savaOrUpdatePersonInfo(PersonInfoInDTO inDTO);


    /**
     * 查看个人信息
     */
    PersonInfoOutDTO queryPersonInfo(String accountId);
}
