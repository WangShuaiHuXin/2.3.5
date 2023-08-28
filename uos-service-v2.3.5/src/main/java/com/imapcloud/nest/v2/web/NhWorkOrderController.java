package com.imapcloud.nest.v2.web;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.pojo.dto.InspectionPlanInfoDto;
import com.imapcloud.nest.pojo.dto.InspectionPlanTriggerDto;
import com.imapcloud.nest.service.InspectionPlanService;
import com.imapcloud.nest.v2.service.NhWorkOrderService;
import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.web.vo.req.*;
import com.imapcloud.nest.v2.web.vo.resp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ApiSupport(author = "chenjiahong", order = 0)
@Api(value = "UOS前台-nh任务工单", tags = "UOS前台-任务工单")
@RequestMapping("v2/nh/order/")
@RestController
public class NhWorkOrderController {


    @Resource
    private NhWorkOrderService nhWorkOrderService;

    @Resource
    private InspectionPlanService inspectionPlanService;

    @ApiOperationSupport(author = "chenjiahong", order = 1)
    @ApiOperation("nh任务工单-查询南海任务工单列表")
    @GetMapping("list")
    public Result<PageResultInfo<NhOrderQueryRespVO>> queryOrderList(@Valid NhOrderQueryReqVO inVo) {
        NhQueryOrderInDTO inDTO = new NhQueryOrderInDTO();
        List<NhOrderQueryRespVO> collect = new ArrayList<>();
        BeanUtils.copyProperties(inVo, inDTO);
        NhQueryOrderOutDTO out = nhWorkOrderService.queryOrderList(inDTO);
        if (ObjectUtils.isEmpty(out)) {
            return Result.ok(PageResultInfo.of(0L, collect));
        }
        collect = out.getInfoList().stream().map(e -> {
            NhOrderQueryRespVO vo = new NhOrderQueryRespVO();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(PageResultInfo.of(out.getTotal(), collect));
    }

    @ApiOperationSupport(author = "chenjiahong", order = 2)
    @ApiOperation("nh任务工单-新增工单")
    @PostMapping("add")
    public Result addOrderInfo(@RequestBody @Valid NhOrderInfoReqVO vo) throws ParseException {
        NhOrderInfoInDTO infoInDTO = new NhOrderInfoInDTO();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Instant beginIns = format.parse(vo.getBeginTime()).toInstant();
        Instant endIns = format.parse(vo.getEndTime()).toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        infoInDTO.setBeginTime(beginIns.atZone(zoneId).toLocalDateTime());
        infoInDTO.setEndTime(endIns.atZone(zoneId).toLocalDateTime());
        BeanUtils.copyProperties(vo, infoInDTO);
        List<NhOrderInfoReqVO.VectorsInfoReq> vectors = vo.getVectors();
        if (CollectionUtil.isEmpty(vectors)) {
            infoInDTO.setVectors(null);
        }
        {
            List<NhOrderInfoInDTO.VectorsInDto> collect = vectors.stream().map(item -> {
                NhOrderInfoInDTO.VectorsInDto inDto = new NhOrderInfoInDTO.VectorsInDto();
                BeanUtils.copyProperties(item, inDto);
                return inDto;
            }).collect(Collectors.toList());
            infoInDTO.setVectors(collect);
        }
        String orderId = nhWorkOrderService.addOrderInfo(infoInDTO);
        Map<String, String> map = new HashMap<>();
        map.put("orderId", orderId);
        return Result.ok(map);
    }

    @ApiOperationSupport(author = "chenjiahong", order = 3)
    @ApiOperation("nh任务工单-编辑工单")
    @PutMapping("edit")
    public Result editOrderInfo(@RequestBody @Valid NhOrderInfoReqVO vo) {
        NhOrderInfoInDTO infoInDTO = new NhOrderInfoInDTO();
        BeanUtils.copyProperties(vo, infoInDTO);
        List<NhOrderInfoReqVO.VectorsInfoReq> vectors = vo.getVectors();
        if (CollectionUtil.isEmpty(vectors)) {
            infoInDTO.setVectors(null);
        }
        {
            List<NhOrderInfoInDTO.VectorsInDto> collect = vectors.stream().map(item -> {
                NhOrderInfoInDTO.VectorsInDto inDto = new NhOrderInfoInDTO.VectorsInDto();
                BeanUtils.copyProperties(item, inDto);
                return inDto;
            }).collect(Collectors.toList());
            infoInDTO.setVectors(collect);
        }
        nhWorkOrderService.editOrderInfo(infoInDTO);
        return Result.ok();
    }

    @ApiOperationSupport(author = "chenjiahong", order = 4)
    @ApiOperation("nh任务工单-查询南海任务工单详情")
    @GetMapping("details/{orderId}")
    public Result<NhOrderDetailRespVO> queryDetail(@PathVariable("orderId") String orderId) {
        NhOrderDetailRespVO vo = new NhOrderDetailRespVO();
        NhQueryDetailOutDTO outDTO = nhWorkOrderService.queryDetail(orderId);
        if (ObjectUtils.isEmpty(outDTO)) {
            return Result.ok();
        }
        BeanUtils.copyProperties(outDTO, vo);
        List<NhOrderDetailRespVO.Process> collect = outDTO.getProcess().stream().map(e -> {
            NhOrderDetailRespVO.Process process = new NhOrderDetailRespVO.Process();
            process.setDesc(e.getDesc());
            process.setNodeId(e.getNodeId());
            process.setUserName(e.getUserName());
            process.setFlag(e.getFlag());
            process.setOperationTime(e.getOperationTime());
            process.setRemark(e.getRemark());
            return process;
        }).collect(Collectors.toList());
        vo.setInspectionBeginTime(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(outDTO.getInspectionBeginTime()));
        vo.setInspectionEndTime(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(outDTO.getInspectionEndTime()));
        vo.setProcess(collect);
        return Result.ok(vo);
    }

    @ApiOperationSupport(author = "chenjiahong", order = 5)
    @ApiOperation("nh任务工单-查询矢量")
    @GetMapping("vector/{orderId}")
    public Result<List<NhOrderVertorRespVO>> queryVertor(@PathVariable("orderId") String orderId) {
        List<NhQueryVectorOutDTO> nhQueryVectorOutDTOS = nhWorkOrderService.queryVertor(orderId);
        List<NhOrderVertorRespVO> vo = Optional.ofNullable(nhQueryVectorOutDTOS)
                .map(e -> {
                    List<NhOrderVertorRespVO> collect = e.stream().map(item -> {
                        NhOrderVertorRespVO singleVo = new NhOrderVertorRespVO();
                        singleVo.setVectorId(item.getVectorId());
                        singleVo.setName(item.getName());
                        singleVo.setIndex(item.getIndex());
                        singleVo.setPointList(item.getPoints());
                        singleVo.setType(item.getType());
                        return singleVo;
                    }).collect(Collectors.toList());
                    return collect;
                })
                .orElse(new ArrayList<>());
        return Result.ok(vo);
    }

    @ApiOperationSupport(author = "chenjiahong", order = 6)
    @ApiOperation("nh任务工单-删除矢量")
    @DeleteMapping("vertor/{vertorId}")
    public Result deleteVertor(@PathVariable("vertorId") String vertorId) {
        nhWorkOrderService.deleteVertor(vertorId);
        return Result.ok();
    }

    @ApiOperationSupport(author = "chenjiahong", order = 7)
    @ApiOperation("nh任务工单-执行流程")
    @PostMapping("/execute/{orderId}")
    public Result executeOrder(@PathVariable("orderId") String orderId, @RequestBody NhExecuteOrderReqVO vo) {
        NhExecuteOrderInDTO inDTO = new NhExecuteOrderInDTO();
        inDTO.setStatus(vo.getStatus());
        inDTO.setRemark(vo.getRemark());
        inDTO.setOrderId(orderId);
        nhWorkOrderService.executeOrder(inDTO);
        return Result.ok();
    }

    @ApiOperationSupport(author = "chenjiahong", order = 8)
    @ApiOperation("nh任务工单-执行流程")
    @GetMapping("/execute/process/{orderId}")
    public Result<List<NhOrderDetailRespVO.Process>> executeProcessOrder(@PathVariable("orderId") String orderId) {
        List<NhQueryDetailOutDTO.OutDtoProcess> processList = nhWorkOrderService.executeProcessOrder(orderId);
        List<NhOrderDetailRespVO.Process> vo = new ArrayList<>();
        if (Optional.ofNullable(processList).isPresent()) {
            vo = processList.stream().map(e -> {
                NhOrderDetailRespVO.Process process = new NhOrderDetailRespVO.Process();
                BeanUtils.copyProperties(e, process);
                return process;
            }).collect(Collectors.toList());
        }
        return Result.ok(vo);
    }

    @ApiOperationSupport(author = "chenjiahong", order = 8)
    @ApiOperation("nh任务工单-任务编排-新增计划")
    @PutMapping("/plan")
    public Result addPlan(@RequestBody NhOrderPlanReqVO vo) {
        InspectionPlanInfoDto dto = new InspectionPlanInfoDto();
        dto.setName(vo.getName());
        dto.setNestId(vo.getNestId());
        dto.setGainDataMode(vo.getGainDataMode());
        dto.setAuto(vo.getAuto());
        dto.setGainVideo(vo.getGainVideo());
        dto.setMissionIds(vo.getMissionIds());
        NhOrderPlanReqVO.InspectionPlanTrigger inspectionPlanTrigger = vo.getInspectionPlanTrigger();
        InspectionPlanTriggerDto inspectionPlanTriggerDto = new InspectionPlanTriggerDto();
        BeanUtils.copyProperties(inspectionPlanTrigger, inspectionPlanTriggerDto);
        dto.setInspectionPlanTrigger(inspectionPlanTriggerDto);
        //planId
        Integer planId = inspectionPlanService.saveInspectionPlan(dto);
        //创建映射关系
        nhWorkOrderService.addPlanRel(vo.getOrderId(), planId);
        return Result.ok();
    }

    @ApiOperationSupport(author = "chenjiahong", order = 9)
    @ApiOperation("nh任务工单-任务编排-删除计划")
    @DeleteMapping("/plan")
    public Result deletedPlan(@RequestBody @Valid NhOrderPlanReqVO.PlanDeleteReqVO planDeleteReqVO) {
        NhOrderPlanInDTO dto = new NhOrderPlanInDTO();
        BeanUtils.copyProperties(planDeleteReqVO, dto);
        inspectionPlanService.deleteInspectionPlan(Integer.valueOf(planDeleteReqVO.getPlanId()));
        nhWorkOrderService.deletedPlan(dto);
        return Result.ok();
    }

    @ApiOperationSupport(author = "chenjiahong", order = 10)
    @ApiOperation("nh任务工单-任务编排-查询计划列表")
    @GetMapping("/plan/list/{orderId}")
    public Result<PageResultInfo<NhOrderPlanListRespVO>> listOrderPlan(@PathVariable("orderId") String orderId, @RequestParam Long pageNo, @RequestParam Long pageSize) {
        NhOrderPlanOutDTO outDTO = nhWorkOrderService.listOrderPlan(orderId, pageNo, pageSize);
        List<NhOrderPlanListRespVO> vos = new ArrayList<>();
        if (ObjectUtils.isEmpty(outDTO)) {
            return Result.ok(PageResultInfo.of(0, vos));
        }
        Long total = outDTO.getTotal();
        List<NhOrderPlanOutDTO.planInfo> records = outDTO.getRecords();
        List<NhOrderPlanListRespVO> collect = records.stream().map(i -> {
            NhOrderPlanListRespVO vo = new NhOrderPlanListRespVO();
            BeanUtils.copyProperties(i, vo);
            List<NhOrderPlanOutDTO.Mission> missionList = i.getMissionList();
            List<NhOrderPlanListRespVO.Mission> voMission = Optional.ofNullable(missionList).map(t -> {
                List<NhOrderPlanListRespVO.Mission> missions = t.stream().map(item -> {
                    NhOrderPlanListRespVO.Mission mission = new NhOrderPlanListRespVO.Mission();
                    mission.setMissionId(item.getMissionId());
                    mission.setMissionName(item.getMissionName());
                    mission.setMissionType(item.getMissionType());
                    mission.setTaskId(item.getTaskId());
                    return mission;
                }).collect(Collectors.toList());
                return missions;
            }).orElse(Collections.EMPTY_LIST);
            vo.setMissionList(voMission);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(PageResultInfo.of(total, collect));
    }

    @ApiOperationSupport(author = "chenjiahong", order = 11)
    @ApiOperation("nh任务工单-任务编排-查询巡检计划下拉数据")
    @GetMapping("data/inspection/option/{orderId}")
    public Result<List<NhOrderPlanOptionRespVO>> listOrderPlanOption(@PathVariable("orderId") String orderId) {
        List<NhOrderPlanOptionOutDTO> dto = nhWorkOrderService.listOrderPlanOption(orderId);
        List<NhOrderPlanOptionRespVO> vos = Optional.ofNullable(dto).map(e -> {
            List<NhOrderPlanOptionRespVO> collect = e.stream().map(item -> {
                NhOrderPlanOptionRespVO vo = new NhOrderPlanOptionRespVO();
                BeanUtils.copyProperties(item, vo);
                return vo;
            }).collect(Collectors.toList());
            return collect;
        }).orElse(Collections.EMPTY_LIST);
        return Result.ok(vos);
    }

    @ApiOperationSupport(author = "chenjiahong", order = 12)
    @ApiOperation("nh任务工单-任务编排-查询架次记录")
    @GetMapping("data/records/option/{planId}")
    public Result<List<NhOrderMissionOptionRespVO>> listRecordsMissionOption(@PathVariable("planId") String planId) {
        List<NhOrderMissionOptionOutDTO> dto = nhWorkOrderService.listRecordsMissionOption(Integer.valueOf(planId));
        List<NhOrderMissionOptionRespVO> vos = Optional.ofNullable(dto).map(e -> {
            List<NhOrderMissionOptionRespVO> collect = e.stream().map(item -> {
                NhOrderMissionOptionRespVO vo = new NhOrderMissionOptionRespVO();
                BeanUtils.copyProperties(item, vo);
                return vo;
            }).collect(Collectors.toList());
            return collect;
        }).orElse(Collections.EMPTY_LIST);
        return Result.ok(vos);
    }


    @ApiOperationSupport(author = "chenjiahong", order = 13)
    @ApiOperation("nh任务工单-数据管理-分页查询工单下图片数据")
    @GetMapping("/data/missonPhoto/getAllPhotoByCondition")
    public Result<PageResultInfo<NhOrderPhotoRespVO>> getAllPhotoByCondition(@Valid NhOrderDataPhotoReqVO inReq) {
        NhOrderPhotoInDTO inDTO = new NhOrderPhotoInDTO();
        List<NhOrderPhotoRespVO> collect = new ArrayList<>();
        BeanUtils.copyProperties(inReq, inDTO);
        NhOrderPhotoOutDTO dto = nhWorkOrderService.getAllPhotoByCondition(inDTO);
        if (ObjectUtils.isEmpty(dto)) {
            return Result.ok(PageResultInfo.of(0, collect));
        }
        collect = dto.getRecords().stream().map(item -> {
            NhOrderPhotoRespVO vo = new NhOrderPhotoRespVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(PageResultInfo.of(dto.getTotal(), collect));
    }

    @ApiOperationSupport(author = "chenjiahong", order = 14)
    @ApiOperation("nh任务工单-数据管理-查询工单下所有图片数据")
    @GetMapping("/data/missonPhoto/getAllPhotoByRecordId")
    public Result<PageResultInfo<NhOrderPhotoRespVO>> getAllPhotoByCondition(@RequestParam String recordId) {
        NhOrderPhotoInDTO inDTO = new NhOrderPhotoInDTO();
        inDTO.setRecordId(Integer.valueOf(recordId));
        NhOrderPhotoOutDTO dto = nhWorkOrderService.getAllPhotoByCondition(inDTO);
        List<NhOrderPhotoRespVO> collect = new ArrayList<>();
        if (ObjectUtils.isEmpty(dto)) {
            return Result.ok(PageResultInfo.of(0, collect));
        }
        collect = dto.getRecords().stream().map(item -> {
            NhOrderPhotoRespVO vo = new NhOrderPhotoRespVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(PageResultInfo.of(collect.size(), collect));
    }

    @ApiOperationSupport(author = "chenjiahong", order = 15)
    @ApiOperation("nh任务工单-数据管理-查询工单下视频")
    @GetMapping("/data/missonVideo/getAllVideoByCondition")
    public Result<PageResultInfo<NhOrderVideoRespVO.videoVo>> getAllVideoByCondition(@RequestParam Integer recordId) {
        NhOrderVideoOutDTO outDTO = nhWorkOrderService.getAllVideoByCondition(recordId);
        Optional<NhOrderVideoOutDTO> optional = Optional.ofNullable(outDTO);
        List<NhOrderVideoRespVO.videoVo> collect = new ArrayList<>();
        if (optional.isPresent()) {
            collect = outDTO.getInfoList().stream().map(e -> {
                NhOrderVideoRespVO.videoVo videoRespVO = new NhOrderVideoRespVO.videoVo();
                BeanUtils.copyProperties(e, videoRespVO);
                return videoRespVO;
            }).collect(Collectors.toList());
        }
        return Result.ok(PageResultInfo.of(collect.size(), collect));
    }

    /**
     * @deprecated 2.2.3，
     * 使用接口{@link NhWorkOrderController#addPatrolReport(com.imapcloud.nest.v2.web.vo.req.NhOrderReportReqVO)}替代，将在后续版本删除
     */
    @Deprecated
    @ApiOperationSupport(author = "chenjiahong", order = 16)
    @ApiOperation("nh任务工单-巡检报告-上传巡检报告")
    @PostMapping("/patrol/report")
    @Transactional
    public Result addPatrolReport(NhOrderReportReqVO reportReqVO, @RequestParam MultipartFile file) {
        throw new UnsupportedOperationException("接口已过时，请使用新接口");
//        nhWorkOrderService.addPatrolReport(reportReqVO, file);
//        return Result.ok();
    }

    @ApiOperationSupport(author = "chenjiahong", order = 16)
    @ApiOperation("nh任务工单-巡检报告-保存巡检报告")
    @PostMapping("/patrol/report/save")
    public Result<Void> addPatrolReport(@RequestBody NhOrderReportReqVO reportReqVO) {
        nhWorkOrderService.addPatrolReport(reportReqVO);
        return Result.ok();
    }

    @ApiOperationSupport(author = "chenjiahong", order = 17)
    @ApiOperation("nh任务工单-巡检报告-删除巡检报告")
    @DeleteMapping("/patrol/report")
    public Result delPatrolReport(@RequestParam String reportId) {
        nhWorkOrderService.delPatrolReport(reportId);
        return Result.ok();
    }

    @ApiOperationSupport(author = "chenjiahong", order = 18)
    @ApiOperation("nh任务工单-巡检报告-查询巡查报告列表")
    @GetMapping("/patrol/report/list")
    public Result<List<NhOrderReportRespVO>> getPatrolReportList(@RequestParam String orderId) {
        List<NhOrderReportOutDTO> dtos = nhWorkOrderService.getPatrolReportList(orderId);
        List<NhOrderReportRespVO> vos = new ArrayList<>();
        vos = dtos.stream().map(item -> {
            NhOrderReportRespVO vo = new NhOrderReportRespVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(vos);
    }
}
