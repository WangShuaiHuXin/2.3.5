package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.NestConstant;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.service.MissionRecordsService;
import com.imapcloud.nest.service.MissionService;
import com.imapcloud.nest.service.TaskService;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DataAnalysisPicPushStatusEnum;
import com.imapcloud.nest.v2.common.enums.DataAnalysisPicSourceEnum;
import com.imapcloud.nest.v2.common.enums.DataAnalysisPicStatusEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.BaseNestEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterBaseEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterDetailEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseNestMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisBaseMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisDetailMapper;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisBaseQueryCriteriaPO;
import com.imapcloud.nest.v2.service.DataAnalysisBaseService;
import com.imapcloud.nest.v2.service.DataAnalysisDetailService;
import com.imapcloud.nest.v2.service.DataAnalysisMarkService;
import com.imapcloud.nest.v2.service.converter.DataAnalysisBaseConverter;
import com.imapcloud.nest.v2.service.converter.DataAnalysisDetailConverter;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisAggSaveInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisBasePageInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisBaseSaveInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisDetailSaveInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisBasePageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisBaseSimpleOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisDetailPageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisStateSumOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisBaseServiceImpl.java
 * @Description DataAnalysisBaseServiceImpl
 * @createTime 2022年07月13日 15:21:00
 */
@Slf4j
@Service
public class DataAnalysisBaseServiceImpl implements DataAnalysisBaseService {

    @Resource
    private DataAnalysisBaseMapper dataAnalysisBaseMapper;

    @Resource
    private DataAnalysisDetailService dataAnalysisDetailService;

    @Resource
    private DataAnalysisDetailMapper dataAnalysisDetailMapper;

    @Resource
    private DataAnalysisMarkService dataAnalysisMarkService;

    @Resource
    private MissionService missionService;

    @Resource
    private TaskService taskService;

    @Resource
    private RedisService redisService;

    @Resource
    private MissionRecordsService missionRecordsService;

    @Resource
    private BaseNestMapper baseNestMapper;


