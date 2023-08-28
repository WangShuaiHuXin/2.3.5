package com.imapcloud.nest.controller;


import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.annotation.SysLogIgnoreParam;
import com.imapcloud.nest.model.AirLineEntity;
import com.imapcloud.nest.pojo.dto.ImportPcRouteDto;
import com.imapcloud.nest.service.AirLineService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.fineUtil.FineInspectionZipParseUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.web.UosDataParseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 航线表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@RequestMapping("/airLine")
@RestController
public class AirLineController {

    @Autowired
    private AirLineService airLineService;

    @GetMapping("/list")
    public RestRes listAirLine() {
        List<AirLineEntity> airLineList = airLineService.listAirLineIdAndName();
        Map<String, Object> map = new HashMap<>(2);

        List<Map<String, Object>> list = airLineList.stream().map(al -> {
            Map<String, Object> map1 = new HashMap<>(2);
            map1.put("id", al.getId());
            map1.put("name", al.getName());
            return map1;
        }).collect(Collectors.toList());

        map.put("airLineList", list);
        return RestRes.ok(map);
    }

    @PostMapping("/add")
    public RestRes addAirLine(@RequestBody AirLineEntity airLine) {
        boolean result = airLineService.save(airLine);
        return result ? RestRes.ok() : RestRes.err();
    }

    /**
     * 本地航线导入，易飞航线或者点云航线
     */
    @PostMapping("/import/point/cloud")
    @SysLogIgnoreParam
    public RestRes importPointCloudAirLine(@Valid ImportPcRouteDto routeDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()));
        }
        return airLineService.importPointCloudAirLine(routeDto);
    }


    @GetMapping("/list/air/line/json/{taskId}")
    public RestRes listAirLineJsonByTaskId(@PathVariable Integer taskId) {
        if (taskId != null) {
            return airLineService.listAirLineJsonByTaskId(taskId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_QUERY_FAILED.getContent()));
    }

//    /**
//     * @deprecated 2.2.3，使用接口{@link UosDataParseController#parseFinePatrolInspectionData(com.imapcloud.nest.v2.web.vo.req.FpiAirlinePackageReqVO)}替代，将在后续版本移除该接口
//     */
//    @Deprecated
//    @PostMapping("/upload/fine/inspection/zip")
//    @SysLogIgnoreParam
//    public RestRes uploadFineInspectionZip(String fileName, String filePath) {
//        if (filePath != null) {
//            Map<String, String> map = FineInspectionZipParseUtil.uploadZipAndUnzip(fileName, filePath);
//            return airLineService.uploadFineInspectionZip(map.get("zipPath"), map.get("originalFilename"));
//        }
//        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
//    }

    /**
     * 请求点云数据与航线数据
     */
    @GetMapping("/request/point/cloud/route/{towerId}")
    public RestRes requestPointCloudAndRoute(@PathVariable Integer towerId) {
        if (towerId != null) {
            return airLineService.requestPointCloudAndRoute(towerId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }


}

