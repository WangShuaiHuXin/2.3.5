package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.DateUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.common.constant.NestConstant;
import com.imapcloud.nest.mapper.MissionPhotoMapper;
import com.imapcloud.nest.mapper.MissionRecordsMapper;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.v2.common.enums.*;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.utils.DownLoadUtils;
import com.imapcloud.nest.v2.dao.entity.*;
import com.imapcloud.nest.v2.dao.mapper.*;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisDetailQueryCriteriaPO;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisDetailSumInPO;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisMarkQueryCriteriaPO;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisMarkUpdateInPO;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisDetailMarkOutPO;
import com.imapcloud.nest.v2.manager.dataobj.in.DataAnalysisHisResultInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DataAnalysisResultGroupOutDO;
import com.imapcloud.nest.v2.manager.event.ConfirmPicEvent;
import com.imapcloud.nest.v2.manager.event.DetailDelEvent;
import com.imapcloud.nest.v2.manager.event.MarkDelResultEvent;
import com.imapcloud.nest.v2.manager.sql.AIAnalysisPhotoManager;
import com.imapcloud.nest.v2.service.DataAnalysisDetailService;
import com.imapcloud.nest.v2.service.DataAnalysisMarkService;
import com.imapcloud.nest.v2.service.DataAnalysisResultService;
import com.imapcloud.nest.v2.service.TopicService;
import com.imapcloud.nest.v2.service.converter.DataAnalysisDetailConverter;
import com.imapcloud.nest.v2.service.converter.DataAnalysisMarkConverter;
import com.imapcloud.nest.v2.service.converter.DataAnalysisMarkMergeConverter;
import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisDetailOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisDetailPageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMissionRecordOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisStateSumOutDTO;
import com.imapcloud.nest.v2.web.vo.req.DataAnalysisResultGroupReqVO;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisDetailServiceImpl.java
 * @Description DataAnalysisDetailServiceImpl
 * @createTime 2022年07月13日 15:22:00
 */
@Slf4j
@Service
public class DataAnalysisDetailServiceImpl implements DataAnalysisDetailService {

    @Resource
    private DataAnalysisDetailMapper dataAnalysisDetailMapper;

    @Resource
    private DataAnalysisBaseMapper dataAnalysisBaseMapper;

    @Resource
    private DataAnalysisMarkMapper dataAnalysisMarkMapper;

    @Resource
    private DataAnalysisMarkService dataAnalysisMarkService;

    @Resource
    private DataAnalysisTopicLevelMapper dataAnalysisTopicLevelMapper;

    @Resource
    private DataAnalysisTopicIndustryMapper dataAnalysisTopicIndustryMapper;

    @Resource
    private DataAnalysisTopicProblemMapper dataAnalysisTopicProblemMapper;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MissionRecordsMapper missionRecordsMapper;

    @Resource
    private DataAnalysisMarkMergeMapper dataAnalysisMarkMergeMapper;

    @Resource
    private DataAnalysisResultGroupMapper dataAnalysisResultGroupMapper;

    @Resource
    private DataAnalysisResultMapper dataAnalysisResultMapper;

    @Resource
    private DataAnalysisResultService dataAnalysisResultService;

    @Resource
    private TopicService topicService;

    @Resource
    private AIAnalysisPhotoManager aiAnalysisPhotoManager;

    @Resource
    private MissionPhotoService missionPhotoService;

    @Resource
    private MissionPhotoMapper missionPhotoMapper;

    /**
     * 根据任务架次记录、照片状态分页筛选照片数据
     *
     * @param dataAnalysisDetailPageInDTO
     * @return
     */
    @Override
    public PageResultInfo<DataAnalysisDetailPageOutDTO> queryDetailPage(DataAnalysisDetailPageInDTO dataAnalysisDetailPageInDTO) {
        List<DataAnalysisDetailPageOutDTO> results = new ArrayList<>();
        long total = 0L;
        DataAnalysisDetailQueryCriteriaPO po = DataAnalysisDetailQueryCriteriaPO.builder()
                .missionRecordId(dataAnalysisDetailPageInDTO.getMissionRecordId())
                .photoState(dataAnalysisDetailPageInDTO.getPhotoState())
                .pushState(dataAnalysisDetailPageInDTO.getPushState())
                .centerBaseId(dataAnalysisDetailPageInDTO.getCenterBaseId())
                .picType(dataAnalysisDetailPageInDTO.getPicType())
                .desc(dataAnalysisDetailPageInDTO.getDesc())
                .build();
        total = this.dataAnalysisDetailMapper.countByCondition(po);
        if (total > 0) {
            List<DataAnalysisCenterDetailEntity> entities = this.dataAnalysisDetailMapper.selectByCondition(po, PagingRestrictDo.getPagingRestrict(dataAnalysisDetailPageInDTO));
            // 获取详情对应的图片AI识别状态
            Map<Long, Integer> detailAIStateMappings = getDetailAIStateMappings(entities);
            results = entities.stream()
                    .map(x -> {
                        DataAnalysisDetailPageOutDTO convert = DataAnalysisDetailConverter.INSTANCES.convert(x);
                        if (!CollectionUtils.isEmpty(detailAIStateMappings) && detailAIStateMappings.containsKey(convert.getCenterDetailId())) {
                            convert.setAiAnalysis(detailAIStateMappings.get(convert.getCenterDetailId()));
                        }
                        return convert;
                    })
                    .collect(Collectors.toList());
        }

        return PageResultInfo.of(total, results);
    }


    private Map<Long, Integer> getDetailAIStateMappings(List<DataAnalysisCenterDetailEntity> entities) {
        if (!CollectionUtils.isEmpty(entities)) {
            Set<Long> centerDetailIds = entities.stream().map(DataAnalysisCenterDetailEntity::getCenterDetailId).collect(Collectors.toSet());
            List<AIAnalysisPhotoEntity> photoEntities = aiAnalysisPhotoManager.fetchAnalysisPhotos(centerDetailIds, AIAnalysisPhotoStateEnum.EXECUTING.getType(), AIAnalysisPhotoStateEnum.PAUSED.getType());
            if (!CollectionUtils.isEmpty(photoEntities)) {
                return photoEntities.stream().collect(Collectors.toMap(AIAnalysisPhotoEntity::getCenterDetailId, AIAnalysisPhotoEntity::getState));
            }
        }
        return Collections.emptyMap();
    }

    /**
     * 根据任务架次记录、查询明细ID
     *
     * @param centerBaseId
     * @return
     */
    @Override
    public List<DataAnalysisDetailOutDTO> queryAllDetail(Long centerBaseId, Integer picStatus, Integer picType) {
        List<DataAnalysisDetailOutDTO> detailList = new ArrayList<>();
        if (centerBaseId != null) {
            DataAnalysisWebPicStatusEnum dataAnalysisWebPicStatusEnum = Optional.ofNullable(picStatus)
                    .flatMap(DataAnalysisWebPicStatusEnum::findMatch)
                    .orElseGet(() -> null);
            LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                    .eq(DataAnalysisCenterDetailEntity::getCenterBaseId, centerBaseId)
                    .eq(dataAnalysisWebPicStatusEnum != null && dataAnalysisWebPicStatusEnum.getCode() != -1, DataAnalysisCenterDetailEntity::getPhotoState, dataAnalysisWebPicStatusEnum.getPhotoState())
                    .eq(dataAnalysisWebPicStatusEnum != null && dataAnalysisWebPicStatusEnum.getCode() != -1, DataAnalysisCenterDetailEntity::getPushState, dataAnalysisWebPicStatusEnum.getPushState())
                    .eq(picType != null, DataAnalysisCenterDetailEntity::getPicType, picType);
            detailList = ((List<DataAnalysisCenterDetailEntity>) this.dataAnalysisDetailMapper.selectList(queryWrapper))
                    .stream()
                    .map(DataAnalysisDetailConverter.INSTANCES::convertDetail)
                    .collect(Collectors.toList());
        }
        return detailList;
    }