    /**
     * 提供时间、任务名、标签名分页查询功能
     *
     * @param dataAnalysisBasePageInDTO
     * @return
     */
    @Override
    public PageResultInfo<DataAnalysisBasePageOutDTO> queryBasePage(DataAnalysisBasePageInDTO dataAnalysisBasePageInDTO) {
        List<DataAnalysisBasePageOutDTO> results = new ArrayList<>();
        long total = 0L;
        //获取该用户拥有的单位全
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        //拼装数据
        DataAnalysisBaseQueryCriteriaPO po = DataAnalysisBaseQueryCriteriaPO.builder()
                .startTime(dataAnalysisBasePageInDTO.getStartTime())
                .endTime(dataAnalysisBasePageInDTO.getEndTime())
                .nestId(dataAnalysisBasePageInDTO.getNestId())
                .missionId(dataAnalysisBasePageInDTO.getMissionId())
                .missionRecordId(dataAnalysisBasePageInDTO.getMissionRecordId())
                .tagName(dataAnalysisBasePageInDTO.getTagName())
                .taskName(dataAnalysisBasePageInDTO.getTaskName())
                .orgCode(dataAnalysisBasePageInDTO.getOrgId())
                .visibleOrgCode(orgCode)
                .build();
        //查询条件下总数
        total = this.dataAnalysisBaseMapper.countByCondition(po);
        if(total > 0){
            //分页查询基础数据
            List<DataAnalysisCenterBaseEntity> dataAnalysisCenterBaseEntities = this.dataAnalysisBaseMapper.selectByCondition(po, PagingRestrictDo.getPagingRestrict(dataAnalysisBasePageInDTO));
            if(CollectionUtil.isNotEmpty(dataAnalysisCenterBaseEntities)){
                //查询基础数据下一共的照片明细数据（只查询主键、推送状态、照片状态）
                LambdaQueryWrapper<DataAnalysisCenterDetailEntity> queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                        .in(DataAnalysisCenterDetailEntity::getCenterBaseId, dataAnalysisCenterBaseEntities.stream().map(DataAnalysisCenterBaseEntity::getCenterBaseId).collect(Collectors.toList()))
                        .eq(DataAnalysisCenterDetailEntity::getDeleted,Boolean.FALSE)
                        .select(DataAnalysisCenterDetailEntity::getCenterBaseId,DataAnalysisCenterDetailEntity::getPushState,DataAnalysisCenterDetailEntity::getPhotoState);
                List<DataAnalysisCenterDetailEntity> detailEntities = dataAnalysisDetailMapper.selectList(queryWrapper);
                //按基础主键给照片分组
                Map<Long,List<DataAnalysisCenterDetailEntity>> detailMap = detailEntities.stream().collect(Collectors.groupingBy(DataAnalysisCenterDetailEntity::getCenterBaseId));
                results = dataAnalysisCenterBaseEntities.stream()
                        .map(x-> {
                            if (StringUtils.isEmpty(x.getBaseNestId())) {
                                x.setBaseNestId(null);
                            }
                            return DataAnalysisBaseConverter.INSTANCES.convert(x);
                        })
                        .map(x->{
                            DataAnalysisStateSumOutDTO sumOutDTO = this.dataAnalysisDetailService.countNumForEntity(detailMap.get(x.getCenterBaseId()));
                            x.setNeedAnalyzeSum(sumOutDTO.getNeedAnalyzeSum()==null?0:sumOutDTO.getNeedAnalyzeSum());
                            x.setNeedConfirmProblemSum(sumOutDTO.getNeedConfirmProblemSum()==null?0:sumOutDTO.getNeedConfirmProblemSum());
                            x.setNeedConfirmNoProblemSum(sumOutDTO.getNeedConfirmNoProblemSum()==null?0:sumOutDTO.getNeedConfirmNoProblemSum());
                            x.setProblemSum(sumOutDTO.getProblemSum()==null?0:sumOutDTO.getProblemSum());
                            x.setNoProblemSum(sumOutDTO.getNoProblemSum()==null?0:sumOutDTO.getNoProblemSum());
                            return x;
                        })
                        .collect(Collectors.toList());
                getBaseNestType(results);
            }
        }
        return PageResultInfo.of(total, results);
    }

    @Override
    public Optional<DataAnalysisBaseSimpleOutDTO> findDataAnalysisBase(Long centerBaseId) {
        if(Objects.nonNull(centerBaseId)){
            LambdaQueryWrapper<DataAnalysisCenterBaseEntity> con = Wrappers.lambdaQuery(DataAnalysisCenterBaseEntity.class)
                    .eq(DataAnalysisCenterBaseEntity::getCenterBaseId, centerBaseId);
            DataAnalysisCenterBaseEntity entity = dataAnalysisBaseMapper.selectOne(con);
            return Optional.ofNullable(entity)
                    .map(r -> {
                        DataAnalysisBaseSimpleOutDTO info = new DataAnalysisBaseSimpleOutDTO();
                        info.setCenterBaseId(centerBaseId);
                        info.setName(r.getBaseName());
                        info.setTaskId(r.getTaskId());
                        info.setTaskName(r.getTaskName());
                        info.setTagId(r.getTagId());
                        info.setTagName(r.getTagName());
                        return info;
                    });
        }
        return Optional.empty();
    }

