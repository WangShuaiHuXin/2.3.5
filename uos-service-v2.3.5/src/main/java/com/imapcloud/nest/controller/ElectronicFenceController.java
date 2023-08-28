package com.imapcloud.nest.controller;


import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.pojo.dto.ElectronicFenceDTO;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 * @author zheng
 * @since 2021-09-26
 * @deprecated 2.1.0，接口已废弃，将在后续版本中删除
 */
@Deprecated
@ApiSupport(author = "boluo", order = 1)
@Api(value = "电子围栏", tags = "电子围栏")
@RestController
@RequestMapping("/electronicFence")
public class ElectronicFenceController {

    /**
     * 获取电子围栏信息
     * @param unitId
     * @param name
     * @param containsChildren 0代表不包含， 1代表包含
     * @return
     */
    @ApiOperationSupport(author = "boluo", order = 1)
    @ApiOperation(value = "查询全部", notes = "列表")
    @GetMapping(value = "/all/list")
    public RestRes selectAllList(String unitId, String name, Integer containsChildren) {
//        return electronicFenceService.selectAllList(unitId, name,containsChildren);
        throw new UnsupportedOperationException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INTERFACE_IS_OUT_OF_DATE_PLEASE_REFER_TO_THE_INTERFACE_DOCUMENTATION_AND_USE_THE_NEW_INTERFACE.getContent()));
    }

    @ApiOperationSupport(author = "boluo", order = 2)
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    public RestRes addElectronicFence(@RequestBody ElectronicFenceDTO electronicFenceDTO) {
//        return electronicFenceService.addElectronicFence(electronicFenceDTO);
        throw new UnsupportedOperationException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INTERFACE_IS_OUT_OF_DATE_PLEASE_REFER_TO_THE_INTERFACE_DOCUMENTATION_AND_USE_THE_NEW_INTERFACE.getContent()));
    }

    @ApiOperationSupport(author = "boluo", order = 3)
    @ApiOperation(value = "修改", notes = "修改")
    @PostMapping("/update/info")
    public RestRes updateElectronicFence(@RequestBody ElectronicFenceDTO electronicFenceDTO) {
//        return electronicFenceService.updateElectronicFence(electronicFenceDTO);
        throw new UnsupportedOperationException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INTERFACE_IS_OUT_OF_DATE_PLEASE_REFER_TO_THE_INTERFACE_DOCUMENTATION_AND_USE_THE_NEW_INTERFACE.getContent()));
    }

    @ApiOperationSupport(author = "boluo", order = 4)
    @ApiOperation(value = "更新状态", notes = "更新状态")
    @GetMapping("update/state")
    public RestRes updateState(Integer id, Integer state) {
//        return electronicFenceService.updateState(id, state);
        throw new UnsupportedOperationException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INTERFACE_IS_OUT_OF_DATE_PLEASE_REFER_TO_THE_INTERFACE_DOCUMENTATION_AND_USE_THE_NEW_INTERFACE.getContent()));
    }


    @ApiOperationSupport(author = "boluo", order = 5)
    @ApiOperation(value = "删除", notes = "删除")
    @PostMapping("/delete")
    public RestRes deleteElectronicFence(@RequestBody Integer[] ids) {
//        return electronicFenceService.deleteElectronicFence(Arrays.asList(ids));
        throw new UnsupportedOperationException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INTERFACE_IS_OUT_OF_DATE_PLEASE_REFER_TO_THE_INTERFACE_DOCUMENTATION_AND_USE_THE_NEW_INTERFACE.getContent()));
    }
}

