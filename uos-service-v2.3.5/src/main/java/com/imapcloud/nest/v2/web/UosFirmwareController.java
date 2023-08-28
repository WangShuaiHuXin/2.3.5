package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.common.enums.FirmwareTypeEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.UosFirmwareService;
import com.imapcloud.nest.v2.service.UosNestFirmwareService;
import com.imapcloud.nest.v2.service.dto.in.NestFirmwareVersionInDTO;
import com.imapcloud.nest.v2.service.dto.out.FirmwarePackageInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestFirmwarePackageInfoOutDTO;
import com.imapcloud.nest.v2.web.transformer.UosFirmwareTransformer;
import com.imapcloud.nest.v2.web.vo.req.FirmwarePackageReqVO;
import com.imapcloud.nest.v2.web.vo.req.FirmwareVersionQueryReqVO;
import com.imapcloud.nest.v2.web.vo.resp.FirmwarePackageInfoRespVO;
import com.imapcloud.nest.v2.web.vo.resp.NestFirmwarePackageInfoRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 基站管理API
 * @author Vastfy
 * @date 2022/07/08 11:11
 * @since 1.9.7
 */
@ApiSupport(author = "wumiao@geoai.com", order = 2)
@Api(value = "固件版本API", tags = "固件版本API")
@RequestMapping("v2/firmwares")
@RestController
public class UosFirmwareController {

    @Resource
    private UosFirmwareTransformer uosFirmwareTransformer;

    @Resource
    private UosFirmwareService uosFirmwareService;

    @Resource
    private UosNestFirmwareService uosNestFirmwareService;

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "分页查询固件安装包信息", notes = "目前固件安装包分CPS固件和MPS固件")
    @GetMapping("packages")
    public Result<PageResultInfo<FirmwarePackageInfoRespVO>> pageFirmwarePackageInfos(FirmwareVersionQueryReqVO condition){
        PageResultInfo<FirmwarePackageInfoOutDTO> pageResultInfo = uosFirmwareService.pageFirmwarePackageInfos(uosFirmwareTransformer.transform(condition));
        return Result.ok(pageResultInfo.map(r -> uosFirmwareTransformer.transform(r)));
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 2)
    @ApiOperation(value = "获取固件安装包版本信息", notes = "该接口目前只支持ZIP和APK格式解析，其他文件格式或解析失败会返回`unknown`")
    @PostMapping("packages/version")
    public Result<String> getFirmwarePackageVersion(@RequestParam("file") MultipartFile file) throws IOException {
        String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String packageFileVersion = uosFirmwareService.getFirmwarePackageVersion(filenameExtension, file.getInputStream());
        return Result.ok(packageFileVersion);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 3)
    @ApiOperation(value = "上传固件安装包信息", notes = "该接口目前只支持ZIP（MPS）和APK（CPS）格式解析，其他文件格式暂不支持")
    @ApiImplicitParam(name = "file", value = "固件安装包", paramType = "form", required = true)
    @PostMapping("packages/upload")
    public Result<String> uploadFirmwarePackage(@RequestParam("file") MultipartFile file, FirmwarePackageReqVO body) throws IOException {
        body.setFileName(file.getOriginalFilename());
        String firmwarePackageId = uosFirmwareService.uploadFirmwarePackage(file.getInputStream(), uosFirmwareTransformer.transform(body));
        return Result.ok(firmwarePackageId);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 4)
    @ApiOperation(value = "删除固件更新包信息", notes = "支持批量删除，删除会同步清理更新包文件")
    @DeleteMapping("packages")
    public Result<Boolean> dropFirmwarePackages(@RequestBody List<String> packageIds){
        return Result.ok(uosFirmwareService.dropFirmwarePackages(packageIds));
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 5)
    @ApiOperation(value = "分页查询基站固件安装包更新记录", notes = "")
    @ApiImplicitParam(name = "nestId", value = "基站编号", paramType = "path", required = true)
    @GetMapping("packages/nests/{nestId}")
    public Result<PageResultInfo<NestFirmwarePackageInfoRespVO>> pageNestFirmwarePackageInfos(@PathVariable String nestId,
                                                                                        FirmwareVersionQueryReqVO condition){
        NestFirmwareVersionInDTO query = uosFirmwareTransformer.transform2(condition);
        query.setNestId(nestId);
        PageResultInfo<NestFirmwarePackageInfoOutDTO> pageResultInfo = uosNestFirmwareService.pageNestFirmwarePackageInfos(query);
        return Result.ok(pageResultInfo.map(r -> uosFirmwareTransformer.transform(r)));
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 6)
    @ApiOperation(value = "推送安装固件安装包到基站", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nestId", value = "基站编号", paramType = "path", required = true),
            @ApiImplicitParam(name = "packageId", value = "固件安装包编号", paramType = "query", required = true),
            @ApiImplicitParam(name = "uavWhich", value = "无人机标识", paramType = "query", required = true)
    })
    @PostMapping("packages/nests/{nestId}/push/install")
    public Result<Boolean> pushFirmwarePackage2NestInstall(@PathVariable String nestId, String packageId , Integer uavWhich){
        return Result.ok(uosFirmwareService.pushFirmwarePackage2NestInstall(nestId, packageId,uavWhich));
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 7)
    @ApiOperation(value = "取消安装固件安装包到基站", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nestId", value = "基站编号", paramType = "path", required = true),
            @ApiImplicitParam(name = "type", value = "基站固件类型【cps或mps】", paramType = "path", allowableValues = "cps, mps", required = true),
            @ApiImplicitParam(name = "uavWhich", value = "无人机标识", paramType = "query", allowableValues = "0", required = true)
    })
    @PostMapping("packages/nests/{nestId}/cancel/install/{type:cps|mps}")
    public Result<Boolean> cancelNestFirmwarePackageInstall(@PathVariable String nestId, @PathVariable("type") String fpType,Integer uavWhich){
        Optional<FirmwareTypeEnum> optional = FirmwareTypeEnum.findMatch(fpType);
        if(!optional.isPresent()){
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONLY_SUPPORTED_FOR_CPS_OR_MPS_FIRMWARE.getContent())
            );
        }
        uosFirmwareService.cancelFirmwarePackage2NestInstall(nestId, optional.get().getType(),uavWhich);
        return Result.ok();
    }

}
