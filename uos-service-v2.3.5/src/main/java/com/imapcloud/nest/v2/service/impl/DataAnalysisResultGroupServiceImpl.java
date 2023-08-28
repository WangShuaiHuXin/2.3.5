package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterDetailEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkMergeEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultGroupEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMergeMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisResultGroupMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisResultMapper;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisResultGroupInPO;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisTraceSpacetimeInPO;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisResultOutPO;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisTraceSpacetimeOutPO;
import com.imapcloud.nest.v2.service.DataAnalysisResultGroupService;
import com.imapcloud.nest.v2.service.converter.DataAnalysisResultGroupConverter;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkDrawInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisResultGroupPageInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisResultImageInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisTraceSpacetimeInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisResultGroupOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisTraceSpacetimeOutDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 问题组业务实现
 *
 * @author boluo
 * @date 2022-10-11
 */
@Service
public class DataAnalysisResultGroupServiceImpl implements DataAnalysisResultGroupService {

    @Resource
    DataAnalysisResultGroupMapper dataAnalysisResultGroupMapper;

    @Resource
    DataAnalysisMarkMergeMapper dataAnalysisMarkMergeMapper;

    @Resource
    DataAnalysisResultGroupConverter dataAnalysisResultGroupConverter;

    @Resource
    private DataAnalysisResultMapper dataAnalysisResultMapper;

    private static final Integer Fail = 0;

    private static final Integer MAX_PHOTONUMS = 200;

    private Map<String, DataAnalysisResultOutPO.GroupInfoOutPO> queryResult(List<String> groupIdList) {
        if (CollUtil.isEmpty(groupIdList)) {
            return Collections.emptyMap();
        }
        List<DataAnalysisResultOutPO.GroupInfoOutPO> groupInfoOutPOList = dataAnalysisResultMapper.selectByResultGroupId(groupIdList);
        if (CollUtil.isEmpty(groupInfoOutPOList)) {
            return Collections.emptyMap();
        }
        return groupInfoOutPOList.stream().collect(Collectors.toMap(DataAnalysisResultOutPO.GroupInfoOutPO::getResultGroupId, bean -> bean, (key1, key2) -> key1));
    }

    @Override
    public PageResultInfo<DataAnalysisResultGroupOutDTO.ResultGroupOutDTO> pageResultGroupList(DataAnalysisResultGroupPageInDTO dto) {
        DataAnalysisResultGroupInPO po = buildDataAnalysisResultGroupCriteria(dto);
        long total = dataAnalysisResultGroupMapper.countByCondition(po);
        if (total == 0) {
            return PageResultInfo.empty();
        }
        List<DataAnalysisResultGroupEntity> rows = dataAnalysisResultGroupMapper.selectByCondition(po, PagingRestrictDo.getPagingRestrict(dto));

        List<String> groupIdList = rows.stream().map(DataAnalysisResultGroupEntity::getResultGroupId).collect(Collectors.toList());
        Map<String, DataAnalysisResultOutPO.GroupInfoOutPO> stringGroupInfoOutPOMap = queryResult(groupIdList);

        List<DataAnalysisResultGroupOutDTO.ResultGroupOutDTO> result = Lists.newLinkedList();
        for (DataAnalysisResultGroupEntity row : rows) {

            DataAnalysisResultGroupOutDTO.ResultGroupOutDTO resultGroupOutDTO = new DataAnalysisResultGroupOutDTO.ResultGroupOutDTO();
            BeanUtils.copyProperties(row, resultGroupOutDTO);

            DataAnalysisResultOutPO.GroupInfoOutPO groupInfoOutPO = stringGroupInfoOutPOMap.get(row.getResultGroupId());
            if (groupInfoOutPO != null) {
                resultGroupOutDTO.setFoundNums(groupInfoOutPO.getNum());
                resultGroupOutDTO.setThumImagePath(groupInfoOutPO.getThumImagePath());
            }
            result.add(resultGroupOutDTO);
        }
        return PageResultInfo.of(total, result);
    }

