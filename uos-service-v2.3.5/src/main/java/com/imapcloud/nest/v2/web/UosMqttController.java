package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.UosMqttService;
import com.imapcloud.nest.v2.service.dto.in.UosMqttPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosMqttQueryOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosMqttSimpleOutDTO;
import com.imapcloud.nest.v2.web.transformer.UosMqttTransformer;
import com.imapcloud.nest.v2.web.vo.req.UosMqttCreationReqVO;
import com.imapcloud.nest.v2.web.vo.req.UosMqttModifyReqVO;
import com.imapcloud.nest.v2.web.vo.req.UosMqttPageReqVO;
import com.imapcloud.nest.v2.web.vo.resp.UosMqttQueryRespVO;
import com.imapcloud.nest.v2.web.vo.resp.UosMqttSimpleRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname UosMqttController
 * @Description Mqtt代理地址API
 * @Date 2022/8/16 11:48
 * @Author Carnival
 */

@ApiSupport(author = "liujiahua@geoai.com", order = 1)
@Api(value = "Mqtt代理地址API", tags = "Mqtt代理地址API")
@RequestMapping("v2/servers/mqtt")
@RestController
public class UosMqttController {

    @Resource
    private UosMqttService uosMqttService;

    @Resource
    private UosMqttTransformer uosMqttTransformer;

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 1)
    @ApiOperation(value = "新建mqtt代理地址")
    @PostMapping()
    public Result<String> addMqtt(@Validated @RequestBody UosMqttCreationReqVO mqttCreationReqVO) {
        String mqttBrokerId = uosMqttService.addMqtt(uosMqttTransformer.transform(mqttCreationReqVO));
        return Result.ok(mqttBrokerId);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 2)
    @ApiOperation(value = "删除mqtt代理地址")
    @DeleteMapping("/{mqttBrokerId}")
    public Result<Boolean> deleteMqtt(@PathVariable String mqttBrokerId) {
        Boolean isSuccess = uosMqttService.deleteMqtt(mqttBrokerId);
        return Result.ok(isSuccess);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 3)
    @ApiOperation(value = "修改mqtt代理地址信息")
    @PutMapping("/{mqttBrokerId}")
    public Result<Boolean> modifyMqttInfo(@PathVariable String mqttBrokerId,
                                          @Validated @RequestBody UosMqttModifyReqVO mqttModifyReqVO) {
        Boolean isSuccess = uosMqttService.modifyMqttInfo(mqttBrokerId, uosMqttTransformer.transform(mqttModifyReqVO));
        return Result.ok(isSuccess);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 4)
    @ApiOperation(value = "查询mqtt代理地址信息")
    @GetMapping("/{mqttBrokerId}")
    public Result<UosMqttQueryRespVO> queryMqttInfo(@PathVariable String mqttBrokerId) {
        UosMqttQueryOutDTO uosMqttQueryOutDTO = uosMqttService.queryMqttInfo(mqttBrokerId);
        return Result.ok(uosMqttTransformer.transform(uosMqttQueryOutDTO));
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 5)
    @ApiOperation(value = "分页查询mqtt地址列表")
    @GetMapping("page")
    public Result<PageResultInfo<UosMqttQueryRespVO>> pageMqttList(UosMqttPageReqVO mqttQueryReqVO) {
        UosMqttPageInDTO mqttPageInDTO = uosMqttTransformer.transform(mqttQueryReqVO);
        PageResultInfo<UosMqttQueryOutDTO> pageResult = uosMqttService.pageMqttList(mqttPageInDTO);
        PageResultInfo<UosMqttQueryRespVO> res = pageResult.map(r -> uosMqttTransformer.transform(r));
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 6)
    @ApiOperation(value = "查询全部mqtt代理地址的简要信息")
    @GetMapping("list/simple")
    public Result<List<UosMqttSimpleRespVO>> listMqttSimpleInfo() {
        List<UosMqttSimpleOutDTO> dtoList = uosMqttService.listMqttSimpleInfo();
        List<UosMqttSimpleRespVO> voList = dtoList.stream()
                .map(r -> uosMqttTransformer.transform(r))
                .collect(Collectors.toList());
        return Result.ok(voList);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 7)
    @ApiOperation(value = "分页查询mqtt代理地址的简要信息")
    @GetMapping("page/simple")
    public Result<PageResultInfo<UosMqttSimpleRespVO>> pageMqttSimpleInfo(UosMqttPageReqVO MqttPageReqVO) {
        UosMqttPageInDTO MqttPageInDTO = uosMqttTransformer.transform(MqttPageReqVO);
        PageResultInfo<UosMqttSimpleOutDTO> pageResultInfo = uosMqttService.pageMqttSimpleInfo(MqttPageInDTO);
        PageResultInfo<UosMqttSimpleRespVO> resultInfo = pageResultInfo.map(r -> uosMqttTransformer.transform(r));
        return Result.ok(resultInfo);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order =8)
    @ApiOperation(value = "重置mqtt客户端")
    @PostMapping("client/reset/{nestId}")
    public Result<Object> pageMqttSimpleInfo(@PathVariable String nestId) {
        uosMqttService.clientReset(nestId);
        return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_RESET_MQTT_CLIENT.getContent()));
    }
}
