package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.util.BeanCopyUtils;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.common.annotation.NestId;
import com.imapcloud.nest.service.NestLogService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.UosNestService;
import com.imapcloud.nest.v2.service.dto.in.CpsSyncNestLogInDTO;
import com.imapcloud.nest.v2.service.dto.in.GimbalAutoFollowDTO;
import com.imapcloud.nest.v2.service.dto.in.GimbalAutoFollowResetDTO;
import com.imapcloud.nest.v2.service.dto.in.NestExtFirmwareQueryInDTO;
import com.imapcloud.nest.v2.service.dto.out.NestExtFirmwareOutDTO;
import com.imapcloud.nest.v2.service.dto.out.RegionNestOutDTO;
import com.imapcloud.nest.v2.web.transformer.UosNestTransformer;
import com.imapcloud.nest.v2.web.vo.req.*;
import com.imapcloud.nest.v2.web.vo.resp.*;
import com.imapcloud.sdk.pojo.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * 基站管理API
 *
 * @author Vastfy
 * @date 2022/07/08 11:11
 * @since 1.9.7
 */
@Slf4j
@ApiSupport(author = "wumiao@geoai.com", order = 1)
@Api(value = "基站管理API（新）", tags = "基站管理API（新）")
@RequestMapping("v2/nests")
@RestController
public class UosNestController {

    @Resource
    private UosNestService uosNestService;

    @Resource
    private UosNestTransformer uosNestTransformer;

