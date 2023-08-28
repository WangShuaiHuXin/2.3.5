package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.DataPanoramaDetailService;
import com.imapcloud.nest.v2.service.DataPanoramaPointService;
import com.imapcloud.nest.v2.service.DataPanoramaRecordsService;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaDetailOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaPointOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaRecordsOutDTO;
import com.imapcloud.nest.v2.web.transformer.DataPanoramaDetailTransformer;
import com.imapcloud.nest.v2.web.transformer.DataPanoramaPointTransformer;
import com.imapcloud.nest.v2.web.transformer.DataPanoramaRecordsTransformer;
import com.imapcloud.nest.v2.web.vo.req.DataPanoramaDetailReqVO;
import com.imapcloud.nest.v2.web.vo.req.DataPanoramaPointReqVO;
import com.imapcloud.nest.v2.web.vo.req.DataPanoramaRecordsReqVO;
import com.imapcloud.nest.v2.web.vo.req.PanoramaDataDetailReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataPanoramaDetailRespVO;
import com.imapcloud.nest.v2.web.vo.resp.DataPanoramaPointRespVO;
import com.imapcloud.nest.v2.web.vo.resp.DataPanoramaRecordsRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaController.java
 * @Description DataPanoramaController
 * @createTime 2022年07月08日 17:03:00
 */
@ApiSupport(author = "zhongtaigbao@geoai.com", order = 2)
@Api(value = "数据管理-全景", tags = "数据管理-全景")
@RequestMapping("v2/data/panorama")
@RestController
public class DataPanoramaController {

    @Resource
    private DataPanoramaRecordsService dataPanoramaRecordsService;

    @Resource
    private DataPanoramaPointService dataPanoramaPointService;

    @Resource
    private DataPanoramaDetailService dataPanoramaDetailService;


