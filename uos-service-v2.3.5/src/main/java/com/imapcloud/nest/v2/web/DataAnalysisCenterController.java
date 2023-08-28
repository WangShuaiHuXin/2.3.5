package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.common.enums.DataAnalysisPicTypeEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.DataAnalysisBaseService;
import com.imapcloud.nest.v2.service.DataAnalysisDetailService;
import com.imapcloud.nest.v2.service.DataAnalysisMarkService;
import com.imapcloud.nest.v2.service.DataAnalysisOperationTipService;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisHisResultDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkSaveInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.web.transformer.DataAnalysisBaseTransformer;
import com.imapcloud.nest.v2.web.transformer.DataAnalysisDetailTransformer;
import com.imapcloud.nest.v2.web.transformer.DataAnalysisMarkTransformer;
import com.imapcloud.nest.v2.web.vo.SelectVo;
import com.imapcloud.nest.v2.web.vo.req.*;
import com.imapcloud.nest.v2.web.vo.resp.*;
import com.imapcloud.sdk.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisCenter.java
 * @Description DataAnalysisCenterController
 * @createTime 2022年07月08日 17:03:00
 */
@ApiSupport(author = "zhongtaigbao@geoai.com", order = 2)
@Api(value = "分析统计-综合", tags = "分析统计-综合")
@RequestMapping("v2/dataAnalysis")
@RestController
public class DataAnalysisCenterController {

    @Resource
    private DataAnalysisBaseService dataAnalysisBaseService;

    @Resource
    private DataAnalysisDetailService dataAnalysisDetailService;

    @Resource
    private DataAnalysisMarkService dataAnalysisMarkService;

    @Resource
    private DataAnalysisOperationTipService dataAnalysisOperationTipService;

