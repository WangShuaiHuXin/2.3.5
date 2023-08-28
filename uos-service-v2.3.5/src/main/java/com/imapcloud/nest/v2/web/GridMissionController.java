package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.mapper.MissionPhotoMapper;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.v2.service.GridMissionService;
import com.imapcloud.nest.v2.service.dto.GridMissionDTO;
import com.imapcloud.nest.v2.service.dto.in.GridHistoryInDTO;
import com.imapcloud.nest.v2.service.dto.out.GridMissionRecordPageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;
import com.imapcloud.nest.v2.web.transformer.GridMissionTransformer;
import com.imapcloud.nest.v2.web.vo.req.GridHistoryReqVO;
import com.imapcloud.nest.v2.web.vo.req.GridMissionRecordPageReqVO;
import com.imapcloud.nest.v2.web.vo.resp.GridMissionRecordPageRespVO;
import com.imapcloud.nest.v2.web.vo.resp.GridRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname GridMissionController
 * @Description 网格任务Controller
 * @Date 2022/12/05 9:56
 * @Author Carnival
 */

@ApiSupport(author = "liujiahua@geoai.com", order = 1)
@Api(value = "网格任务管理API", tags = "网格任务管理API")
@RequestMapping("v2/grid/mission")
@RestController
public class GridMissionController {

    @Resource
    private GridMissionService gridMissionService;

    @Resource
    private GridMissionTransformer gridMissionTransformer;


    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 1)
    @ApiOperation(value = "分页查询网格任务列表", notes = "支持网格任务名称模糊搜索")
    @GetMapping("page")
    public Result<PageResultInfo<GridMissionRecordPageRespVO>> pageGridMissionRecord(GridMissionRecordPageReqVO vo) {
        PageResultInfo<GridMissionRecordPageRespVO> res = null;
        PageResultInfo<GridMissionRecordPageOutDTO> dto = gridMissionService.pageGridMissionRecord(gridMissionTransformer.transform(vo));
        if (!ObjectUtils.isEmpty(dto)) {
            res = dto.map(r -> {
                GridMissionRecordPageRespVO transform = gridMissionTransformer.transform(r);
                transform.setCreatedTime(dealTime(transform.getCreatedTime()));
                return transform;
            });
        }
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 2)
    @ApiOperation(value = "根据管理网格ID获取网格航点缩略图", notes = "可批量查询")
    @PostMapping("photo")
    public Result<List<GridRespVO.GridPhotoVO>> queryPhotoByGridManageIds(@RequestBody List<String> gridManageIds,
                                                                          @RequestParam(required = false) String gridInspectId,
                                                                          @RequestParam(required = false) Integer missionRecordsId,
                                                                          @RequestParam(required = false) String orgCode) {
        List<GridOutDTO.PhotoDTO> photoDTOS = gridMissionService.queryPhotoByGridIds(gridManageIds, gridInspectId, missionRecordsId, orgCode);
        if (CollectionUtils.isEmpty(photoDTOS)) {
            return Result.ok();
        }
        List<GridRespVO.GridPhotoVO> res = photoDTOS.stream().map(r -> gridMissionTransformer.transform(r)).collect(Collectors.toList());
        return Result.ok(res);
    }


    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 3)
    @ApiOperation(value = "根据管理网格ID获取巡检列表")
    @GetMapping("inspect")
    public Result<List<GridRespVO.InspectRecordVO>> queryMissionRocordByGridManageId(@RequestParam String gridManageId) {
        List<GridOutDTO.InspectRecordDTO> InspectRecordDTOS = gridMissionService.queryInspectByGridIds(gridManageId);
        if (CollectionUtils.isEmpty(InspectRecordDTOS)) {
            return Result.ok();
        }
        List<GridRespVO.InspectRecordVO> res = InspectRecordDTOS.stream().map(r -> gridMissionTransformer.transform(r)).collect(Collectors.toList());
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 4)
    @ApiOperation(value = "根据管理网格ID获取统计结果")
    @PostMapping("statistics")
    public Result<List<GridRespVO.GridStatisticsVO>> queryGridStatistics(@RequestBody List<String> gridManageIds) {
        List<GridOutDTO.GridStatisticsDTO> gridStatisticsDTOS = gridMissionService.queryGridStatistics(gridManageIds);
        if (CollectionUtils.isEmpty(gridStatisticsDTOS)) {
            return Result.ok();
        }
        List<GridRespVO.GridStatisticsVO> res = gridStatisticsDTOS.stream().map(r -> gridMissionTransformer.transform(r)).collect(Collectors.toList());
        return Result.ok(res);
    }


    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 5)
    @ApiOperation(value = "根据数据网格ID获取历史照片数据")
    @GetMapping("history")
    public Result<List<GridOutDTO.Photo>> queryHistoryByGridDataId(GridHistoryReqVO gridHistoryReqVO) {
        GridHistoryInDTO dto = gridMissionTransformer.transform(gridHistoryReqVO);
        List<GridOutDTO.Photo> photos = gridMissionService.queryHistoryByGridDataId(dto);
        return Result.ok(photos);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 6)
    @ApiOperation(value = "判断管理网格是否有数据", notes = "可批量查询")
    @PostMapping("hasData")
    public Result<List<GridRespVO.GridManageHasDataVO>> gridManageHasData(@RequestBody List<String> gridManageIds,
                                                                          @RequestParam(required = false) String startTime,
                                                                          @RequestParam(required = false) String endTime) {
        List<GridOutDTO.GridManageHasDataDTO> photoDTOS = gridMissionService.gridManageHasData(gridManageIds, startTime, endTime);
        if (CollectionUtils.isEmpty(photoDTOS)) {
            return Result.ok();
        }
        List<GridRespVO.GridManageHasDataVO> res = photoDTOS.stream().map(r -> gridMissionTransformer.transform(r)).collect(Collectors.toList());
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 7)
    @ApiOperation(value = "根据巡检记录ID获取架次状态")
    @GetMapping("missionRecordsStatus")
    public Result<List<GridRespVO.MissionStatusVO>> queryMissionStatus(String gridMangeId,
                                                                       @RequestParam(required = false) String gridInspectId,
                                                                       @RequestParam(required = false) String orgCode) {
        List<GridOutDTO.MissionStatusDTO> dto = gridMissionService.queryMissionStatus(gridMangeId, gridInspectId, orgCode);
        if (!CollectionUtils.isEmpty(dto)) {
            List<GridRespVO.MissionStatusVO> res = dto.stream().map(r -> gridMissionTransformer.transform(r)).collect(Collectors.toList());
            return Result.ok(res);
        }
        return Result.ok();
    }


    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 100)
    @ApiOperation(value = "test")
    @GetMapping("test")
    public Result<Boolean> queryTaskByGridIds() {
        return Result.ok();
    }

    private String dealTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime localDate = LocalDateTime.parse(time, formatter);
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDate);
    }
}
