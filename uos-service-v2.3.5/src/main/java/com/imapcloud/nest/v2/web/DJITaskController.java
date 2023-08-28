package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.DJITaskFileService;
import com.imapcloud.nest.v2.service.dto.out.DJITaskOutDTO;
import com.imapcloud.nest.v2.web.transformer.DJITaskTransformer;
import com.imapcloud.nest.v2.web.vo.req.DJITaskReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DJITaskRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJILiveController.java
 * @Description DJILiveController
 * @createTime 2022年08月16日 11:57:00
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "大疆航线", tags = "大疆航线")
@RequestMapping("v2/dji/task")
@RestController
public class DJITaskController {

    @Resource
    private DJITaskFileService djiTaskService;

    /**
     * 大疆文件上传 kmz
     * @param djiTaskFileReqVO
     * @param file
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "大疆航线-文件上传", notes = "文件上传")
    @PostMapping("/uploadFile")
    public Result<DJITaskRespVO.DJITaskFileInfoRespVO> uploadFile( DJITaskReqVO.DJITaskFileReqVO djiTaskFileReqVO,
                                                                   @RequestParam @Valid @NotNull(message = "上传文件不能为空！") MultipartFile file){
        DJITaskOutDTO.DJITaskFileInfoOutDTO djiTaskFileInfoOutDTO= this.djiTaskService.uploadFile(djiTaskFileReqVO.getFileName(), djiTaskFileReqVO.getFileMD5(), file);
        DJITaskRespVO.DJITaskFileInfoRespVO djiTaskFileInfoRespVO = DJITaskTransformer.INSTANCES.transform(djiTaskFileInfoOutDTO);
        return Result.ok(djiTaskFileInfoRespVO);
    }

    /**
     * 根据taskId查询
     * @param taskId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "大疆航线-查询", notes = "大疆航线-查询")
    @GetMapping("/{taskId}")
    public Result<DJITaskRespVO.DJITaskInfoRespVO> getDJIAirLine(@PathVariable String taskId ){
        DJITaskRespVO.DJITaskInfoRespVO djiTaskInfoRespVO = new DJITaskRespVO.DJITaskInfoRespVO();
        DJITaskOutDTO.DJITaskInfoOutDTO djiTaskInfoOutDTO= this.djiTaskService.queryDJIAirLine(taskId);
        djiTaskInfoRespVO = DJITaskTransformer.INSTANCES.transform(djiTaskInfoOutDTO);
        return Result.ok(djiTaskInfoRespVO);
    }


    /**
     * KMZ解析->返回newJson字符
     */
    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 2)
    @ApiOperation(value = "KMZ解析", notes = "单纯KMZ解析，不包含文件上传")
    @PostMapping("/analysis")
    public Result<String> analysisKMZ(@RequestParam @Valid @NotNull(message = "上传文件不能为空！") MultipartFile file){
        String res = djiTaskService.analysisKMZ(file);
        return Result.ok(res);
    }
}
