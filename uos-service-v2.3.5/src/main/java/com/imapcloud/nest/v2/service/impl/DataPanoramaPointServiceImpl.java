package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.NestConstant;
import com.imapcloud.nest.mapper.MissionRecordsMapper;
import com.imapcloud.nest.v2.common.enums.DataPanoramaPointTypeEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.DataPanoramaPointEntity;
import com.imapcloud.nest.v2.dao.mapper.DataPanoramaPointMapper;
import com.imapcloud.nest.v2.dao.po.in.DataPanoramaPointCriteriaPO;
import com.imapcloud.nest.v2.service.DataPanoramaDetailService;
import com.imapcloud.nest.v2.service.DataPanoramaPointService;
import com.imapcloud.nest.v2.service.converter.DataPanoramaPointConverter;
import com.imapcloud.nest.v2.service.dto.in.DataPanoramaPointInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaDetailOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaPointOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaPointServiceImpl.java
 * @Description DataPanoramaPointServiceImpl
 * @createTime 2022年09月27日 16:35:00
 */
@Slf4j
@Service
public class DataPanoramaPointServiceImpl implements DataPanoramaPointService {

    @Resource
    private DataPanoramaPointMapper dataPanoramaPointMapper;

    @Resource
    private DataPanoramaDetailService dataPanoramaDetailService;

    @Resource
    private MissionRecordsMapper missionRecordsMapper;

