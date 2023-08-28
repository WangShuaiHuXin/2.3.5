package com.imapcloud.nest.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.imapcloud.nest.common.constant.ProblemConstant;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.common.validator.Assert;
import com.imapcloud.nest.enums.DataTypeEnum;
import com.imapcloud.nest.enums.PhotoTypeEnum;
import com.imapcloud.nest.mapper.DataProblemMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.DefectPhotoDTO;
import com.imapcloud.nest.pojo.dto.RecordDto;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.DeleteDataDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.PhotoIdNameTagIdDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TaskPhotoDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TaskRecordPhotoDTO;
import com.imapcloud.nest.pojo.dto.respDto.DataProblemDto;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.*;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.manager.nacos.NacosConfigurationService;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhengxd
 * @since 2021-06-16
 */
@Service
@Slf4j
public class DataProblemServiceImpl extends ServiceImpl<DataProblemMapper, DataProblemEntity> implements DataProblemService {
    @Autowired
    private RecordServiceImpl recordService;

    @Resource
    private DefectInfoService defectInfoService;

    @Resource
    private MissionPhotoService missionPhotoService;

    @Resource
    private MissionPhotoTagRelService missionPhotoTagRelService;

    @Resource
    private MissionPhotoTypeRelService missionPhotoTypeRelService;

    @Resource
    private MissionRecordsService missionRecordsService;

    @Resource
    private NacosConfigurationService nacosConfigurationService;

    @Override
    public RestRes getTaskProblemPhotoList(Integer tagId, List<Integer> problemSourceList, String startTime, String endTime) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        // 获取任务-有问题照片List
        List<TaskPhotoDTO> taskProblemPhotoList = baseMapper.getTaskProblemPhotoList(tagId, problemSourceList, startTime, endTime);
        List<DefectInfoEntity> defectInfoEntitys = defectInfoService.list(new QueryWrapper<DefectInfoEntity>().eq("deleted", false));
        taskProblemPhotoList.forEach(e -> {
            Integer problemSource = e.getProblemSource();
            // 不是违建、违章、智慧工地类型的时候，获取每个问题照片的详细信息List
            if (problemSource != 8 && problemSource != 17 && problemSource != 24) {
                List<DefectInfoEntity> defectInfoEntityList = defectInfoEntitys.stream().filter(defectInfoEntity ->
                        defectInfoEntity.getRecordId().equals(e.getProblemId())
                                && defectInfoEntity.getPhotoId().equals(e.getDataId())
                                && defectInfoEntity.getSource().equals(e.getProblemSource())
                ).collect(Collectors.toList());
                if (ToolUtil.isNotEmpty(defectInfoEntityList)) {
                    e.setDefectInfoEntityList(defectInfoEntityList);
                }
            }
        });