    //数据管理推送
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "推送分析中心", notes = "推送数据到数据分析中心（手动）")
    @PostMapping("/pushData")
    public Result<Boolean> pushData(@RequestBody DataManagerPushReqVO dataManagerPushReqVO) {
        return Result.ok();
    }


    /**
     * 提供时间、任务名、标签名分页查询功能
     *
     * @param dataAnalysisTaskPageReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "识别分析-任务列表-分页查询", notes = "根据时间、任务名、标签名分页查询功能")
    @PostMapping("/list/task/queryPage")
    public Result<PageResultInfo<DataAnalysisCenterBaseRespVO>> queryPage(@RequestBody DataAnalysisTaskPageReqVO dataAnalysisTaskPageReqVO) {
        PageResultInfo<DataAnalysisBasePageOutDTO> pageInfo = this.dataAnalysisBaseService.queryBasePage(DataAnalysisBaseTransformer.INSTANCES.transform(dataAnalysisTaskPageReqVO));
        PageResultInfo<DataAnalysisCenterBaseRespVO> pageResultInfo = pageInfo.map(DataAnalysisBaseTransformer.INSTANCES::transform);
        return Result.ok(pageResultInfo);
    }

    /**
     * 根据任务架次记录统计所有照片状态的照片数
     *
     * @param dataAnalysisTaskSumPicForRecordReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 3)
    @ApiOperation(value = "识别分析-任务列表-状态统计汇总", notes = "根据任务架次记录统计所有照片状态的照片数")
    @PostMapping("/list/task/collect")
    public Result<DataAnalysisSumPicRespVO> collect(@RequestBody DataAnalysisTaskSumPicForRecordReqVO dataAnalysisTaskSumPicForRecordReqVO) {
        DataAnalysisStateSumOutDTO dataAnalysisStateSumOutDTO = this.dataAnalysisDetailService.countStateSum(DataAnalysisDetailTransformer.INSTANCES.transform(dataAnalysisTaskSumPicForRecordReqVO));
        return Result.ok(DataAnalysisDetailTransformer.INSTANCES.transform(dataAnalysisStateSumOutDTO));
    }

    /**
     * 根据任务架次记录查询所有照片，按分页(可以跟照片列表queryPicByTaskAndState合并)--可废弃
     *
     * @param dataAnalysisTaskSumPicPageReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 4)
    @ApiOperation(value = "识别分析-任务列表-照片分页查询", notes = "根据任务架次记录查询所有照片，按分页")
    @PostMapping("/list/task/queryPicPage")
    public Result<PageResultInfo<DataAnalysisCenterDetailRespVO>> queryPicPage(@RequestBody DataAnalysisTaskSumPicPageReqVO dataAnalysisTaskSumPicPageReqVO) {
        PageResultInfo<DataAnalysisDetailPageOutDTO> pageInfo = this.dataAnalysisDetailService.queryDetailPage(DataAnalysisDetailTransformer.INSTANCES.transform(dataAnalysisTaskSumPicPageReqVO));
        PageResultInfo<DataAnalysisCenterDetailRespVO> pageResultInfo = pageInfo.map(DataAnalysisDetailTransformer.INSTANCES::transform);
        return Result.ok(pageResultInfo);
    }

    /**
     * 根据任务架次记录查询所有照片-主要给上下页浏览用
     *
     * @param picAllReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 4)
    @ApiOperation(value = "识别分析-任务列表-部分信息", notes = "根据任务架次返回照片部分信息-主要给上下页浏览用")
    @PostMapping("/list/task/queryPicForBrowseMark")
    public Result<List<DataAnalysisDetailMarkRespVO>> queryPicForBrowseMark(@RequestBody @Valid DataAnalysisQueryPicAllReqVO picAllReqVO) {
        List<DataAnalysisDetailOutDTO> dataAnalysisDetailOutDTOS = this.dataAnalysisDetailService.queryPicForBrowseMark(Long.valueOf(picAllReqVO.getCenterBaseId())
                , picAllReqVO.getPicStatus(), picAllReqVO.getPicType(), picAllReqVO.getDesc());
        List<DataAnalysisDetailMarkRespVO> dataAnalysisDetailMarkRespVOS = dataAnalysisDetailOutDTOS.stream()
                .map(DataAnalysisDetailTransformer.INSTANCES::transform)
                .collect(Collectors.toList());
        return Result.ok(dataAnalysisDetailMarkRespVOS);
    }

    /**
     * 根据任务架次记录查询所有照片-全量
     *
     * @param picAllReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 4)
    @ApiOperation(value = "识别分析-任务列表-照片全量查询-地图展示", notes = "照片全量查询-地图展示")
    @PostMapping("/list/task/queryPicAll")
    public Result<List<DataAnalysisDetailMarkAllRespVO>> queryPicAll(@RequestBody @Valid DataAnalysisQueryPicAllReqVO picAllReqVO) {
        List<DataAnalysisDetailOutDTO> dataAnalysisDetailOutDTOS = this.dataAnalysisDetailService.queryAllDetail(Long.valueOf(picAllReqVO.getCenterBaseId()), picAllReqVO.getPicStatus(), picAllReqVO.getPicType());
        List<DataAnalysisDetailMarkAllRespVO> dataAnalysisDetailMarkAllRespVOS = dataAnalysisDetailOutDTOS.stream().map(DataAnalysisDetailTransformer.INSTANCES::transformForBrowse).collect(Collectors.toList());
        return Result.ok(dataAnalysisDetailMarkAllRespVOS);
    }

    /**
     * 统计所有待分析、待统计数据
     *
     * @param dataAnalysisTaskSumPicForAllReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 5)
    @ApiOperation(value = "识别分析-任务列表-状态统计汇总", notes = "统计所有待分析、待核实数据")
    @PostMapping("/list/task/collectPic")
    public Result<DataAnalysisSumPicRespVO> collectPic(@RequestBody DataAnalysisTaskSumPicForAllReqVO dataAnalysisTaskSumPicForAllReqVO) {
        DataAnalysisStateSumOutDTO dataAnalysisStateSumOutDTO = this.dataAnalysisDetailService.countStateSum(DataAnalysisDetailTransformer.INSTANCES.transform(dataAnalysisTaskSumPicForAllReqVO));
        return Result.ok(DataAnalysisDetailTransformer.INSTANCES.transform(dataAnalysisStateSumOutDTO));
    }

    /**
     * 统计时间范围内每一天各个状态照片汇总数
     *
     * @param dataAnalysisTaskSumPicReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 6)
    @ApiOperation(value = "识别分析-任务列表-状态统计汇总返回按天", notes = "根据时间段返回每一天各个状态照片的汇总数")
    @PostMapping("/list/task/collectPicByDate")
    public Result<List<DataAnalysisSumPicRespVO>> collectPicByDate(@RequestBody DataAnalysisTaskSumPicForDateReqVO dataAnalysisTaskSumPicReqVO) {
        List<DataAnalysisStateSumOutDTO> dataAnalysisStateSumOutDTO = this.dataAnalysisDetailService.countStateSumByDate(DataAnalysisDetailTransformer.INSTANCES.transform(dataAnalysisTaskSumPicReqVO), true);
        List<DataAnalysisSumPicRespVO> dataAnalysisSumPicRespVOList = dataAnalysisStateSumOutDTO.stream().map(DataAnalysisDetailTransformer.INSTANCES::transform).collect(Collectors.toList());
        return Result.ok(dataAnalysisSumPicRespVOList);
    }

    /**
     * 批量删除任务
     *
     * @param baseIds
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 11)
    @ApiOperation(value = "识别分析-照片列表-批量删除任务", notes = "批量删除任务")
    @DeleteMapping("/list/task/delete")
    public Result<Boolean> deleteBase(@RequestParam @Valid @NotNull(message = "{geoai_uos_cannot_empty_detailids}") List<String> baseIds) {
        this.dataAnalysisBaseService.deleteData(baseIds.stream().map(Long::parseLong).collect(Collectors.toList()));
        return Result.ok();
    }

    /**
     * 根据任务架次记录、照片状态分页筛选照片数据
     *
     * @param dataAnalysisPicPageReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 7)
    @ApiOperation(value = "识别分析-照片列表-筛选照片数据", notes = "根据任务架次记录、照片状态分页筛选照片数据")
    @PostMapping("/list/detail/queryPicByTaskAndState")
    public Result<PageResultInfo<DataAnalysisCenterDetailRespVO>> queryPicByTaskAndState(@RequestBody DataAnalysisPicPageReqVO dataAnalysisPicPageReqVO) {
        PageResultInfo<DataAnalysisDetailPageOutDTO> pageInfo = this.dataAnalysisDetailService.queryDetailPage(DataAnalysisDetailTransformer.INSTANCES.transform(dataAnalysisPicPageReqVO));
        PageResultInfo<DataAnalysisCenterDetailRespVO> pageResultInfo = pageInfo.map(DataAnalysisDetailTransformer.INSTANCES::transform);
        return Result.ok(pageResultInfo);
    }

    /**
     * 返回照片类型（可见、红外）
     *
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 8)
    @ApiOperation(value = "识别分析-照片列表-获取照片类型", notes = "返回照片类型（可见、红外）")
    @GetMapping("/list/detail/getPicType")
    public Result<DataAnalysisPicTypeRespVO> getPicType() {
        DataAnalysisPicTypeRespVO dataAnalysisPicTypeRespVO = new DataAnalysisPicTypeRespVO();
        dataAnalysisPicTypeRespVO.setPicTypeEnum(DataAnalysisPicTypeEnum.toList());
        return Result.ok(dataAnalysisPicTypeRespVO);
    }

//    @Deprecated
//    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 9)
//    @ApiOperation(value = "识别分析-照片列表-批量修改照片状态", notes = "批量修改照片状态")
//    @PostMapping("/list/detail/modifyPicState")
//    public Result<Boolean> modifyPicState(@RequestBody List<String> photoId){
//        return Result.ok();
//    }

    /**
     * 批量核实图片 -一键核实
     *
     * @param detailIds
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 10)
    @ApiOperation(value = "识别分析-照片列表-一键核实", notes = "批量核实图片")
    @PostMapping("/list/detail/confirmPics")
    public Result<Boolean> confirmPics(@RequestBody @Valid @NotNull(message = "markID 不能为空") List<String> detailIds) {
        this.dataAnalysisDetailService.confirmPicList(detailIds.stream().filter(StringUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList()));
        return Result.ok();
    }

    /**
     * 重置照片-一键重置
     *
     * @param detailIds
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 10)
    @ApiOperation(value = "识别分析-照片列表-一键重置", notes = "批量重置照片")
    @PostMapping("/list/detail/resetPics")
    public Result<Boolean> resetPics(@RequestBody @Valid @NotNull(message = "{geoai_uos_cannot_empty_detailids}") List<String> detailIds) {
        this.dataAnalysisDetailService.resetPicList(detailIds.stream().filter(StringUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList()));
        return Result.ok();
    }

    /**
     * 撤回功能
     *
     * @param detailIds
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 10)
    @ApiOperation(value = "识别分析-照片列表-撤回", notes = "撤回照片")
    @PostMapping("/list/detail/unDoPics")
    public Result<Boolean> unDoPics(@RequestBody @Valid @NotNull(message = "{geoai_uos_cannot_empty_detailids}") List<String> detailIds) {
        this.dataAnalysisDetailService.unDoPics(detailIds.stream().filter(StringUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList()));
        return Result.ok();
    }

    /**
     * 批量下载图片-文件内存映射方式-下载为zip包含各标注原图、汇总图、地址图等
     *
     * @param detailIds
     * @param response
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 10)
    @ApiOperation(value = "识别分析-照片列表-批量下载照片", notes = "批量下载照片（下载标注后的图）")
    @GetMapping("/list/detail/download")
    public void download(@RequestParam("detailIds") @Valid @NotNull(message = "{geoai_uos_cannot_empty_detailids}") List<String> detailIds, HttpServletResponse response) {
        this.dataAnalysisDetailService.downloadData(detailIds.stream().filter(StringUtil::isNotEmpty).map(Long::parseLong).collect(Collectors.toList()), response);
    }

    /**
     * 批量删除
     *
     * @param detailIds
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 11)
    @ApiOperation(value = "识别分析-照片列表-批量删除照片", notes = "批量删除照片")
    @DeleteMapping("/list/detail/delete")
    public Result<Boolean> delete(@RequestParam @Valid @NotNull(message = "{geoai_uos_cannot_empty_baseid}") String baseId, @RequestParam @Valid @NotNull(message = "detailIds 不能为空") List<String> detailIds) {
        this.dataAnalysisBaseService.deleteData(Long.parseLong(baseId), detailIds.stream().map(Long::parseLong).collect(Collectors.toList()));
        return Result.ok();
    }


    /**
     * 照片标记
     *
     * @param dataAnalysisMarkReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 12)
    @ApiOperation(value = "识别分析-照片标记-照片标注", notes = "照片标注，记录对应坐标比例、缩放、裁剪比例、底图偏移量比例；问题等级、行业、问题类型  ")
    @PostMapping("/card/mark/markPic")
    public Result<List<String>> markPic(@RequestBody List<DataAnalysisMarkReqVO> dataAnalysisMarkReqVO) {
        List<DataAnalysisMarkSaveInDTO> collect = dataAnalysisMarkReqVO.stream()
                .map(r -> {
                    DataAnalysisMarkSaveInDTO transform = DataAnalysisMarkTransformer.INSTANCES.transform(r);
                    transform.setAiProblemName(r.getTopicProblemName());
                    return transform;
                })
                .collect(Collectors.toList());
        List<Long> markIds = this.dataAnalysisMarkService.saveBatch(collect);
        return Result.ok(markIds.stream().map(String::valueOf).collect(Collectors.toList()));
    }

    /**
     * 照片标注地址重置
     *
     * @param markAddrReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 12)
    @ApiOperation(value = "识别分析-照片标记-地址重置", notes = "照片标注地址重置")
    @PostMapping("/card/mark/markAddrReset")
    public Result<String> markAddrReset(@RequestBody DataAnalysisMarkAddrReqVO markAddrReqVO) {
        Long returnMarkId = this.dataAnalysisMarkService.markAddrReset(Long.parseLong(markAddrReqVO.getMarkId()), markAddrReqVO.getLongitude(), markAddrReqVO.getLatitude());
        return Result.ok(String.valueOf(returnMarkId));
    }

    /**
     * 照片标记地址图片清空
     *
     * @param markId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 12)
    @ApiOperation(value = "识别分析-照片标记-地址图片清空", notes = "地址图片清空")
    @PostMapping("/card/mark/markAddrDel/{markId}")
    public Result<String> markAddrDel(@PathVariable("markId") String markId) {
        Long returnMarkId = this.dataAnalysisMarkService.markAddrDel(Long.parseLong(markId));
        return Result.ok(String.valueOf(returnMarkId));
    }

    /**
     * 标注分页查询
     *
     * @param dataAnalysisMarkQueryPageReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 13)
    @ApiOperation(value = "识别分析-照片标记-照片标注分页查询", notes = "照片标注分页查询 ")
    @PostMapping("/card/mark/queryMarkPage")
    public Result<PageResultInfo<DataAnalysisMarkRespVO>> queryMarkPage(@RequestBody DataAnalysisMarkQueryPageReqVO dataAnalysisMarkQueryPageReqVO) {
        PageResultInfo<DataAnalysisMarkPageOutDTO> pageInfo = this.dataAnalysisMarkService.queryMarkPage(DataAnalysisMarkTransformer.INSTANCES.transform(dataAnalysisMarkQueryPageReqVO));
        PageResultInfo<DataAnalysisMarkRespVO> pageResultInfo = pageInfo.map(DataAnalysisMarkTransformer.INSTANCES::transform);
        return Result.ok(pageResultInfo);
    }

    /**
     * 标注查询
     *
     * @param dataAnalysisMarkQueryReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 13)
    @ApiOperation(value = "识别分析-照片标记-照片标注查询", notes = "照片标注查询 ")
    @PostMapping("/card/mark/queryMark")
    public Result<List<DataAnalysisMarkRespVO>> queryMark(@RequestBody DataAnalysisMarkQueryReqVO dataAnalysisMarkQueryReqVO) {
        List<DataAnalysisMarkOutDTO> dataAnalysisMarkOutDTOS = this.dataAnalysisMarkService.queryMark(DataAnalysisMarkTransformer.INSTANCES.transform(dataAnalysisMarkQueryReqVO));
        List<DataAnalysisMarkRespVO> results = dataAnalysisMarkOutDTOS.stream().map(DataAnalysisMarkTransformer.INSTANCES::transform).collect(Collectors.toList());
        return Result.ok(results);
    }

    /**
     * 标注删除
     *
     * @param markIds
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 14)
    @ApiOperation(value = "识别分析-照片标记-标注删除", notes = "照片标注删除（已核实不允许删除）")
    @DeleteMapping("/card/mark/delete")
    public Result<Boolean> deleteMark(@RequestParam @Valid @NotNull(message = "markIds 不能为空") List<String> markIds) {
        this.dataAnalysisMarkService.deleteMarks(markIds.stream()
                        .filter(StringUtil::isNotEmpty)
                        .map(Long::parseLong)
                        .collect(Collectors.toList())
                , Boolean.TRUE);
        return Result.ok();
    }

    /**
     * 标注核实
     *
     * @param markId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 15)
    @ApiOperation(value = "识别分析-照片标记-标注核实", notes = "标注核实")
    @PostMapping("/card/mark/confirm")
    public Result<Boolean> confirm(@Valid @NotNull(message = "markId不能为空") String markId) {
        Boolean bol = this.dataAnalysisMarkService.confirmMark(Long.parseLong(markId));
        return Result.ok(bol);
    }

//    /**
//     * 地址截图上传
//     * @deprecated 2.2.3，使用新接口{@link DataAnalysisCenterController#savePicAddrInfo(MarkAddrInfoReqVO)}替代，将在后续版本删除
//     */
//    @Deprecated
//    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 16)
//    @ApiOperation(value = "识别分析-照片标记-地址截图上传", notes = "地址截图上传")
//    @PostMapping("/card/mark/uploadAddrPic")
//    public Result<String> uploadAddrPic(MarkAddrInfo markAddrInfo, @RequestParam MultipartFile fileData) {
//        String addrImagePath = this.dataAnalysisMarkService.uploadAddrPic(Long.parseLong(markAddrInfo.getMarkId())
//                , markAddrInfo.getLongitude()
//                , markAddrInfo.getLatitude()
//                , markAddrInfo.getAddr()
//                , fileData);
//        return Result.ok(addrImagePath);
//    }

    /**
     * 历史照片
     *
     * @param dataAnalysisHistoryPicPageReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 17)
    @ApiOperation(value = "识别分析-照片标记-历史照片查询", notes = "历史照片查询")
    @PostMapping("/card/mark/queryHistory")
    public Result<PageResultInfo<DataAnalysisCenterDetailRespVO>> queryHistory(@Valid @RequestBody DataAnalysisHistoryPicPageReqVO dataAnalysisHistoryPicPageReqVO) {
        PageResultInfo<DataAnalysisDetailPageOutDTO> pageInfo = this.dataAnalysisDetailService.queryHistory(DataAnalysisDetailTransformer.INSTANCES.transform(dataAnalysisHistoryPicPageReqVO));
        PageResultInfo<DataAnalysisCenterDetailRespVO> resultInfo = pageInfo.map(DataAnalysisDetailTransformer.INSTANCES::transform);
        return Result.ok(resultInfo);
    }

    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 18)
    @ApiOperation(value = "识别分析-问题统计-统计不同等级数量", notes = "统计所有累计发现问题、不同等级问题数量")
    @GetMapping("/list/result/collect")
    public Result<List<DataAnalysisResultSumRespVO>> collectResult() {
        return Result.ok();
    }

    /**
     * 1 提示 0 不提示
     *
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 19)
    @ApiOperation(value = "识别分析-照片标记-设置操作提示", notes = "设置操作提示")
    @PostMapping("/operation/tip/{enable}")
    public Result<Boolean> setOperationTip(@PathVariable("enable") Integer enable) {
        this.dataAnalysisOperationTipService.setOperationTip(enable);
        return Result.ok();
    }

    /**
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 20)
    @ApiOperation(value = "识别分析-识别标记-获取操作提示", notes = "获取操作提示")
    @GetMapping("/operation/tip/get")
    public Result<Integer> getOperationTip() {
        return Result.ok(this.dataAnalysisOperationTipService.getOperationTip());
    }

    @ApiOperationSupport(order = 21)
    @ApiOperation(value = "识别分析-识别标记-架次记录筛选项", notes = "架次记录筛选项")
    @PostMapping("/card/mark/missionRecordCondition")
    public Result<Object> missionRecordCondition(@Valid @RequestBody DataAnalysisHistoryPicPageReqVO dataAnalysisHistoryPicPageReqVO) {
        List<DataAnalysisMissionRecordOutDTO> outDTOList = dataAnalysisDetailService.missionRecordCondition(DataAnalysisDetailTransformer.INSTANCES.transform(dataAnalysisHistoryPicPageReqVO));

        List<SelectVo> selectVoList = Lists.newLinkedList();
        for (DataAnalysisMissionRecordOutDTO dataAnalysisMissionRecordOutDTO : outDTOList) {

            SelectVo selectVo = new SelectVo();
            selectVo.setValue(dataAnalysisMissionRecordOutDTO.getMissionRecordId());
            selectVo.setLabel(dataAnalysisMissionRecordOutDTO.getName());
            selectVoList.add(selectVo);
        }
        return Result.ok(selectVoList);
    }

    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 22)
    @ApiOperation(value = "识别分析-照片标注-获取历史同类型问题数据", notes = "获取历史同类型问题数据")
    @GetMapping("list/hisResults")
    @Validated
    public Result<List<DataAnalysisHisResultRespVO.HisResultRespVO>> hisResults(
            @RequestParam @NotNull(message = "{geoai_uos_task_number_cannot_be_empty}") String missionId,
            @NotNull(message = "{geoai_uos_unit_cannot_be_null}") @RequestParam String orgCode,
            @RequestParam String topicProblemId,
            @RequestParam String beginTime,
            @RequestParam String endTime
    ) {
        List<DataAnalysisHisResultDTO.HisResultDTO> hisResultDTOS = this.dataAnalysisDetailService.hisResults(missionId, orgCode, topicProblemId, beginTime, endTime);
        List<DataAnalysisHisResultRespVO.HisResultRespVO> hisResultRespVO = hisResultDTOS.stream().map(item -> {
            DataAnalysisHisResultRespVO.HisResultRespVO vo = new DataAnalysisHisResultRespVO.HisResultRespVO();
            BeanUtils.copyProperties(item, vo);
            List<DataAnalysisHisResultRespVO.GroupicRespVo> groupicRespVoList = item.getGroupPics().stream().map(pic -> {
                DataAnalysisHisResultRespVO.GroupicRespVo build = DataAnalysisHisResultRespVO.GroupicRespVo.builder().thumImagePath(pic.getThumImagePath())
                        .markId(pic.getMarkId())
                        .resultId(pic.getResultId())
                        .resultImgPath(pic.getResultImgPath()).build();
                return build;
            }).collect(Collectors.toList());
            vo.setGroupPics(groupicRespVoList);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(hisResultRespVO);
    }

    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 23)
    @ApiOperation(value = "识别分析-照片标注-合并问题", notes = "合并问题")
    @PostMapping("detail/mergeResult")
    public Result mergeResult(@Valid @RequestBody DataAnalysisResultGroupReqVO.ResultMergeReqVO vo) {
        Boolean aBoolean = this.dataAnalysisDetailService.mergeResult(vo);
        if (aBoolean) {
            return Result.ok();
        }
        return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_MERGER.getContent()));
    }

    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 24)
    @ApiOperation(value = "识别分析-照片标注-取消合并问题", notes = "取消合并问题")
    @PostMapping("detail/undoMergeResult")
    public Result undoMergeResult(@Valid @RequestBody DataAnalysisResultGroupReqVO.ResultMergeReqVO vo) {
        Boolean aBoolean = this.dataAnalysisDetailService.undoMergeResult(vo);
        if (aBoolean) {
            this.dataAnalysisDetailService.resultBalance(vo.getGroupId());
            return Result.ok();
        }
        return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_CANCEL_MERGE.getContent())
        );
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 25)
    @ApiOperation(value = "识别分析-照片标注-保存截图", notes = "保存截图")
    @PostMapping("picAddr/save")
    public Result<Void> savePicAddrInfo(@Valid @RequestBody MarkAddrInfoReqVO body) {
        this.dataAnalysisMarkService.savePicAddrInfo(DataAnalysisMarkTransformer.INSTANCES.transform(body));
        return Result.ok();
    }

}
