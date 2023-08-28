package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.LayerVectorsService;
import com.imapcloud.nest.v2.service.dto.out.LayerVectorsOutDTO;
import com.imapcloud.nest.v2.web.transformer.LayerVectorsTransformer;
import com.imapcloud.nest.v2.web.vo.resp.LayerVectorsRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname LayerVectorsController
 * @Description 矢量管理API
 * @Date 2022/12/14 18:54
 * @Author Carnival
 */
@ApiSupport(author = "liujiahua@geoai.com", order = 1)
@Api(value = "图层矢量管理API", tags = "图层矢量管理API")
@RequestMapping("v2/vector")
@RestController
public class LayerVectorsController {

    @Resource
    private LayerVectorsService layerVectorsService;

    @Resource
    private LayerVectorsTransformer layerVectorsTransformer;
    
    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 1)
    @ApiOperation(value = "上传矢量图层")
    @PostMapping("upload")
    public Result<Boolean> uploadVectors(@RequestParam("file") MultipartFile file,
                                                    @RequestParam String orgCode, 
                                                    @RequestParam String layerVectorName) {
        Boolean res = layerVectorsService.uploadVectors(file, orgCode, layerVectorName);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 2)
    @ApiOperation(value = "根据单位获取矢量图层")
    @GetMapping("/{orgCode}")
    public Result<List<LayerVectorsRespVO>> queryVectors(@PathVariable String orgCode) {
        List<LayerVectorsOutDTO> layerVectorsOutDTOS = layerVectorsService.queryVectors(orgCode);
        if (CollectionUtils.isEmpty(layerVectorsOutDTOS)) {
            return Result.ok();
        }
        List<LayerVectorsRespVO> collect = layerVectorsOutDTOS.stream().map(r -> layerVectorsTransformer.transform(r)).collect(Collectors.toList());
        return Result.ok(collect);
    }


    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 3)
    @ApiOperation(value = "修改矢量图层名称")
    @PutMapping("/{layerVectorId}")
    public Result<Boolean> updateVectors(@PathVariable String layerVectorId, @RequestParam String name) {
        Boolean res = layerVectorsService.updateVectors(layerVectorId, name);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 4)
    @ApiOperation(value = "删除矢量图层")
    @DeleteMapping("/{layerVectorId}")
    public Result<Boolean> deleteVectors(@PathVariable String layerVectorId) {
        Boolean res = layerVectorsService.deleteVectors(layerVectorId);
        return Result.ok(res);
    }
}
