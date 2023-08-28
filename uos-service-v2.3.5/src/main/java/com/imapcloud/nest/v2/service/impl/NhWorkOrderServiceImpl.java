package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.MissionVideoEntity;
import com.imapcloud.nest.service.impl.DataCenterServiceImpl;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.config.DataAnalysisConfig;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.enums.NhOrderBuryPointEnum;
import com.imapcloud.nest.v2.common.enums.NhOrderStatusEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.po.in.*;
import com.imapcloud.nest.v2.dao.po.out.*;
import com.imapcloud.nest.v2.manager.dataobj.out.*;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.feign.OrgServiceClient;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.sql.*;
import com.imapcloud.nest.v2.service.NhWorkOrderService;
import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.web.vo.req.NhOrderReportReqVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NhWorkOrderServiceImpl implements NhWorkOrderService {


    @Resource
    private WorkOrderInfoManager workOrderInfoManager;

    @Resource
    private OrgServiceClient orgServiceClient;

    @Resource
    private WorkVectorsInfoManager workVectorsInfoManager;

    @Resource
    private WorkHisRecordManager workHisRecordManager;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private WorkOrderPlanManager workOrderPlanManager;

    @Resource
    private InspectionPlanManager inspectionPlanManager;

    @Resource
    private BaseNestAccountManager baseNestAccountManager;

    @Resource
    private BaseNestManager baseNestManager;

    @Resource
    private InspectionPlanMissionManager inspectionPlanMissionManager;

    @Resource
    private MissionRecordsManager missionRecordsManager;

    @Resource
    private MissionPhotoManager missionPhotoManager;

    @Resource
    private MissionVideoManager missionVideoManager;

    @Resource
    private WorkOrderReportManager workOrderReportManager;

    @Resource
    private MissionManager missionManager;

    @Resource
    private DataCenterServiceImpl dataCenterService;

    @Resource
    private FileManager fileManager;

    @Resource
    private RedisService redisService;

    @Resource
    private DataAnalysisConfig dataAnalysisConfig;

    @Override
    public NhQueryOrderOutDTO queryOrderList(NhQueryOrderInDTO inDTO) {
        NhQueryOrderOutDTO outDTO = new NhQueryOrderOutDTO();
        QueryOrderInPO build = QueryOrderInPO.builder()
                .degree(inDTO.getDegree())
                .orgCode(inDTO.getOrgCode())
                .status(inDTO.getStatus())
                .pageNo(inDTO.getPageNo())
                .pageSize(inDTO.getPageSize())
                .title(inDTO.getTitle())
                .userOrgCode(TrustedAccessTracerHolder.get().getOrgCode()).build();
        build.setBeginTime(timeFormat(inDTO.getBeginTime(), 0));
        build.setEndTime(timeFormat(inDTO.getEndTime(), 1));
        NhQueryOrderOutPO po = workOrderInfoManager.queryListByCondition(build);
        if (ObjectUtils.isEmpty(po)) {
            return null;
        }
        Result<List<OrgSimpleOutDO>> listResult = orgServiceClient.listAllOrgSimpleInfos();
        Map<String, OrgSimpleOutDO> map = listResult.getData().stream().collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, q -> q));
        outDTO.setTotal(po.getTotal());
        outDTO.setInfoList(po.getInfoList().stream().map(e -> {
            NhQueryOrderOutDTO.OrderInfo info = new NhQueryOrderOutDTO.OrderInfo();
            BeanUtils.copyProperties(e, info);
            OrgSimpleOutDO orgSimpleOutDO = map.get(e.getOrgCode());
            if (!ObjectUtils.isEmpty(orgSimpleOutDO)) {
                info.setOrgName(orgSimpleOutDO.getOrgName());
            }
            info.setInspectionBeginTime(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(e.getInspectionBeginTime()));
            info.setInspectionEndTime(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(e.getInspectionEndTime()));
            return info;
        }).collect(Collectors.toList()));
        return outDTO;
    }

    @Override
    @Transactional
    public String addOrderInfo(NhOrderInfoInDTO infoInDTO) {
        //检查标题是否冲突 1. 相同单位下工单名称不能重复
        boolean flag = checkNameConfilct(infoInDTO);
        if (flag) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NHWORKORDERSERVICEIMPL_001.getContent()));
        }
        //保存工单
        OrderInfoInPO infoInPO = new OrderInfoInPO();
        String orderId = BizIdUtils.snowflakeIdStr();
        BeanUtils.copyProperties(infoInDTO, infoInPO);
        infoInPO.setOrderId(orderId);
        infoInPO.setUserId(TrustedAccessTracerHolder.get().getAccountId());
        infoInPO.setOrderStatus(Integer.valueOf(infoInDTO.getOrderStatus()));
        infoInPO.setDegree(Integer.valueOf(infoInDTO.getDegree()));
        infoInPO.setFrequency(infoInDTO.getFrequency());
        infoInPO.setVerificationMethod(infoInDTO.getVerificationMethod());
        infoInPO.setOrderType(Integer.valueOf(infoInDTO.getOrderType()));
        workOrderInfoManager.saveOrder(infoInPO);
        //保存矢量信息
        List<NhOrderInfoInDTO.VectorsInDto> vectors = infoInDTO.getVectors();
        if (CollectionUtil.isNotEmpty(vectors)) {
            List<OrderInfoInPO.VectorsInPO> collect = new ArrayList<>();
            vectors.stream().forEach(item -> {
                OrderInfoInPO.VectorsInPO inPO = new OrderInfoInPO.VectorsInPO();
                inPO.setName(item.getName());
                inPO.setPoints(item.getPoints());
                inPO.setOrder(item.getOrder());
                inPO.setType(item.getType());
                inPO.setOrderId(orderId);
                inPO.setUserId(TrustedAccessTracerHolder.get().getAccountId());
                collect.add(inPO);
            });
            workVectorsInfoManager.saveVectors(collect);
        }
        NhExecuteOrderInDTO inDTO = new NhExecuteOrderInDTO();
        inDTO.setStatus(Integer.valueOf(infoInDTO.getOrderStatus()));
        inDTO.setOrderId(orderId);
        balance(inDTO);
        return orderId;
    }

    @Override
    @Transactional
    public void editOrderInfo(NhOrderInfoInDTO infoInDTO) {
        //检查标题是否冲突 1. 相同单位下工单名称不能重复
        NhQueryOrderOutPO.OrderInfo po = workOrderInfoManager.queryOneById(infoInDTO.getOrderId());
        if (ObjectUtils.isEmpty(po)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NHWORKORDERSERVICEIMPL_002.getContent()));
        }
        //检查相同单位下是否存在冲突的名字
        boolean flag = checkNameConfilct(infoInDTO);
        if (flag) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NHWORKORDERSERVICEIMPL_001.getContent()));
        }
        //编辑工单
        OrderInfoInPO infoInPO = new OrderInfoInPO();
        BeanUtils.copyProperties(infoInDTO, infoInPO);
        infoInPO.setOrderId(infoInDTO.getOrderId());
        infoInPO.setVersionId(po.getVersionId());
        infoInPO.setUserId(TrustedAccessTracerHolder.get().getAccountId());
        workOrderInfoManager.editOrder(infoInPO);
        workVectorsInfoManager.deleVectors(infoInDTO.getOrderId(), null);
        List<NhOrderInfoInDTO.VectorsInDto> vectors = infoInDTO.getVectors();
        if (CollectionUtil.isNotEmpty(vectors)) {
            List<OrderInfoInPO.VectorsInPO> collect = new ArrayList<>();
            vectors.stream().forEach(item -> {
                OrderInfoInPO.VectorsInPO inPO = new OrderInfoInPO.VectorsInPO();
                inPO.setName(item.getName());
                inPO.setPoints(item.getPoints());
                inPO.setOrder(item.getOrder());
                inPO.setType(item.getType());
                inPO.setOrderId(infoInDTO.getOrderId());
                inPO.setUserId(TrustedAccessTracerHolder.get().getAccountId());
                collect.add(inPO);
            });
            workVectorsInfoManager.saveVectors(collect);
        }

    }

    private boolean checkNameConfilct(NhOrderInfoInDTO infoInDTO) {
        return workOrderInfoManager.checkNameConfilct(infoInDTO.getTitle(), infoInDTO.getOrgCode(), infoInDTO.getOrderId());
    }

    public String timeFormat(String time, int addDay) {
        if (StringUtils.isNotEmpty(time)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat tdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = sdf.parse(time);
                if (addDay != 0) {
                    date = DateUtils.addDays(date, 1);
                }
                return tdf.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public NhQueryDetailOutDTO queryDetail(String orderId) {
        //查找工单数据
        NhQueryOrderOutPO.OrderInfo orderInfo = workOrderInfoManager.queryOneById(orderId);
        if (ObjectUtils.isEmpty(orderInfo)) {
            return null;
        }
        //查找流程数据
        List<NhQueryHisRecordOutPO> outPO = workHisRecordManager.queryExecutingRecords(orderId);
        NhQueryDetailOutDTO dto = new NhQueryDetailOutDTO();
        BeanUtils.copyProperties(orderInfo, dto);
        dto.setGdCode(generateGdCode(orderInfo.getId()));
        List<String> accountIds = outPO.stream().map(NhQueryHisRecordOutPO::getCreatorId).collect(Collectors.toList());
        accountIds.add(orderInfo.getCreatorId());
        Result<List<AccountOutDO>> listResult = accountServiceClient.listAccountInfos(accountIds);
        Map<String, String> collect = new HashMap<>();
        if (CollectionUtil.isNotEmpty(listResult.getData())) {
            collect = listResult.getData().stream().collect(Collectors.toMap(AccountOutDO::getAccountId, AccountOutDO::getName));
        }
        Result<OrgSimpleOutDO> orgDetails = orgServiceClient.getOrgDetails(orderInfo.getOrgCode());
        OrgSimpleOutDO data = orgDetails.getData();
        //Map<String, String> map = orgDetails.getData().stream().collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, OrgSimpleOutDO::getOrgName));
        List<NhQueryDetailOutDTO.OutDtoProcess> processList = new ArrayList<>();
        for (NhQueryHisRecordOutPO item : outPO) {
            NhQueryDetailOutDTO.OutDtoProcess process = new NhQueryDetailOutDTO.OutDtoProcess();
            process.setDesc(item.getDesc());
            process.setNodeId(item.getRecordId());
            process.setUserName(ObjectUtils.isEmpty(collect) ? null : collect.get(item.getCreatorId()));
            process.setOperationTime(item.getCreatedTime());
            process.setFlag(item.isFlag());
            process.setRemark(item.getMark());
            processList.add(process);
        }
        dto.setCreatorName(ObjectUtils.isEmpty(collect) ? null : collect.get(orderInfo.getCreatorId()));
        dto.setProcess(processList);
        dto.setOrgName(data.getOrgName());
        dto.setLatitude(data.getLatitude());
        dto.setLongitude(data.getLongitude());
        return dto;
    }

    @Override
    public List<NhQueryVectorOutDTO> queryVertor(String orderId) {
        List<NhQueryVectorsOutPO> outPO = workVectorsInfoManager.queryVertorsByOrderId(orderId);
        Optional<List<NhQueryVectorsOutPO>> outPOS = Optional.ofNullable(outPO);
        if (outPOS.isPresent()) {
            List<NhQueryVectorOutDTO> collect = outPOS.get().stream().map(e -> {
                NhQueryVectorOutDTO dto = new NhQueryVectorOutDTO();
                dto.setVectorId(e.getVectorId());
                dto.setName(e.getName());
                dto.setIndex(e.getIndex());
                dto.setPoints(e.getPoints());
                dto.setType(e.getType());
                return dto;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }


    @Override
    public void deleteVertor(String vertorId) {
        workVectorsInfoManager.deleVectors(null, vertorId);
    }

    @Override
    @Transactional
    public void executeOrder(NhExecuteOrderInDTO inDTO) {
        //更新工单状态
        NhQueryOrderOutPO.OrderInfo orderInfo = workOrderInfoManager.queryOneById(inDTO.getOrderId());
        OrderInfoInPO infoInPO = new OrderInfoInPO();
        infoInPO.setOrderId(inDTO.getOrderId());
        infoInPO.setOrderStatus(inDTO.getStatus());
        infoInPO.setVersionId(orderInfo.getVersionId());
        infoInPO.setUserId(TrustedAccessTracerHolder.get().getAccountId());
        infoInPO.setFrequency(orderInfo.getFrequency());
        workOrderInfoManager.editOrder(infoInPO);
        //判断工单是否存在相同状态节点
        balance(inDTO);
        return;
    }

    public void balance(NhExecuteOrderInDTO inDTO) {
        //检查是否存在历史节点
        OrderHisRecordInPO inPO = new OrderHisRecordInPO();
        inPO.setOrderId(inDTO.getOrderId());
        inPO.setOrderStatus(inDTO.getStatus());
        boolean flag = workHisRecordManager.checkHisExist(inPO);
        //存在   新增一步 设置之前状态值相同的记录为废弃记录
        NhOrderBuryPointEnum enumByCode = NhOrderBuryPointEnum.getEnumByCode(inDTO.getStatus(), flag);
        inPO.setProcessCode(enumByCode.getProcess());
        workHisRecordManager.updateDisuseRecord(inPO);
        //更新process大于当前应设置状态的flag为0
        NhOrderBuryPointEnum delete = NhOrderBuryPointEnum.getEnumByCode(inDTO.getStatus(), !flag);
        //更新记录操作即可
        inPO.setRecordId(BizIdUtils.snowflakeIdStr());
        inPO.setFlag(true);
        inPO.setDescription(enumByCode.getDesc());
        inPO.setMark(inDTO.getRemark());
        inPO.setOrderStatus(inDTO.getStatus());
        inPO.setDeleted(false);
        inPO.setProcessCode(enumByCode.getProcess());
        inPO.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
        inPO.setProcessDir(enumByCode.getProcessDir());
        workHisRecordManager.addRecord(inPO);
        if (!ObjectUtils.isEmpty(delete)) {
            int process = delete.getProcess();
            workHisRecordManager.updateProcess(inPO, process);
        }
    }

    @Override
    public List<NhQueryDetailOutDTO.OutDtoProcess> executeProcessOrder(String orderId) {
        List<NhQueryHisRecordOutPO> outPOS = workHisRecordManager.queryHistRecords(orderId);
        List<NhQueryDetailOutDTO.OutDtoProcess> processList = new ArrayList<>();
        processList = Optional.ofNullable(outPOS).map(e -> {
            List<String> accountIds = e.stream().map(NhQueryHisRecordOutPO::getCreatorId).collect(Collectors.toList());
            Result<List<AccountOutDO>> listResult = accountServiceClient.listAccountInfos(accountIds);
            Map<String, AccountOutDO> collect = new HashMap<>();
            if (CollectionUtil.isNotEmpty(listResult.getData())) {
                collect = listResult.getData().stream().collect(Collectors.toMap(AccountOutDO::getAccountId, q -> q));
            }
            List<NhQueryDetailOutDTO.OutDtoProcess> outDtoProcesses = new ArrayList<>();
            for (NhQueryHisRecordOutPO item : e) {
                NhQueryDetailOutDTO.OutDtoProcess process = new NhQueryDetailOutDTO.OutDtoProcess();
                process.setDesc(item.getDesc());
                process.setNodeId(item.getRecordId());
                process.setFlag(item.isFlag());
                process.setOperationTime(item.getCreatedTime());
                AccountOutDO accountOutDO = collect.get(item.getCreatorId());
                if (!ObjectUtils.isEmpty(accountOutDO)) {
                    process.setUserName(accountOutDO.getName());
                    process.setMobile(accountOutDO.getMobile());
                }
                outDtoProcesses.add(process);
            }
            return outDtoProcesses;
        }).orElse(null);
        return processList;
    }

    @Override
    public void addPlanRel(String orderId, Integer planId) {
        workOrderPlanManager.save(orderId, planId);
    }

    @Override
    public void deletedPlan(NhOrderPlanInDTO planInDTO) {
        workOrderPlanManager.deletePlan(planInDTO);
    }

    @Override
    public NhOrderPlanOutDTO listOrderPlan(String orderId, Long pageNum, Long pageSize) {
        NhOrderPlanOutDTO dto = new NhOrderPlanOutDTO();
        //查询工单关联计划ID
        List<Integer> ids = workOrderPlanManager.selectPlanIds(orderId);
        //查询计划信息  task表
        if (CollectionUtil.isEmpty(ids)) {
            return null;
        }
        BaseNestAccountOutDO outDO = baseNestAccountManager.selectByUserId(TrustedAccessTracerHolder.get().getAccountId());
        if (ObjectUtils.isEmpty(outDO)) {
            return null;
        }
        NhOrderPlanOutPO outPO = inspectionPlanManager.queryPlans(ids, pageNum, pageSize, outDO.getBaseNestId());
        List<NhOrderPlanOutPO.planInfo> records = outPO.getRecords();
        if (!CollectionUtil.isEmpty(records)) {
            //查詢用戶名稱
            List<String> accoutIds = records.stream().map(item -> {
                return String.valueOf(item.getUserId());
            }).collect(Collectors.toList());
            Result<List<AccountOutDO>> listResult = accountServiceClient.listAccountInfos(accoutIds);
            List<AccountOutDO> data = listResult.getData();
            Map<String, String> accountMap = new HashMap<>();
            if (CollectionUtil.isEmpty(data)) {
                accountMap = data.stream().collect(Collectors.toMap(AccountOutDO::getAccountId, AccountOutDO::getName));
            }
            //查询基站名称
            List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOS = baseNestManager.selectListByNestIdList(outDO.getBaseNestId());
            Map<String, BaseNestOutDO.BaseNestEntityOutDO> collect = baseNestEntityOutDOS.stream().collect(Collectors.toMap(BaseNestOutDO.BaseNestEntityOutDO::getNestId, q -> q));
            //查询任务信息
            Map<Integer, List<NhOrderPlanMisOutPO>> outpo = inspectionPlanMissionManager.queryMissionIds(ids);
            List<NhOrderPlanOutDTO.planInfo> infos = new ArrayList<>();
            for (NhOrderPlanOutPO.planInfo record : records) {
                NhOrderPlanOutDTO.planInfo info = new NhOrderPlanOutDTO.planInfo();
                info.setRegularExecutionDate(record.getRegularExecutionDate());
                info.setCycleExecutionUnit(record.getCycleExecutionUnit());
                info.setCycleExecutionTime(record.getCycleExecutionTime());
                info.setIntervalTime(record.getIntervalTime());

                info.setPlanId(record.getPlanId());
                info.setPlanName(record.getPlanName());
                info.setPlanType(record.getPlanType());
                info.setNestId(record.getNestId());
                info.setNestName(Optional.ofNullable(collect.get(record.getNestId())).map(BaseNestOutDO.BaseNestEntityOutDO::getName).orElse(null));
                info.setCreatedTime(record.getCreatedTime());
                info.setCreatorName(accountMap.get(record.getUserId()));
                List<NhOrderPlanMisOutPO> orderPlanMisOutPOS = outpo.get(record.getPlanId());
                List<NhOrderPlanOutDTO.Mission> missionList = new ArrayList<>();
                for (NhOrderPlanMisOutPO orderPlanMisOutPO : orderPlanMisOutPOS) {
                    NhOrderPlanOutDTO.Mission mission = new NhOrderPlanOutDTO.Mission();
                    mission.setMissionId(orderPlanMisOutPO.getMissionId());
                    mission.setMissionName(orderPlanMisOutPO.getMissionName());
                    mission.setMissionType(orderPlanMisOutPO.getMissionType());
                    mission.setTaskId(orderPlanMisOutPO.getTaskId());
                    missionList.add(mission);
                }
                info.setMissionList(missionList);
                infos.add(info);
            }
            dto.setRecords(infos);
            dto.setTotal(outPO.getTotal());
            return dto;
        }
        return null;
    }

    @Override
    public List<NhOrderPlanOptionOutDTO> listOrderPlanOption(String orderId) {
        List<NhOrderPlanOptionOutPO> outPO = workOrderPlanManager.listOrderPlanOption(orderId);
        List<NhOrderPlanOptionOutDTO> dtos = Optional.ofNullable(outPO).map(e -> {
            List<NhOrderPlanOptionOutDTO> collect = e.stream().map(item -> {
                NhOrderPlanOptionOutDTO outDTO = new NhOrderPlanOptionOutDTO();
                BeanUtils.copyProperties(item, outDTO);
                return outDTO;
            }).collect(Collectors.toList());
            return collect;
        }).orElse(null);
        return dtos;
    }

    @Override
    public List<NhOrderMissionOptionOutDTO> listRecordsMissionOption(Integer planId) {
        List<NhOrderMissionOptionOutDTO> dtos = new ArrayList<>();
        List<Integer> planIds = new ArrayList<>();
        planIds.add(planId);
        Map<Integer, List<NhOrderPlanMisOutPO>> map = inspectionPlanMissionManager.queryMissionIds(planIds);
        if (ObjectUtils.isEmpty(map)) {
            return dtos;
        }
        List<NhOrderPlanMisOutPO> orderPlanMisOutPOS = map.get(planId);
        //查找missionID
        Map<Integer, NhOrderPlanMisOutPO> missionMap = orderPlanMisOutPOS.stream().collect(Collectors.toMap(NhOrderPlanMisOutPO::getMissionId, q -> q));
        //查询找 missionRecord记录
        Set<Integer> missionKey = missionMap.keySet();
        List<NhOrderRecordInfoOutPO> outPOLis = missionManager.selectRecordInfo(missionKey);
        Map<Integer, NhOrderRecordInfoOutPO> recordInfoOutPOMap = outPOLis.stream().filter(e -> !ObjectUtils.isEmpty(e.getId())).collect(Collectors.toMap(NhOrderRecordInfoOutPO::getId, q -> q));
        Set<Integer> integers = missionMap.keySet();
        List<MissionRecordsOutDO> missionRecordsOutDOS = missionRecordsManager.selectByMissionIdlist(integers);
        List<NhOrderMissionOptionOutDTO> result = Optional.of(missionRecordsOutDOS).map(item -> {
            List<NhOrderMissionOptionOutDTO> collect = item.stream().map(bean -> {
                NhOrderMissionOptionOutDTO dto = new NhOrderMissionOptionOutDTO();
                NhOrderRecordInfoOutPO nhOrderRecordInfoOutPO = recordInfoOutPOMap.get(bean.getMissionRecordsId().intValue());
                if (!ObjectUtils.isEmpty(nhOrderRecordInfoOutPO)) {
                    BeanUtils.copyProperties(nhOrderRecordInfoOutPO, dto);
                }
                dto.setMissionId(bean.getMissionId());
                dto.setMissionRecordId(bean.getMissionRecordsId());
                dto.setMissionName(missionMap.get(bean.getMissionId()).getMissionName());
                dto.setFlyIndex(bean.getFlyIndex());
                dto.setStartTime(bean.getStartTime());
                return dto;
            }).collect(Collectors.toList());
            return collect;
        }).orElse(null);
        return result;
    }

    @Override
    public NhOrderPhotoOutDTO getAllPhotoByCondition(NhOrderPhotoInDTO inDTO) {
        //根据recordId查找mission_photo
        NhOrderPhotoInPO inPO = new NhOrderPhotoInPO();
        BeanUtils.copyProperties(inDTO, inPO);
        NhOrderPhotoOutPO entities = missionPhotoManager.selectPageByCondition(inPO);
        NhOrderPhotoOutDTO outDTO = Optional.ofNullable(entities)
                .map(e -> {
                    //查询图片信息
                    NhOrderPhotoOutDTO dto = new NhOrderPhotoOutDTO();
                    dto.setTotal(e.getTotal());
                    List<NhOrderPhotoOutDTO.OrderPhotoInfo> collect = e.getInfoList().stream().map(item -> {
                        NhOrderPhotoOutDTO.OrderPhotoInfo info = new NhOrderPhotoOutDTO.OrderPhotoInfo();
                        info.setId(item.getId());
                        info.setName(item.getName());
                        info.setPhotoUrl(item.getPhotoUrl());
                        info.setThumbnailUrl(item.getThumbnailUrl());
                        info.setMissionRecordsId(item.getMissionRecordsId());
                        info.setFileId(item.getFileId());
                        info.setFileName(item.getFileName());
                        info.setLatitude(item.getLatitude());
                        info.setLongitude(item.getLongitude());
                        info.setAltitude(item.getAltitude());
                        info.setMediaType(item.getMediaType());
                        info.setTimeCreated(item.getTimeCreated());
                        info.setPhotoType(item.getPhotoType());
                        info.setTaskId(item.getTaskId());
                        info.setCreateTime(item.getCreateTime());
                        return info;
                    }).collect(Collectors.toList());
                    dto.setRecords(collect);
                    return dto;
                }).orElse(null);

        return outDTO;
    }


    @Override
    public NhOrderVideoOutDTO getAllVideoByCondition(Integer recordId) {
        List<MissionVideoEntity> allVideoByRecordId = missionVideoManager.getAllVideoByRecordId(recordId, TrustedAccessTracerHolder.get().getOrgCode());
        NhOrderVideoOutDTO dto = new NhOrderVideoOutDTO();
        List<NhOrderVideoOutDTO.vidoeInfo> result = Optional.ofNullable(allVideoByRecordId).map(e -> {
            List<NhOrderVideoOutDTO.vidoeInfo> collect = e.stream().map(item -> {
                NhOrderVideoOutDTO.vidoeInfo vidoeInfo = new NhOrderVideoOutDTO.vidoeInfo();
                vidoeInfo.setId(item.getId());
                vidoeInfo.setName(item.getName());
                vidoeInfo.setAlias(item.getAlias());
                vidoeInfo.setVideoUrl(item.getVideoUrl());
                vidoeInfo.setType(item.getType());
                vidoeInfo.setRecordStatus(item.getRecordStatus());
                vidoeInfo.setMissionId(item.getMissionId());
                vidoeInfo.setMissionRecordsId(item.getMissionRecordsId());
                vidoeInfo.setExecId(item.getExecId());
                vidoeInfo.setCreateUserId(item.getCreateUserId());
                vidoeInfo.setCreateTime(item.getCreateTime());
                vidoeInfo.setModifyTime(item.getModifyTime());
                vidoeInfo.setDeleted(item.getDeleted());
                vidoeInfo.setPhysicalDeleted(item.getPhysicalDeleted());
                vidoeInfo.setLat(item.getLat());
                vidoeInfo.setLng(item.getLng());
                vidoeInfo.setOrgCode(item.getOrgCode());
                vidoeInfo.setTagVersion(item.getTagVersion());
                dataCenterService.setSrtJson(item);
                vidoeInfo.setSrtJson(item.getSrtJson());
                vidoeInfo.setSrtUrl(item.getSrtUrl());
                return vidoeInfo;
            }).collect(Collectors.toList());
            return collect;
        }).orElse(null);
        dto.setTotal((long) result.size());
        dto.setInfoList(result);
        return dto;
    }

//    @Override
//    public void addPatrolReport(NhOrderReportReqVO reportReqVO, MultipartFile file) {
//        //查询工单信息,判断工单状态是否为   ’待发布‘，’待签收‘及'已拒收'
//        NhQueryOrderOutPO.OrderInfo orderInfo = workOrderInfoManager.queryOneById(reportReqVO.getOrderId());
//        if (orderInfo.getOrderStatus() == NhOrderStatusEnum.To_Be_Publish.getStatus() ||
//                orderInfo.getOrderStatus() == NhOrderStatusEnum.To_Be_Signed.getStatus() ||
//                orderInfo.getOrderStatus() == NhOrderStatusEnum.Declined.getStatus()) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NHWORKORDERSERVICEIMPL_003.getContent()));
//        }
//        String picPath = String.format("%s%s.%s", UploadTypeEnum.NH_WORK_ORDER_PDF.getPath(), BizIdUtils.snowflakeIdStr(), org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename()));
//        try {
//
//            boolean b = MinIoUnit.putObject(picPath, file.getInputStream(), MinIoUnit.getFileType(file.getInputStream()));
//            if (b) {
//                String minioPath = geoaiUosProperties.getStore().getOriginPath() + picPath;
//                OrderReportInPO build = OrderReportInPO.builder().name(reportReqVO.getName())
//                        .orderId(reportReqVO.getOrderId())
//                        .orgCode(reportReqVO.getOrgCode())
//                        .path(minioPath)
//                        .userId(TrustedAccessTracerHolder.get().getAccountId()).build();
//                workOrderReportManager.saveOne(build);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void addPatrolReport(NhOrderReportReqVO reportReqVO) {
        //查询工单信息,判断工单状态是否为   ’待发布‘，’待签收‘及'已拒收'
        NhQueryOrderOutPO.OrderInfo orderInfo = workOrderInfoManager.queryOneById(reportReqVO.getOrderId());
        if (orderInfo.getOrderStatus() == NhOrderStatusEnum.To_Be_Publish.getStatus() ||
                orderInfo.getOrderStatus() == NhOrderStatusEnum.To_Be_Signed.getStatus() ||
                orderInfo.getOrderStatus() == NhOrderStatusEnum.Declined.getStatus()) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NHWORKORDERSERVICEIMPL_003.getContent()));
        }
        OrderReportInPO build = OrderReportInPO.builder().name(reportReqVO.getName())
                .orderId(reportReqVO.getOrderId())
                .orgCode(reportReqVO.getOrgCode())
                .path(reportReqVO.getFilePath())
                .userId(TrustedAccessTracerHolder.get().getAccountId()).build();
        workOrderReportManager.saveOne(build);
    }

    @Override
    public void delPatrolReport(String reportId) {
        OrderReportInPO build = OrderReportInPO.builder().reportId(reportId).build();
        List<OrderReportOutPO> orderReportOutPOS = workOrderReportManager.selectByCondition(build);
        if (CollectionUtils.isEmpty(orderReportOutPOS)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NHWORKORDERSERVICEIMPL_004.getContent()));
        }
        boolean flag = workOrderReportManager.updateToDel(reportId);
        List<String> pathIds = orderReportOutPOS.stream().map(OrderReportOutPO::getPath).collect(Collectors.toList());
        if (flag) {
            CompletableFuture.runAsync(() -> {
                fileManager.deleteFiles(pathIds);
//                    MinIoUnit.rmObjects(pathIds);
            });
        }
    }

    @Override
    public List<NhOrderReportOutDTO> getPatrolReportList(String orderId) {
        OrderReportInPO build = OrderReportInPO.builder().orderId(orderId).build();
        List<OrderReportOutPO> orderReportOutPOS = workOrderReportManager.selectByCondition(build);
        if (CollectionUtils.isEmpty(orderReportOutPOS)) {
            return Collections.EMPTY_LIST;
        }
        List<String> accoutIds = orderReportOutPOS.stream().map(OrderReportOutPO::getCreatorId).collect(Collectors.toList());
        Result<List<AccountOutDO>> listResult = accountServiceClient.listAccountInfos(accoutIds);
        List<AccountOutDO> data = listResult.getData();
        Map<String, String> accountMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(data)) {
            accountMap = data.stream().collect(Collectors.toMap(AccountOutDO::getAccountId, AccountOutDO::getName));
        }
        List<NhOrderReportOutDTO> dtos = new ArrayList<>();
        for (OrderReportOutPO item : orderReportOutPOS) {
            NhOrderReportOutDTO dto = new NhOrderReportOutDTO();
            dto.setName(item.getReportName());
            dto.setReportId(item.getReportId());
            dto.setUrl(item.getPath());
            dto.setCreatorName(accountMap.get(item.getCreatorId()));
            dto.setCreatedTime(item.getCreatedTime());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public void exportPatrolReport(String reportId, HttpServletResponse response) {
        String redisKey = RedisKeyConstantList.NH_WORK_PATROL;
        long increment = redisService.incr(redisKey);
        try {
            log.info("#ExpotyPatrolReport# increment={}", increment);
            if (increment > dataAnalysisConfig.getMaxExportNum()) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TOO_MANY_PEOPLE_EXPORTED.getContent()));
            }
            OrderReportInPO build = OrderReportInPO.builder().reportId(reportId).build();
            List<OrderReportOutPO> orderReportOutPOS = workOrderReportManager.selectByCondition(build);
            if (CollectionUtils.isEmpty(orderReportOutPOS)) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NHWORKORDERSERVICEIMPL_005.getContent()));
            }
            String path = orderReportOutPOS.get(0).getPath();
            //下载文件
//            InputStream object = MinIoUnit.getObject(path);
            try(InputStream object = fileManager.getInputSteam(path)){
                if(Objects.isNull(object)){
                    return;
                }
                String fileName = URLEncoder.encode(orderReportOutPOS.get(0).getReportName(), "UTF-8");
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                try {
                    ServletOutputStream outputStream = response.getOutputStream();
                    FastByteArrayOutputStream read = IoUtil.read(object);
                    byte[] bytes = read.toByteArray();
                    outputStream.write(bytes);
                } catch (IOException e) {
                    log.error("#ExpotyPatrolReport# error", e);
                } finally {
                    redisService.decr(redisKey);
                }
            }
        } catch (Exception e) {
            log.info("#ExpotyPatrolReport# error=", e);
        } finally {
            redisService.decr(redisKey);
        }
    }

    public String generateGdCode(Long id) {
        //直接使用GD00000000+ 自增主键简单实现
        return "GD" + String.format("%08d", id);
    }
}