    /**
     * 填充字段
     * @param baseEntity
     */
    public DataAnalysisCenterBaseEntity fillFields(DataAnalysisCenterBaseEntity baseEntity){
        TaskEntity taskEntity = taskService.lambdaQuery()
                .eq(TaskEntity::getId, baseEntity.getTaskId())
                .select(TaskEntity::getType, TaskEntity::getMold)
                .one();
        Integer type = Optional.ofNullable(taskEntity)
                .map(TaskEntity::getType)
                .orElseThrow(()->new BusinessException(String.format("根据taskId->{}" + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_CORRESPONDING_ROUTE.getContent()),baseEntity.getTaskId())));
        baseEntity.setTaskType(type);
        MissionEntity missionEntity = missionService.lambdaQuery()
                .eq(MissionEntity::getId,baseEntity.getMissionId())
                .select(MissionEntity::getSeqId)
                .one();
        Integer seqId = Optional.ofNullable(missionEntity)
                .map(MissionEntity::getSeqId)
                .orElseThrow(()->new BusinessException(String.format("根据missionId->{},查询不到对应的架次，请检查！",baseEntity.getMissionId())) );
        baseEntity.setMissionSeqId(seqId);
        return baseEntity;
    }

    /**
     * 加分布式锁
     * @param redisKey
     */
    public void onLock(String redisKey , String uuid){
        log.info("打印:redisKey -> {} ", redisKey );
        if(!this.redisService.tryLock(redisKey, uuid , 3 , TimeUnit.MINUTES)){
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALREADY_PUSH.getContent()));
        }
    }


    /**
     * 推送分析统计-主接口
     * @param dataAnalysisAggSaveInDTO
     * @return
     */
    public Long pushCenterMain(DataAnalysisAggSaveInDTO dataAnalysisAggSaveInDTO,Integer sourceType) {
        Long baseId = null;
        String redisKey = "" , uuid = UUID.randomUUID().toString();
        try {
            if (dataAnalysisAggSaveInDTO != null) {
                //保存表头基础数据
                if (log.isDebugEnabled()) {
                    log.debug("【pushCenterMain】:{}", dataAnalysisAggSaveInDTO.toString());
                }
                //加锁用的missionRecordId不能为空
                Long missionRecordId = Optional.ofNullable(dataAnalysisAggSaveInDTO).map(DataAnalysisAggSaveInDTO::getMissionRecordId).orElseThrow(()->new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_MISSIONRECORDSID.getContent())));
                redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.DATA_ANALYSIS_PUSH, String.valueOf(missionRecordId));
                this.onLock(redisKey,uuid);
                //分布式事务校验
                DataAnalysisBaseSaveInDTO baseSaveInDTO = DataAnalysisBaseConverter.INSTANCES.convert(dataAnalysisAggSaveInDTO);
                baseId = this.saveBase(baseSaveInDTO);
                //获取表体照片数据
                List<DataAnalysisDetailSaveInDTO> dataAnalysisDetailSaveInDTO = dataAnalysisAggSaveInDTO.getDataAnalysisDetailSaveInDTOS();

                if (CollectionUtil.isNotEmpty(dataAnalysisDetailSaveInDTO)) {
                    //查询重复数据，过滤
                    LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                            .in(DataAnalysisCenterDetailEntity::getPhotoId
                                    , dataAnalysisDetailSaveInDTO.stream().map(DataAnalysisDetailSaveInDTO::getPhotoId).collect(Collectors.toList()))
                            .select(DataAnalysisCenterDetailEntity::getPhotoId);
                    List<Long> photoId = ((List<DataAnalysisCenterDetailEntity>) this.dataAnalysisDetailMapper.selectList(queryWrapper))
                            .stream()
                            .map(DataAnalysisCenterDetailEntity::getPhotoId)
                            .collect(Collectors.toList());

                    dataAnalysisDetailSaveInDTO = dataAnalysisDetailSaveInDTO.stream()
                            .filter(x -> !photoId.contains(x.getPhotoId()))
                            .collect(Collectors.toList());
                    if (CollectionUtil.isEmpty(dataAnalysisDetailSaveInDTO)) {
                        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_DATA_THAT_CAN_BE_PUSHED.getContent()));
                    }

                    Long finalBaseId = baseId;
                    dataAnalysisDetailSaveInDTO.stream().forEach(x -> {
                                DataAnalysisDetailConverter.INSTANCES.updateDetailDTO(x, baseSaveInDTO);
                                x.setCenterBaseId(finalBaseId);
                                if (DataAnalysisPicSourceEnum.DATA_SCENE.getType() == sourceType) {
                                    x.setSrcDataType(DataAnalysisPicSourceEnum.DATA_SCENE.getType());
                                    if (CollectionUtil.isNotEmpty(x.getDataAnalysisMarkSaveInDTOList()) && x.getDataAnalysisMarkSaveInDTOList().size() > 0) {
                                        x.setPhotoState(DataAnalysisPicStatusEnum.PROBLEM.getType());
                                    } else {
                                        x.setPhotoState(DataAnalysisPicStatusEnum.NO_PROBLEM.getType());
                                    }
                                    x.setPushState(DataAnalysisPicPushStatusEnum.COMMIT.getType());
                                } else if (DataAnalysisPicSourceEnum.PHOTO.getType() == sourceType
                                        || DataAnalysisPicSourceEnum.VIDEO.getType() == sourceType) {
                                    x.setPhotoState(DataAnalysisPicStatusEnum.NEED_ANALYZE.getType());
                                    x.setPushState(DataAnalysisPicPushStatusEnum.COMMIT.getType());
                                } else {
                                    //云冠、AI
                                }
                            }
                    );
                    //保存表体照片以及标注数据
                    this.dataAnalysisDetailService.saveDetailAndMark(dataAnalysisDetailSaveInDTO);

                } else {
                    throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PUSH_EMPTY_TABLE_BODY.getContent()));
                }
            }
        } catch (Exception e) {
            log.error("推送过程出错", e);
            throw new BusinessException(e.getMessage());
        } finally {
            //释放锁
            this.redisService.releaseLock(redisKey,uuid);
        }
        return baseId;
    }

    /**
     * 推送同个架次记录下“单个或者多个”照片数据 -- 来源数据管理
     * 默认状态为 “提交态” “待分析”
     * @param dataAnalysisAggSaveInDTO
     * @return
     */
    @Override
    @Transactional(propagation= Propagation.REQUIRED , rollbackFor = Exception.class)
    public Long pushCenterAggMain(DataAnalysisAggSaveInDTO dataAnalysisAggSaveInDTO) {
        return this.pushCenterMain(dataAnalysisAggSaveInDTO,DataAnalysisPicSourceEnum.PHOTO.getType());
    }

    /**
     * 推送数据 每次推一张照片，可以多个标注（预留口子） -- 数据来源现场取证
     *
     * @param dataAnalysisAggSaveInDTO
     * @return
     */
    @Override
    @Transactional(propagation= Propagation.REQUIRED , rollbackFor = Exception.class)
    public Long pushCenterAggAndMarkMain(DataAnalysisAggSaveInDTO dataAnalysisAggSaveInDTO) {
        return this.pushCenterMain(dataAnalysisAggSaveInDTO,DataAnalysisPicSourceEnum.DATA_SCENE.getType());
    }

    /**
     * 新增或修改数据 -基础数据
     *
     * @param dataAnalysisBaseSaveInDTO
     * @return
     */
    @Override
    public Long saveBase(DataAnalysisBaseSaveInDTO dataAnalysisBaseSaveInDTO) {
        if(ObjectUtils.isEmpty(dataAnalysisBaseSaveInDTO)){
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_BASE_INFORMATION.getContent()));
        }
        Long recordId = dataAnalysisBaseSaveInDTO.getMissionRecordId();
        if(recordId == null){
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_MISSIONID.getContent()));
        }
        //校验字段非空
        Optional.ofNullable(dataAnalysisBaseSaveInDTO).map(DataAnalysisBaseSaveInDTO::getMissionId).orElseThrow(()->new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_MISSIONID_1.getContent())));
        Optional.ofNullable(dataAnalysisBaseSaveInDTO).map(DataAnalysisBaseSaveInDTO::getOrgId).orElseThrow(()->new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_ORGID.getContent())));
        Optional.ofNullable(dataAnalysisBaseSaveInDTO).map(DataAnalysisBaseSaveInDTO::getTagId).orElseThrow(()->new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_TAGID.getContent())));
        Optional.ofNullable(dataAnalysisBaseSaveInDTO).map(DataAnalysisBaseSaveInDTO::getTaskId).orElseThrow(()->new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ANALYTICS_CENTER_CANNOT_EMPTY_TASKID.getContent())));
        Optional.ofNullable(dataAnalysisBaseSaveInDTO).map(DataAnalysisBaseSaveInDTO::getTaskType).orElseThrow(()->new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_TASKTYPE.getContent())));
        Optional.ofNullable(dataAnalysisBaseSaveInDTO).map(DataAnalysisBaseSaveInDTO::getMissionSeqId).orElseThrow(()->new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_MISSIONSEQID.getContent())));
        Optional.ofNullable(dataAnalysisBaseSaveInDTO).map(DataAnalysisBaseSaveInDTO::getMissionRecordTime).orElseThrow(()->new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_MISSIONRECORDTIME.getContent())));
        Optional.ofNullable(dataAnalysisBaseSaveInDTO).map(DataAnalysisBaseSaveInDTO::getSubType).orElseThrow(()->new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_SUBTYPE.getContent())));

        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterBaseEntity.class)
                .eq(DataAnalysisCenterBaseEntity::getMissionRecordId,recordId)
                .select(DataAnalysisCenterBaseEntity::getCenterBaseId);
        Long baseId = ((List<DataAnalysisCenterBaseEntity>)this.dataAnalysisBaseMapper.selectList(queryWrapper)).stream()
                .map(DataAnalysisCenterBaseEntity::getCenterBaseId)
                .findFirst()
                .orElseGet(()->null);
        Integer flyIndex = this.missionRecordsService.getFlyIndex(dataAnalysisBaseSaveInDTO.getMissionRecordId().intValue());
        dataAnalysisBaseSaveInDTO.setMissionFlyIndex(flyIndex==null?"":String.valueOf(flyIndex));
        if(baseId == null){
            List<Long> returnBaseIds = this.saveBatch(CollectionUtil.newArrayList(dataAnalysisBaseSaveInDTO));
            if(CollectionUtil.isEmpty(returnBaseIds)){
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_ADDING_ANALYSIS_CENTER_BASE_DATA.getContent()));
            }
            baseId = returnBaseIds.get(0);
        }else{
            LambdaUpdateWrapper updateWrapper = Wrappers.lambdaUpdate(DataAnalysisCenterBaseEntity.class)
                    .set(DataAnalysisCenterBaseEntity::getModifiedTime, LocalDateTime.now())
                    .eq(DataAnalysisCenterBaseEntity::getCenterBaseId,baseId);
            this.dataAnalysisBaseMapper.update(null,updateWrapper);
        }
        return baseId;
    }

    /**
     * 删除数据接口
     * @param baseId
     * @param detailIds
     * @return
     */
    @Override
    public boolean deleteData(Long baseId ,List<Long> detailIds) {
        //当前数据有存在正在核实中的标注时，提示不允许进行删除操作
        this.dataAnalysisDetailService.checkResultStatus(detailIds);
        //删除mark数据
        this.dataAnalysisMarkService.deleteMarksByDetail(detailIds);
        //删除detail数据
        this.dataAnalysisDetailService.deleteDetails(detailIds);
        //当所有表体数据被删除时，删除base数据
        if(!StringUtils.isEmpty(baseId)){
            LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                    .eq(DataAnalysisCenterDetailEntity::getCenterBaseId,baseId)
                    .select(DataAnalysisCenterDetailEntity::getCenterDetailId);
            int count = this.dataAnalysisDetailMapper.selectCount(queryWrapper);
            if(count==0){
                LambdaUpdateWrapper wrapper = Wrappers.lambdaUpdate(DataAnalysisCenterBaseEntity.class)
                        .set(DataAnalysisCenterBaseEntity::getDeleted, NestConstant.DeleteType.DELETED)
                        .eq(DataAnalysisCenterBaseEntity::getCenterBaseId,baseId);
                this.dataAnalysisBaseMapper.update(null,wrapper);
            }
        }
        return true;
    }

    /**
     * 删除数据接口
     *
     * @param baseIds
     * @return
     */
    @Override
    @Transactional(propagation= Propagation.REQUIRED , rollbackFor = Exception.class)
    public boolean deleteData(List<Long> baseIds) {
        if(CollectionUtil.isNotEmpty(baseIds)){
            LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                    .in(DataAnalysisCenterDetailEntity::getCenterBaseId,baseIds)
                    .select(DataAnalysisCenterDetailEntity::getCenterBaseId,DataAnalysisCenterDetailEntity::getCenterDetailId);
            List<DataAnalysisCenterDetailEntity> detailEntities = this.dataAnalysisDetailMapper.selectList(queryWrapper);
            Map<Long,List<Long>> baseToDetailsMap = detailEntities.stream()
                    .collect(Collectors.groupingBy(DataAnalysisCenterDetailEntity::getCenterBaseId
                            , Collectors.mapping(DataAnalysisCenterDetailEntity::getCenterDetailId,Collectors.toList())));
            //依次调用
            baseToDetailsMap.forEach((k,v)->{
                this.deleteData(k,v);
            });
        }
        return true;
    }

    /**
     * 批量保存数据
     *
     * @param dataAnalysisBaseSaveInDTOS
     * @return
     */
    @Override
    public List<Long> saveBatch(List<DataAnalysisBaseSaveInDTO> dataAnalysisBaseSaveInDTOS) {
        List<Long> baseIds = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(dataAnalysisBaseSaveInDTOS)){
            //查询如果recordsId存在，则直接返回数据
            List<DataAnalysisCenterBaseEntity> baseEntities = dataAnalysisBaseSaveInDTOS.stream()
                    .map(r->{
                        DataAnalysisCenterBaseEntity r1 =  DataAnalysisBaseConverter.INSTANCES.convert (r);
                        r1.setOrgCode(r.getOrgId());
                        r1.setBaseNestId(r.getNestId());
                        return r1;
                    })
                    //填充航线信息
//                    .map(x->fillFields(x))
                    .collect(Collectors.toList());
            this.dataAnalysisBaseMapper.saveBatch(baseEntities);
            baseIds = baseEntities.stream().map(DataAnalysisCenterBaseEntity::getCenterBaseId).collect(Collectors.toList());
        }
        return baseIds;
    }

    /**
     * 批量保存更新数据
     *
     * @param dataAnalysisBaseSaveInDTOS
     * @return
     */
    @Override
    public List<Long> saveOrUpdateBatch(List<DataAnalysisBaseSaveInDTO> dataAnalysisBaseSaveInDTOS) {
        List<Long> baseIds = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(dataAnalysisBaseSaveInDTOS)){
            List<DataAnalysisCenterBaseEntity> baseEntities = dataAnalysisBaseSaveInDTOS.stream()
                    .map(DataAnalysisBaseConverter.INSTANCES::convert)
                    .collect(Collectors.toList());
            this.dataAnalysisBaseMapper.saveOrUpdateBath(baseEntities);
            baseIds = baseEntities.stream().map(DataAnalysisCenterBaseEntity::getCenterBaseId).collect(Collectors.toList());
        }
        return baseIds;
    }

    /**
     * 获取机巢类型
     */
    private void getBaseNestType(List<DataAnalysisBasePageOutDTO> results) {
        if (CollectionUtils.isEmpty(results)) {
            return;
        }
        List<Long> nestIds = results.stream().map(DataAnalysisBasePageOutDTO::getNestId).collect(Collectors.toList());
        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).in(BaseNestEntity::getNestId, nestIds);
        List<BaseNestEntity> baseNestEntityList = baseNestMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(baseNestEntityList)) {
            return;
        }
        Map<String, BaseNestEntity> nestEntityMap = baseNestEntityList.stream().collect(Collectors.toMap(BaseNestEntity::getNestId, bean -> bean, (key1, key2) -> key1));
        for (DataAnalysisBasePageOutDTO result : results) {
            BaseNestEntity baseNestEntity = nestEntityMap.get(String.valueOf(result.getNestId()));
            if (baseNestEntity == null) {
                continue;
            }
            result.setNestType(baseNestEntity.getType());
        }
    }
}