    @Resource
    private NestLogService nestLogService;

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C18)
    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "重置基站摄像机基本设置", notes = "如果基站挂载多个相机，则会重置当前使用的相机配置")
    @PostMapping("{nestId}/cameras/reset")
    public Result<Boolean> resetNestCameraSettings(@PathVariable @NestId String nestId) {
        boolean success = uosNestService.resetCameraSettings(nestId);
        return Result.ok(success);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 2)
    @ApiOperation(value = "批量查询基站信息", notes = "该接口返回值会以`区域`为维度进行分组展示")
    @GetMapping("all")
    public Result<List<RegionNestRespVO>> listNestsGroupByRegion(NestQueryReqVO condition) {
        List<RegionNestOutDTO> results = uosNestService.listGroupByRegion(uosNestTransformer.transform(condition));
        return Result.ok(uosNestTransformer.transform(results));
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 3)
    @ApiOperation(value = "分页查询基站信息（含基站当前固件版本信息）", notes = "该接口返回值除基站信息外，还可获取基站当前固件版本信息")
    @GetMapping("firmwares")
    public Result<PageResultInfo<NestExtFirmwareRespVO>> pageNestInfoWithFirmwares(NestExtFirmwareQueryReqVO condition) {
        NestExtFirmwareQueryInDTO transform = uosNestTransformer.transform(condition);
        PageResultInfo<NestExtFirmwareOutDTO> pageResultInfo = uosNestService.pageNestInfoWithFirmwares(transform);
        return Result.ok(pageResultInfo.map(uosNestTransformer::transform));
    }


    @ApiOperationSupport(author = "wangmin@geoai.com", order = 4)
    @ApiOperation(value = "保存基站信息", notes = "基站编辑和保存都是同一个接口")
    @PostMapping("/save/update/nest/details")
    public Result<NestDetailsRespDataVO> saveOrUpdateNestDetails(@RequestBody NestDetailsSaveVO nestDetailsSaveVO) {
        NestDetailsRespDataVO nestDetailsRespDataVO = new NestDetailsRespDataVO();
        return Result.ok(nestDetailsRespDataVO);
    }

    @ApiOperationSupport(author = "wangmin@geoai.com", order = 5)
    @ApiOperation(value = "获取基站信息详情", notes = "nestId为业务nest表的业务id")
    @GetMapping("/get/nest/details/{nestId}")
    public Result<NestDetailsRespVO> getNestDetails(@PathVariable Long nestId) {
        NestDetailsRespVO nestDetailsRespVO = new NestDetailsRespVO();
        return Result.ok(nestDetailsRespVO);
    }

    @ApiOperationSupport(author = "wangmin@geoai.com", order = 6)
    @ApiOperation(value = "删除基站", notes = "nestId为业务nest表的业务id，这里为软删除")
    @DeleteMapping("/delete/nest/{nestId}")
    public Result<Boolean> deleteNest(@PathVariable Long nestId) {
        return Result.ok(true);
    }
    @ApiOperationSupport(author = "wangmin@geoai.com", order = 4)
    @ApiOperation(value = "查询在线人姓名列表")
    @GetMapping("/list/on/line/username/{nestId}")
    public Result<List<String>> listOnLineUsernames(@PathVariable String nestId) {
        if (Objects.nonNull(nestId)) {
            List<String> userNameList = uosNestService.listOnLineUsernames(nestId);
            return Result.ok(userNameList);
        }
        return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @ApiOperationSupport(author = "wangmin@geoai.com", order = 5)
    @ApiOperation(value = "分页查询操作记录列表")
    @GetMapping("/list/nest/code/oper/records/{nestId}/{currPage}/{pageSize}")
    public Result<PageResultInfo<NestCodeOperationVO>> listNestCodeOperationRecords(@PathVariable String nestId, @PathVariable Integer currPage, @PathVariable Integer pageSize) {
        PageResultInfo<NestCodeOperationVO> pageResultInfo = uosNestService.listNestCodeOperationRecords(nestId, currPage, pageSize);
        return Result.ok(pageResultInfo);
    }


    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C60)
    @ApiOperationSupport(author = "wangmin@geoai.com", order = 6)
    @ApiOperation(value = "开启云台自动跟随")
    @PostMapping("/start/gimbal/auto/follow")
    public Result<Object> startGimbalAutoFollow(@RequestBody GimbalAutoFollowVO gimbalAutoFollowVO) {
        if (Objects.nonNull(gimbalAutoFollowVO)) {
            GimbalAutoFollowDTO dto = new GimbalAutoFollowDTO();
            BeanUtils.copyProperties(gimbalAutoFollowVO,dto);
            if (uosNestService.startGimbalAutoFollow(dto)) {
                return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TURN_ON_PTZ_AUTO_FOLLOW.getContent()));
            }
        }
        return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C61)
    @ApiOperationSupport(author = "wangmin@geoai.com", order = 7)
    @ApiOperation(value = "取消云台自动跟随")
    @PostMapping("/cancel/gimbal/auto/follow/{nestId}")
    public Result<Object> cancelGimbalAutoFollow(@PathVariable @NestId String nestId) {
        if (Objects.nonNull(nestId)) {
            if (uosNestService.cancelGimbalAutoFollow(nestId)) {
                return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CANCELLATION_OF_THE_HEAD_AUTO_FOLLOW.getContent()));
            }
        }
        return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @ApiOperationSupport(author = "wangmin@geoai.com", order = 8)
    @ApiOperation(value = "退出云台自动跟随模式")
    @PostMapping("/exit/gimbal/auto/follow/{nestId}")
    public Result<Object> exitGimbalAutoFollow(@PathVariable String nestId) {
        if (Objects.nonNull(nestId)) {
            if (uosNestService.exitGimbalAutoFollow(nestId)) {
                return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_EXIT_PTZ_AUTO_FOLLOW_MODE.getContent()));
            }
        }
        return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @ApiOperationSupport(author = "wangmin@geoai.com", order = 9)
    @ApiOperation(value = "进入云台自动跟随模式")
    @PostMapping("/exec/gimbal/auto/follow/{nestId}")
    public Result<Object> execGimbalAutoFollow(@PathVariable String nestId) {
        if(Objects.nonNull(nestId)) {
            if (uosNestService.execGimbalAutoFollow(nestId)) {
                return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_ENTER_THE_AUTOMATIC_FOLLOWING_MODE.getContent()));
            }
        }
        return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @NestCodeRecord({Constant.AIRCRAFT_CAMERA_MANAGER_C38,Constant.AIRCRAFT_CAMERA_MANAGER_C35})
    @ApiOperationSupport(author = "wangmin@geoai.com", order = 10)
    @ApiOperation(value = "云台自动跟随重置镜头")
    @PostMapping("/gimbal/auto/follow/reset")
    public Result<Object> gimbalAutoFollowReset(@RequestBody @Valid GimbalAutoFollowResetVO vo) {
        GimbalAutoFollowResetDTO dto = new GimbalAutoFollowResetDTO();
        BeanUtils.copyProperties(vo,dto);
        Boolean reset = uosNestService.gimbalAutoFollowReset(dto);
        if(reset) {
            return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_RESETTING_THE_LENS.getContent()));
        }

        return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @ApiOperationSupport(author = "daolin@geoai.com", order = 11)
    @ApiOperation(value = "获取相机镜头视频源")
    @GetMapping("{nestId}/cameras/video/source")
    public Result<Integer> getCameraVideoSource(@PathVariable String nestId) {
        if(Objects.nonNull(nestId)) {
            Integer source = uosNestService.getCameraVideoSource(nestId);
            return Result.ok(source);
        }
        return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

//    /**
//     * @deprecated 2.3.2，将在后续版本删除
//     * 使用接口【v2/nests/1570606184550826115/live/play?deviceUse=0】替代
//     */
//    @ApiOperationSupport(author = "daolin@geoai.com", order = 12)
//    @ApiOperation(value = "重置监控推流，不区分巢内、外")
//    @PostMapping("{nestId}/cameras/stream/reset")
//    @Deprecated
//    public Result<Object> resetCameraStream(@PathVariable String nestId) {
//        if(Objects.nonNull(nestId)) {
//            return uosNestService.resetCameraStream(nestId);
//        }
//        return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
//    }

    @ApiOperationSupport(author = "daolin@geoai.com", order = 13)
    @ApiOperation(value = "CPS同步基站日志存储信息平台")
    @PostMapping("logs/sync")
    public Result<String> cpsSyncNestLogs(@RequestBody CpsSyncNestLogReqVO body) {
        log.info("CPS传递日志保存信息 ==> {}", body);
        CpsSyncNestLogInDTO data = new CpsSyncNestLogInDTO();
        BeanCopyUtils.copyProperties(body, data, true);
        String id = nestLogService.saveNestLogStorageInfo(data);
        return Result.ok(id);
    }


    @ApiOperationSupport(author = "wumiao@geoai.com", order = 13)
    @ApiOperation(value = "获取基站局域网设备配置访问地址", notes = "同步调用CPS指令，可能会超时")
    @GetMapping("{nestId}/device/setting/url")
    public Result<String> getNestDeviceSettingUrl(@PathVariable String nestId, @RequestParam String lanIp) {
        return Result.ok(uosNestService.getNestDeviceSettingUrl(nestId, lanIp));
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 14)
    @ApiOperation(value = "保存基站流媒体信息", notes = "保存基站流媒体信息")
    @PutMapping("{nestId}/media")
    public Result<Boolean> saveNestMediaInfo(@PathVariable String nestId, @RequestBody NestMediaSaveInfoVO info) {
        Boolean res = uosNestService.saveNestMediaInfo(nestId, info);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 15)
    @ApiOperation(value = "点播基站设备实时视频", notes = "点播基站设备监控")
    @PostMapping("{nestId}/live/play")
    public Result<LivePlayInfoRespVO> playNestLive(@PathVariable String nestId,
                                                   @RequestParam Integer deviceUse) {
        LivePlayInfoRespVO respVO = uosNestService.playNestLive(nestId, deviceUse);
        return Result.ok(respVO);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 3)
    @ApiOperation(value = "分页查询基站信息", notes = "该接口只返回基站基本信息")
    @GetMapping("query")
    public Result<PageResultInfo<NestBasicRespVO>> pageNestInfos(NestExtMonitorQueryReqVO condition) {
        NestExtFirmwareQueryInDTO transform = uosNestTransformer.transform(condition);
        PageResultInfo<NestExtFirmwareOutDTO> pageResultInfo = uosNestService.pageNestInfoWithFirmwares(transform);
        return Result.ok(pageResultInfo.map(r -> uosNestTransformer.transform(r)));
    }


}
