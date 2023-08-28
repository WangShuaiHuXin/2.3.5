package com.imapcloud.nest.v2.web;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.bytearray.ByteArrayImageConverter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.DateUtils;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.common.collect.Lists;
import com.imapcloud.nest.utils.excel.EasyExcelStyleUtils;
import com.imapcloud.nest.utils.excel.InspectionReportStyleStrategy;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.config.DataAnalysisConfig;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.enums.PowerDeviceStateEnum;
import com.imapcloud.nest.v2.common.enums.PowerDsicernTypesEnum;
import com.imapcloud.nest.v2.common.enums.PowerInspectionExcelItemEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.service.PowerComponentService;
import com.imapcloud.nest.v2.service.PowerEquipmentService;
import com.imapcloud.nest.v2.service.PowerHomeService;
import com.imapcloud.nest.v2.service.PowerInspectionService;
import com.imapcloud.nest.v2.service.converter.PowerDataConverter;
import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.web.vo.req.*;
import com.imapcloud.nest.v2.web.vo.resp.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 电力相关接口
 *
 * @author boluo
 * @date 2022-11-24
 */
@RequestMapping("v2/power/")
@RestController
@Slf4j
public class PowerController {

    @Resource
    private PowerEquipmentService powerEquipmentService;

    @Resource
    private PowerComponentService powerComponentService;

    @Resource
    private PowerHomeService powerHomeService;

    @Resource
    private PowerInspectionService powerInspectionService;

    @Resource
    private RedisService redisService;

    @Resource
    private DataAnalysisConfig dataAnalysisConfig;

    @Resource
    private FileManager fileManager;