        // 根据任务名称进行分类
        Map<String, List<TaskPhotoDTO>> taskDataProblemListMap = taskProblemPhotoList.stream().collect(Collectors.groupingBy(TaskPhotoDTO::getTaskName, Collectors.toList()));
        // 将数据进行封装
        Iterator<Map.Entry<String, List<TaskPhotoDTO>>> it = taskDataProblemListMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<TaskPhotoDTO>> entry = it.next();
            Map<String, Object> resultMap = new HashMap<>(3);
            resultMap.put("taskName", entry.getKey());
            resultMap.put("taskProblemList", entry.getValue());
            resultList.add(resultMap);
        }

        Map<String, Object> map = new HashMap<>(2);
        map.put("list", resultList);
        return RestRes.ok(map);
    }

    @Override
    public RestRes getHZTaskProblemPhotoList(Integer problemSource, String startTime, String endTime) {
//        List<Map<String, Object>> resultList = new ArrayList<>();
        // 获取任务-有问题照片List
        List<TaskPhotoDTO> taskProblemPhotoList = baseMapper.getHZTaskProblemPhotoList(problemSource, startTime, endTime);
//        List<DefectInfoEntity> defectInfoEntitys = defectInfoService.list(new QueryWrapper<DefectInfoEntity>().eq("deleted", false));
//        taskProblemPhotoList.forEach(e -> {
//            // 不是违建、违章、智慧工地类型的时候，获取每个问题照片的详细信息List
//            if (problemSource != 8 && problemSource != 17 && problemSource != 24) {
//                List<DefectInfoEntity> defectInfoEntityList = defectInfoEntitys.stream().filter(defectInfoEntity ->
//                        defectInfoEntity.getRecordId().equals(e.getProblemId())
//                                && defectInfoEntity.getPhotoId().equals(e.getDataId())
//                                && defectInfoEntity.getSource().equals(e.getProblemSource())
//                ).collect(Collectors.toList());
//                if (ToolUtil.isNotEmpty(defectInfoEntityList)) {
//                    e.setDefectInfoEntityList(defectInfoEntityList);
//                }
//            }
//        });
//
//        // 根据任务名称进行分类
//        Map<String, List<TaskPhotoDTO>> taskDataProblemListMap = taskProblemPhotoList.stream().collect(Collectors.groupingBy(TaskPhotoDTO::getTaskName, Collectors.toList()));
//        // 将数据进行封装
//        Iterator<Map.Entry<String, List<TaskPhotoDTO>>> it = taskDataProblemListMap.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<String, List<TaskPhotoDTO>> entry = it.next();
//            Map<String, Object> resultMap = new HashMap<>(3);
//            resultMap.put("taskName", entry.getKey());
//            resultMap.put("taskProblemList", entry.getValue());
//            resultList.add(resultMap);
//        }

        Map<String, Object> map = new HashMap<>(2);
        map.put("list", taskProblemPhotoList);
        return RestRes.ok(map);
    }

    @Override
    public RestRes getTaskProblemPhotoListElectric(Integer tagId, List<Integer> problemSourceList, String startTime, String endTime) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        // 获取任务-有问题照片List
        List<TaskPhotoDTO> taskProblemPhotoList = baseMapper.getTaskProblemPhotoListElectric(tagId, problemSourceList, startTime, endTime);
        Iterator<TaskPhotoDTO> iterator = taskProblemPhotoList.iterator();
        while(iterator.hasNext()) {
        	TaskPhotoDTO taskPhotoDTO = iterator.next();
        	if(taskPhotoDTO.getDataId() == null) {
                iterator.remove();
            }
        	if(taskPhotoDTO.getDataId() == null) {
                iterator.remove();
            }
            else {
                List<DefectInfoEntity> entities = defectInfoService.list(new QueryWrapper<DefectInfoEntity>()
                        .eq("photo_id",taskPhotoDTO.getDataId())
                        .eq("deleted", false).eq("source", taskPhotoDTO.getProblemSource()));
                taskPhotoDTO.setDefectInfoEntityList(entities);
            }
        }
        // 根据任务名称进行分类
        Map<String, List<TaskPhotoDTO>> taskDataProblemListMap = taskProblemPhotoList.stream().collect(Collectors.groupingBy(TaskPhotoDTO::getTaskName, Collectors.toList()));
        // 将数据进行封装
        Iterator<Map.Entry<String, List<TaskPhotoDTO>>> it = taskDataProblemListMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<TaskPhotoDTO>> entry = it.next();
            Map<String, Object> resultMap = new HashMap<>(3);
            resultMap.put("taskName", entry.getKey());
            resultMap.put("taskProblemList", entry.getValue());
            resultList.add(resultMap);
        }

        Map<String, Object> map = new HashMap<>(2);
        map.put("list", resultList);
        return RestRes.ok(map);
    }

    @Override
    public List<TaskPhotoDTO> getTagProblemList(Integer tagId, List<Integer> problemSourceList, String startTime, String endTime) {
        return baseMapper.getTaskProblemPhotoList(tagId, problemSourceList, startTime, endTime);
    }

    @Override
    public RestRes getTaskPhotoList(Integer tagId, Integer problemSource, String startTime, String endTime) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        // 获取任务-有问题照片List
        List<TaskRecordPhotoDTO> taskPhotoList = baseMapper.getTaskPhotoList(tagId, problemSource, startTime, endTime);
        taskPhotoList.removeAll(Collections.singleton(null));
        List<DataProblemEntity> dataProblemEntities = this.lambdaQuery().eq(DataProblemEntity::getDeleted, 0).eq(DataProblemEntity::getFlag, 1).list();
        if (ToolUtil.isNotEmpty(taskPhotoList)) {
            // 根据任务名称进行分类
            Map<String, List<TaskRecordPhotoDTO>> taskDataProblemListMap = taskPhotoList.stream().collect(Collectors.groupingBy(TaskRecordPhotoDTO::getTaskName, Collectors.toList()));

            // 将数据进行封装
            Iterator<Map.Entry<String, List<TaskRecordPhotoDTO>>> it = taskDataProblemListMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, List<TaskRecordPhotoDTO>> entry = it.next();
                Map<String, Object> resultMap = new HashMap<>(3);

                // 重复的missionRecordId进行去重
                Set<TaskRecordPhotoDTO> taskPhotoSet = new TreeSet<>(Comparator.comparing(TaskRecordPhotoDTO::getMissionRecordId));
                taskPhotoSet.addAll(entry.getValue());
                List<TaskRecordPhotoDTO> missionRecordList = new ArrayList<>(taskPhotoSet);

                // 获取架次记录id下的照片有没有问题
                missionRecordList.forEach(e -> {
                    List<DataProblemEntity> dataProblemEntityList = dataProblemEntities.stream().filter(d->d.getTagId()==tagId&&d.getProblemSource()==problemSource&&d.getMissionRecordId()==e.getMissionRecordId()).collect(Collectors.toList());
                    e.setHasProblem(dataProblemEntityList.size() > 0 ? true : false);
                });

                resultMap.put("taskName", entry.getKey());
                resultMap.put("taskProblemList", missionRecordList);
                resultList.add(resultMap);
            }
        }

        Map<String, Object> map = new HashMap<>(2);
        map.put("list", resultList);
        return RestRes.ok(map);
    }

    @Override
    public RestRes getPhotoProblemList(List<Integer> missionRecordIdList, List<Integer> tagIdList, Integer problemSource, String startTime, String endTime) {
        // 获取照片信息
        List<TaskPhotoDTO> photoProblemList = baseMapper.getPhotoListByMissionRecordId(missionRecordIdList, tagIdList, problemSource, startTime, endTime);

        //  获取照片的问题信息
        getProblemInfo(photoProblemList, problemSource);

        Map<String, Object> map = new HashMap<>(2);
        map.put("list", photoProblemList);
        return RestRes.ok(map);
    }

    @Override
    public RestRes getPhotoIdNameTagIdList(List<Integer> missionRecordIdList, List<Integer> tagIdList, Integer problemSource, String startTime, String endTime) {
        List<PhotoIdNameTagIdDTO> photoIdNameTagIdDTOList = baseMapper.getPhotoIdNameTagId(missionRecordIdList, tagIdList, problemSource, startTime, endTime);
        Map<String, Object> map = new HashMap<>(2);
        map.put("list", photoIdNameTagIdDTOList);
        return RestRes.ok(map);
    }

    @Override
    public RestRes getPhotoProblemPageList(Integer page, Integer limit, List<Integer> missionRecordIdList, List<Integer> tagIdList, Integer problemSource, String startTime, String endTime) {
        IPage<TaskPhotoDTO> totalPage = baseMapper.getPhotoListByMissionRecordId(new Page<>(page, limit), missionRecordIdList, tagIdList, problemSource, startTime, endTime);
        List<TaskPhotoDTO> photoProblemList = totalPage.getRecords();

        //  获取照片的问题信息
        getProblemInfo(photoProblemList, problemSource);

        Map<String, Object> map = new HashMap<>(2);
        map.put("page", new PageUtils(totalPage));
        return RestRes.ok(map);
    }

    @Override
    public RestRes solveProblem(List<Integer> problemIdList) {
        // 问题状态修改为已解决
        this.lambdaUpdate().in(DataProblemEntity::getId, problemIdList).set(DataProblemEntity::getProblemStatus, ProblemConstant.ProblemStatus.HAS_SOLVE).update();
//        // 删除问题信息
//        defectInfoService.lambdaUpdate().in(DefectInfoEntity::getRecordId, problemIdList).set(DefectInfoEntity::getDeleted, true).update();
        return RestRes.ok();
    }

    @Override
    public RestRes deleteDataProblem(DeleteDataDTO deleteDataDTO) {
        Integer problemSource = deleteDataDTO.getProblemSource();
        List<Long> photoIdList = deleteDataDTO.getPhotoIdList();

        // 删除图片对应的问题记录、类型、标签、问题信息
        this.lambdaUpdate().set(DataProblemEntity::getDeleted, 1).set(DataProblemEntity::getProblemUrl, "")
                .set(DataProblemEntity::getProblemStatus, ProblemConstant.ProblemStatus.NOT_IDENTIFY).set(DataProblemEntity::getFlag, ProblemConstant.FlagType.NOT_PROBLEM)
                .in(DataProblemEntity::getDataId, photoIdList).eq(DataProblemEntity::getProblemSource, problemSource).eq(DataProblemEntity::getDeleted, 0).update();

        missionPhotoTypeRelService.lambdaUpdate().set(MissionPhotoTypeRelEntity::getDeleted, 1).in(MissionPhotoTypeRelEntity::getMissionPhotoId, photoIdList).eq(MissionPhotoTypeRelEntity::getType, problemSource).update();
        missionPhotoTagRelService.lambdaUpdate().set(MissionPhotoTagRelEntity::getDeleted, 1).in(MissionPhotoTagRelEntity::getMissionPhotoId, photoIdList).update();

        defectInfoService.lambdaUpdate().in(DefectInfoEntity::getPhotoId, photoIdList).eq(DefectInfoEntity::getSource, problemSource).set(DefectInfoEntity::getDeleted, true).update();
        return RestRes.ok();
    }

    @Override
    public RestRes resetDataProblem(DeleteDataDTO deleteDataDTO) {
        Integer problemSource = deleteDataDTO.getProblemSource();
        List<Long> photoIdList = deleteDataDTO.getPhotoIdList();

        // 删除图片对应的问题记录、问题信息
        this.lambdaUpdate().set(DataProblemEntity::getDeleted, 1).set(DataProblemEntity::getProblemUrl, "")
                .set(DataProblemEntity::getProblemStatus, ProblemConstant.ProblemStatus.NOT_IDENTIFY).set(DataProblemEntity::getFlag, ProblemConstant.FlagType.NOT_PROBLEM)
                .in(DataProblemEntity::getDataId, photoIdList).eq(DataProblemEntity::getProblemSource, problemSource).eq(DataProblemEntity::getDeleted, 0).update();
        defectInfoService.lambdaUpdate().in(DefectInfoEntity::getPhotoId, photoIdList).eq(DefectInfoEntity::getSource, problemSource).set(DefectInfoEntity::getDeleted, true).update();
        return RestRes.ok();
    }

    @Override
    public RestRes getHistoryPhotoInfo(Integer problemSource, Long photoId, Double range, String startTime, String endTime) {
        List<TaskPhotoDTO> photoInfoList = getHistoryPhotoInfoList(problemSource, photoId, range, startTime, endTime);
        if (photoInfoList != null) {
            for (TaskPhotoDTO taskPhotoDTO : photoInfoList) {
                DataProblemEntity dataProblemEntity = this.getOne(new QueryWrapper<DataProblemEntity>().eq("problem_source", problemSource).eq("data_id", taskPhotoDTO.getPhotoId()).eq("deleted", false));
                if (dataProblemEntity != null) {
                    taskPhotoDTO.setProblemId(dataProblemEntity.getId());
                    taskPhotoDTO.setProblemUrl(dataProblemEntity.getProblemUrl());
                    taskPhotoDTO.setFlag(dataProblemEntity.getFlag());
                    taskPhotoDTO.setProblemStatus(dataProblemEntity.getProblemStatus());
                    List<DefectInfoEntity> entities = defectInfoService.list(new QueryWrapper<DefectInfoEntity>()
                            .eq("record_id", taskPhotoDTO.getProblemId()).eq("source", problemSource).eq("deleted", false));
                    taskPhotoDTO.setDefectInfoEntityList(entities);
                }
            }
        } else {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_LATITUDE_AND_LONGITUDE_INFORMATION.getContent()));
        }

        Map map = new HashMap(2);
        map.put("list", photoInfoList);
        return RestRes.ok(map);
    }

    /**
     * 获取历史照片
     *
     * @param problemSource
     * @param photoId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<TaskPhotoDTO> getHistoryPhotoInfoList(Integer problemSource, Long photoId, Double range, String startTime, String endTime) {
        // 根据照片获取图片名称，经纬度信息
        MissionPhotoEntity missionPhotoEntity = missionPhotoService.getOne(new QueryWrapper<MissionPhotoEntity>().eq("id", photoId));
        Double latitude = missionPhotoEntity.getLatitude();
        Double longitude = missionPhotoEntity.getLongitude();
        if (ToolUtil.isNotEmpty(latitude) && ToolUtil.isNotEmpty(longitude)) {
            Integer missionRecordsId = missionPhotoService.getById(photoId).getMissionRecordsId();
            Integer taskId = missionRecordsService.getTaskIdByRecordsId(missionRecordsId);
            List<Integer> missionRecordsIdList = missionRecordsService.getMissionRecordsIdByTaskId(taskId);
            missionRecordsIdList.remove(missionRecordsId);


            range = range == null ? 50 : range;
            Double relRangeLat = ToolUtil.isNotEmpty(latitude) ? range / (111194.926644 * Math.cos(latitude / 180 * Math.PI)) : null;
            Double relRangeLon = ToolUtil.isNotEmpty(longitude) ? range / 111194.926644558737 : null;
            log.info("匹配历史照片： latitude-: " + (latitude - relRangeLat) + ";  latitude+: " + (latitude + relRangeLat));
            log.info("匹配历史照片: longitude-: " + (longitude - relRangeLon) + "; longitude+: " + (longitude + relRangeLon));

            // 获取同名 且 照片经纬度在50米误差范围内的历史照片
            List<TaskPhotoDTO> photoInfoList = baseMapper.getHistoryPhotoInfo(missionRecordsIdList, problemSource, photoId,
                    latitude, longitude, relRangeLat, relRangeLon, startTime, endTime);
            return photoInfoList;

        }
        return null;
    }


//    /**
//     *  此方法弃用在v2.2.3后弃用，若后续版本无异常则可以删除，若有异常则去掉该注释并写明方法
//     */
//    @Deprecated
//    private void downloadDefectPhoto(List photoList, Integer problemSource, ZipOutputStream zipOut) {
//        List<String> resultPhotoUrlList = new ArrayList<>();
//        photoList.forEach(e -> {
//            DataProblemEntity dataProblemEntity = this.lambdaQuery().eq(DataProblemEntity::getDataId, e).eq(DataProblemEntity::getProblemSource, problemSource).eq(DataProblemEntity::getDeleted, 0).one();
//            if (dataProblemEntity != null) {
//                // 有有问题照片则下载问题照片
//                resultPhotoUrlList.add(dataProblemEntity.getProblemUrl() != null ? dataProblemEntity.getProblemUrl() : dataProblemEntity.getPhotoUrl());
//            } else {
//                // 无问题则下载原图
//                String photoUrl = missionPhotoService.getOne(new QueryWrapper<MissionPhotoEntity>().eq("id", e).eq("deleted", 0)).getPhotoUrl();
//                resultPhotoUrlList.add(photoUrl);
//            }
//        });
//
//        resultPhotoUrlList.forEach(e -> {
//            // 将问题照片或原图打包进压缩包
//            InputStream input = null;
//            try {
//                input = new FileInputStream(e);
//                DownLoadZipUtil.zipOutInput(e.substring(e.lastIndexOf("/")), input, zipOut);
//            } catch (IOException ex) {
//                ex.printStackTrace();
//                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_DOWNLOADING.getContent()));
//            }
//        });
//    }



    /**
     * 获取照片的问题信息
     *
     * @param photoProblemList
     * @param problemSource
     */
    private void getProblemInfo(List<TaskPhotoDTO> photoProblemList, Integer problemSource) {
        photoProblemList.forEach(e -> {
            // 获取照片的问题信息
            DataProblemEntity photoProblem = this.getOne(new QueryWrapper<DataProblemEntity>().eq("data_id", e.getPhotoId())
                    .eq("tag_id", e.getTagId()).eq("problem_source", problemSource).eq("deleted", false));
            if (ToolUtil.isNotEmpty(photoProblem)) {
                e.setProblemId(photoProblem.getId());
                e.setProblemStatus(photoProblem.getProblemStatus());
                e.setProblemName(photoProblem.getProblemName());
                e.setProblemUrl(photoProblem.getProblemUrl());
                e.setProblemThumbUrl(photoProblem.getProblemThumbUrl());
                e.setProblemTime(photoProblem.getModifyTime());
                e.setFlag(photoProblem.getFlag());

                // 获取每个问题照片的详细信息List
                List<DefectInfoEntity> defectInfoEntityList = defectInfoService.list(new QueryWrapper<DefectInfoEntity>()
                        .eq("record_id", e.getProblemId()).eq("photo_id", e.getPhotoId()).eq("deleted", false));
                if (ToolUtil.isNotEmpty(defectInfoEntityList)) {
                    e.setDefectInfoEntityList(defectInfoEntityList);
                }
            }
            // 设置排污监测可见光图
            if (problemSource == 3) {
                MissionPhotoEntity photoEntity = missionPhotoService.getById(e.getPhotoId());
                if (photoEntity.getPhotoType() == PhotoTypeEnum.INFRARED.getValue()) {
                    List<MissionPhotoEntity> missionPhotoEntityList = missionPhotoService.lambdaQuery().eq(MissionPhotoEntity::getPhotoType, PhotoTypeEnum.VISIBLE.getValue()).eq(MissionPhotoEntity::getMissionRecordsId, photoEntity.getMissionRecordsId())
                            .eq(MissionPhotoEntity::getWaypointIndex, photoEntity.getWaypointIndex()).list();
                    if (ToolUtil.isNotEmpty(missionPhotoEntityList)) {
                        e.setPhotoUrlVisible(missionPhotoEntityList.get(0).getPhotoUrl());
                        e.setThumbnailUrlVisible(missionPhotoEntityList.get(0).getThumbnailUrl());
                    }
                }
//                if (photoEntity.getPhotoType() == PhotoTypeEnum.THRM.getValue()) {
//                    MissionPhotoEntity missionPhotoEntity = missionPhotoService.lambdaQuery().eq(MissionPhotoEntity::getPhotoType, PhotoTypeEnum.WIDE.getValue()).eq(MissionPhotoEntity::getMissionRecordsId, photoEntity.getMissionRecordsId())
//                            .eq(MissionPhotoEntity::getWaypointIndex, photoEntity.getWaypointIndex()).list().get(0);
//                    e.setPhotoUrlVisible(missionPhotoEntity.getPhotoUrl());
//                    e.setThumbnailUrlVisible(missionPhotoEntity.getThumbnailUrl());
//                }
            }
        });
    }


}
