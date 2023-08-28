package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.PersonManageService;
import com.imapcloud.nest.v2.service.dto.in.PersonManageInDTO;
import com.imapcloud.nest.v2.service.dto.in.PersonManagePageInDTO;
import com.imapcloud.nest.v2.service.dto.out.PersonManageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.PersonManagePageOutDTO;
import com.imapcloud.nest.v2.web.vo.req.PersonManagePageReqVO;
import com.imapcloud.nest.v2.web.vo.req.PersonManageReqVO;
import com.imapcloud.nest.v2.web.vo.req.PersonUploadFileReqVO;
import com.imapcloud.nest.v2.web.vo.resp.PersonManagePageRespVO;
import com.imapcloud.nest.v2.web.vo.resp.PersonManageRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Classname PersonManageController
 * @Description 人员管理
 * @Date 2023/3/28 10:40
 * @Author Carnival
 */
@ApiSupport(author = "liujiahua@geoai.com", order = 1)
@Api(value = "人员管理", tags = "人员管理")
@RequestMapping("v2/personManage")
@RestController
public class PersonManageController {

    @Resource
    private PersonManageService personManageService;

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 1)
    @ApiOperation(value = "添加人员")
    @PostMapping()
    public Result<String> addPerson(@Validated @RequestBody PersonManageReqVO reqVO) {
        PersonManageInDTO inDTO = new PersonManageInDTO();
        BeanUtils.copyProperties(reqVO, inDTO);
        String res = personManageService.addPerson(inDTO);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 2)
    @ApiOperation(value = "删除人员")
    @DeleteMapping()
    public Result<Boolean> deletePerson(String personId) {
        Boolean res = personManageService.deletePerson(personId);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 3)
    @ApiOperation(value = "修改人员信息")
    @PutMapping("/{personId}")
    public Result<Boolean> modifyPerson(@PathVariable String personId, @Validated @RequestBody PersonManageReqVO reqVO) {
        PersonManageInDTO inDTO = new PersonManageInDTO();
        BeanUtils.copyProperties(reqVO, inDTO);
        Boolean res = personManageService.modifyPerson(personId, inDTO);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 4)
    @ApiOperation(value = "查看人员信息")
    @GetMapping("/{personId}")
    public Result<PersonManageRespVO> queryPersonInfo(@PathVariable String personId) {
        PersonManageOutDTO outDTO = personManageService.queryPersonInfo(personId);
        PersonManageRespVO res = new PersonManageRespVO();
        BeanUtils.copyProperties(outDTO, res);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 5)
    @ApiOperation(value = "分页查询人员管理", notes = "支持空域名称模糊搜索")
    @GetMapping("page")
    public Result<PageResultInfo<PersonManagePageRespVO>> pagePersonInfo(PersonManagePageReqVO reqVO) {
        PersonManagePageInDTO inDTO = new PersonManagePageInDTO();
        BeanUtils.copyProperties(reqVO, inDTO);
        PageResultInfo<PersonManagePageOutDTO> pagePersonInfo = personManageService.pagePersonInfo(inDTO);
        PageResultInfo<PersonManagePageRespVO> res = pagePersonInfo.map(r -> {
            PersonManagePageRespVO vo = new PersonManagePageRespVO();
            BeanUtils.copyProperties(r, vo);
            return vo;
        });
        return Result.ok(res);
    }

    /**
     * @deprecated 2.2.3, 将在后续版本删除
     */
    @Deprecated
    @ApiOperationSupport(author = "jiahua@geoai.com", order = 6)
    @ApiOperation(value = "人员模块上传", notes = "类型：0-身份证正面、1-身份证反面、2-驾驶证")
    @PostMapping("upload")
    public Result<String> uploadPhoto(PersonUploadFileReqVO reqVO) {
        throw new UnsupportedOperationException("接口已过时，请使用新接口");
//        PersonUploadFileInDTO inDTO = new PersonUploadFileInDTO();
//        BeanUtils.copyProperties(reqVO, inDTO);
//        String res = personManageService.uploadPhoto(inDTO);
//        return Result.ok(res);
    }

    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 7)
    @ApiOperation(value = "统计人员信息", notes = "模糊查询")
    @GetMapping("/type/count")
    public Result<PersonManageRespVO.PersonManageCountRespVO> getTypeCount(@RequestParam(required = false) String orgCode, @RequestParam(required = false) String keyWord) {
        PersonManageOutDTO.PersonManageCountOutDTO outDTO = personManageService.getTypeCount(orgCode, keyWord);
        PersonManageRespVO.PersonManageCountRespVO vo = new PersonManageRespVO.PersonManageCountRespVO();
        BeanUtils.copyProperties(outDTO, vo);
        return Result.ok(vo);
    }
}