    @Override
    public List<DataAnalysisDetailOutDTO> findDataAnalysisDetails(List<Long> centerDetailIds) {
        if (!CollectionUtils.isEmpty(centerDetailIds)) {
            LambdaQueryWrapper<DataAnalysisCenterDetailEntity> con = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                    .in(DataAnalysisCenterDetailEntity::getCenterDetailId, centerDetailIds);
            List<DataAnalysisCenterDetailEntity> entities = dataAnalysisDetailMapper.selectList(con);
            if (!CollectionUtils.isEmpty(entities)) {
                return entities.stream()
                        .map(DataAnalysisDetailConverter.INSTANCES::convertDetail)
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    /**
     * 根据任务架次记录、查询明细ID
     *
     * @param centerBaseId
     * @param picStatus
     * @param picType
     * @return
     */
    @Override
    public List<DataAnalysisDetailOutDTO> queryPicForBrowseMark(Long centerBaseId, Integer picStatus, Integer picType, int desc) {
        List<DataAnalysisDetailOutDTO> detailList = new ArrayList<>();
        if (centerBaseId != null) {
            DataAnalysisWebPicStatusEnum dataAnalysisWebPicStatusEnum = Optional.ofNullable(picStatus)
                    .flatMap(DataAnalysisWebPicStatusEnum::findMatch)
                    .orElseGet(() -> null);
            LambdaQueryWrapper<DataAnalysisCenterDetailEntity> queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                    .eq(DataAnalysisCenterDetailEntity::getCenterBaseId, centerBaseId)
                    .eq(dataAnalysisWebPicStatusEnum != null && dataAnalysisWebPicStatusEnum.getCode() != -1, DataAnalysisCenterDetailEntity::getPhotoState, dataAnalysisWebPicStatusEnum.getPhotoState())
                    .eq(dataAnalysisWebPicStatusEnum != null && dataAnalysisWebPicStatusEnum.getCode() != -1, DataAnalysisCenterDetailEntity::getPushState, dataAnalysisWebPicStatusEnum.getPushState())
                    .eq(picType != null, DataAnalysisCenterDetailEntity::getPicType, picType)
                    .select(DataAnalysisCenterDetailEntity::getCenterDetailId
                            , DataAnalysisCenterDetailEntity::getCenterBaseId
                            , DataAnalysisCenterDetailEntity::getPhotoId
                            , DataAnalysisCenterDetailEntity::getPhotoState
                            , DataAnalysisCenterDetailEntity::getPushState
                            , DataAnalysisCenterDetailEntity::getImagePath
                            , DataAnalysisCenterDetailEntity::getLongitude
                            , DataAnalysisCenterDetailEntity::getLatitude
                            , DataAnalysisCenterDetailEntity::getOrgCode
                            , DataAnalysisCenterDetailEntity::getPhotoName
                            , DataAnalysisCenterDetailEntity::getPhotoCreateTime
                            , DataAnalysisCenterDetailEntity::getThumImagePath
                    );
            if (desc == 1) {

                queryWrapper.orderByDesc(DataAnalysisCenterDetailEntity::getPhotoCreateTime);
            } else {

                queryWrapper.orderByAsc(DataAnalysisCenterDetailEntity::getPhotoCreateTime);
            }
            queryWrapper.orderByDesc(DataAnalysisCenterDetailEntity::getId);
            detailList = ((List<DataAnalysisCenterDetailEntity>) this.dataAnalysisDetailMapper.selectList(queryWrapper))
                    .stream()
                    .map(DataAnalysisDetailConverter.INSTANCES::convertDetail)
                    .collect(Collectors.toList());
            getPhotoAltitude(detailList);
        }
        return detailList;
    }

    /**
     * 统计状态数量 - 汇总时间段
     *
     * @param dataAnalysisDetailSumInDTO
     * @return
     */
    @Override
    public DataAnalysisStateSumOutDTO countStateSum(DataAnalysisDetailSumInDTO dataAnalysisDetailSumInDTO) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        DataAnalysisDetailSumInPO dataAnalysisDetailSumInPO = DataAnalysisDetailSumInPO.builder()
                .startTime(dataAnalysisDetailSumInDTO.getStartTime())
                .endTime(dataAnalysisDetailSumInDTO.getEndTime())
                .missionRecordId(dataAnalysisDetailSumInDTO.getMissionRecordId())
                .missionId(dataAnalysisDetailSumInDTO.getMissionId())
                .orgCode(dataAnalysisDetailSumInDTO.getOrgId())
                .visibleOrgCode(orgCode)
                .centerBaseId(dataAnalysisDetailSumInDTO.getCenterBaseId())
                .build();
        //查询所有满足条件数据
        List<DataAnalysisCenterDetailEntity> dataAnalysisCenterDetailEntityList = this.dataAnalysisDetailMapper.queryByCondition(dataAnalysisDetailSumInPO);
        //按状态分别分组
        DataAnalysisStateSumOutDTO sumOutDTO = countNumForEntity(dataAnalysisCenterDetailEntityList);
        //拼装返回最后结果
        return Optional.ofNullable(sumOutDTO).orElseGet(DataAnalysisStateSumOutDTO::new);
    }

    /**
     * 统计汇总 -将list下的数据，进行状态汇总
     *
     * @param dataAnalysisCenterDetailEntityList
     * @return
     */
    @Override
    public DataAnalysisStateSumOutDTO countNumForEntity(List<DataAnalysisCenterDetailEntity> dataAnalysisCenterDetailEntityList) {
        DataAnalysisStateSumOutDTO sumOutDTO = new DataAnalysisStateSumOutDTO();
        if (CollectionUtil.isNotEmpty(dataAnalysisCenterDetailEntityList)) {
            sumOutDTO = new DataAnalysisStateSumOutDTO();
            sumOutDTO.setPhotoId(dataAnalysisCenterDetailEntityList.get(0).getPhotoId())
                    .setCenterBaseId(dataAnalysisCenterDetailEntityList.get(0).getCenterBaseId())
                    .setCenterDetailId(dataAnalysisCenterDetailEntityList.get(0).getCenterDetailId());
            for (DataAnalysisCenterDetailEntity entity : dataAnalysisCenterDetailEntityList) {
                //【提交态】【待分析】
                if (Objects.equals(DataAnalysisPicStatusEnum.NEED_ANALYZE.getType(), entity.getPhotoState())
                        && Objects.equals(DataAnalysisPicPushStatusEnum.COMMIT.getType(), entity.getPushState())) {
                    sumOutDTO.setNeedAnalyzeSum(sumOutDTO.getNeedAnalyzeSum() == null ? 1 : sumOutDTO.getNeedAnalyzeSum() + 1);
                    //【提交态】【有问题】
                } else if (Objects.equals(DataAnalysisPicStatusEnum.PROBLEM.getType(), entity.getPhotoState())
                        && Objects.equals(DataAnalysisPicPushStatusEnum.COMMIT.getType(), entity.getPushState())) {
                    sumOutDTO.setNeedConfirmProblemSum(sumOutDTO.getNeedConfirmProblemSum() == null ? 1 : sumOutDTO.getNeedConfirmProblemSum() + 1);
                    //【提交态】【无问题】
                } else if (Objects.equals(DataAnalysisPicStatusEnum.NO_PROBLEM.getType(), entity.getPhotoState())
                        && Objects.equals(DataAnalysisPicPushStatusEnum.COMMIT.getType(), entity.getPushState())) {
                    sumOutDTO.setNeedConfirmNoProblemSum(sumOutDTO.getNeedConfirmNoProblemSum() == null ? 1 : sumOutDTO.getNeedConfirmNoProblemSum() + 1);
                    //【核实态】【有问题】
                } else if (Objects.equals(DataAnalysisPicStatusEnum.PROBLEM.getType(), entity.getPhotoState())
                        && Objects.equals(DataAnalysisPicPushStatusEnum.VERIFY.getType(), entity.getPushState())) {
                    sumOutDTO.setProblemSum(sumOutDTO.getProblemSum() == null ? 1 : sumOutDTO.getProblemSum() + 1);
                    //【核实态】【无问题】
                } else if (Objects.equals(DataAnalysisPicStatusEnum.NO_PROBLEM.getType(), entity.getPhotoState())
                        && Objects.equals(DataAnalysisPicPushStatusEnum.VERIFY.getType(), entity.getPushState())) {
                    sumOutDTO.setNoProblemSum(sumOutDTO.getNoProblemSum() == null ? 1 : sumOutDTO.getNoProblemSum() + 1);
                }
            }
        }
        return sumOutDTO;
    }

    /**
     * 单个-核实照片
     *
     * @param detailId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean confirmPic(Long detailId) {
        boolean returnValue = false;
        if (detailId != null) {
            returnValue = this.dataAnalysisMarkService.confirmMarkForPic(detailId);
            //回写照片状态
            this.handlePicState(CollectionUtil.newArrayList(detailId));
//            List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = this.dataAnalysisMarkService.queryMarkByDetails(CollectionUtil.newArrayList(new Long[]{detailId}));
            List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS = this.dataAnalysisMarkMapper.queryDetailMark(CollectionUtil.newArrayList(detailId));
            this.fillTopicData(dataAnalysisDetailMarkOutPOS);
            List<DataAnalysisMarkDrawInDTO> dataAnalysisMarkDrawInDTOS = dataAnalysisDetailMarkOutPOS.stream().map(DataAnalysisMarkConverter.INSTANCES::convertDraw).collect(Collectors.toList());
            //触发事件
            this.applicationContext.publishEvent(new ConfirmPicEvent(dataAnalysisMarkDrawInDTOS));
        }
        return returnValue;
    }

    /**
     * 批量核实图片
     *
     * @param detailIds
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean confirmPicList(List<Long> detailIds) {
        if (CollectionUtil.isNotEmpty(detailIds)) {
            checkAIAnalysisStatus(detailIds);
            //过滤掉已经核实过的数据
            detailIds = this.filterData(detailIds);
            if (CollectionUtil.isEmpty(detailIds)) {
                return true;
            }
            detailIds.stream().forEach(pid -> {
                this.dataAnalysisMarkService.confirmMarkForPic(pid);
            });
            //回写照片状态
            this.handlePicState(detailIds);

            List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS = this.dataAnalysisMarkMapper.queryDetailMark(CollectionUtil.newArrayList(detailIds));
            //填充专题类型字段
            this.fillTopicDataWithCheck(dataAnalysisDetailMarkOutPOS);

            List<DataAnalysisMarkDrawInDTO> dataAnalysisMarkDrawInDTOS = dataAnalysisDetailMarkOutPOS.stream().map(DataAnalysisMarkConverter.INSTANCES::convertDraw).collect(Collectors.toList());

            //发布事件
            this.applicationContext.publishEvent(new ConfirmPicEvent(dataAnalysisMarkDrawInDTOS));
            log.info("打印【confirmPicList】-批量核实图片原始数据：{}，\r\n成功数据:{}"
                    , detailIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        return true;
    }

    /**
     * 拼装内容
     *
     * @param dataAnalysisDetailMarkOutPOS
     * @return
     */
    @Override
    public List<DataAnalysisDetailMarkOutPO> fillTopicData(List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS) {
        if (CollectionUtil.isEmpty(dataAnalysisDetailMarkOutPOS)) {
            return dataAnalysisDetailMarkOutPOS;
        }
        List<Long> levelIds = dataAnalysisDetailMarkOutPOS.stream()
                .map(DataAnalysisDetailMarkOutPO::getTopicLevelId).distinct().collect(Collectors.toList());
        Map<Long, String> levelMap = this.getLevel(levelIds);

        List<Integer> industryIds = dataAnalysisDetailMarkOutPOS.stream()
                .map(DataAnalysisDetailMarkOutPO::getIndustryType)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Integer, String> industryMap = this.getIndustry(industryIds);

        List<Long> problemIds = dataAnalysisDetailMarkOutPOS.stream()
                .map(DataAnalysisDetailMarkOutPO::getTopicProblemId).distinct().collect(Collectors.toList());
        Map<Long, String> problemMap = this.getProblem(problemIds);

        dataAnalysisDetailMarkOutPOS
                .forEach(x -> {
                    x.setTopicLevelName(MessageUtils.getMessage(levelMap.get(x.getTopicLevelId())));
                    x.setTopicIndustryName(industryMap.get(x.getIndustryType()));
                    x.setTopicProblemName(problemMap.getOrDefault(x.getTopicProblemId(), x.getAiProblemName()));
                });

        return dataAnalysisDetailMarkOutPOS;
    }

    @Override
    public List<DataAnalysisDetailMarkOutPO> fillTopicDataWithCheck(List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS) {
        if (CollectionUtil.isEmpty(dataAnalysisDetailMarkOutPOS)) {
            return dataAnalysisDetailMarkOutPOS;
        }
        List<Long> levelIds = dataAnalysisDetailMarkOutPOS.stream()
                .map(DataAnalysisDetailMarkOutPO::getTopicLevelId).distinct().collect(Collectors.toList());
        Map<Long, String> levelMap = this.getLevel(levelIds);

        List<Integer> industryTypes = dataAnalysisDetailMarkOutPOS.stream()
                .map(DataAnalysisDetailMarkOutPO::getIndustryType)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Integer, String> industryMap = this.getIndustry(industryTypes);

        List<Long> problemIds = dataAnalysisDetailMarkOutPOS.stream()
                .map(DataAnalysisDetailMarkOutPO::getTopicProblemId).distinct().collect(Collectors.toList());
        Map<Long, String> problemMap = this.getProblem(problemIds);

        dataAnalysisDetailMarkOutPOS
                .forEach(x -> {
                    x.setTopicLevelName(MessageUtils.getMessage(levelMap.get(x.getTopicLevelId())));
                    if (StringUtil.isEmpty(industryMap.get(x.getIndustryType()))) {
                        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PROBLEM_INDUSTRY_UNDEFINE.getContent()));
                    }
                    x.setTopicIndustryName(industryMap.get(x.getIndustryType()));
                    if (StringUtil.isEmpty(problemMap.get(x.getTopicProblemId()))) {
                        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PROBLEM_INDUSTRY_UNDEFINE.getContent()));
                    }
                    x.setTopicProblemName(problemMap.get(x.getTopicProblemId()));
                });

        return dataAnalysisDetailMarkOutPOS;
    }

    /**
     * 获取等级
     *
     * @param levelIds
     * @return
     */
    public Map<Long, String> getLevel(List<Long> levelIds) {
        Map<Long, String> map = new HashMap<Long, String>(8);
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisTopicLevelEntity.class)
                .in(DataAnalysisTopicLevelEntity::getTopicLevelId, levelIds)
                .select(DataAnalysisTopicLevelEntity::getTopicLevelId, DataAnalysisTopicLevelEntity::getTopicLevelName);
        List<DataAnalysisTopicLevelEntity> resultList = this.dataAnalysisTopicLevelMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(resultList)) {
            map = resultList.stream()
                    .collect(Collectors.toMap(DataAnalysisTopicLevelEntity::getTopicLevelId
                            , DataAnalysisTopicLevelEntity::getTopicLevelName
                            , (k, v) -> v));
        }
        return map;
    }

    /**
     * 获取行业信息
     *
     * @param industryTypes
     * @return
     */
    public Map<Integer, String> getIndustry(List<Integer> industryTypes) {
        Map<String, String> industryMappings = topicService.getIndustryMappings();
        if (!CollectionUtils.isEmpty(industryMappings)) {
            return industryTypes.stream()
                    .filter(r -> industryMappings.containsKey(r.toString()))
                    .collect(Collectors.toMap(r -> r, r -> industryMappings.get(r.toString())));
        }
        return Collections.emptyMap();
    }

    /**
     * 获取问题信息
     *
     * @param problemIds
     * @return
     */
    public Map<Long, String> getProblem(List<Long> problemIds) {
        Map<Long, String> map = new HashMap<Long, String>(8);
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisTopicProblemEntity.class)
                .in(DataAnalysisTopicProblemEntity::getTopicProblemId, problemIds)
                .eq(DataAnalysisTopicProblemEntity::getDeleted, 0)
                .select(DataAnalysisTopicProblemEntity::getTopicProblemId, DataAnalysisTopicProblemEntity::getTopicProblemName);
        List<DataAnalysisTopicProblemEntity> resultList = this.dataAnalysisTopicProblemMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(resultList)) {
            map = resultList.stream()
                    .collect(Collectors.toMap(DataAnalysisTopicProblemEntity::getTopicProblemId
                            , DataAnalysisTopicProblemEntity::getTopicProblemName
                            , (k, v) -> v));
        }
        return map;
    }


    /**
     * 过滤已核实数据
     *
     * @param details
     * @return
     */
    public List<Long> filterData(List<Long> details) {
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                .in(DataAnalysisCenterDetailEntity::getCenterDetailId, details)
                .eq(DataAnalysisCenterDetailEntity::getPushState, DataAnalysisPicPushStatusEnum.COMMIT.getType())
                .select(DataAnalysisCenterDetailEntity::getCenterDetailId);
        List<Long> filteredDetails = ((List<DataAnalysisCenterDetailEntity>) this.dataAnalysisDetailMapper.selectList(queryWrapper)).stream().map(DataAnalysisCenterDetailEntity::getCenterDetailId).collect(Collectors.toList());
        return filteredDetails;
    }

    /**
     * @param pidSuccessList
     */
    public void handlePicState(List<Long> pidSuccessList) {
        if (CollectionUtil.isNotEmpty(pidSuccessList)) {
            //找出待分析的数据，核实会变为无问题

            LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                    .eq(DataAnalysisCenterDetailEntity::getPhotoState, DataAnalysisPicStatusEnum.NEED_ANALYZE.getType())
                    .in(DataAnalysisCenterDetailEntity::getCenterDetailId, pidSuccessList);
//                    .eq(DataAnalysisCenterDetailEntity::getDeleted,NestConstant.DeleteType.NOT_DELETE);
            List<Long> analysisIds = ((List<DataAnalysisCenterDetailEntity>) this.dataAnalysisDetailMapper.selectList(queryWrapper)).stream()
                    .map(DataAnalysisCenterDetailEntity::getCenterDetailId)
                    .collect(Collectors.toList());

            //更新【提交态】【待分析】数据为【核实态】【无问题】
            this.writeBackPicState(analysisIds, DataAnalysisPicStatusEnum.NO_PROBLEM.getType(), DataAnalysisPicPushStatusEnum.VERIFY.getType());
            //更新非【提交态】【待分析】的数据
            this.writeBackPicState(CollectionUtil.subtractToList(pidSuccessList, analysisIds), null, DataAnalysisPicPushStatusEnum.VERIFY.getType());
        }
    }

    /**
     * 回写照片状态
     *
     * @param detailIds
     * @param photoState
     * @param pushState
     * @return
     */
    @Override
    public boolean writeBackPicState(List<Long> detailIds, Integer photoState, Integer pushState) {
        boolean success = true;
        if (CollectionUtil.isNotEmpty(detailIds)) {
            if (photoState == null && pushState == null) {
                return true;
            }
            LambdaUpdateWrapper<DataAnalysisCenterDetailEntity> updateWrapper = Wrappers.lambdaUpdate(DataAnalysisCenterDetailEntity.class)
                    .set(photoState != null, DataAnalysisCenterDetailEntity::getPhotoState, photoState)
                    .set(pushState != null, DataAnalysisCenterDetailEntity::getPushState, pushState)
                    .in(DataAnalysisCenterDetailEntity::getCenterDetailId, detailIds);
            success = this.dataAnalysisDetailMapper.update(null, updateWrapper) > 0 ? Boolean.TRUE : Boolean.FALSE;

        }
        return success;
    }

    /**
     * 一键重置图片 - 一键重置
     * 将所有状态重置为 【待分析】，会删除对应数据
     *
     * @param detailIds
     * @return
     */
    @Override
    public boolean resetPicList(List<Long> detailIds) {
       /* //查询对应detailIds所有状态为核实的标注
        List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = this.dataAnalysisMarkMapper.selectList(Wrappers.<DataAnalysisMarkEntity>lambdaQuery()
                .in(DataAnalysisMarkEntity::getDetailId, detailIds)
                .eq(DataAnalysisMarkEntity::getDeleted, "0")
                .eq(DataAnalysisMarkEntity::getMarkState, DataAnalysisPicPushStatusEnum.VERIFY.getType()));
        //查询是否已核实的标注在结果表都有数据
        if (CollectionUtil.isNotEmpty(dataAnalysisMarkEntities)) {
            List<Long> markIds = dataAnalysisMarkEntities.stream().map(DataAnalysisMarkEntity::getMarkId).collect(Collectors.toList());
            List<DataAnalysisResultEntity> dataAnalysisResultEntities = dataAnalysisResultMapper.selectList(Wrappers.<DataAnalysisResultEntity>lambdaQuery()
                    .in(DataAnalysisResultEntity::getMarkId, markIds)
                    .eq(DataAnalysisResultEntity::getDeleted, "0"));
            //如果已核实结果数和应核实记录数不一致，则不允许撤回
            if (markIds.size() != dataAnalysisResultEntities.size()) {
                throw new BusinessException("正在核实中,请稍后再进行撤回操作");
            }
        }*/
        this.checkResultStatus(detailIds);
        //定位为待分析
        DataAnalysisWebPicStatusEnum dataAnalysisWebPicStatusEnum = DataAnalysisWebPicStatusEnum.findMatch(0).get();
        LambdaUpdateWrapper wrapper = Wrappers.lambdaUpdate(DataAnalysisCenterDetailEntity.class)
                .set(DataAnalysisCenterDetailEntity::getPhotoState, dataAnalysisWebPicStatusEnum.getPhotoState())
                .set(DataAnalysisCenterDetailEntity::getPushState, dataAnalysisWebPicStatusEnum.getPushState())
//                .set(DataAnalysisCenterDetailEntity::getImageMarkPath,"")
//                .set(DataAnalysisCenterDetailEntity::getThumImageMarkPath,"")
                .in(DataAnalysisCenterDetailEntity::getCenterDetailId, detailIds);
//                .eq(DataAnalysisCenterDetailEntity::getDeleted,NestConstant.DeleteType.NOT_DELETE);
        this.dataAnalysisDetailMapper.update(null, wrapper);
        this.dataAnalysisMarkService.deleteMarksByDetail(detailIds);
        this.applicationContext.publishEvent(new DetailDelEvent(detailIds));
        this.applicationContext.publishEvent(new MarkDelResultEvent(detailIds, false));
        return true;
    }

    /**
     * 撤回
     *
     * @param detailIds
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean unDoPics(List<Long> detailIds) {
        this.checkResultStatus(detailIds);
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                .in(DataAnalysisCenterDetailEntity::getCenterDetailId, detailIds);
        List<DataAnalysisCenterDetailEntity> detailEntities = this.dataAnalysisDetailMapper.selectList(queryWrapper);
        List<Long> details = detailEntities.stream().map(DataAnalysisCenterDetailEntity::getCenterDetailId).collect(Collectors.toList());
        LambdaUpdateWrapper updateWrapper = Wrappers.lambdaUpdate(DataAnalysisCenterDetailEntity.class)
                .set(DataAnalysisCenterDetailEntity::getPushState, DataAnalysisPicPushStatusEnum.COMMIT.getType())
                .in(DataAnalysisCenterDetailEntity::getCenterDetailId, details);
        this.dataAnalysisDetailMapper.update(null, updateWrapper);
        //取消核实标注
        LambdaUpdateWrapper updateWrapperForMark = Wrappers.lambdaUpdate(DataAnalysisMarkEntity.class)
                .set(DataAnalysisMarkEntity::getMarkState, DataAnalysisMarkStatusEnum.NOT_CONFIRM.getType())
                .in(DataAnalysisMarkEntity::getDetailId, detailIds);
        this.dataAnalysisMarkMapper.update(null, updateWrapperForMark);
        //触发事件
        this.applicationContext.publishEvent(new DetailDelEvent(detailIds));
        /*this.applicationContext.publishEvent(new MarkDelResultEvent(detailIds, true));
        this.applicationContext.publishEvent(new MarkDelResultEvent(details, true));*/
        detailIds.addAll(details);
        if (CollectionUtil.isNotEmpty(details)) {
            List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = this.dataAnalysisMarkMapper.queryDeleteDataByDetailId(details);
            List<Long> markIds = dataAnalysisMarkEntities.stream().map(DataAnalysisMarkEntity::getMarkId).collect(Collectors.toList());
            //为 true 则为撤回图片
            this.dataAnalysisResultService.deleteByMarkIdList(markIds, true);
        }
        //v2.1.2取消核实的问题是否属于已合并问题处理
        //this.applicationContext.publishEvent(new MarkUndoMergeEvent(detailIds));
        return true;
    }

    /**
     * 批量下载图片信息
     *
     * @param detailIds
     * @param response
     */
    @Override
    public void downloadData(List<Long> detailIds, HttpServletResponse response) {
        List<String> markPathList = new ArrayList<>(), addrPathList = new ArrayList<>(), photoPathList = new ArrayList<>(), allPathList = new ArrayList<>();
        //查询detail数据
        LambdaQueryWrapper wrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                .in(DataAnalysisCenterDetailEntity::getCenterDetailId, detailIds);
        List<DataAnalysisCenterDetailEntity> dataAnalysisCenterDetailEntities = this.dataAnalysisDetailMapper.selectList(wrapper);
        DataAnalysisWebPicStatusEnum webPicStatusEnum = DataAnalysisWebPicStatusEnum.findMatch(3).orElseThrow(() -> new BusinessException("查询不到对应的状态"));
        dataAnalysisCenterDetailEntities = dataAnalysisCenterDetailEntities.stream()
                .filter(x -> (webPicStatusEnum.getPhotoState() == x.getPhotoState() && webPicStatusEnum.getPushState() == x.getPushState()))
                .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(dataAnalysisCenterDetailEntities)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THERE_IS_NO_DATA_THAT_CAN_BE_EXPORTED_PLEASE_CHECK.getContent()));
        }
        photoPathList = dataAnalysisCenterDetailEntities.stream()
                .map(DataAnalysisCenterDetailEntity::getImageMarkPath)
                .filter(StringUtil::isNotEmpty)
                .collect(Collectors.toList());

        List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = this.dataAnalysisMarkService.queryMarkByDetails(detailIds);
        if (CollectionUtil.isNotEmpty(dataAnalysisMarkEntities)) {
            markPathList = dataAnalysisMarkEntities.stream().map(DataAnalysisMarkEntity::getMarkImagePath)
                    .filter(StringUtil::isNotEmpty)
                    .collect(Collectors.toList());
            addrPathList = dataAnalysisMarkEntities.stream().map(DataAnalysisMarkEntity::getAddrImagePath)
                    .filter(StringUtil::isNotEmpty)
                    .collect(Collectors.toList());
        }

        allPathList.addAll(markPathList);
        allPathList.addAll(addrPathList);
        allPathList.addAll(photoPathList);
        if (CollectionUtil.isEmpty(allPathList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THERE_IS_NO_DATA_THAT_CAN_BE_EXPORTED_PLEASE_CHECK.getContent()));
        }

        if (response == null) {
            return;
        }

        //获取任务名
        Long baseId = dataAnalysisCenterDetailEntities.get(0).getCenterBaseId();
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterBaseEntity.class)
                .eq(DataAnalysisCenterBaseEntity::getCenterBaseId, baseId)
                .select(DataAnalysisCenterBaseEntity::getTaskName
                        , DataAnalysisCenterBaseEntity::getMissionFlyIndex
                        , DataAnalysisCenterBaseEntity::getMissionSeqId);
        //拼接文件名 - 任务名 架次号 执行次数
        String taskName = ((List<DataAnalysisCenterBaseEntity>) this.dataAnalysisBaseMapper.selectList(queryWrapper))
                .stream()
                .map(x -> String.format("%s-架次%s%s"
                        , x.getTaskName()
                        , x.getMissionSeqId() == null ? "" : String.valueOf(x.getMissionSeqId())
                        , StringUtils.isEmpty(x.getMissionFlyIndex()) ? "" : ("-" + x.getMissionFlyIndex()))
                )
                .findFirst()
                .orElseGet(() -> "");

        DownLoadUtils.downloadZipWithPip(allPathList, taskName, response);

    }

    /**
     * 删除数据
     *
     * @param detailIds
     * @return
     */
    @Override
    public boolean deleteDetails(List<Long> detailIds) {
        if (CollectionUtil.isNotEmpty(detailIds)) {
            LambdaUpdateWrapper wrapper = Wrappers.lambdaUpdate(DataAnalysisCenterDetailEntity.class)
                    .set(DataAnalysisCenterDetailEntity::getDeleted, NestConstant.DeleteType.DELETED)
                    .in(DataAnalysisCenterDetailEntity::getCenterDetailId, detailIds);
            this.dataAnalysisDetailMapper.update(null, wrapper);

            this.applicationContext.publishEvent(new DetailDelEvent(detailIds));
        }
        return true;
    }

    /**
     * 批量保存数据
     *
     * @param dataAnalysisDetailSaveInDTOS
     * @return
     */
    @Override
    public List<Long> saveBatch(List<DataAnalysisDetailSaveInDTO> dataAnalysisDetailSaveInDTOS) {
        List<Long> detailIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dataAnalysisDetailSaveInDTOS)) {
            List<DataAnalysisCenterDetailEntity> detailEntities = dataAnalysisDetailSaveInDTOS.stream()
                    .map(DataAnalysisDetailConverter.INSTANCES::convert)
                    .collect(Collectors.toList());
            this.dataAnalysisDetailMapper.saveBatch(detailEntities);
            detailIds = detailEntities.stream().map(DataAnalysisCenterDetailEntity::getCenterDetailId).collect(Collectors.toList());
        }
        return detailIds;
    }

    /**
     * 批量保存更新数据
     *
     * @param dataAnalysisDetailSaveInDTOS
     * @return
     */
    @Override
    public List<Long> saveOrUpdateBatch(List<DataAnalysisDetailSaveInDTO> dataAnalysisDetailSaveInDTOS) {
        List<Long> detailIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dataAnalysisDetailSaveInDTOS)) {
            List<DataAnalysisCenterDetailEntity> detailEntities = dataAnalysisDetailSaveInDTOS.stream()
                    .map(DataAnalysisDetailConverter.INSTANCES::convert)
                    .collect(Collectors.toList());
            this.dataAnalysisDetailMapper.saveOrUpdateBath(detailEntities);
            detailIds = detailEntities.stream().map(DataAnalysisCenterDetailEntity::getCenterDetailId).collect(Collectors.toList());
        }
        return detailIds;
    }

    /**
     * 批量保存数据
     *
     * @param dataAnalysisDetailSaveInDTOS
     * @return
     */
    @Override
    public List<Long> saveDetailAndMark(List<DataAnalysisDetailSaveInDTO> dataAnalysisDetailSaveInDTOS) {
        List<Long> detailIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dataAnalysisDetailSaveInDTOS)) {
            for (DataAnalysisDetailSaveInDTO detailSaveInDTO : dataAnalysisDetailSaveInDTOS) {
                DataAnalysisCenterDetailEntity detailEntity = DataAnalysisDetailConverter.INSTANCES.convert(detailSaveInDTO);
                detailEntity.setOrgCode(detailSaveInDTO.getOrgId());
                detailEntity.setBaseNestId(detailSaveInDTO.getNestId());
                this.dataAnalysisDetailMapper.insert(detailEntity);
                Long detailId = detailEntity.getCenterDetailId();
                List<DataAnalysisMarkSaveInDTO> markSaveInDTOS = detailSaveInDTO.getDataAnalysisMarkSaveInDTOList();
                if (CollectionUtil.isNotEmpty(markSaveInDTOS)) {
                    markSaveInDTOS.stream().forEach(x -> {
                        x.setDetailId(detailId);
                        x.setPhotoId(detailSaveInDTO.getPhotoId());
                        x.setAiMark(Boolean.FALSE);
                        x.setMarkState(DataAnalysisMarkStatusEnum.NOT_CONFIRM.getType());
                    });
                    this.dataAnalysisMarkService.saveBatch(markSaveInDTOS);
                }
                detailIds.add(detailId);
            }
        }
        return detailIds;
    }

    private Long getMissionRecordIdByDetailId(Long detailId) {
        LambdaQueryWrapper<DataAnalysisCenterDetailEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                .eq(DataAnalysisCenterDetailEntity::getCenterDetailId, detailId);
        List<DataAnalysisCenterDetailEntity> entityList = dataAnalysisDetailMapper.selectList(wrapper);
        if (CollUtil.isEmpty(entityList)) {
            throw new BusinessException("detailId不合法");
        }
        return entityList.get(0).getMissionRecordId();
    }

    /**
     * 查询历史照片，按任务ID以及正方形距离查询
     * 默认经度纬度小数点后五位数为1米， 实际经度加0.00001 == 0.85米 纬度加0.00001 = 1.1米
     *
     * @param dataAnalysisHistoryPicPageDTO
     * @return
     */
    @Override
    public PageResultInfo<DataAnalysisDetailPageOutDTO> queryHistory(DataAnalysisHistoryPicPageDTO dataAnalysisHistoryPicPageDTO) {

        AtomicReference<List<DataAnalysisDetailPageOutDTO>> dataAnalysisDetailPageOutDTOS = new AtomicReference<List<DataAnalysisDetailPageOutDTO>>();
        AtomicLong total = new AtomicLong(0L);
        String endTime = LocalDateTime.of(DateUtils.toLocalDate(dataAnalysisHistoryPicPageDTO.getEndTime()
                , DateUtils.DATE_FORMATTER_OF_CN)
                , LocalTime.of(23, 59, 59)).toString();
        Optional.ofNullable(dataAnalysisHistoryPicPageDTO).ifPresent(x -> {
            DataAnalysisDetailQueryCriteriaPO queryCriteriaPO = DataAnalysisDetailQueryCriteriaPO.builder()
                    .taskId(dataAnalysisHistoryPicPageDTO.getTaskId())
                    .leftDistinct(dataAnalysisHistoryPicPageDTO.getLongitude().subtract(BigDecimal.valueOf(dataAnalysisHistoryPicPageDTO.getDistinct() * 0.00001)))
                    .rightDistinct(dataAnalysisHistoryPicPageDTO.getLongitude().add(BigDecimal.valueOf(dataAnalysisHistoryPicPageDTO.getDistinct() * 0.00001)))
                    .upDistinct(dataAnalysisHistoryPicPageDTO.getLatitude().add(BigDecimal.valueOf(dataAnalysisHistoryPicPageDTO.getDistinct() * 0.00001)))
                    .downDistinct(dataAnalysisHistoryPicPageDTO.getLatitude().subtract(BigDecimal.valueOf(dataAnalysisHistoryPicPageDTO.getDistinct() * 0.00001)))
                    .startTime(dataAnalysisHistoryPicPageDTO.getStartTime())
                    .endTime(endTime)
                    .notExistDetailId(dataAnalysisHistoryPicPageDTO.getDetailId())
                    .missionId(dataAnalysisHistoryPicPageDTO.getMissionId())
                    .missionRecordId(dataAnalysisHistoryPicPageDTO.getMissionRecordId())
                    .desc(1)
                    .build();
            log.info("历史照片查询参数:{}", queryCriteriaPO.toString());
            total.set(this.dataAnalysisDetailMapper.countByCondition(queryCriteriaPO));
            if (total.get() > 0) {
                List<DataAnalysisCenterDetailEntity> detailEntities = this.dataAnalysisDetailMapper.selectByCondition(queryCriteriaPO, PagingRestrictDo.getPagingRestrict(dataAnalysisHistoryPicPageDTO));
                // 查询架次第几次飞行
                List<Long> centerBaseIdList = detailEntities.stream().map(DataAnalysisCenterDetailEntity::getCenterBaseId).collect(Collectors.toList());

                LambdaQueryWrapper<DataAnalysisCenterBaseEntity> queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterBaseEntity.class)
                        .in(DataAnalysisCenterBaseEntity::getCenterBaseId, centerBaseIdList);
                List<DataAnalysisCenterBaseEntity> dataAnalysisCenterBaseEntityList = dataAnalysisBaseMapper.selectList(queryWrapper);
                Map<Long, String> baseIdFlyIndexMap = dataAnalysisCenterBaseEntityList.stream()
                        .collect(Collectors.toMap(DataAnalysisCenterBaseEntity::getCenterBaseId, DataAnalysisCenterBaseEntity::getMissionFlyIndex, (key1, key2) -> key1));

                dataAnalysisDetailPageOutDTOS.set(detailEntities.stream().map(DataAnalysisDetailConverter.INSTANCES::convert)
                        .map(out -> {
                            out.setMissionFlyIndex(baseIdFlyIndexMap.getOrDefault(out.getCenterBaseId(), ""));
                            return out;
                        })
                        .collect(Collectors.toList()));
            }
        });

        return PageResultInfo.of(total.get(), dataAnalysisDetailPageOutDTOS.get());
    }

    @Override
    public List<DataAnalysisMissionRecordOutDTO> missionRecordCondition(DataAnalysisHistoryPicPageDTO dataAnalysisHistoryPicPageDTO) {

        Long excludeMissionRecordId = getMissionRecordIdByDetailId(dataAnalysisHistoryPicPageDTO.getDetailId());

        String endTime = LocalDateTime.of(DateUtils.toLocalDate(dataAnalysisHistoryPicPageDTO.getEndTime()
                , DateUtils.DATE_FORMATTER_OF_CN)
                , LocalTime.of(23, 59, 59)).toString();
        DataAnalysisDetailQueryCriteriaPO queryCriteriaPO = DataAnalysisDetailQueryCriteriaPO.builder()
                .taskId(dataAnalysisHistoryPicPageDTO.getTaskId())
                .leftDistinct(dataAnalysisHistoryPicPageDTO.getLongitude().subtract(BigDecimal.valueOf(dataAnalysisHistoryPicPageDTO.getDistinct() * 0.00001)))
                .rightDistinct(dataAnalysisHistoryPicPageDTO.getLongitude().add(BigDecimal.valueOf(dataAnalysisHistoryPicPageDTO.getDistinct() * 0.00001)))
                .upDistinct(dataAnalysisHistoryPicPageDTO.getLatitude().add(BigDecimal.valueOf(dataAnalysisHistoryPicPageDTO.getDistinct() * 0.00001)))
                .downDistinct(dataAnalysisHistoryPicPageDTO.getLatitude().subtract(BigDecimal.valueOf(dataAnalysisHistoryPicPageDTO.getDistinct() * 0.00001)))
                .startTime(dataAnalysisHistoryPicPageDTO.getStartTime())
                .endTime(endTime)
                .missionId(dataAnalysisHistoryPicPageDTO.getMissionId())
                .build();
        List<DataAnalysisCenterDetailEntity> entityList = dataAnalysisDetailMapper.queryMissionRecordInfo(queryCriteriaPO);
        List<DataAnalysisMissionRecordOutDTO> result = Lists.newLinkedList();
        if (CollUtil.isEmpty(entityList)) {
            return result;
        }

        LambdaQueryWrapper<MissionRecordsEntity> missionRecordsEntityLambdaQueryWrapper = Wrappers.lambdaQuery(MissionRecordsEntity.class)
                .in(MissionRecordsEntity::getId, entityList.stream().map(DataAnalysisCenterDetailEntity::getMissionRecordId).collect(Collectors.toList()));
        List<MissionRecordsEntity> missionRecordsEntityList = missionRecordsMapper.selectList(missionRecordsEntityLambdaQueryWrapper);
        if (CollUtil.isEmpty(missionRecordsEntityList)) {
            return result;
        }
        Map<Integer, MissionRecordsEntity> recordsEntityMap = missionRecordsEntityList.stream()
                .collect(Collectors.toMap(MissionRecordsEntity::getId, bean -> bean, (key1, key2) -> key1));
        for (DataAnalysisCenterDetailEntity dataAnalysisCenterDetailEntity : entityList) {
            MissionRecordsEntity missionRecordsEntity = recordsEntityMap.get(dataAnalysisCenterDetailEntity.getMissionRecordId().intValue());
            if (missionRecordsEntity == null) {
                continue;
            }
            DataAnalysisMissionRecordOutDTO dataAnalysisMissionRecordOutDTO = new DataAnalysisMissionRecordOutDTO();
            dataAnalysisMissionRecordOutDTO.setMissionRecordId(dataAnalysisCenterDetailEntity.getMissionRecordId().toString());
            StringBuilder sb = new StringBuilder();

            if (missionRecordsEntity.getFlyIndex() != null) {
                sb.append("架次#").append(missionRecordsEntity.getFlyIndex()).append(" ");
            }

            sb.append(missionRecordsEntity.getCreateTime().format(DateUtils.DATE_TIME_FORMATTER_OF_CN));
            dataAnalysisMissionRecordOutDTO.setName(sb.toString());
            dataAnalysisMissionRecordOutDTO.setCreateTime(missionRecordsEntity.getCreateTime());
            result.add(dataAnalysisMissionRecordOutDTO);
        }
        result.sort(Comparator.comparing(DataAnalysisMissionRecordOutDTO::getCreateTime).reversed());
        return result;
    }

    /**
     * 填充是否合并状态
     *
     * @param dataAnalysisDetailMarkOutPOS
     * @return
     */
    @Override
    public List<DataAnalysisDetailMarkOutPO> fillMergeState(List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS) {
        dataAnalysisDetailMarkOutPOS.stream().forEach(e -> {
            String groupId = dataAnalysisMarkMergeMapper.selectIsMergeGroup(e.getMarkId());
            if (!StringUtil.isEmpty(groupId)) {
                e.setResultGroupId(groupId);
            } else {
                e.setResultGroupId("");
            }
        });
        return dataAnalysisDetailMarkOutPOS;
    }

    /**
     * 查询是否有已经核实过
     *
     * @param detailIds
     * @return
     */
    @Override
    public List<Long> hasConfirmPic(List<Long> detailIds, List<Long> markIds) {
        List<Long> returnDetails = new ArrayList<>();
        if (CollectionUtil.isEmpty(detailIds) && CollectionUtil.isEmpty(markIds)) {
            return returnDetails;
        }
        if (CollectionUtil.isEmpty(detailIds)) {
            LambdaQueryWrapper queryWrap = Wrappers.lambdaQuery(DataAnalysisMarkEntity.class)
                    .in(DataAnalysisMarkEntity::getMarkId, markIds)
                    .select(DataAnalysisMarkEntity::getDetailId);
            List<DataAnalysisMarkEntity> markEntities = this.dataAnalysisMarkMapper.selectList(queryWrap);
            if (CollectionUtil.isEmpty(markEntities)) {
                return returnDetails;
            }
            detailIds = markEntities.stream().map(DataAnalysisMarkEntity::getDetailId).collect(Collectors.toList());
        }

        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                .eq(DataAnalysisCenterDetailEntity::getPushState, DataAnalysisPicPushStatusEnum.VERIFY.getType())
                .in(DataAnalysisCenterDetailEntity::getCenterDetailId, detailIds);
        List<DataAnalysisCenterDetailEntity> detailEntities = this.dataAnalysisDetailMapper.selectList(queryWrapper);
        returnDetails = detailEntities.stream().map(DataAnalysisCenterDetailEntity::getCenterDetailId).collect(Collectors.toList());
        return returnDetails;
    }

    /**
     * 统计状态数量 - 按时间段内每天汇总
     *
     * @param dataAnalysisDetailSumInDTO
     * @param isFill
     * @return
     */
    @Override
    public List<DataAnalysisStateSumOutDTO> countStateSumByDate(DataAnalysisDetailSumInDTO dataAnalysisDetailSumInDTO, boolean isFill) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        DataAnalysisStateSumOutDTO sumOutDTO = null;
        DataAnalysisDetailSumInPO dataAnalysisDetailSumInPO = DataAnalysisDetailSumInPO.builder()
                .startTime(String.format("%s %s", dataAnalysisDetailSumInDTO.getStartTime(), "00:00:00"))
                .endTime(String.format("%s %s", dataAnalysisDetailSumInDTO.getEndTime(), "23:59:59"))
                .missionRecordId(dataAnalysisDetailSumInDTO.getMissionRecordId())
                .orgCode(dataAnalysisDetailSumInDTO.getOrgId())
                .visibleOrgCode(orgCode)
                .build();
        //查询所有满足条件数据
        List<DataAnalysisCenterDetailEntity> detailEntities = this.dataAnalysisDetailMapper.queryByCondition(dataAnalysisDetailSumInPO);
        //按状态分别分组 - 按日期分组
        //【提交态】【待分析】
        List<DataAnalysisCenterDetailEntity> needAnalyzes = detailEntities.stream()
                .filter(x -> DataAnalysisPicStatusEnum.NEED_ANALYZE.getType() == x.getPhotoState()
                        && DataAnalysisPicPushStatusEnum.COMMIT.getType() == x.getPushState())
                .collect(Collectors.toList());
        Map<String, Integer> needAnalyzesMap = needAnalyzes.stream()
                .collect(Collectors.groupingBy(x -> x.getModifiedTime().toLocalDate().toString()
                        , HashMap::new
                        , Collectors.collectingAndThen(Collectors.toList(), y -> y.size())));

        //【提交态】【有问题】
        List<DataAnalysisCenterDetailEntity> commitProblems = detailEntities.stream()
                .filter(x -> DataAnalysisPicStatusEnum.PROBLEM.getType() == x.getPhotoState()
                        && DataAnalysisPicPushStatusEnum.COMMIT.getType() == x.getPushState())
                .collect(Collectors.toList());
        Map<String, Integer> commitProblemsMap = commitProblems.stream()
                .collect(Collectors.groupingBy(x -> x.getModifiedTime().toLocalDate().toString(), HashMap::new, Collectors.collectingAndThen(Collectors.toList(), y -> y.size())));

        //【提交态】【无问题】
        List<DataAnalysisCenterDetailEntity> commitNoProblems = detailEntities.stream()
                .filter(x -> DataAnalysisPicStatusEnum.NO_PROBLEM.getType() == x.getPhotoState()
                        && DataAnalysisPicPushStatusEnum.COMMIT.getType() == x.getPushState())
                .collect(Collectors.toList());
        Map<String, Integer> commitNoProblemsMap = commitNoProblems.stream()
                .collect(Collectors.groupingBy(x -> x.getModifiedTime().toLocalDate().toString(), HashMap::new, Collectors.collectingAndThen(Collectors.toList(), y -> y.size())));

        //【核实态】【有问题】
        List<DataAnalysisCenterDetailEntity> confirmProblems = detailEntities.stream()
                .filter(x -> DataAnalysisPicStatusEnum.PROBLEM.getType() == x.getPhotoState()
                        && DataAnalysisPicPushStatusEnum.VERIFY.getType() == x.getPushState())
                .collect(Collectors.toList());
        Map<String, Integer> confirmProblemsMap = confirmProblems.stream()
                .collect(Collectors.groupingBy(x -> x.getModifiedTime().toLocalDate().toString(), HashMap::new, Collectors.collectingAndThen(Collectors.toList(), y -> y.size())));

        //【核实态】【有问题】
        List<DataAnalysisCenterDetailEntity> confirmNoProblems = detailEntities.stream()
                .filter(x -> DataAnalysisPicStatusEnum.NO_PROBLEM.getType() == x.getPhotoState()
                        && DataAnalysisPicPushStatusEnum.VERIFY.getType() == x.getPushState())
                .collect(Collectors.toList());
        Map<String, Integer> confirmNoProblemsMap = confirmNoProblems.stream()
                .collect(Collectors.groupingBy(x -> x.getModifiedTime().toLocalDate().toString(), HashMap::new, Collectors.collectingAndThen(Collectors.toList(), y -> y.size())));

        LocalDate startDate = LocalDate.parse(dataAnalysisDetailSumInDTO.getStartTime()), endDate = LocalDate.parse(dataAnalysisDetailSumInDTO.getEndTime()).plusDays(1);

        List<DataAnalysisStateSumOutDTO> sumOutDTOList = new ArrayList<>();

        if (log.isDebugEnabled()) {
            log.debug("打印【countStateSumByDate】:{}-{}", startDate, endDate);
            log.debug("打印:【countStateSumByDate】:【待分析】-{} ,\r\n【待确认】【有问题】-{} ,\r\n【待确认】【无问题】-{},\r\n【有问题】-{},\r\n【无问题】-{}"
                    , needAnalyzesMap.toString()
                    , commitProblemsMap.toString()
                    , commitNoProblemsMap.toString()
                    , confirmProblemsMap.toString()
                    , confirmNoProblemsMap.toString());
        }

        Integer needAnalyzeSum = 0, needConfirmProblemSum = 0, needConfirmNoProblemSum = 0, problemSum = 0, noProblemSum = 0;

        //是否补充日期数据
        if (isFill) {
            String key = "";
            while (endDate.isAfter(startDate)) {
                key = startDate.toString();
                sumOutDTO = new DataAnalysisStateSumOutDTO();
                sumOutDTO.setPhotoId(CollectionUtil.isEmpty(detailEntities) ? 0 : detailEntities.get(0).getPhotoId())
                        .setCenterBaseId(CollectionUtil.isEmpty(detailEntities) ? 0 : detailEntities.get(0).getCenterBaseId())
                        .setCenterDetailId(CollectionUtil.isEmpty(detailEntities) ? 0 : detailEntities.get(0).getCenterDetailId())
                        .setSortDate(key);
                needAnalyzeSum = needAnalyzesMap.get(key) == null ? 0 : needAnalyzesMap.get(key);
                needConfirmProblemSum = commitProblemsMap.get(key) == null ? 0 : commitProblemsMap.get(key);
                needConfirmNoProblemSum = commitNoProblemsMap.get(key) == null ? 0 : commitNoProblemsMap.get(key);
                problemSum = confirmProblemsMap.get(key) == null ? 0 : confirmProblemsMap.get(key);
                noProblemSum = confirmNoProblemsMap.get(key) == null ? 0 : confirmNoProblemsMap.get(key);
                sumOutDTO.setHadAnalyzeSum(needConfirmProblemSum + needConfirmNoProblemSum + problemSum + noProblemSum)
                        .setHadFoundProblemSum(problemSum);
                sumOutDTOList.add(sumOutDTO);
                startDate = startDate.plusDays(1);
            }
        } else {
            //汇总所有状态存在的日期key
            Set<String> keySets = new HashSet<>();
            keySets.addAll(needAnalyzesMap.keySet());
            keySets.addAll(commitProblemsMap.keySet());
            keySets.addAll(commitNoProblemsMap.keySet());
            keySets.addAll(confirmProblemsMap.keySet());
            keySets.addAll(confirmNoProblemsMap.keySet());
            //循环遍历key，将各状态数据汇总到每天的DTO
            for (String key : keySets) {
                sumOutDTO = new DataAnalysisStateSumOutDTO();
                sumOutDTO.setPhotoId(CollectionUtil.isEmpty(detailEntities) ? 0 : detailEntities.get(0).getPhotoId())
                        .setCenterBaseId(CollectionUtil.isEmpty(detailEntities) ? 0 : detailEntities.get(0).getCenterBaseId())
                        .setCenterDetailId(CollectionUtil.isEmpty(detailEntities) ? 0 : detailEntities.get(0).getCenterDetailId())
                        .setSortDate(key);
                needAnalyzeSum = needAnalyzesMap.get(key) == null ? 0 : needAnalyzesMap.get(key);
                needConfirmProblemSum = commitProblemsMap.get(key) == null ? 0 : commitProblemsMap.get(key);
                needConfirmNoProblemSum = commitNoProblemsMap.get(key) == null ? 0 : commitNoProblemsMap.get(key);
                problemSum = confirmProblemsMap.get(key) == null ? 0 : confirmProblemsMap.get(key);
                noProblemSum = confirmNoProblemsMap.get(key) == null ? 0 : confirmNoProblemsMap.get(key);
                sumOutDTO.setHadAnalyzeSum(needConfirmProblemSum + needConfirmNoProblemSum + problemSum + noProblemSum)
                        .setHadFoundProblemSum(problemSum);
                sumOutDTOList.add(sumOutDTO);
                sumOutDTOList.add(sumOutDTO);
            }
        }
        return sumOutDTOList;
    }

    @Override
    public List<DataAnalysisHisResultDTO.HisResultDTO> hisResults(String missionId, String orgId, String topicProblemId, String beginTIme, String endTime) {
        DataAnalysisHisResultInDO condition = DataAnalysisHisResultInDO.builder().beginTIme(beginTIme)
                .endTime(endTime)
                .orgId(orgId)
                .topicProblemId(topicProblemId)
                .missionId(missionId).build();
        //查询历史同类数据 DataAnalysisHisResultRespVO.HisResultRespVO
        List<DataAnalysisResultGroupOutDO> hisResultRespDtos;
        hisResultRespDtos = dataAnalysisResultGroupMapper.queryHisResultByCondition(condition);

        List<DataAnalysisHisResultDTO.HisResultDTO> hisResultDtos = hisResultRespDtos.stream().map(item -> {
            DataAnalysisHisResultDTO.HisResultDTO vo = new DataAnalysisHisResultDTO.HisResultDTO();
            BeanUtil.copyProperties(item, vo);
            vo.setCreateTime(item.getLatestTime());
            return vo;
        }).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(hisResultDtos)) {
            return hisResultDtos;
        }
        //封装结果数据
        List<String> groupIds = hisResultDtos.stream().map(dto -> {
            return dto.getResultGroupId();
        }).collect(Collectors.toList());
        LambdaQueryWrapper<DataAnalysisResultEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisResultEntity.class);
        wrapper.in(DataAnalysisResultEntity::getResultGroupId, groupIds);
        wrapper.eq(DataAnalysisResultEntity::getDeleted, "0");
        wrapper.orderByDesc(DataAnalysisResultEntity::getCreatedTime);
        //批量查询结果
        List<DataAnalysisResultEntity> dataAnalysisResultEntities = dataAnalysisResultMapper.selectList(wrapper);
        Map<String, List<DataAnalysisResultEntity>> resultMap = dataAnalysisResultEntities.stream().collect(Collectors.groupingBy(DataAnalysisResultEntity::getResultGroupId));

        hisResultDtos = hisResultDtos.stream().map(item -> {
            String resultGroupId = item.getResultGroupId();
            List<DataAnalysisResultEntity> dataAnalysisResultEntity = resultMap.get(resultGroupId);
            List<DataAnalysisHisResultDTO.GroupicResultDTO> picsList = Lists.newArrayList();
            if (dataAnalysisResultEntity != null) {
                picsList = dataAnalysisResultEntity.stream().map(pojo -> {
                    DataAnalysisHisResultDTO.GroupicResultDTO pics = DataAnalysisHisResultDTO.GroupicResultDTO.builder().markId(pojo.getMarkId())
                            .thumImagePath(pojo.getThumImagePath())
                            .resultImgPath(pojo.getResultImagePath())
                            .resultId(pojo.getResultId()).build();
                    return pics;
                }).collect(Collectors.toList());
            }
            item.setGroupPics(picsList);
            return item;
        }).collect(Collectors.toList());
        return hisResultDtos;
    }

    @Override
    public Boolean mergeResult(DataAnalysisResultGroupReqVO.ResultMergeReqVO vo) {
        List<DataAnalysisMarkMergeEntity> analysisMarkMergeEntities = Lists.newArrayList();
        //检查对应的标注是否存在
        DataAnalysisMarkQueryCriteriaPO criteriaPO = DataAnalysisMarkQueryCriteriaPO.builder().markId(vo.getMarkId()).build();
        List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS = this.dataAnalysisMarkMapper.queryMarks(criteriaPO);
        if (dataAnalysisDetailMarkOutPOS == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_MERGER.getContent()));
        }
        //查询对应的分组所有的result记录
        LambdaQueryWrapper<DataAnalysisResultEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataAnalysisResultEntity::getResultGroupId, vo.getGroupId());
        wrapper.eq(DataAnalysisResultEntity::getDeleted, "0");
        List<DataAnalysisResultEntity> dataAnalysisResultEntities = this.dataAnalysisResultMapper.selectList(wrapper);
        List<Long> resultMarkIds = dataAnalysisResultEntities.stream().map(DataAnalysisResultEntity::getMarkId).collect(Collectors.toList());
        //查询对应分组在mark_merge的数据
        LambdaQueryWrapper<DataAnalysisMarkMergeEntity> markMergeWrapper = new LambdaQueryWrapper<>();
        markMergeWrapper.eq(DataAnalysisMarkMergeEntity::getResultGroupId, vo.getGroupId());
        markMergeWrapper.eq(DataAnalysisMarkMergeEntity::getDeleted, "0");
        List<DataAnalysisMarkMergeEntity> analysisMarkMergeEntities1 = dataAnalysisMarkMergeMapper.selectList(markMergeWrapper);
        //如果结果为空表示被核实的问题也是初次合并
        if (CollectionUtil.isEmpty(analysisMarkMergeEntities1)) {
            List<DataAnalysisMarkMergeEntity> listVo = resultMarkIds.stream().map(item -> {
                DataAnalysisResultGroupReqVO.ResultMergeReqVO ivo = new DataAnalysisResultGroupReqVO.ResultMergeReqVO();
                ivo.setMarkId(item);
                ivo.setGroupId(vo.getGroupId());
                DataAnalysisMarkMergeEntity convert = DataAnalysisMarkMergeConverter.INSTANCES.convert(ivo);
                convert.setCreatedTime(LocalDateTime.now());
                convert.setModifiedTime(LocalDateTime.now());
                convert.setDeleted(false);
                return convert;
            }).collect(Collectors.toList());
            analysisMarkMergeEntities.addAll(listVo);
        }
        DataAnalysisMarkMergeEntity convert = DataAnalysisMarkMergeConverter.INSTANCES.convert(vo);
        convert.setCreatedTime(LocalDateTime.now());
        convert.setModifiedTime(LocalDateTime.now());
        convert.setDeleted(false);
        analysisMarkMergeEntities.add(convert);
        int total = this.dataAnalysisMarkMergeMapper.batchInsert(analysisMarkMergeEntities);
        if (total == 0) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_MERGER.getContent()));
        }
        //合并后更新标注的参数
        DataAnalysisResultGroupEntity dataAnalysisResultGroupEntity = this.dataAnalysisResultGroupMapper.selectOne(Wrappers.<DataAnalysisResultGroupEntity>lambdaQuery()
                .eq(DataAnalysisResultGroupEntity::getResultGroupId, vo.getGroupId())
                .eq(DataAnalysisResultGroupEntity::getDeleted, "0"));
        if (dataAnalysisResultGroupEntity != null) {
            DataAnalysisMarkUpdateInPO inPO = DataAnalysisMarkUpdateInPO.builder().addr(dataAnalysisResultGroupEntity.getAddr())
                    .addrImagePath(dataAnalysisResultGroupEntity.getAddrImagePath())
                    .latitude(dataAnalysisResultGroupEntity.getLatitude())
                    .longitude(dataAnalysisResultGroupEntity.getLongitude())
                    .topicLevelId(dataAnalysisResultGroupEntity.getTopicLevelId())
                    .markId(String.valueOf(vo.getMarkId())).build();
            this.dataAnalysisMarkMapper.updateMarkByMarkId(inPO);
        }
        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public Boolean undoMergeResult(DataAnalysisResultGroupReqVO.ResultMergeReqVO vo) {
        //删除merge信息
        DataAnalysisMarkQueryCriteriaPO criteriaPO = DataAnalysisMarkQueryCriteriaPO.builder().markId(vo.getMarkId()).build();
        List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS = this.dataAnalysisMarkMapper.queryMarks(criteriaPO);
        if (dataAnalysisDetailMarkOutPOS == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_ISSUE_DOES_NOT_NEED_TO_BE_UNMERGED.getContent()));
        }
        String mergeGroup = this.dataAnalysisMarkMergeMapper.selectIsMergeGroup(vo.getMarkId());
        if (mergeGroup == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_ISSUE_DOES_NOT_NEED_TO_BE_UNMERGED.getContent()));
        }
        LambdaUpdateWrapper<DataAnalysisMarkMergeEntity> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(DataAnalysisMarkMergeEntity::getResultGroupId, vo.getGroupId());
        updateWrapper.eq(DataAnalysisMarkMergeEntity::getMarkId, vo.getMarkId());
        updateWrapper.eq(DataAnalysisMarkMergeEntity::getDeleted, 0);
        updateWrapper.set(DataAnalysisMarkMergeEntity::getDeleted, 1);
        updateWrapper.set(DataAnalysisMarkMergeEntity::getModifiedTime, LocalDateTime.now());
        updateWrapper.set(DataAnalysisMarkMergeEntity::getModifierId, TrustedAccessTracerHolder.get().getAccountId());
        int num = this.dataAnalysisMarkMergeMapper.update(null, updateWrapper);
        if (num == 0) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_ISSUE_DOES_NOT_NEED_TO_BE_UNMERGED.getContent()));
        }
        //更新markId数据
        LambdaUpdateWrapper<DataAnalysisMarkEntity> markUpdateInPO = Wrappers.lambdaUpdate();
        markUpdateInPO.eq(DataAnalysisMarkEntity::getMarkId, vo.getMarkId());
        markUpdateInPO.set(DataAnalysisMarkEntity::getAddrImagePath, "");
        markUpdateInPO.set(DataAnalysisMarkEntity::getAddr, "");
        markUpdateInPO.set(DataAnalysisMarkEntity::getLatitude, null);
        markUpdateInPO.set(DataAnalysisMarkEntity::getLongitude, null);
        markUpdateInPO.set(DataAnalysisMarkEntity::getModifiedTime, LocalDateTime.now());
        this.dataAnalysisMarkMapper.update(null, markUpdateInPO);
        return Boolean.TRUE;
    }

    /**
     * 查找下载统计数据
     *
     * @param detailIds
     * @param orgCode
     * @return
     */
    @Override
    public int selectNum(List<String> detailIds, String orgCode) {
        LambdaQueryWrapper queryWrapper = Wrappers.<DataAnalysisCenterDetailEntity>lambdaQuery()
                .in(DataAnalysisCenterDetailEntity::getCenterDetailId, detailIds)
                .likeRight(DataAnalysisCenterDetailEntity::getOrgCode, orgCode)
                .eq(DataAnalysisCenterDetailEntity::getDeleted, false);
        return this.dataAnalysisDetailMapper.selectCount(queryWrapper);
    }

    @Override
    public void resultBalance(String groupId) {
        //调用balance进行处理
        Collection<String> groupIds = CollectionUtil.newArrayList();
        groupIds.add(groupId);
        dataAnalysisResultService.balance(groupIds);
    }

    public void checkResultStatus(List<Long> detailIds) {
        //查询对应detailIds所有状态为核实的标注
        List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = this.dataAnalysisMarkMapper.selectList(Wrappers.<DataAnalysisMarkEntity>lambdaQuery()
                .in(DataAnalysisMarkEntity::getDetailId, detailIds)
                .eq(DataAnalysisMarkEntity::getDeleted, "0")
                .eq(DataAnalysisMarkEntity::getMarkState, DataAnalysisPicPushStatusEnum.VERIFY.getType()));
        //查询是否已核实的标注在结果表都有数据
        if (CollectionUtil.isNotEmpty(dataAnalysisMarkEntities)) {
            List<Long> markIds = dataAnalysisMarkEntities.stream().map(DataAnalysisMarkEntity::getMarkId).collect(Collectors.toList());
            List<DataAnalysisResultEntity> dataAnalysisResultEntities = dataAnalysisResultMapper.selectList(Wrappers.<DataAnalysisResultEntity>lambdaQuery()
                    .in(DataAnalysisResultEntity::getMarkId, markIds)
                    .eq(DataAnalysisResultEntity::getDeleted, "0"));
            //如果当前图片内的标注核实时间在十分钟之前，则允许撤回
            for (int i = 0; i < dataAnalysisMarkEntities.size(); i++) {
                LocalDateTime localDateTime = dataAnalysisMarkEntities.get(i).getModifiedTime().plusMinutes(10);
                if (LocalDateTime.now().isBefore(localDateTime) && markIds.size() != dataAnalysisResultEntities.size()) {
                    throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_IN_THE_PROCESS_OF_VERIFICATION_PLEASE_WITHDRAW_RESET_OPERATION_LATER.getContent()));
                }
            }
            //如果已核实结果数和应核实记录数不一致，则不允许撤回
            /*if (markIds.size() != dataAnalysisResultEntities.size()) {
                throw new BusinessException("正在核实中,请稍后再进行撤回/重置操作");
            }*/
        }
        // 校验AI分析进度状态
        checkAIAnalysisStatus(detailIds);
    }

    private void checkAIAnalysisStatus(List<Long> detailIds) {
        List<AIAnalysisPhotoEntity> photoEntities = aiAnalysisPhotoManager.fetchAnalysisPhotos(detailIds, AIAnalysisPhotoStateEnum.EXECUTING.getType(), AIAnalysisPhotoStateEnum.PAUSED.getType());
        if (!CollectionUtils.isEmpty(photoEntities)) {
            throw new BizParameterException("当前所选图片存在AI分析进行中数据，不允许进行该操作");
        }
    }

    /**
     * 获取照片海拔高度
     */
    private void getPhotoAltitude(List<DataAnalysisDetailOutDTO> results) {
        if (!CollectionUtils.isEmpty(results)) {
            List<Long> photoIds = results.stream().map(DataAnalysisDetailOutDTO::getPhotoId).collect(Collectors.toList());
            List<MissionPhotoEntity> photoEntityList = missionPhotoMapper.queryPhotos(photoIds);
            if (!CollectionUtils.isEmpty(photoEntityList)) {
                Map<Long, MissionPhotoEntity> photoMap = photoEntityList.stream().collect(Collectors.toMap(MissionPhotoEntity::getId, r -> r));
                for (DataAnalysisDetailOutDTO result : results) {
                    MissionPhotoEntity entity = photoMap.get(result.getPhotoId());
                    if (!ObjectUtils.isEmpty(entity)) {
                        result.setAltitude(entity.getAltitude());
                        result.setLenType(entity.getLenType());
                    }
                }
            }
        }
    }
}