    /**
     * 新增全景点
     *
     * @param addPointInDTO
     * @return
     */
    @Override
    public Boolean addPoint(DataPanoramaPointInDTO.AddPointInDTO addPointInDTO) {
        log.info("addPoint: addPointInDTO -> {} ", addPointInDTO);
        //校验
        if(addPointInDTO==null){
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_SAVING_FULL_POINT__FULL_POINT_DATA_CANNOT_BE_EMPTY.getContent()));
        }
        fillData(addPointInDTO);
        validate(addPointInDTO);
        DataPanoramaPointEntity dataPanoramaPointEntity = DataPanoramaPointConverter.INSTANCES.convert(addPointInDTO);
        //插入
        int count = this.dataPanoramaPointMapper.saveBatch(CollectionUtil.newArrayList(dataPanoramaPointEntity));
        if(log.isDebugEnabled()){
            log.debug("addPoint: 成功插入 {} 条 ",count);
        }
        return Boolean.TRUE;
    }

    /**
     * 校验
     * @return
     */
    public void validate(DataPanoramaPointInDTO.AddPointInDTO addPointInDTO){
        //手动创建
        if(addPointInDTO.getPointType() == 1){
            if(addPointInDTO.getTaskId() == null || addPointInDTO.getAirPointId() == null){
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANT_SELECT_ONLY_ONE_WAYPOINT_FOR_THE_ROUTE.getContent()));
            }
        }
    }

    /**
     *填充字段
     * @param addPointInDTO
     */
    public void fillData(DataPanoramaPointInDTO.AddPointInDTO addPointInDTO){
       if(addPointInDTO.getPointType()==null){
           addPointInDTO.setPointType(DataPanoramaPointTypeEnum.MANUAL_TYPE.getType());
       }
       if(StringUtils.hasText(addPointInDTO.getTaskId()) && StringUtils.isEmpty(addPointInDTO.getAirLineId())){
           String airLineId = this.missionRecordsMapper.getAirLineId("",addPointInDTO.getTaskId());
           addPointInDTO.setAirLineId(airLineId);
       }
       if(StringUtils.isEmpty(addPointInDTO.getOrgCode())){
           addPointInDTO.setOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
       }

    }

    /**
     * 删除全景点
     *
     * @param pointIds
     * @return
     */
    @Override
    public Boolean deletePoints(List<String> pointIds) {
        if(log.isDebugEnabled()){
            log.debug("deletePoints:pointIds -> {}", pointIds);
        }
        if(CollectionUtil.isEmpty(pointIds)){
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_DATA_TO_BE_DELETED_IS_EMPTY_PLEASE_CHECK.getContent()));
        }
        LambdaUpdateWrapper updateWrapper = Wrappers.lambdaUpdate(DataPanoramaPointEntity.class)
                .set(DataPanoramaPointEntity::getDeleted, NestConstant.DeleteType.DELETED)
                .in(DataPanoramaPointEntity::getPointId,pointIds);
        int count = this.dataPanoramaPointMapper.delete(updateWrapper);
        if(log.isDebugEnabled()){
            log.debug("deletePoints: 成功删除 {} 条 ",count);
        }
        return Boolean.TRUE;
    }

    /**
     * 更新全景点
     *
     * @param updatePointInDTO
     * @return
     */
    @Override
    public Boolean updatePoint(DataPanoramaPointInDTO.UpdatePointInDTO updatePointInDTO) {
        log.info("updatePoint: updatePointInDTO -> {} ", updatePointInDTO);
        if(updatePointInDTO==null){
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_DATA_TO_BE_UPDATED_IS_EMPTY_PLEASE_CHECK.getContent()));
        }
        LambdaUpdateWrapper updateWrapper = Wrappers.lambdaUpdate(DataPanoramaPointEntity.class)
                .eq(DataPanoramaPointEntity::getPointId,updatePointInDTO.getPointId());
        DataPanoramaPointEntity panoramaPointEntity = DataPanoramaPointConverter.INSTANCES.convert(updatePointInDTO);
        int count = this.dataPanoramaPointMapper.update(panoramaPointEntity,updateWrapper);
        if(log.isDebugEnabled()){
            log.debug("updatePoint: 成功更新 {} 条 ",count);
        }
        return Boolean.TRUE;
    }

    /**
     * 查询分页
     *
     * @param queryPageInDTO
     * @return
     */
    @Override
    public PageResultInfo<DataPanoramaPointOutDTO.QueryPageOutDTO> queryPointPage(DataPanoramaPointInDTO.QueryPageInDTO queryPageInDTO) {
        List<DataPanoramaPointOutDTO.QueryPageOutDTO> results = new ArrayList<>();
        long total = 0L;
        //获取该用户拥有的单位全
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        String startTime = ObjectUtil.isEmpty(queryPageInDTO.getStartTime())?"":LocalDateTime.of(queryPageInDTO.getStartTime()
                , LocalTime.of(00, 00, 00)).toString(),
                endTime = ObjectUtil.isEmpty(queryPageInDTO.getEndTime())?"":LocalDateTime.of(queryPageInDTO.getEndTime()
                        , LocalTime.of(23, 59, 59)).toString();
        //拼装数据
        DataPanoramaPointCriteriaPO po = DataPanoramaPointCriteriaPO.builder()
                .startTime(startTime)
                .endTime(endTime)
                .visibleOrgCode(orgCode)
                .airLineId(queryPageInDTO.getAirLineId())
                .airPointId(queryPageInDTO.getAirPointId())
                .tagId(queryPageInDTO.getTagId())
                .pointName(queryPageInDTO.getPointName())
                .pointType(queryPageInDTO.getPointType())
                .orgCode(queryPageInDTO.getOrgCode())
                .build();
        //查询条件下总数
        total = this.dataPanoramaPointMapper.countByCondition(po);
        if(total > 0){
            //分页查询基础数据
            List<DataPanoramaPointEntity> outPOList = this.dataPanoramaPointMapper.selectByCondition(po, PagingRestrictDo.getPagingRestrict(queryPageInDTO));
            //获取pointId跟detailUrl的对应Map
            List<String> pointIdList = outPOList.stream().map(DataPanoramaPointEntity::getPointId).collect(Collectors.toList());
            Map<String, DataPanoramaDetailOutDTO.QueryOutDTO> pointIdToURLMap = this.dataPanoramaDetailService.getPointToURL(pointIdList,queryPageInDTO.getMissionId(),queryPageInDTO.getMissionRecordsId());
            results = outPOList.stream()
                    .map(DataPanoramaPointConverter.INSTANCES::convertPage)
                    .map(x->{
                        x.setDetailUrl(pointIdToURLMap.get(x.getPointId())==null?"":pointIdToURLMap.get(x.getPointId()).getDetailUrl());
                        x.setAcquisitionTime(pointIdToURLMap.get(x.getPointId())==null?null:pointIdToURLMap.get(x.getPointId()).getAcquisitionTime());
                        return x;
                    })
                    .collect(Collectors.toList());
        }
        return PageResultInfo.of(total, results);
    }

    /**
     * 查询全量
     *
     * @param queryInDTO
     * @return
     */
    @Override
    public List<DataPanoramaPointOutDTO.QueryLessOutDTO> queryAllPoints(DataPanoramaPointInDTO.QueryInDTO queryInDTO) {
        List<DataPanoramaPointOutDTO.QueryLessOutDTO> queryLessOutDTOList = new ArrayList<>();
        //获取该用户拥有的单位全
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        String startTime = ObjectUtil.isEmpty(queryInDTO.getStartTime())?"":LocalDateTime.of(queryInDTO.getStartTime()
                , LocalTime.of(00, 00, 00)).toString(),
                endTime = ObjectUtil.isEmpty(queryInDTO.getEndTime())?"":LocalDateTime.of(queryInDTO.getEndTime()
                        , LocalTime.of(23, 59, 59)).toString();
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataPanoramaPointEntity.class)
                .eq(StringUtils.hasText(queryInDTO.getAirLineId()),DataPanoramaPointEntity::getAirLineId,queryInDTO.getAirLineId())
                .eq(StringUtils.hasText(queryInDTO.getAirPointId()),DataPanoramaPointEntity::getAirPointId,queryInDTO.getAirPointId())
                .eq(StringUtils.hasText(queryInDTO.getPointName()),DataPanoramaPointEntity::getPointName,queryInDTO.getPointName())
                .eq(StringUtils.hasText(queryInDTO.getTagId()),DataPanoramaPointEntity::getTagId,queryInDTO.getTagId())
                .eq(ObjectUtil.isNotNull(queryInDTO.getPointType()),DataPanoramaPointEntity::getPointType,queryInDTO.getPointType())
                .gt(StringUtils.hasText(startTime),DataPanoramaPointEntity::getCreatedTime,startTime)
                .lt(StringUtils.hasText(endTime),DataPanoramaPointEntity::getCreatedTime,endTime)
                .eq(ObjectUtil.isNotNull(queryInDTO.getOrgCode()),DataPanoramaPointEntity::getOrgCode,queryInDTO.getOrgCode())
                .likeRight(DataPanoramaPointEntity::getOrgCode,orgCode)
                .orderByDesc();
        List<DataPanoramaPointEntity> dataPanoramaPointEntities = this.dataPanoramaPointMapper.selectList(queryWrapper);
        if(CollectionUtil.isEmpty(dataPanoramaPointEntities)){
            return queryLessOutDTOList;
        }
        //获取pointId跟detailUrl的对应Map
        List<String> pointIdList = dataPanoramaPointEntities.stream().map(DataPanoramaPointEntity::getPointId).collect(Collectors.toList());
        Map<String,DataPanoramaDetailOutDTO.QueryOutDTO> pointIdToURLMap = this.dataPanoramaDetailService.getPointToURL(pointIdList,queryInDTO.getMissionId(),queryInDTO.getMissionRecordsId());
        queryLessOutDTOList = dataPanoramaPointEntities.stream()
                .map(DataPanoramaPointConverter.INSTANCES::convertLess)
                .map(x->{
                    x.setDetailUrl(pointIdToURLMap.get(x.getPointId())==null?"":pointIdToURLMap.get(x.getPointId()).getDetailUrl());
                    return x;
                })
                .collect(Collectors.toList());
        return queryLessOutDTOList;
    }

    /**
     * 查询单条
     * @param queryOneInDTO
     * @return
     */
    @Override
    public List<DataPanoramaPointOutDTO.QueryOutDTO> queryPoint(DataPanoramaPointInDTO.QueryOneInDTO queryOneInDTO) {
        List<DataPanoramaPointOutDTO.QueryOutDTO> queryOutDTOList = new ArrayList<>();
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataPanoramaPointEntity.class)
                .eq(StringUtils.hasText(queryOneInDTO.getPointId()),DataPanoramaPointEntity::getPointId,queryOneInDTO.getPointId())
                .orderByDesc();
        List<DataPanoramaPointEntity> dataPanoramaPointEntities = this.dataPanoramaPointMapper.selectList(queryWrapper);
        if(CollectionUtil.isEmpty(dataPanoramaPointEntities)){
            return queryOutDTOList;
        }
        //获取pointId跟detailUrl的对应Map
        List<String> pointIdList = dataPanoramaPointEntities.stream().map(DataPanoramaPointEntity::getPointId).collect(Collectors.toList());
        Map<String,DataPanoramaDetailOutDTO.QueryOutDTO> pointIdToURLMap = this.dataPanoramaDetailService.getPointToURL(pointIdList,queryOneInDTO.getMissionId(),queryOneInDTO.getMissionRecordsId());
        queryOutDTOList = dataPanoramaPointEntities.stream()
                .map(DataPanoramaPointConverter.INSTANCES::convert)
                .map(x->{
                    x.setDetailUrl(pointIdToURLMap.get(x.getPointId())==null?"":pointIdToURLMap.get(x.getPointId()).getDetailUrl());
                    return x;
                })
                .collect(Collectors.toList());
        return queryOutDTOList;
    }
}