    @Override
    public List<DataAnalysisResultGroupOutDTO.ResultGroupOutDTO> listResultGroupList(DataAnalysisResultGroupPageInDTO dto) {
        DataAnalysisResultGroupInPO po = buildDataAnalysisResultGroupCriteria(dto);
        List<DataAnalysisResultGroupEntity> entityList = dataAnalysisResultGroupMapper.selectAllByCondition(po);
        if (CollUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        List<String> stringList = entityList.stream().map(DataAnalysisResultGroupEntity::getResultGroupId).collect(Collectors.toList());
        Map<String, DataAnalysisResultOutPO.GroupInfoOutPO> stringGroupInfoOutPOMap = queryResult(stringList);

        List<DataAnalysisResultGroupOutDTO.ResultGroupOutDTO> resultGroupOutDTOList = Lists.newLinkedList();

        for (DataAnalysisResultGroupEntity entity : entityList) {
            DataAnalysisResultGroupOutDTO.ResultGroupOutDTO groupOutDTO = new DataAnalysisResultGroupOutDTO.ResultGroupOutDTO();
            BeanUtils.copyProperties(entity, groupOutDTO);
            DataAnalysisResultOutPO.GroupInfoOutPO groupInfoOutPO = stringGroupInfoOutPOMap.get(entity.getResultGroupId());
            if (groupInfoOutPO != null) {
                groupOutDTO.setThumImagePath(groupInfoOutPO.getThumImagePath());
            }
            resultGroupOutDTOList.add(groupOutDTO);
        }
        return resultGroupOutDTOList;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteResultGroup(List<String> problemResultList) {
        boolean res = true;
        LambdaQueryWrapper<DataAnalysisMarkMergeEntity> conMarkMerge = Wrappers.lambdaQuery(DataAnalysisMarkMergeEntity.class)
                .in(DataAnalysisMarkMergeEntity::getResultGroupId, problemResultList);
        List<DataAnalysisMarkMergeEntity> markMergeEntities = dataAnalysisMarkMergeMapper.selectList(conMarkMerge);
        if (!CollectionUtils.isEmpty(markMergeEntities)) {
            List<Long> markMergeIds = markMergeEntities.stream().map(DataAnalysisMarkMergeEntity::getId).collect(Collectors.toList());
            conMarkMerge = Wrappers.lambdaQuery(DataAnalysisMarkMergeEntity.class).in(DataAnalysisMarkMergeEntity::getId, markMergeIds);
            int resMarkMerge = dataAnalysisMarkMergeMapper.delete(conMarkMerge);
            if (resMarkMerge == 0) res = false;
        }

        LambdaQueryWrapper<DataAnalysisResultGroupEntity> conResultGroup = Wrappers.lambdaQuery(DataAnalysisResultGroupEntity.class)
                .in(DataAnalysisResultGroupEntity::getResultGroupId, problemResultList);
        List<DataAnalysisResultGroupEntity> resultGroupEntities = dataAnalysisResultGroupMapper.selectList(conResultGroup);
        if (!CollectionUtils.isEmpty(resultGroupEntities)) {
            List<Long> resultGroupIds = resultGroupEntities.stream().map(DataAnalysisResultGroupEntity::getId).collect(Collectors.toList());
            conResultGroup = Wrappers.lambdaQuery(DataAnalysisResultGroupEntity.class).in(DataAnalysisResultGroupEntity::getId, resultGroupIds);
            int resResultGroup = dataAnalysisResultGroupMapper.delete(conResultGroup);
            if (resResultGroup == 0) res = false;
        }

        return res;
    }

    @Override
    public DataAnalysisResultGroupOutDTO.ResultGroupExportResultOutDTO resultGroupExportList(List<String> problemResultList) {

        List<DataAnalysisResultGroupEntity> resultGroupEntities = dataAnalysisResultGroupMapper.selectExportList(problemResultList);
        List<DataAnalysisResultGroupOutDTO.ResultGroupExportOutDTO> resultGroupList = resultGroupEntities.stream()
                .map(r -> dataAnalysisResultGroupConverter.convertExport(r))
                .collect(Collectors.toList());
        int maxImageNum = 0;
        for (DataAnalysisResultGroupOutDTO.ResultGroupExportOutDTO dto : resultGroupList) {
            // 拼接经纬度
            String lngAndLat = String.format("%s,%s", dto.getLongitude(), dto.getLatitude());
            // 获取发现问题数
            Integer foundNums = dataAnalysisResultGroupMapper.countFoundNums(dto.getResultGroupId());
            // 如果地址为空位置链接列为空
            String urlAddr;
            if (StringUtils.isEmpty(dto.getAddr())) {
                urlAddr = "";
            } else {
                urlAddr = mergeUrl(dto.getAddr(), dto.getLongitude(), dto.getLatitude());
            }
            // 获取问题结果照片
            List<String> imageUrlList = getResultGroupIds(dto.getResultGroupId());

            dto.setFoundNums(foundNums);
            dto.setLngAndLat(lngAndLat);
            dto.setUrlAddr(urlAddr);
            dto.setResultImages(imageUrlList);
            maxImageNum = Math.max(maxImageNum, imageUrlList.size());


        }

        DataAnalysisResultGroupOutDTO.ResultGroupExportResultOutDTO result = new DataAnalysisResultGroupOutDTO.ResultGroupExportResultOutDTO();
        result.setResultList(resultGroupList);
        result.setMaxImageNum(maxImageNum);
        return result;
    }

    @Override
    public Map<String, List<DataAnalysisTraceSpacetimeOutDTO>> traceSpacetime(DataAnalysisTraceSpacetimeInDTO inDTO) {

        DataAnalysisTraceSpacetimeInPO po = buildTracrSpacetimeCriteria(inDTO);
        // 获取符合要求的全量问题照片
        List<DataAnalysisCenterDetailEntity> sameMissonPhotoList = dataAnalysisResultGroupMapper.getSameMissonPhoto(po);
        // 以架次为纬度分类
        Map<Long, List<DataAnalysisCenterDetailEntity>> missonRecodeIdMap = new HashMap<>();
        List<DataAnalysisCenterDetailEntity> list;
        for (DataAnalysisCenterDetailEntity photo : sameMissonPhotoList) {
            Long missionRecordId = photo.getMissionRecordId();
            if (missonRecodeIdMap.containsKey(missionRecordId)) {
                list = missonRecodeIdMap.get(missionRecordId);
                list.add(photo);
                missonRecodeIdMap.put(missionRecordId, list);
            } else {
                list = new ArrayList<>();
                list.add(photo);
                missonRecodeIdMap.put(missionRecordId, list);
            }
        }
        List<Long> problemPhotos = new ArrayList<>();
        List<Long> noProblemPhotos = new ArrayList<>();
        List<Long> photoIdFromResultGroups = new ArrayList<>();


        // 优先获取问题组的问题照片
        List<DataAnalysisTraceSpacetimeOutPO> mapsFromResultGroup = dataAnalysisResultGroupMapper.selectPhotoByResultGroupId(po);
        List<DataAnalysisResultImageInDTO> listFromResultGroup = mapsFromResultGroup.stream().map(r -> {
            DataAnalysisResultImageInDTO dto = dataAnalysisResultGroupConverter.convertResultImage(r);
            problemPhotos.add(dto.getPhotoId());
            missonRecodeIdMap.remove(dto.getMissionRecordsId());
            photoIdFromResultGroups.add(dto.getPhotoId());
            return dto;
        }).collect(Collectors.toList());


        // 其次获取同类问题的问题照片
        Set<Long> missionRecodeSet = missonRecodeIdMap.keySet();
        List<DataAnalysisResultImageInDTO> listFromTopicProblem = new ArrayList<>();
        if (!CollectionUtils.isEmpty(missionRecodeSet)) {
            po.setMissionRecodeIds(missionRecodeSet);
            List<DataAnalysisTraceSpacetimeOutPO> mapsFromTopicProblem = dataAnalysisResultGroupMapper.selectPhotoByTopicProblemId(po);
            listFromTopicProblem = mapsFromTopicProblem.stream().map(r -> {
                DataAnalysisResultImageInDTO dto = dataAnalysisResultGroupConverter.convertResultImage(r);
                problemPhotos.add(dto.getPhotoId());
                missonRecodeIdMap.remove(dto.getMissionRecordsId());
                return dto;
            }).collect(Collectors.toList());
        }


        // 处理不存在同类问题的问题截图
        handleMissionResult(missonRecodeIdMap, noProblemPhotos);

        // 照片去重
        List<Long> problemPhotosDistinct = problemPhotos.stream().distinct().collect(Collectors.toList());
        List<Long> noProblemPhotosPhotosDistinct = noProblemPhotos.stream().distinct().collect(Collectors.toList());


        // 得到有问题照片dto
        List<DataAnalysisTraceSpacetimeOutDTO> allPhotos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(problemPhotosDistinct)) {
            List<DataAnalysisCenterDetailEntity> entitiesForProblem = dataAnalysisResultGroupMapper.selectAllPhoto(problemPhotosDistinct);
            List<DataAnalysisResultImageInDTO> finalListFromTopicProblem = listFromTopicProblem;
            List<DataAnalysisTraceSpacetimeOutDTO> collectForProblem = entitiesForProblem.stream().map(r -> {
                DataAnalysisTraceSpacetimeOutDTO dto = dataAnalysisResultGroupConverter.convertSpacetime(r);
                dto.setIsProblem(1);
                dto.setIsResultGroup(0);
                Long photoId = dto.getPhotoId();
                if (photoIdFromResultGroups.contains(photoId)) {
                    dto.setIsResultGroup(1);
                }
                // 标记照片替换原照片
                DataAnalysisResultImageInDTO imageInDTO = null;
                for (DataAnalysisResultImageInDTO tempImageInDTO : listFromResultGroup) {
                    if (Objects.equals(tempImageInDTO.getPhotoId(), photoId)) imageInDTO = tempImageInDTO;
                }
                if (imageInDTO == null) {
                    for (DataAnalysisResultImageInDTO tempImageInDTO : finalListFromTopicProblem) {
                        if (Objects.equals(tempImageInDTO.getPhotoId(), photoId)) imageInDTO = tempImageInDTO;
                    }
                }
                if (imageInDTO != null && !StringUtils.isEmpty(imageInDTO.getThumImagePath()) && !StringUtils.isEmpty(imageInDTO.getResultImagePath())) {
                    dto.setImagePath(imageInDTO.getResultImagePath());
                    dto.setThumImagePath(imageInDTO.getThumImagePath());
                }
                return dto;
            }).collect(Collectors.toList());
            allPhotos.addAll(collectForProblem);
        }

        // 得到无问题照片dto
        if (!CollectionUtils.isEmpty(noProblemPhotosPhotosDistinct)) {
            List<DataAnalysisCenterDetailEntity> entitiesForNoProblem = dataAnalysisResultGroupMapper.selectAllPhoto(noProblemPhotosPhotosDistinct);
            List<DataAnalysisTraceSpacetimeOutDTO> collectForNoProblem = entitiesForNoProblem.stream().map(r -> {
                DataAnalysisTraceSpacetimeOutDTO dto = dataAnalysisResultGroupConverter.convertSpacetime(r);
                dto.setIsProblem(0);
                dto.setIsResultGroup(0);
                return dto;
            }).collect(Collectors.toList());
            allPhotos.addAll(collectForNoProblem);
        }


        // K：时间——年月，V：架次照片
        //   Map<Integer, List<DataAnalysisTraceSpacetimeOutDTO>> resMap = new HashMap<>();

        Map<String, List<DataAnalysisTraceSpacetimeOutDTO>> resMap = buildSpacetimeMap(allPhotos);

        // sortByDate(resMap);

        return resMap;
    }

    @Override
    public int selectNum(List<String> list, String orgCode) {
        LambdaQueryWrapper wrapper = Wrappers.<DataAnalysisResultGroupEntity>lambdaQuery()
                .eq(DataAnalysisResultGroupEntity::getDeleted, false)
                .in(DataAnalysisResultGroupEntity::getResultGroupId,list )
                .likeRight(DataAnalysisResultGroupEntity::getOrgCode, orgCode);
        Integer integer = dataAnalysisResultGroupMapper.selectCount(wrapper);
        return integer;

    }

    /**
     * key和value按年月日排序(注：json对象在键名是数字的情况下，会被自动排序)
     */
    private void sortByDate(Map<Integer, List<DataAnalysisTraceSpacetimeOutDTO>> resMap) {
//        Arrays.sort(resMap.keySet().toArray(), Collections.reverseOrder());
//        for (Integer key : resMap.keySet()) {
//            List<DataAnalysisTraceSpacetimeOutDTO> dataList = resMap.get(key);
//            dataList.sort(Comparator.comparing(DataAnalysisTraceSpacetimeOutDTO::getPhotoCreateTime).reversed());
//            resMap.put(key, dataList);
//        }
//        Map<Long,List<DataAnalysisMarkDrawInDTO>> detailToDrawMap = resMap.stream()
//                .collect(Collectors.groupingBy(DataAnalysisMarkDrawInDTO::getDetailId));
    }

    /**
     * 构建结果Map
     */
    private Map<String, List<DataAnalysisTraceSpacetimeOutDTO>> buildSpacetimeMap(List<DataAnalysisTraceSpacetimeOutDTO> list) {
        // 如果最大照片数大于200张，取前200张的照片
        list.sort(Comparator.comparing(DataAnalysisTraceSpacetimeOutDTO::getPhotoCreateTime).reversed());
        if (list.size() > MAX_PHOTONUMS) {
            list = list.subList(0, MAX_PHOTONUMS);
        }

        for (DataAnalysisTraceSpacetimeOutDTO dto : list) {
            String yearMonth = converteLocalDateTime(dto.getPhotoCreateTime());
            dto.setYearMonth(yearMonth);
        }
        return list.stream().collect(Collectors.groupingBy(DataAnalysisTraceSpacetimeOutDTO::getYearMonth));

    }


    /**
     * 获取剩余问题照片
     */
    private void handleMissionResult(Map<Long, List<DataAnalysisCenterDetailEntity>> missonRecodeIdMap, List<Long> noProblemPhotos) {
        for (Long missionId : missonRecodeIdMap.keySet()) {
            List<DataAnalysisCenterDetailEntity> entities = missonRecodeIdMap.get(missionId);
            entities.sort((o1, o2) -> o2.getPhotoCreateTime().compareTo(o1.getPhotoCreateTime()));
            if (!CollectionUtils.isEmpty(entities)) {
                // 返回第一张照片
                noProblemPhotos.add(entities.get(0).getPhotoId());
            }
        }
    }

    /**
     * 拼接腾讯地图链接地址（腾讯地图有可能以后用不了）
     */
    private String mergeUrl(String addr, Double longitude, Double latitude) {
        return "https://apis.map.qq.com/uri/v1/marker?marker=coord:" +
                latitude +
                "," +
                longitude +
                ";title:" +
                addr +
                ";&referer=myapp";
    }

    /**
     * 获取问题结果照片
     */
    private List<String> getResultGroupIds(String resultGroupId) {
        List<HashMap<String, String>> resultImages = dataAnalysisResultGroupMapper.getResultImages(resultGroupId);
        List<String> imageUrlList = new ArrayList<>();
        if (resultImages.size() > 0) {
            for (HashMap<String, String> resultImage : resultImages) {
                imageUrlList.add(resultImage.get("thum_image_path"));
            }
        }
        return imageUrlList;
    }

    /**
     * 建立问题统计分组标准PO
     */
    private DataAnalysisResultGroupInPO buildDataAnalysisResultGroupCriteria(DataAnalysisResultGroupPageInDTO dto) {
        return DataAnalysisResultGroupInPO.builder()
                .startTime(dto.getStartTime())
                .endTime(addDay(dto.getEndTime()))
                .orgCode(dto.getOrgCode())
                .industryType(dto.getIndustryType())
                .topicLevelId(dto.getTopicLevelId())
                .topicProblemId(dto.getTopicProblemId())
                .topicKey(dto.getTopicKey())
                .tagName(dto.getTagName())
                .visibleOrgCode(TrustedAccessTracerHolder.get().getOrgCode())
                .build();
    }

    private DataAnalysisTraceSpacetimeInPO buildTracrSpacetimeCriteria(DataAnalysisTraceSpacetimeInDTO inDTO) {
        Double distance = inDTO.getDistance();
        BigDecimal longitude = inDTO.getLongitude();
        BigDecimal latitude = inDTO.getLatitude();
        return DataAnalysisTraceSpacetimeInPO.builder()
                .longitude(inDTO.getLongitude())
                .latitude(inDTO.getLatitude())
                .leftDistinct(longitude.subtract(BigDecimal.valueOf(distance * 0.00001)))
                .rightDistinct(longitude.add(BigDecimal.valueOf(distance * 0.00001)))
                .upDistinct(latitude.add(BigDecimal.valueOf(distance * 0.00001)))
                .downDistinct(latitude.subtract(BigDecimal.valueOf(distance * 0.00001)))
                .startTime(inDTO.getStartTime())
                .endTime(addDay(inDTO.getEndTime()))
                .distance(inDTO.getDistance())
                .topicProblemId(inDTO.getTopicProblemId())
                .missionId(inDTO.getMissionId())
                .resultGroupId(inDTO.getResultGroupId())
                .build();
    }

    /**
     * 时间转换
     */
    private String converteLocalDateTime(LocalDateTime data) {
        String dataString = data.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String[] split = dataString.split("-");
        String yearMonth = split[0] + split[1];
        return yearMonth;
    }

    /**
     * 增加一天
     */
    private String addDay(String date) {
        if (StringUtils.isEmpty(date)) return "";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime localDateTime = LocalDate.parse(date, df).atStartOfDay().plusDays(1);
        return localDateTime.format(df);
    }

}