    @PostMapping("/equipment/upload")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 1)
    @ApiOperation("设备台账管理-导入")
    public Result<Object> equipmentExcelUpload(@RequestParam String orgCode, MultipartFile file) {
        PowerEquipmentDTO.PowerEquipmentUploadDto powerEquipmentUploadDto = powerEquipmentService.equipmentExcelUpload(file, orgCode);
        if (!powerEquipmentUploadDto.getFlag()) {
            throw new BusinessException(powerEquipmentUploadDto.getResultString());
        }
        return Result.ok(powerEquipmentUploadDto.getResultString());
    }

    @GetMapping("/equipment/list")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 2)
    @ApiOperation("设备台账管理-查询")
    public Result<PageResultInfo> equipmentListQuery(@RequestParam Integer pageNo,
                                                     @RequestParam Integer pageSize,
                                                     @RequestParam String orgCode,
                                                     @RequestParam(required = false) String equipmentName,
                                                     @RequestParam(required = false) String spacingUnitName,
                                                     @RequestParam(required = false) String voltageLevel,
                                                     @RequestParam(required = false) String beginTime,
                                                     @RequestParam(required = false) String endTime,
                                                     @RequestParam(required = false) String equipmentType
    ) {
        PowerEquipmentListOutDTO powerEquipmentListOutDTO = powerEquipmentService.equipmentListQuery(pageNo, pageSize, orgCode, equipmentName, spacingUnitName, voltageLevel, beginTime, endTime, equipmentType);
        List<PowerEquipmentListRespVO> powerEquipmentListRespVOS = new ArrayList<>();
        List<PowerEquipmentListOutDTO.PowerEquipmentObj> powerEquipmentLists = powerEquipmentListOutDTO.getPowerEquipmentLists();
        if (CollectionUtils.isEmpty(powerEquipmentLists)) {
            return Result.ok(PageResultInfo.of(0, powerEquipmentListRespVOS));
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        powerEquipmentListRespVOS = powerEquipmentLists.stream().map(e -> {
            PowerEquipmentListRespVO vo = new PowerEquipmentListRespVO();
            vo.setEquipmentId(e.getEquipmentId());
            vo.setOperationTime(e.getOperationTime().format(fmt));
            vo.setLastOperator(e.getLastOperator());
            vo.setSpacingUnit(e.getSpacingUnit());
            vo.setStationName(e.getStationName());
            vo.setVoltageLevel(e.getVoltageLevel());
            vo.setEquipmentType(e.getEquipmentType());
            vo.setEquipmentPmsId(e.getEquipmentPmsId());
            vo.setEquipmentName(e.getEquipmentName());
            vo.setCreatedTime(e.getCreatedTime().format(fmt));
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(PageResultInfo.of(powerEquipmentListOutDTO.getTotal(), powerEquipmentListRespVOS));
    }

    @PostMapping("/equipment/saveOrUpdate")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 3)
    @ApiOperation("设备台账管理-更新/新增")
    public Result<Object> equipmentSaveOrUpdate(@Valid @RequestBody PowerEquipmentEditReqVO vo) {
        PowerEquipmentInDTO.PowerEquipmentSaveOrUpdateDTO saveOrUpdateDTO = new PowerEquipmentInDTO.PowerEquipmentSaveOrUpdateDTO();
        BeanUtils.copyProperties(vo, saveOrUpdateDTO);
        powerEquipmentService.equipmentSaveOrUpdate(saveOrUpdateDTO);
        return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_001.getContent()));
    }

    @GetMapping("/equipment/{equipmentId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 4)
    @ApiOperation("设备台账管理-查询详情")
    public Result<PowerEquipmentListRespVO> queryEquipmentById(@PathVariable String equipmentId) {
        PowerEquipmentListOutDTO.PowerEquipmentObj powerEquipmentObj = powerEquipmentService.queryEquipmentById(equipmentId);
        PowerEquipmentListRespVO vo = new PowerEquipmentListRespVO();
        BeanUtils.copyProperties(powerEquipmentObj, vo);
        return Result.ok(vo);
    }

    @DeleteMapping("/equipment/delete/{equipmentId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 5)
    @ApiOperation("设备台账管理-删除设备")
    public Result deleteEquipment(@PathVariable("equipmentId") String equipmentId) {
        boolean b = powerEquipmentService.deleteEquipment(equipmentId);
        if (b) {
            return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_001.getContent()));
        }
        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_002.getContent()));

    }

    @PostMapping("/equipment/list/delete")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 6)
    @ApiOperation("设备台账管理-删除设备")
    public Result deleteEquipments(@RequestBody List<String> equipmentIds) {
        boolean b = powerEquipmentService.deleteEquipments(equipmentIds);
        if (b) {
            return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_001.getContent()));
        }
        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_003.getContent()));
    }

    @PostMapping("/waypoint/json/upload")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 7)
    @ApiOperation("航点台账管理-上传航点台账")
    public Result waypointEquipmentJsonUpload(@RequestParam String orgCode, MultipartFile file) {
        boolean b = powerEquipmentService.waypointEquipmentJsonUpload(orgCode, file);
        if (b) {
            return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_001.getContent()));
        }
        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_004.getContent()));
    }

    @GetMapping("/waypoint/list")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 8)
    @ApiOperation("航点台账管理-查询台账")
    @ApiImplicitParams({@ApiImplicitParam(value = "单位编码", name = "orgCode", dataType = "String"),
            @ApiImplicitParam(value = "设备层", name = "deviceLayer", dataType = "String"),
            @ApiImplicitParam(value = "单元层", name = "unitLayer", dataType = "String"),
            @ApiImplicitParam(value = "子区域", name = "subRegion", dataType = "String"),
            @ApiImplicitParam(value = "设备区域", name = "equipmentArea", dataType = "String"),
            @ApiImplicitParam(value = "设备层匹配状态", name = "equipmentStatu", dataType = "String"),
            @ApiImplicitParam(value = "部件层匹配状态", name = "componentStatu", dataType = "String"),
            @ApiImplicitParam(value = "当前页", name = "pageNo", dataType = "String"),
            @ApiImplicitParam(value = "页大小", name = "pageSize", dataType = "String"),
    })
    public Result<PageResultInfo<PowerWaypointListRespVO>> waypointEquipmentList(@RequestParam String orgCode,
                                                                                 @RequestParam(required = false) String deviceLayer,
                                                                                 @RequestParam(required = false) String unitLayer,
                                                                                 @RequestParam(required = false) String subRegion,
                                                                                 @RequestParam(required = false) String equipmentArea,
                                                                                 @RequestParam String equipmentStatu,
                                                                                 @RequestParam String componentStatu,
                                                                                 @RequestParam Integer pageNo,
                                                                                 @RequestParam Integer pageSize
    ) {
        PowerWaypointListInfoOutDTO dto = powerEquipmentService.waypointEquipmentList(orgCode, deviceLayer, unitLayer, subRegion, equipmentArea, equipmentStatu, componentStatu, pageNo, pageSize);
        List<PowerWaypointListInfoOutDTO.PowerWaypointInfoDTO> infoDTOList = dto.getInfoDTOList();
        List<PowerWaypointListRespVO> collect = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(infoDTOList)) {
            collect = infoDTOList.stream().map(e -> {
                PowerWaypointListRespVO vo = new PowerWaypointListRespVO();
                BeanUtils.copyProperties(e, vo);
                return vo;
            }).collect(Collectors.toList());
            return Result.ok(PageResultInfo.of(dto.getTotal(), collect));
        }
        return Result.ok(PageResultInfo.of(0, collect));
    }

    @GetMapping("/equipment/options/list")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 9)
    @ApiOperation("查找设备下拉")
    public Result<List<EquipmentOptionsListRespVO>> equipmentOptionList(String orgCode, @RequestParam(required = false) String keyWord) {
        List<EquipmentOptionListOutDTO> dtos = powerEquipmentService.equipmentOptionList(orgCode, keyWord);
        List<EquipmentOptionsListRespVO> collect = new ArrayList<>();
        if (dtos != null) {
            collect = dtos.stream().map(e -> {
                EquipmentOptionsListRespVO vo = new EquipmentOptionsListRespVO();
                vo.setEquipmentId(e.getEquipmentId());
                vo.setEquipmentName(e.getEquipmentName());
                return vo;
            }).collect(Collectors.toList());
        }
        return Result.ok(collect);
    }

    @PostMapping("/waypoint/equipmatch/artificial")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 10)
    public Result equipmatchArtificial(@RequestBody PowerArtificialReqVO vo) {
        powerEquipmentService.equipmatchArtificial(vo);
        return Result.ok();
    }

    @PostMapping("/waypoint/componentmatch/artificial")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 11)
    public Result componentmatchArtificial(@RequestBody PowerArtificialReqVO vo) {
        powerEquipmentService.componentmatchArtificial(vo);
        return Result.ok();
    }

    @PostMapping("/waypoint/equipmatch/auto")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 12)
    public Result<PowerEquipmentMatchRespVO> equipmatchAuto(@Validated @RequestBody PowerEquipmentMatchReqVO vo) {
        PowerEquipmentMatchOutDTO dto = powerEquipmentService.equipmatchAuto(vo);
        PowerEquipmentMatchRespVO resultVo = new PowerEquipmentMatchRespVO();
        resultVo.setFailureCount(dto.getFailureCount().toString());
        resultVo.setSuccessCount(dto.getSuccessCount().toString());
        return Result.ok(resultVo);
    }

    @PostMapping("/waypoint/componentmatch/auto")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 13)
    public Result<PowerEquipmentMatchRespVO> componentmatchAuto(@RequestBody PowerEquipmentMatchReqVO vo) {
        PowerEquipmentMatchOutDTO dto = powerEquipmentService.componentmatchAuto(vo);
        PowerEquipmentMatchRespVO resultVo = new PowerEquipmentMatchRespVO();
        resultVo.setFailureCount(dto.getFailureCount().toString());
        resultVo.setSuccessCount(dto.getSuccessCount().toString());
        return Result.ok(resultVo);
    }

    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 14)
    @GetMapping("/component/options/list")
    public Result<List<PowerComponentOptionListRespVO>> componentOptionList(String orgCode,
                                                                            @RequestParam(required = false) String keyWord,
                                                                            @RequestParam String wayPointStationId) {
        List<ComponentOptionListOutDTO> dtos = powerComponentService.componentOptionList(orgCode, keyWord, wayPointStationId);
        List<PowerComponentOptionListRespVO> collect = new ArrayList<>();
        if (dtos != null) {
            collect = dtos.stream().map(e -> {
                PowerComponentOptionListRespVO vo = new PowerComponentOptionListRespVO();
                vo.setComponentId(e.getComponentId());
                vo.setComponentName(e.getComponentName());
                return vo;
            }).collect(Collectors.toList());
        }
        return Result.ok(collect);
    }

    @PostMapping("component/saveOrUpdate")
    public Result<Object> componentSaveOrUpdate(@Valid @RequestBody PowerReqVO.ComponentSaveReqVO componentSaveReqVO) {

        PowerComponentInDTO.SaveOrUpdateInDTO saveOrUpdateInDTO = new PowerComponentInDTO.SaveOrUpdateInDTO();
        BeanUtils.copyProperties(componentSaveReqVO, saveOrUpdateInDTO);
        saveOrUpdateInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        powerComponentService.saveOrUpdate(saveOrUpdateInDTO);
        return Result.ok(true);
    }

    @PostMapping("component/delete/{componentId}")
    public Result<Object> componentDelete(@PathVariable String componentId) {

        String accountId = TrustedAccessTracerHolder.get().getAccountId();
        powerComponentService.componentDeleteByComponentId(componentId, accountId);
        return Result.ok(true);
    }

    @PostMapping("component/rule/update")
    public Result<Object> componentRuleEdit(@Valid @RequestBody PowerReqVO.ComponentRuleReqVO componentRuleReqVO) {

        PowerComponentInDTO.ComponentRuleInDTO componentRuleInDTO = new PowerComponentInDTO.ComponentRuleInDTO();
        componentRuleInDTO.setComponentId(componentRuleReqVO.getComponentId());
        componentRuleInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        List<PowerComponentInDTO.RuleInDTO> ruleInDTOList = Lists.newLinkedList();
        componentRuleInDTO.setRuleInDTOList(ruleInDTOList);
        if (CollUtil.isNotEmpty(componentRuleReqVO.getRuleInfoList())) {
            for (PowerReqVO.RuleInfoReqVO ruleInfoReqVO : componentRuleReqVO.getRuleInfoList()) {
                PowerComponentInDTO.RuleInDTO ruleInDTO = new PowerComponentInDTO.RuleInDTO();
                BeanUtils.copyProperties(ruleInfoReqVO, ruleInDTO);
                ruleInDTOList.add(ruleInDTO);
            }
        }
        powerComponentService.componentRuleEdit(componentRuleInDTO);
        return Result.ok(true);
    }

    @GetMapping("component/detail/{componentId}")
    public Result<PowerRespVO.ComponentInfoRespVO> componentDetail(@PathVariable String componentId) {

        PowerComponentOutDTO.PowerComponentInfoOutDTO powerComponentInfoOutDTO = powerComponentService.componentDetail(componentId);
        PowerRespVO.ComponentInfoRespVO componentInfoRespVO = new PowerRespVO.ComponentInfoRespVO();
        BeanUtils.copyProperties(powerComponentInfoOutDTO, componentInfoRespVO);
        return Result.ok(componentInfoRespVO);
    }

    @GetMapping("component/list")
    public Result<PageResultInfo<PowerRespVO.ComponentInfoRespVO>> componentList(@Valid PowerReqVO.ComponentListReqVO componentListReqVO) {

        PowerComponentInDTO.ComponentListInDTO componentListInDTO = new PowerComponentInDTO.ComponentListInDTO();
        componentListInDTO.setOrgCode(componentListReqVO.getOrgCode());
        componentListInDTO.setAnalysisType(componentListReqVO.getAnalysisType());
        componentListInDTO.setEquipmentType(componentListReqVO.getEquipmentType());
        componentListInDTO.setComponentName(componentListReqVO.getComponentName());
        componentListInDTO.setPageNo(componentListReqVO.getPageNo());
        componentListInDTO.setPageSize(componentListReqVO.getPageSize());

        try {
            if (!StringUtils.isEmpty(componentListReqVO.getStartTime())) {
                LocalDate start = DateUtils.toLocalDate(componentListReqVO.getStartTime(), DateUtils.DATE_FORMATTER_OF_CN);
                LocalDateTime startTime = LocalDateTime.of(start, LocalTime.of(0, 0, 0));
                componentListInDTO.setStart(startTime);
            }

            if (!StringUtils.isEmpty(componentListReqVO.getEndTime())) {
                LocalDate end = DateUtils.toLocalDate(componentListReqVO.getEndTime(), DateUtils.DATE_FORMATTER_OF_CN);
                LocalDateTime endTime = LocalDateTime.of(end, LocalTime.of(23, 59, 59));
                componentListInDTO.setEnd(endTime);
            }
        } catch (Exception e) {
            return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_005.getContent()));
        }

        PageResultInfo<PowerComponentOutDTO.PowerComponentInfoOutDTO> pageResultInfo = powerComponentService.componentList(componentListInDTO);

        List<PowerRespVO.ComponentInfoRespVO> componentInfoRespVOList = Lists.newLinkedList();
        for (PowerComponentOutDTO.PowerComponentInfoOutDTO powerComponentInfoOutDTO : pageResultInfo.getRecords()) {
            PowerRespVO.ComponentInfoRespVO componentInfoRespVO = new PowerRespVO.ComponentInfoRespVO();
            BeanUtils.copyProperties(powerComponentInfoOutDTO, componentInfoRespVO);
            componentInfoRespVOList.add(componentInfoRespVO);
        }

        return Result.ok(PageResultInfo.of(pageResultInfo.getTotal(), componentInfoRespVOList));
    }

    @GetMapping("/home/{orgCode}/data/static")
    public Result<PowerHomeBaseSettingRespVO> queryDataStatic(@PathVariable String orgCode) {
        PowerHomeSettingOutDTO dto = powerHomeService.queryDataStatic(orgCode);
        PowerHomeBaseSettingRespVO convert = PowerDataConverter.INSTANCE.convert(dto);
        return Result.ok(convert);
    }

    @ApiOperation("核实")
    @PostMapping("/meters/verification/batch")
    public Result<Object> verificationBatch(@RequestBody InspectionVerificationBatchReqVO reqVO) {
        InspectionVerificationBatchInDTO build = InspectionVerificationBatchInDTO.builder().ids(reqVO.getIds())
                .verificationStatus(reqVO.getVerificationStatus())
                .analysisType(reqVO.getAnalysisType().toString()).build();
        boolean flag = powerInspectionService.verificationBatch(build);
        if (flag) {
            return Result.ok();
        }
        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_008.getContent()));
    }

    @PostMapping("/meters/withdrawal/batch")
    public Result<Object> withdrawalBatch(@RequestBody List<String> batchIds) {
        boolean flag = powerInspectionService.withdrawalBatch(batchIds);
        if (flag) {
            return Result.ok();
        }
        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_008.getContent()));
    }

    @ApiOperation("首页-根据设备类型查询巡检报告信息")
    @GetMapping("/home/Inspection/equipment/list")
    public Result<List<PowerInspectionEquipmentListRespVO>> inspectionEquipmentList(InspectionEquipmentReqVO reqVO) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        InspectionEquipmentInDTO inDto = new InspectionEquipmentInDTO();
        BeanUtils.copyProperties(reqVO, inDto);
        inDto.setOrgCode(orgCode);
        List<PowerInspectionEquipmentListOutDTO> dto = powerHomeService.inspectionEquipmentList(inDto);
        List<PowerInspectionEquipmentListRespVO> collect = dto.stream().map(e -> {
            PowerInspectionEquipmentListRespVO vo = new PowerInspectionEquipmentListRespVO();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(collect);
    }

    @GetMapping("/home/{orgCode}/equipment/tree")
    public Result<PageResultInfo<PowerEquipmentTreeRespVO>> equipmentTree(@PathVariable String orgCode, @RequestParam(required = false) String keyWord) {
        PowerEquipmentTreeOutDTO dto = powerHomeService.equipmentTree(orgCode, keyWord);
        List<PowerEquipmentTreeRespVO> treeRespVOS = dto.getInfoList().stream().map(e -> {
            PowerEquipmentTreeRespVO vo = new PowerEquipmentTreeRespVO();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(PageResultInfo.of(dto.getTotal(), treeRespVOS));
    }

    @PostMapping("/home/point/{orgCode}/inspection/queryBy")
    public Result<PageResultInfo<PowerHomeInspectionQueryRespVO>> inspectionQueryBy(@PathVariable String orgCode, @RequestBody PowerHomeInspectionQueryByReqVO reqVO) {
        reqVO.setOrgCode(orgCode);
        if (!StringUtils.hasText(orgCode)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_007.getContent()));
        }
        PowerHomeInspectionQueryByOutDTO dto = powerHomeService.inspectionQueryBy(reqVO);
        List<PowerHomeInspectionQueryRespVO> collect = new ArrayList<>();
        if (dto.getTotal() > 0) {
            collect = dto.getInfoList().stream().map(e -> {
                PowerHomeInspectionQueryRespVO vo = new PowerHomeInspectionQueryRespVO();
                BeanUtils.copyProperties(e, vo);
                return vo;
            }).collect(Collectors.toList());
        }
        return Result.ok(PageResultInfo.of(dto.getTotal(), collect));
    }

    @GetMapping("/home/{orgCode}/inspection/statistics")
    public Result<List<PowerHomeInspectionStatisticsRespVO>> inspcetionStatistics(@PathVariable String orgCode, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime) {
        List<PowerHomeInspectionStatisticsOutDTO> dto = powerHomeService.inspcetionStatistics(orgCode, beginTime, endTime);
        List<PowerHomeInspectionStatisticsRespVO> collect = dto.stream().map(e -> {
            PowerHomeInspectionStatisticsRespVO vo = new PowerHomeInspectionStatisticsRespVO();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(collect);
    }

    @PostMapping("/home/{orgCode}/data/static")
    public Result<Object> inspectionStatisticsEdit(@PathVariable String orgCode, @Valid @RequestBody PowerHomeBaseSettingRespVO reqVO) {
        InspectionStatisticsInDTO build = new InspectionStatisticsInDTO();
        build.setCoverageArea(reqVO.getCoverageArea());
        build.setInspectionPoints(reqVO.getInspectPoint());
        build.setOrgCode(orgCode);
        build.setGeneralInspection(reqVO.getTotalTimes());
        build.setTodayInspection(reqVO.getTodayTimes());
        build.setCumulativePhotography(reqVO.getCumulativePhoto());
        build.setInspectionNormal(reqVO.getInspectionNormal());
        build.setGeneralDefects(reqVO.getInspectionGeneralDefects());
        build.setSeriousDefects(reqVO.getInspectionSeriousDefects());
        build.setCriticalDefects(reqVO.getInspectionCriticalDefects());
        build.setStatisticsProcessed(reqVO.getAlarmProcessed());
        build.setStatisticsPending(reqVO.getAlarmPending());
        BeanUtils.copyProperties(reqVO, build);
        build.setOrgCode(orgCode);
        boolean flag = powerHomeService.inspectionStatisticsEdit(build);
        if (flag) {
            return Result.ok();
        }
        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_008.getContent()));
    }

    @ApiOperation("已处理/未处理统计查询")
    @GetMapping("/home/{orgCode}/alarm/statistics")
    @Validated
    public Result<PowerHomeInspectionStatisticsRespVO.PowerHomeAlarmStatisticsRespVO> alarmStatistics(@PathVariable String orgCode,
                                                                                                      @RequestParam(required = false) String beginTime,
                                                                                                      @RequestParam(required = false) String endTime) {
        PowerHomeInspectionStatisticsOutDTO.PowerHomeAlarmStatisticsOutDTO dto = powerHomeService.alarmStatistics(orgCode, beginTime, endTime);
        PowerHomeInspectionStatisticsRespVO.PowerHomeAlarmStatisticsRespVO vo = new PowerHomeInspectionStatisticsRespVO.PowerHomeAlarmStatisticsRespVO();
        vo.setAlarmProcessed(dto.getAlarmProcessed());
        vo.setAlarmPending(dto.getAlarmPending());
        return Result.ok(vo);
    }

    @ApiOperation("设备类型异常查询")
    @GetMapping("/home/equipmentType/inspection/statistics")
    public Result<List<PowerHomeEquipmentTypeInspcetionRespVO>> equipmentTypeInspectionStatistics(@RequestParam(required = false) String beginTime,
                                                                                                  @RequestParam(required = false) String endTime) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        List<PowerHomeEquipmentTypeInspcetionOutDTO> dto = powerHomeService.equipmentTypeInspectionStatistics(orgCode, beginTime, endTime);
        List<PowerHomeEquipmentTypeInspcetionRespVO> collect = dto.stream().map(e -> {
            PowerHomeEquipmentTypeInspcetionRespVO vo = new PowerHomeEquipmentTypeInspcetionRespVO();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(collect);
    }

    @ApiOperation("巡检报告列表查询")
    @PostMapping("/inspection/{orgCode}/queryPage")
    public Result<PageResultInfo<InspectionQueryPageRespVO>> inspectionQueryPage(@PathVariable String orgCode, @RequestBody InspectionQueryPageReqVO reqVO) {
        InspectionQueryPageInDTO indto = new InspectionQueryPageInDTO();
        BeanUtils.copyProperties(reqVO, indto);
        indto.setOrgCode(orgCode);
        InspectionQueryPageOutDTO dto = powerInspectionService.inspectionQueryPage(indto);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<InspectionQueryPageRespVO> vos = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dto.getInfoList())) {
            List<InspectionQueryPageOutDTO.InspectionQueryPageOutInfo> infoList = dto.getInfoList();
            vos = infoList.stream().map(e -> {
                InspectionQueryPageRespVO vo = new InspectionQueryPageRespVO();
                BeanUtils.copyProperties(e, vo);
                vo.setInspectionPhotoUrl(e.getInsprctionPhotoUrl());
                vo.setAnalysisConclusion(e.getAnalysisConclusion().toString());
                vo.setPhotographyTime(fmt.format(e.getPhotographyTime()));
                vo.setInspcetionId(e.getInspcetionId());
                vo.setAnalysisConclusionDesc(e.getAnalysisConclusionDesc());
                return vo;
            }).collect(Collectors.toList());
        }
        return Result.ok(PageResultInfo.of(dto.getTotal(), vos));
    }


    //@GetMapping("/inspection/export") //@RequestParam String items) {
    @CrossOrigin
    @PostMapping("/inspection/export")
    public void inspectionExport(HttpServletResponse response, @RequestBody PowerInspectionExportReqVO reqVO) {
        String redisKey = RedisKeyConstantList.INSPECTION_REPORT_EXPORT;
        List<InspectionExportRespVO> respVOS = new ArrayList<>();
        String ids = reqVO.getIds();
        String items = reqVO.getItems();
        //通过items获取对应需要展示的字段index
        String[] itemSplit = items.split(",");
        List<String> itemNos = Arrays.asList(itemSplit);
//        InputStream object = null;
//        InputStream object2 = null;
        try {
            long increment = redisService.incr(redisKey);
            log.info("#PowerController.inspectionExport# increment={}", increment);
            if (increment > dataAnalysisConfig.getMaxExportNum()) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TOO_MANY_PEOPLE_EXPORTED.getContent()));
            }
            if (StringUtils.hasText(ids)) {
                List<String> list = Lists.newLinkedList();
                String[] split = ids.split(",");
                for (String s : split) {
                    list.add(s);
                }
                InspectionQueryPageInDTO inDTO = new InspectionQueryPageInDTO();
                inDTO.setIds(list);
                InspectionQueryPageOutDTO dto = powerInspectionService.inspectionQueryPage(inDTO);
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                if (dto.getTotal() > 0) {
                    List<InspectionQueryPageOutDTO.InspectionQueryPageOutInfo> infoList = dto.getInfoList();
                    for (InspectionQueryPageOutDTO.InspectionQueryPageOutInfo info : infoList) {
                        InspectionExportRespVO vo = new InspectionExportRespVO();
                        if (itemNos.contains(PowerInspectionExcelItemEnum.ANA_SCREENSHOTS.getCode().toString())) {
                            try(InputStream inputStream = fileManager.getInputSteam(info.getScreenShootUrl())){
                                byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
                                vo.setMeterImg(bytes);
                            }
//                            object = MinIoUnit.getObject(info.getScreenShootUrl());
//                            if (object != null) {
//                                vo.setMeterImg(IoUtil.readBytes(object));
//                            }
                        }
                        if (itemNos.contains(PowerInspectionExcelItemEnum.PATROL_PHOTOS.getCode().toString())) {
                            try(InputStream inputStream = fileManager.getInputSteam(info.getThumbnailUrl())){
                                byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
                                vo.setInspectionImg(bytes);
                            }
//                            object2 = MinIoUnit.getObject(info.getThumbnailUrl());
//                            if (object2 != null) {
//                                vo.setInspectionImg(IoUtil.readBytes(object2));
//                            }
                        }
                        vo.setComponenName(info.getComponentName());
                        vo.setEquipmentName(info.getEquipmentName());
                        vo.setInspectionType(PowerDsicernTypesEnum.getValueByCode(info.getAnalysisType()));
                        StringBuffer buffer = new StringBuffer();
                        List<InspectionQueryPageOutDTO.ReadingInfo> readingInfos = info.getReadingInfos();
                        if (CollectionUtil.isNotEmpty(readingInfos)) {
                            readingInfos.forEach(item -> {
                                if (StringUtils.isEmpty(item.getValue())) {
                                    buffer.append(item.getKey());
                                } else {
                                    buffer.append(ObjectUtils.isNotEmpty(item.getKey()) ? item.getKey() + " :" : " " + " :");
                                    buffer.append(item.getValue() + " ");
                                }
                            });
                            vo.setInspectionResult(buffer.toString());
                        }
                        if (StringUtils.hasText(info.getAlarmReson())) {
                            vo.setInspectionConclusion(PowerDeviceStateEnum.getValueByCode(info.getAnalysisConclusion()) + "(" + info.getAlarmReson() + ")");
                        } else {
                            vo.setInspectionConclusion(PowerDeviceStateEnum.getValueByCode(info.getAnalysisConclusion()));
                        }
                        vo.setEquipmentType(info.getEquipmentType());
                        vo.setSpacUnit(info.getSpacUnit());
                        vo.setVoltageLevel(info.getVoltageLevel());
                        vo.setPhotoGraphyTime(fmt.format(info.getPhotographyTime()));
                        respVOS.add(vo);
                    }
                }
            }
            //表头
            List<List<String>> head = head(itemNos);
            //数据
            List<List<Object>> lists = listData(respVOS, InspectionExportRespVO.class, head);
            String fileName = String.format(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_009
                    .getContent()) + "-%s", LocalDateTime.now().format(DateUtils.DATE_TIME_FORMATTER));
            //设置返回文件名的编码格式
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            //response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel");
            ExcelWriterBuilder write = EasyExcel.write(response.getOutputStream());
            ExcelWriterSheetBuilder sheet = write.sheet();
            ArrayList<WriteHandler> writeHandlers = Lists.newArrayList(new InspectionReportStyleStrategy.WidthStyleStrategy(),
                    EasyExcelStyleUtils.getStyleStrategy(),
                    new InspectionReportStyleStrategy.RowHeightStyleStrategy());
            for (WriteHandler writeHandler : writeHandlers) {
                sheet.registerWriteHandler(writeHandler);
            }
            sheet.registerConverter(new ByteArrayImageConverter());
            sheet.sheetName(fileName);
            sheet.head(head);
            sheet.doWrite(lists);
            /*EasyExcelUtils.postDownLoad(fileName, InspectionExportRespVO.class,
                    respVOS, response, Lists.newArrayList(new InspectionReportStyleStrategy.WidthStyleStrategy(indexs),
                            EasyExcelStyleUtils.getStyleStrategy(),
                            new InspectionReportStyleStrategy.RowHeightStyleStrategy()));*/
        } catch (Exception e) {
            log.error("#PowerController.inspectionExport#", e);
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_DOWNLOADING.getContent()));
        } finally {
            redisService.decr(redisKey);
//            for (InspectionExportRespVO vo : respVOS) {
//                try {
//                    if (vo.getInspectionImg() != null) {
//                        object.close();
//                    }
//                    if (vo.getMeterImg() != null) {
//                        object2.close();
//                    }
//                } catch (IOException e) {
//                    log.error("#PowerController.inspectionExport#", e);
//                }
//            }
        }

    }

    List<Integer> getIndexs(List<String> items) {
        List<Integer> index = new ArrayList<>();
        Class<InspectionExportRespVO> inspectionExportRespVOClass = InspectionExportRespVO.class;
        List<String> names = PowerInspectionExcelItemEnum.getValueByIds(items);
        //获取所有字段
        Field[] declaredFields = inspectionExportRespVOClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            ExcelProperty annotation = declaredField.getAnnotation(ExcelProperty.class);
            if (names.contains(annotation.value()[0])) {
                index.add(annotation.index());
            }
        }
        return index;
    }

    List<List<String>> head(List<String> items) {
        List<List<String>> result = new ArrayList<>();
        items.forEach(item -> {
            List<String> head = new ArrayList<>();
            head.add(PowerInspectionExcelItemEnum.getValueById(item));
            result.add(head);
        });
        return result;
    }

    List<List<Object>> listData(List<InspectionExportRespVO> vos, Class<InspectionExportRespVO> clzz, List<List<String>> headers) {
        List<List<Object>> object = new ArrayList<>();
        for (InspectionExportRespVO vo : vos) {
            List<Object> objects = new ArrayList<>();
            for (List<String> header : headers) {
                //获取每个字段
                Field[] declaredFields = clzz.getDeclaredFields();
                //遍历字段上的注解判断名称是否可表头名称一致
                for (Field declaredField : declaredFields) {
                    ExcelProperty annotation = declaredField.getAnnotation(ExcelProperty.class);
                    log.info("header.get(0),{}", header.get(0));
                    log.info("annotation.value(),{}", annotation.value());
                    if (annotation.value()[0].equals(header.get(0))) {
                        try {
                            PropertyDescriptor pd = new PropertyDescriptor(declaredField.getName(), clzz);
                            Method getMethod = pd.getReadMethod();// 获得get方法
                            Object invoke = getMethod.invoke(vo);
                            if (ObjectUtils.isEmpty(invoke)) {
                                objects.add("");
                            } else {
                                objects.add(invoke);
                            }
                        } catch (IntrospectionException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            object.add(objects);
        }
        return object;
    }

    @ApiOperation("巡检报告批量删除")
    @DeleteMapping("/inspection/deleteBatch")
    public Result<Object> inspectionDeleteBatch(@RequestBody List<String> ids) {
        boolean flag = powerInspectionService.inspectionDeleteBatch(ids);
        if (flag) {
            return Result.ok();
        }
        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_008.getContent()));
    }

    @ApiOperation("查询单位下设备点")
    @GetMapping("/home/point/{orgCode}/queryAll")
    public Result<List<PowerHomePointQueryRespVO>> homePointQuery(@PathVariable String orgCode) {
        List<PowerHomePointQueryOutDTO> dtos = powerHomeService.homePointQuery(orgCode);
        List<PowerHomePointQueryRespVO> collect = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dtos)) {
            collect = dtos.stream().map(e -> {
                PowerHomePointQueryRespVO vo = new PowerHomePointQueryRespVO();
                BeanUtils.copyProperties(e, vo);
                return vo;
            }).collect(Collectors.toList());
        }
        return Result.ok(collect);
    }

    @ApiOperation("查询设备点关联的设备信息")
    @GetMapping("/home/point/{orgCode}/equipment/queryBy")
    public Result<List<PowerHomePointQueryByRespVO>> homePointQueryBy(@PathVariable String orgCode, @RequestParam String pointId) {
        List<PowerHomePointQueryByOutDTO> dtos = powerHomeService.homePointQueryBy(pointId, orgCode);
        List<PowerHomePointQueryByRespVO> collect = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dtos)) {
            collect = dtos.stream().map(e -> {
                PowerHomePointQueryByRespVO vo = new PowerHomePointQueryByRespVO();
                BeanUtils.copyProperties(e, vo);
                return vo;
            }).collect(Collectors.toList());
        }
        return Result.ok(collect);
    }

    @PostMapping("component/infrared/rule/edit")
    public Result<Object> componentInfraredRuleEdit(@Valid @RequestBody PowerReqVO.ComponentInfraredRuleReqVO componentInfraredRuleReqVO) {

        PowerComponentInDTO.ComponentRuleInfraredInDTO componentRuleInDTO = new PowerComponentInDTO.ComponentRuleInfraredInDTO();
        componentRuleInDTO.setComponentId(componentInfraredRuleReqVO.getComponentId());
        componentRuleInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        List<PowerComponentInDTO.InfraredRuleInDTO> infraredRuleInfraredRuleInDTOList = Lists.newLinkedList();
        componentRuleInDTO.setInfraredRuleInDTOList(infraredRuleInfraredRuleInDTOList);
        if (CollUtil.isNotEmpty(componentInfraredRuleReqVO.getRuleInfoList())) {
            for (PowerReqVO.InfraredRuleInfoReqVO ruleInfoReqVO : componentInfraredRuleReqVO.getRuleInfoList()) {
                PowerComponentInDTO.InfraredRuleInDTO ruleInDTO = new PowerComponentInDTO.InfraredRuleInDTO();
                BeanUtils.copyProperties(ruleInfoReqVO, ruleInDTO);
                infraredRuleInfraredRuleInDTOList.add(ruleInDTO);
            }
        }
        powerComponentService.componentInfraredRuleEdit(componentRuleInDTO);
        return Result.ok(true);
    }

    @GetMapping("/home/inspection/{orgCode}/alarm/events/count")
    public Result homeInspectionAlarmEventsCount(@PathVariable String orgCode) {
        Integer total = powerHomeService.homeInspectionAlarmEventsCount(orgCode);
        Map<String, Integer> result = new HashMap<>();
        result.put("total", total);
        return Result.ok(result);
    }

    @GetMapping("/home/inspection/{orgCode}/alarm/events")
    public Result<List<PowerHomeInspectionAlarmEventsRespVO>> homeInspectionAlarmEvents(@PathVariable String orgCode) {
        List<PowerHomeInspectionAlarmEventsOutDTO> dtos = powerHomeService.homeInspectionAlarmEvents(orgCode);
        List<PowerHomeInspectionAlarmEventsRespVO> result = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dtos)) {
            result = dtos.stream().map(e -> {
                PowerHomeInspectionAlarmEventsRespVO vo = new PowerHomeInspectionAlarmEventsRespVO();
                BeanUtils.copyProperties(e, vo);
                vo.setDefectStatus(e.getDefectStatus().toString());
                return vo;
            }).collect(Collectors.toList());
        }
        return Result.ok(result);
    }

    @PostMapping("/infrared/withdrawal/batch")
    public Result infraredWithdrawalBatch(@RequestBody List<String> ids) {
        boolean flag = powerInspectionService.infraredWithdrawalBatch(ids);
        if (flag) {
            return Result.ok();
        }
        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_008.getContent()));
    }

    @PostMapping("/defect/withdrawal/batch")
    public Result defectWithdrawalBatch(@RequestBody List<String> ids) {

        boolean flag = powerInspectionService.defectWithdrawalBatch(ids);
        if (flag) {
            return Result.ok();
        }
        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_008.getContent()));
    }
}

