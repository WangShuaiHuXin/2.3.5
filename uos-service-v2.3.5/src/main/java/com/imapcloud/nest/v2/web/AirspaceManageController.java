package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.util.BeanCopyUtils;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.AirspaceManageService;
import com.imapcloud.nest.v2.service.dto.in.AirspaceManageInDTO;
import com.imapcloud.nest.v2.service.dto.in.AirspacePageInDTO;
import com.imapcloud.nest.v2.service.dto.in.AirspaceUploadFileInDTO;
import com.imapcloud.nest.v2.service.dto.out.AirspaceManageAirCoorOutDTO;
import com.imapcloud.nest.v2.service.dto.out.AirspaceManageOutDTO;
import com.imapcloud.nest.v2.web.vo.req.AirspaceManageReqVO;
import com.imapcloud.nest.v2.web.vo.req.AirspacePageReqVO;
import com.imapcloud.nest.v2.web.vo.req.AirspaceUploadFileReqVO;
import com.imapcloud.nest.v2.web.vo.resp.AirspaceManageAirCoorRespVO;
import com.imapcloud.nest.v2.web.vo.resp.AirspaceManageRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Classname AirspaceManageController
 * @Description 空域管理
 * @Date 2023/3/7 15:37
 * @Author Carnival
 */
@ApiSupport(author = "liujiahua@geoai.com", order = 1)
@Api(value = "空域管理", tags = "空域管理")
@RequestMapping("v2/airspace")
@RestController
public class AirspaceManageController {

    @Resource
    private AirspaceManageService airspaceManageService;

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 1)
    @ApiOperation(value = "申请空域")
    @PostMapping()
    public Result<String> addAirspace(@Validated @RequestBody AirspaceManageReqVO airspaceManageReqVO) {
        AirspaceManageInDTO inDTO = new AirspaceManageInDTO();
        BeanUtils.copyProperties(airspaceManageReqVO, inDTO);
        String res = airspaceManageService.addAirspace(inDTO);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 2)
    @ApiOperation(value = "批量删除空域域")
    @DeleteMapping()
    public Result<Boolean> deleteBatchAirspace(@RequestBody List<String> airspaceIds) {
        Boolean res = airspaceManageService.deleteBatchAirspace(airspaceIds);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 3)
    @ApiOperation(value = "分页查询空域列表", notes = "支持空域名称模糊搜索")
    @GetMapping("page")
    public Result<PageResultInfo<AirspaceManageRespVO>> pageRegionList(AirspacePageReqVO airspacePageReqVO) {
        AirspacePageInDTO pageInDTO = new AirspacePageInDTO();
        BeanCopyUtils.copyProperties(airspacePageReqVO, pageInDTO, true);
        PageResultInfo<AirspaceManageOutDTO> pageResultInfo = airspaceManageService.pageAirspaceList(pageInDTO);
        PageResultInfo<AirspaceManageRespVO> res = pageResultInfo.map(r -> {
            AirspaceManageRespVO vo = new AirspaceManageRespVO();
            BeanUtils.copyProperties(r, vo);
            return vo;
        });
        return Result.ok(res);
    }

    /**
     * @deprecated 2.2.3, 将在后续版本删除，
     * 使用新接口{@link AirspaceManageController#saveApprovalFile(com.imapcloud.nest.v2.web.vo.req.AirspaceUploadFileReqVO)}替代
     */
    @Deprecated
    @ApiOperationSupport(author = "jiahua@geoai.com", order = 4)
    @ApiOperation(value = "上传批复文件", notes = "")
    @PostMapping("upload/approvalFile")
    public Result<String> uploadApprovalFile(@Validated AirspaceUploadFileReqVO airspaceUploadFileReqVO) {
        throw new UnsupportedOperationException("接口已过时，请使用新接口");
//        AirspaceUploadFileInDTO inDTO = new AirspaceUploadFileInDTO();
//        BeanUtils.copyProperties(airspaceUploadFileReqVO, inDTO);
//        String path = airspaceManageService.uploadApprovalFile(inDTO);
//        return Result.ok(path);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 4)
    @ApiOperation(value = "保存批复文件信息", notes = "")
    @PostMapping("save/approvalFile")
    public Result<String> saveApprovalFile(@Validated @RequestBody AirspaceUploadFileReqVO airspaceUploadFileReqVO) {
        AirspaceUploadFileInDTO inDTO = new AirspaceUploadFileInDTO();
        BeanUtils.copyProperties(airspaceUploadFileReqVO, inDTO);
        String path = airspaceManageService.saveApprovalFile(inDTO);
        return Result.ok(path);
    }

    @ApiOperationSupport(author = "jiahua@geoai.com", order = 5)
    @ApiOperation(value = "上传边界范围", notes = "")
    @PostMapping("upload/airCoor")
    public Result<AirspaceManageAirCoorRespVO> uploadAirCoor(MultipartFile file) {
        AirspaceManageAirCoorOutDTO outDTO = airspaceManageService.uploadAirCoor(file);
        AirspaceManageAirCoorRespVO vo = new AirspaceManageAirCoorRespVO();
        BeanUtils.copyProperties(outDTO, vo);
        return Result.ok(vo);
    }

    /**
     * @deprecated 2.2.3，使用文件服务上传接口替代，将在后续版本删除
     */
    @Deprecated
    @ApiOperationSupport(author = "jiahua@geoai.com", order = 6)
    @ApiOperation(value = "上传范围截图", notes = "")
    @PostMapping("upload/photo")
    public Result<String> uploadPhoto(MultipartFile file) {
        throw new UnsupportedOperationException("接口已过时，请使用新接口");
//        String path = airspaceManageService.uploadPhoto(file);
//        return Result.ok(path);
    }

}