    /**
     *  任务列表-分页查询（当前用户拥有的基站下面的巡检记录）
     * @param recordsReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "任务列表-分页查询", notes = "任务列表-分页查询（当前用户拥有的基站下面的巡检记录）")
    @GetMapping("/point/task/list")
    public Result<PageResultInfo<DataPanoramaRecordsRespVO.RecordsRespVO>> queryPointRecordsPage(@Valid DataPanoramaRecordsReqVO.RecordsReqVO recordsReqVO){
        PageResultInfo<DataPanoramaRecordsOutDTO.RecordsPageOutDTO> pageInfo = this.dataPanoramaRecordsService.queryPointRecordsPage(DataPanoramaRecordsTransformer.INSTANCES.transform(recordsReqVO));
        PageResultInfo<DataPanoramaRecordsRespVO.RecordsRespVO> pageResultInfo = pageInfo.map(DataPanoramaRecordsTransformer.INSTANCES::transform);
        return Result.ok(pageResultInfo);
    }

    /**
     *  任务列表- 根据架次记录/taskId查询航点信息
     * @param missionRecordsId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "任务列表-根据架次记录/taskId查询航点信息", notes = "根据架次记录/taskId查询航点信息")
    @GetMapping("/point/task/list/airPoint")
    public Result<List<DataPanoramaRecordsRespVO.AirPointRespVO>> queryAirPoint(@RequestParam(value = "missionRecordsId",required = false) String missionRecordsId , @RequestParam(value = "taskId",required = false) String taskId){
        List<DataPanoramaRecordsOutDTO.AirPointOutDTO> airPointOutDTOs = this.dataPanoramaRecordsService.queryAirPoint(missionRecordsId,taskId);
        List<DataPanoramaRecordsRespVO.AirPointRespVO> airPointRespVOs = airPointOutDTOs.stream().map(DataPanoramaRecordsTransformer.INSTANCES::transform).collect(Collectors.toList());
        return Result.ok(airPointRespVOs);
    }

    /**
     *  任务列表- 根据架次记录以及航点查找照片源数据 全量
     * @param picReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "任务列表-根据架次记录以及航点查找照片源数据 全量", notes = "任务列表-根据架次记录以及航点查找照片源数据 31条数据")
    @GetMapping("/point/task/list/pic")
    public Result<List<DataPanoramaRecordsRespVO.PicRespVO>> queryPointRecordsPic(@Valid DataPanoramaRecordsReqVO.PicReqVO picReqVO){
        List<DataPanoramaRecordsOutDTO.PicOutDTO> picOutDTOs = this.dataPanoramaRecordsService.queryPointRecordsPic(DataPanoramaRecordsTransformer.INSTANCES.transform(picReqVO));
        List<DataPanoramaRecordsRespVO.PicRespVO> picRespVOs = picOutDTOs.stream().map(DataPanoramaRecordsTransformer.INSTANCES::transform).collect(Collectors.toList());
        return Result.ok(picRespVOs);
    }

    /**
     *  全景点-查询全景航线
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "全景点-查询全景航线", notes = "全景点-查询全景航线")
    @GetMapping("/point/task/list/airLine")
    public Result<List<DataPanoramaRecordsRespVO.TaskRespVO>> queryPanoramaAirLine(){
        List<DataPanoramaRecordsOutDTO.TaskOutDTO> taskOutDTOS= this.dataPanoramaRecordsService.queryPanoramaTask();
        List<DataPanoramaRecordsRespVO.TaskRespVO> taskRespVOS = taskOutDTOS.stream().map(DataPanoramaRecordsTransformer.INSTANCES::transform).collect(Collectors.toList());
        return Result.ok(taskRespVOS);
    }

    /**
     * 全景点-新增
     * @param addPointReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "全景点新增", notes = "全景点新增")
    @PostMapping("/point/add")
    public Result<Boolean> addPoint(@RequestBody DataPanoramaPointReqVO.AddPointReqVO addPointReqVO){
        this.dataPanoramaPointService.addPoint(DataPanoramaPointTransformer.INSTANCES.transform(addPointReqVO));
        return Result.ok();
    }

    /**
     * 全景点-批量删除
     * @param pointIds
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "全景点-批量删除任务", notes = "全景点-批量删除任务")
    @DeleteMapping("/point/deleteBatch")
    public Result<Boolean> deletePoints(@RequestParam @Valid @NotNull(message = "{geoai_uos_cannot_empty_detailids}") List<String> pointIds){
        this.dataPanoramaPointService.deletePoints(pointIds);
        return Result.ok();
    }

    /**
     * 全景点-修改
     * @param updatePointReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 3)
    @ApiOperation(value = "全景点修改", notes = "全景点修改")
    @PostMapping("/point/update")
    public Result<Boolean> updatePoint(@Valid @RequestBody DataPanoramaPointReqVO.UpdatePointReqVO updatePointReqVO){
        this.dataPanoramaPointService.updatePoint(DataPanoramaPointTransformer.INSTANCES.transform(updatePointReqVO));
        return Result.ok();
    }

    /**
     *  全景点-分页查询
     * @param queryPageReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "全景点-分页查询", notes = "全景点-分页查询功能")
    @GetMapping("/point/queryPage")
    public Result<PageResultInfo<DataPanoramaPointRespVO.QueryPageRespVO>> queryPointPage(DataPanoramaPointReqVO.QueryPageReqVO queryPageReqVO){
        PageResultInfo<DataPanoramaPointOutDTO.QueryPageOutDTO> pageInfo = this.dataPanoramaPointService.queryPointPage(DataPanoramaPointTransformer.INSTANCES.transform(queryPageReqVO));
        PageResultInfo<DataPanoramaPointRespVO.QueryPageRespVO> pageResultInfo = pageInfo.map(DataPanoramaPointTransformer.INSTANCES::transform);
        return Result.ok(pageResultInfo);
    }

    /**
     *  全景点-全量查询
     * @param queryReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "全景点-全量查询-返回字段较少", notes = "全景点-全量查询-返回字段较少")
    @GetMapping("/point/queryAll")
    public Result<List<DataPanoramaPointRespVO.QueryLessRespVO>> queryAllPoints(DataPanoramaPointReqVO.QueryReqVO queryReqVO){
        List<DataPanoramaPointOutDTO.QueryLessOutDTO> queryLessOutDTOList = this.dataPanoramaPointService.queryAllPoints(DataPanoramaPointTransformer.INSTANCES.transform(queryReqVO));
        List<DataPanoramaPointRespVO.QueryLessRespVO> queryLessRespVOList = queryLessOutDTOList.stream()
                .map(DataPanoramaPointTransformer.INSTANCES::transform)
                .collect(Collectors.toList());
        return Result.ok(queryLessRespVOList);
    }

    /**
     *  全景点-指定查询-返回字段较全
     * @param queryOneReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "全景点-指定查询-返回字段较全", notes = "全景点-指定查询-返回字段较全")
    @GetMapping("/point/query")
    public Result<List<DataPanoramaPointRespVO.QueryRespVO>> queryPoint(DataPanoramaPointReqVO.QueryOneReqVO queryOneReqVO){
        List<DataPanoramaPointOutDTO.QueryOutDTO> queryOutDTOList = this.dataPanoramaPointService.queryPoint(DataPanoramaPointTransformer.INSTANCES.transform(queryOneReqVO));
        List<DataPanoramaPointRespVO.QueryRespVO> queryRespVOS = queryOutDTOList.stream()
                .map(DataPanoramaPointTransformer.INSTANCES::transform)
                .collect(Collectors.toList());
        return Result.ok(queryRespVOS);
    }

    /**
     * 全景明细-上传
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 4)
    @ApiOperation(value = "全景明细上传", notes = "全景明细上传")
    @PostMapping("/detail/upload")
    public Result<DataPanoramaDetailRespVO.QueryRespVO> uploadDetail(@Valid DataPanoramaDetailReqVO.DetailUploadReqVO dataPanoramaDetailReqVO,
                                                                     @RequestParam @Valid @NotNull(message = "压缩包不能为空!") MultipartFile fileData){
        DataPanoramaDetailOutDTO.QueryOutDTO queryOutDTO = this.dataPanoramaDetailService.uploadDetail(DataPanoramaDetailTransformer.INSTANCES.transform(dataPanoramaDetailReqVO), fileData);
        DataPanoramaDetailRespVO.QueryRespVO queryRespVO = DataPanoramaDetailTransformer.INSTANCES.transform(queryOutDTO);
        return Result.ok(queryRespVO);
    }

    /**
     * 全景明细-批量删除
     * @param detailIds
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 5)
    @ApiOperation(value = "全景明细-批量删除任务", notes = "全景明细-批量删除任务")
    @DeleteMapping("/detail/deleteBatch")
    public Result<Boolean> deleteDetails(@RequestParam @Valid @NotNull(message = "{geoai_uos_cannot_empty_detailids}") List<String> detailIds){
        this.dataPanoramaDetailService.deleteDetails(detailIds);
        return Result.ok();
    }


    /**
     *  全景明细-分页查询
     * @param queryPageReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "全景明细-分页查询", notes = "全景明细-分页查询功能")
    @GetMapping("/detail/queryPage")
    public Result<PageResultInfo<DataPanoramaDetailRespVO.QueryPageRespVO>> queryDetailPage(DataPanoramaDetailReqVO.QueryPageReqVO queryPageReqVO){
        PageResultInfo<DataPanoramaDetailOutDTO.QueryPageOutDTO> pageInfo = this.dataPanoramaDetailService.queryDetailPage(DataPanoramaDetailTransformer.INSTANCES.transform(queryPageReqVO));
        PageResultInfo<DataPanoramaDetailRespVO.QueryPageRespVO> pageResultInfo = pageInfo.map(DataPanoramaDetailTransformer.INSTANCES::transform);
        return Result.ok(pageResultInfo);
    }

    /**
     *  全景明细-全量查询-返回字段较少
     * @param queryReqVO
     * @return
     */

    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "全景明细-全量查询-返回字段较少", notes = "全景明细-全量查询-返回字段较少")
    @GetMapping("/detail/queryAll")
    public Result<List<DataPanoramaDetailRespVO.QueryLessRespVO>> queryAllDetail(DataPanoramaDetailReqVO.QueryReqVO queryReqVO){
        List<DataPanoramaDetailOutDTO.QueryLessOutDTO> queryLessOutDTOList = this.dataPanoramaDetailService.queryAllDetails(DataPanoramaDetailTransformer.INSTANCES.transform(queryReqVO));
        List<DataPanoramaDetailRespVO.QueryLessRespVO> queryLessRespVOList = queryLessOutDTOList.stream()
                .map(DataPanoramaDetailTransformer.INSTANCES::transform)
                .collect(Collectors.toList());
        return Result.ok(queryLessRespVOList);
    }

    /**
     *  全景明细-指定查询-返回字段较全
     * @param queryOneReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "全景明细-指定查询-返回字段较全", notes = "全景明细-指定查询-返回字段较全")
    @GetMapping("/detail/query")
    public Result<List<DataPanoramaDetailRespVO.QueryRespVO>> queryDetail(DataPanoramaDetailReqVO.QueryOneReqVO queryOneReqVO){
        List<DataPanoramaDetailOutDTO.QueryOutDTO> queryOutDTOList = this.dataPanoramaDetailService.queryDetail(DataPanoramaDetailTransformer.INSTANCES.transform(queryOneReqVO));
        List<DataPanoramaDetailRespVO.QueryRespVO> queryRespVOList = queryOutDTOList.stream()
                .map(DataPanoramaDetailTransformer.INSTANCES::transform)
                .collect(Collectors.toList());
        return Result.ok(queryRespVOList);
    }


    @ApiOperationSupport(author = "wumiao@geoai.com", order = 3)
    @ApiOperation(value = "保存全景明细数据")
    @PostMapping("detail/save")
    public Result<DataPanoramaDetailRespVO.QueryRespVO> savePanoramaDetail(@Validated @RequestBody PanoramaDataDetailReqVO body){
        DataPanoramaDetailOutDTO.QueryOutDTO queryOutDTO = dataPanoramaDetailService.savePanoramaDetail(DataPanoramaDetailTransformer.INSTANCES.transform(body));
        DataPanoramaDetailRespVO.QueryRespVO queryRespVO = DataPanoramaDetailTransformer.INSTANCES.transform(queryOutDTO);
        return Result.ok(queryRespVO);
    }

}
