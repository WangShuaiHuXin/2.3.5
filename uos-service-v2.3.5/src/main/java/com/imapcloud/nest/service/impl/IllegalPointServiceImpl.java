package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.ProblemConstant;
import com.imapcloud.nest.enums.DataTypeEnum;
import com.imapcloud.nest.mapper.IllegalPointMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.DeleteDataDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TagOrthoDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TagPointCloudDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TaskPhotoDTO;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.JFreeChartUtils;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.SSHHelper;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.properties.StoreConfig;
import com.imapcloud.nest.v2.service.OrgAccountService;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 违建点信息表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-03-17
 */
@Slf4j
@Service
public class IllegalPointServiceImpl extends ServiceImpl<IllegalPointMapper, IllegalPointEntity> implements IllegalPointService {

    @Autowired
    private SSHHelper sshHelper;

    @Autowired
    private DataPointCloudService dataPointCloudService;

    @Autowired
    private DataOrthoService dataOrthoService;

    @Autowired
    private DataProblemService dataProblemService;

    @Resource
    private MissionPhotoService missionPhotoService;

    @Autowired
    private RecordServiceImpl recordService;

    @Resource
    private DefectInfoService defectInfoService;

    @Resource
    private DataTagTypeService dataTagTypeService;
    @Resource
    private DataMissionRecordService dataMissionRecordService;
    @Resource
    private JFreeChartUtils jFreeChartUtils;
    @Resource
    private DataMultispectralService dataMultispectralService;
    @Resource
    private DataMultispectralDirectorService dataMultispectralDirectorService;
    @Resource
    private DataOrthoJsonService dataOrthoJsonService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    /**
     * 是否使用华为云（Huawei Cloud Machine）主机
     */
    private boolean isUsedHCM(){
        return geoaiUosProperties.getAi().isUseHuawei();
    }

    @Override
    public RestRes getIllegalPointList(Integer beforeFileId, Integer afterFileId, Integer problemSource) {
        Map<String, Object> map = new HashMap<>(2);
        List<IllegalPointEntity> illegalPointEntityList = this.list(new QueryWrapper<IllegalPointEntity>().eq("problem_source", problemSource).eq("before_file_id", beforeFileId).eq("after_file_id", afterFileId));

        illegalPointEntityList.forEach(e -> {
            if (e.getPhotoId() != null) {
                // 查询该违建点的问题
                Integer problemId = dataProblemService.getOne(new QueryWrapper<DataProblemEntity>().eq("data_id", e.getId())
                        .eq("problem_source", problemSource).eq("deleted", false)).getId();

                List<DefectInfoEntity> entities = defectInfoService.list(new QueryWrapper<DefectInfoEntity>()
                        .eq("record_id", problemId).eq("photo_id", e.getPhotoId()).eq("source", problemSource).eq("deleted", false));
                e.setDefectInfoEntityList(entities);
            }
        });

        map.put("list", illegalPointEntityList);
        map.put("size", illegalPointEntityList.size());
        map.put("area", illegalPointEntityList.stream().mapToDouble(IllegalPointEntity::getArea).sum());
        return RestRes.ok(map);
    }

    @Override
    public RestRes getHZIllegalPointList(Integer problemSource) {
        Map<String, Object> map = new HashMap<>(2);
        List<IllegalPointEntity> illegalPointEntityList = this.list(new QueryWrapper<IllegalPointEntity>().eq("problem_source", problemSource).eq("deleted", false));
        map.put("list", illegalPointEntityList);
        return RestRes.ok(map);
    }
    @Override
    public RestRes saveIllegalPoint(List<IllegalPointEntity> illegalPointEntityList) {
        illegalPointEntityList.forEach(e -> {
            if (e.getId() == null) {
                e.setType(1);
            }
            // 保存到问题表
            setDataProblem(e);
        });
        // 保存违建点
        this.saveOrUpdateBatch(illegalPointEntityList);
        return RestRes.ok();
    }

    @Override
    @Transactional
    public RestRes updateIllegalPoint(IllegalPointEntity illegalPointEntity) {
        // 如果为新增，则设置为手动标记类型
        if (illegalPointEntity.getId() == null) {
            illegalPointEntity.setType(1);
        }
        this.saveOrUpdate(illegalPointEntity);
        setDataProblem(illegalPointEntity);
        Map map = new HashMap(2);
        map.put("id", illegalPointEntity.getId());
        map.put("photoUrl", illegalPointEntity.getPhotoUrl());
        return RestRes.ok(map);
    }

    @Override
    public RestRes deleteIllegalPoint(List idList) {
        this.removeByIds(idList);
        dataProblemService.lambdaUpdate().set(DataProblemEntity::getDeleted, 1).in(DataProblemEntity::getDataId, idList).update();
        return RestRes.ok();
    }

    @Override
    public RestRes smartAnalysis(Integer beforeFileId, Integer afterFileId, String threshold, String kmlUrl) {
        log.info("beforeFileId: " + beforeFileId + "; afterFileId: " + afterFileId);
        Integer state = dataPointCloudService.getOne(new QueryWrapper<DataPointCloudEntity>().eq("deleted", 0).eq("id", beforeFileId)).getState();
        if (state == 1) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_POINT_CLOUD_IS_BEING_ANALYZED_PLEASE_DO_NOT_REPEAT_THE_IDENTIFICATION_AT_THE_SAME_TIME.getContent()));
        }
        // 修改状态标识为正常：正在分析
        dataPointCloudService.updateStateById(beforeFileId, afterFileId, 1);

        List<IllegalPointEntity> illegalPointEntityList = new ArrayList<>();
        // 获取2个点云的文件路径
        String beforeUrl = dataPointCloudService.getLasUrlById(beforeFileId);
        String afterUrl = dataPointCloudService.getLasUrlById(afterFileId);

        // 将nginx路径替换成点云在服务器上的实际路径，用于智能分析的命令
        if (beforeUrl != null && afterUrl != null) {
            StoreConfig store = geoaiUosProperties.getStore();
            beforeUrl = store.getOriginRelPath() + beforeUrl.substring(store.getOriginPath().length());
            afterUrl = store.getOriginRelPath() + afterUrl.substring(store.getOriginPath().length());

            kmlUrl = kmlUrl != null ? store.getOriginRelPath() + kmlUrl.substring(store.getOriginPath().length()) : " ";

        } else {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_SELECTED_POINT_CLOUD_FILE_DOES_NOT_HAVE_A_LAS_FILE_SO_IT_CANNOT_BE_ANALYZED.getContent()));
        }

//            String command = "cd D:\\Test\\测试流程程序 && Win32Project.exe 49 D:\\Test\\las输出\\ D:\\Test\\测试流程程序\\gocesiumtiler.exe D:\\Test\\3dtiles输出\\ " + beforeUrl + " " + afterUrl + " " + threshold;
//            String str = executeCmd(command);
        String command = "cd /data/pcl-detect && ./pclDetection 51 /data/pcl-detect/test-data/lasoutput/ " +
                "/data/pcl-detect/gocesium/main /data/pcl-detect/test-data/3dtiles/ " + "\"" + kmlUrl
                + "\"  \"" + beforeUrl + "\"  \"" + afterUrl + "\"" + " " + threshold;
        String str = sshHelper.exec(command);
//        log.info("智能分析的结果： " + str);
        if (ToolUtil.isNotEmpty(str)) {
            String[] coordinates = str.split(";");
            for (int i = 0; i < coordinates.length; i++) {
                log.info("coordinates的第[ " + i + " ]个： " + coordinates[i]);
                IllegalPointEntity illegalPointEntity = new IllegalPointEntity();

                // 获取经纬度等信息
                String[] info = coordinates[i].split(",");
                if (info.length == 6) {
                    illegalPointEntity.setLongitude(Double.valueOf(info[0]));
                    illegalPointEntity.setLatitude(Double.valueOf(info[1]));
                    illegalPointEntity.setHeight(Double.valueOf(info[2]));
                    Double area = Double.valueOf(info[3]) * Double.valueOf(info[4]);
                    area = (double) Math.round(area * 1000) / 1000;
                    illegalPointEntity.setArea(area);
                    illegalPointEntity.setCoordinates(coordinates[i]);
                    illegalPointEntity.setName("疑似违建");
                    illegalPointEntity.setBeforeFileId(beforeFileId);
                    illegalPointEntity.setAfterFileId(afterFileId);
                    illegalPointEntity.setType(2);
                    illegalPointEntity.setSource(3);
                    illegalPointEntity.setProblemSource(8);
                    illegalPointEntityList.add(illegalPointEntity);
                }
            }
        } else {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_ILLEGAL_POINTS_ARE_ANALYZED.getContent()));
        }

        if (illegalPointEntityList.size() > 0) {
            // 将结果处理并保存到数据库
            saveResult(beforeFileId, afterFileId, illegalPointEntityList);

            // 进行智能匹配照片
            illegalPointEntityList.forEach(e -> {
                Integer tagId = dataPointCloudService.getById(afterFileId).getTagId();
                autoMatchPhoto(e.getId(), 3, 8, tagId, e.getLatitude(), e.getLongitude(), 50.0);
            });

            // 修改状态标识为2：分析完毕
            dataPointCloudService.updateStateById(beforeFileId, afterFileId, 2);
            return RestRes.ok();
        } else {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_ILLEGAL_POINTS_ARE_ANALYZED.getContent()));
        }
    }

    @Override
    public RestRes stopSmartAnalysis(Integer beforeFileId, Integer afterFileId) {
        log.info("终止分析");
        dataPointCloudService.updateStateById(beforeFileId, afterFileId, 0);
//        pointCloudConvert(threshold);
        log.info("终止分析结束");
        return RestRes.ok();
    }



    @Override
    public RestRes autoMatchPhoto(Integer id, Integer dataType, Integer problemSource, Integer tagId, Double latitude, Double longitude, Double range) {
        if (id != null) {
            // 获取在匹配范围的照片(本点云关联的照片)
            IllegalPointEntity illegalPointEntity = this.getById(id);
            List<Long> photoIdList = baseMapper.getRelatedPhoto(illegalPointEntity.getAfterFileId(), dataType).stream().map(TaskPhotoDTO::getPhotoId).collect(Collectors.toList());
            if (ToolUtil.isEmpty(photoIdList)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_POINT_IS_NOT_ASSOCIATED_WITH_THE_TASK_PHOTO_CAN_NOT_MATCH.getContent()));
            }
            Double relRangeLat = range / (111194.926644 * Math.cos(latitude / 180 * Math.PI));
            Double relRangeLon = range / 111194.926644558737;
            log.info("智能匹配照片： latitude-: " + (latitude - relRangeLat) + ";  latitude+: " + (latitude + relRangeLat));
            log.info("智能匹配照片: longitude-: " + (longitude - relRangeLon) + "; longitude+: " + (longitude + relRangeLon));
            log.info("照片id范围： " + photoIdList);
            List<MissionPhotoEntity> closeLatitudePhoto = baseMapper.getCloseLatitudePhoto(photoIdList, latitude, longitude, relRangeLat, relRangeLon);
            if (ToolUtil.isNotEmpty(closeLatitudePhoto)) {
                String thumbnailUrl = closeLatitudePhoto.get(0).getThumbnailUrl();
                String photoUrl = closeLatitudePhoto.get(0).getPhotoUrl();
                Long photoId = closeLatitudePhoto.get(0).getId();
                if (closeLatitudePhoto.size() > 1 && closeLatitudePhoto.get(0).getLatitude().equals(closeLatitudePhoto.get(1).getLatitude())) {
                    MissionPhotoEntity closeLongitudePhoto = baseMapper.getCloseLongitudePhoto(latitude, longitude);
                    if (ToolUtil.isNotEmpty(closeLongitudePhoto)) {
                        photoUrl = closeLongitudePhoto.getPhotoUrl();
                        thumbnailUrl = closeLongitudePhoto.getThumbnailUrl();
                        photoId = closeLongitudePhoto.getId();
                    }
                }

                illegalPointEntity.setPhotoId(photoId);
                illegalPointEntity.setPhotoUrl(photoUrl);
                illegalPointEntity.setThumbnailUrl(thumbnailUrl);
                this.updateById(illegalPointEntity);

                dataProblemService.lambdaUpdate().set(DataProblemEntity::getProblemUrl, photoUrl).eq(DataProblemEntity::getDataId, id)
                        .eq(DataProblemEntity::getProblemSource, problemSource)
                        .eq(DataProblemEntity::getDeleted, 0).update();

                Map map = new HashMap(2);
                map.put("photoUrl", photoUrl);
                return RestRes.ok(map);
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_MATCHING_PHOTOS.getContent()));

        } else {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_POINT_IS_NOT_SAVED.getContent()));
        }
    }

    @Override
    public List<IllegalPointEntity> getIllegalListByTag(Integer tagId, Integer type, String startTime, String endTime) {
        return baseMapper.getIllegalListByTag(tagId, type, startTime, endTime);
    }

    @Resource
    private OrgAccountService orgAccountService;

    @Override
    public Map<String, Object> getTagPointCloudList() {

        // 查询用户单位信息
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        // 获取标签点云列表
        List<TagPointCloudDTO> tagPointCloudList = baseMapper.getTagPointCloudList(orgCode);

        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> tagList = new ArrayList();

        if (ToolUtil.isNotEmpty(tagPointCloudList)) {
            Map<String, List<TagPointCloudDTO>> tagNameMap = tagPointCloudList.stream().collect(Collectors.groupingBy(TagPointCloudDTO::getTagName));
            for (Map.Entry<String, List<TagPointCloudDTO>> entry : tagNameMap.entrySet()) {
                List<TagPointCloudDTO> TagNamePointCloudList = entry.getValue();
                Map<String, List<TagPointCloudDTO>> taskNameMap = TagNamePointCloudList.stream().collect(Collectors.groupingBy(TagPointCloudDTO::getTaskName));
                List<Map<String, Object>> taskList = new ArrayList();
                for (Map.Entry<String, List<TagPointCloudDTO>> taskEntry : taskNameMap.entrySet()) {
                    // 任务map
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("taskName", taskEntry.getKey());
                    // 点云数据List
                    taskMap.put("dataList", taskEntry.getValue());
                    taskList.add(taskMap);
                }
                // 标签map
                Map<String, Object> tagMap = new HashMap<>();
                tagMap.put("taskList", taskList);
                tagMap.put("tagName", entry.getKey());
                tagList.add(tagMap);
            }
        }
        map.put("list", tagList);
        return map;
    }

    @Override
    public Map<String, Object> getTagOrthoList(Integer problemSource) {

        // 查询用户单位信息
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        // 获取标签正射列表
        List<TagOrthoDTO> tagOrthoList = baseMapper.getTagOrthoList(orgCode, problemSource);

        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> tagList = new ArrayList();
        if (ToolUtil.isNotEmpty(tagOrthoList)) {
            Map<String, List<TagOrthoDTO>> tagNameMap = tagOrthoList.stream().collect(Collectors.groupingBy(TagOrthoDTO::getTagName));
            for (Map.Entry<String, List<TagOrthoDTO>> entry : tagNameMap.entrySet()) {
                List<TagOrthoDTO> TagNamePointCloudList = entry.getValue();
                Map<String, List<TagOrthoDTO>> taskNameMap = TagNamePointCloudList.stream().collect(Collectors.groupingBy(TagOrthoDTO::getTaskName));
                List<Map<String, Object>> taskList = new ArrayList();
                for (Map.Entry<String, List<TagOrthoDTO>> taskEntry : taskNameMap.entrySet()) {
                    // 任务map
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("taskName", taskEntry.getKey());
                    // 正射数据List
                    taskMap.put("dataList", taskEntry.getValue());
                    taskList.add(taskMap);
                }
                // 标签map
                Map<String, Object> tagMap = new HashMap<>();
                tagMap.put("taskList", taskList);
                tagMap.put("tagName", entry.getKey());
                tagList.add(tagMap);
            }
        }
        map.put("list", tagList);
        return map;
    }

    @Override
    public Map<String, Object> getHistoryPhotoList(Integer dataId, Integer taskId, Long photoId, Integer dataType, Integer problemSource, Integer tagId, Double range, String startTime, String endTime) {
        // 根据照片获取图片名称，经纬度信息
        MissionPhotoEntity missionPhotoEntity = missionPhotoService.getById(photoId);
        Double latitude = missionPhotoEntity.getLatitude();
        Double longitude = missionPhotoEntity.getLongitude();
        if (ToolUtil.isEmpty(latitude) || ToolUtil.isEmpty(longitude)) {
            return null;
        } else {
            Double relRangeLat = range / (111194.926644 * Math.cos(latitude / 180 * Math.PI));
            Double relRangeLon = range / 111194.926644558737;

            // 获取在匹配范围的照片(同任务下除了本架次外的其他架次点云关联的照片 + 推送分析到违建的全部照片)
            List<Long> rangePhotoIdList = new ArrayList<>();
//            Integer missionRecordsId = missionPhotoEntity.getMissionRecordsId();
//            Integer taskId = missionRecordsService.getTaskIdByRecordsId(missionRecordsId);
//            List<Integer> missionRecordsIdList = baseMapper.getRecordsIdBySameTask(taskId, dataType, missionRecordsId);

            // 根据taskId查询同任务其他架次已推送分析的点云的idList
            List<Integer> dataIdList = null;
            if (dataType == 3) {
                dataIdList = baseMapper.getPointCloudIdByTaskId(taskId, dataId, problemSource);
            } else if (dataType == 2) {
                dataIdList = baseMapper.getOrthoIdByTaskId(taskId, dataId, problemSource);
            }
            if (ToolUtil.isNotEmpty(dataIdList)) {
                // 根据点云idList，获取点云关联的照片recordIdList
                List<Integer> missionRecordsIdList = dataMissionRecordService.list(new QueryWrapper<DataMissionRecordEntity>().eq("deleted", 0).in("data_id", dataIdList)).stream().map(DataMissionRecordEntity::getMissionRecordsId).collect(Collectors.toList());
                if (ToolUtil.isNotEmpty(missionRecordsIdList)) {
                    // 同任务下其他点云关联的照片
                    rangePhotoIdList = missionPhotoService.list(new QueryWrapper<MissionPhotoEntity>().in("mission_records_id", missionRecordsIdList).eq("deleted", false)).stream().map(MissionPhotoEntity::getId).collect(Collectors.toList());
                }
            }
            // 推送分析到违建的全部照片
            List<Long> illegalPhotoIdList = baseMapper.getIllegalPhotoIdList(problemSource, tagId);
            if (ToolUtil.isNotEmpty(illegalPhotoIdList)) {
                rangePhotoIdList.addAll(illegalPhotoIdList);
            }
            List<TaskPhotoDTO> photoInfoList = new ArrayList<>();
            rangePhotoIdList.add(photoId);
            if (ToolUtil.isNotEmpty(rangePhotoIdList)) {
                log.info("匹配历史照片： latitude-: " + (latitude - relRangeLat) + ";  latitude+: " + (latitude + relRangeLat));
                log.info("匹配历史照片: longitude-: " + (longitude - relRangeLon) + "; longitude+: " + (longitude + relRangeLon));
                log.info("照片id范围： " + rangePhotoIdList);
                // 获取照片范围内 且 照片经纬度在10米误差范围内的历史照片
                photoInfoList = baseMapper.getHistoryPhotoInfo(missionPhotoEntity.getMissionRecordsId(), rangePhotoIdList,
                        missionPhotoEntity.getName(), photoId, latitude, longitude, relRangeLat, relRangeLon, startTime, endTime);
            }
            Map map = new HashMap(2);
            map.put("list", photoInfoList);
            return map;
        }
    }

    @Override
    public Map<String, Object> getRelatedPhoto(Integer afterFileId, Integer dataType) {
//        List<TaskPhotoDTO> relatedPhoto = new ArrayList<>();
//        if (DataTypeEnum.ORTHO.getValue() == dataType) {
//            relatedPhoto = baseMapper.getOrthoRelatedPhoto(afterFileId, dataType);
//        } else if (DataTypeEnum.POINTCLOUD.getValue() == dataType) {
//            relatedPhoto = baseMapper.getPointCloudRelatedPhoto(afterFileId, dataType);
//        }
        List<TaskPhotoDTO> relatedPhoto = baseMapper.getRelatedPhoto(afterFileId, dataType);
        Map map = new HashMap(2);
        map.put("list", relatedPhoto);
        return map;
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void markPhoto(IllegalPointMarkDTO illegalPointMarkDTO) {
//        Integer illegalPointId = illegalPointMarkDTO.getIllegalPointId();
//        Long photoId = illegalPointMarkDTO.getPhotoId();
//        Integer problemSource = illegalPointMarkDTO.getProblemSource();
//        List<DefectInfoEntity> defectInfoEntityList = illegalPointMarkDTO.getDefectInfoEntityList();
//
//        // 查询该违建点的问题
//        DataProblemEntity dataProblemEntity = dataProblemService.getOne(new QueryWrapper<DataProblemEntity>().eq("data_id", illegalPointId).eq("problem_source", problemSource).eq("deleted", false));
//
//        // 查询该图片的全部缺陷，并删除该图片已有的全部缺陷
//        List<DefectInfoEntity> oldDefectInfoEntityList = defectInfoService.list(new QueryWrapper<DefectInfoEntity>().eq("photo_id", photoId).eq("source", problemSource).eq("record_id", dataProblemEntity.getId()).eq("deleted", false));
//        if (ToolUtil.isNotEmpty(oldDefectInfoEntityList)) {
//            defectInfoService.removeByIds(oldDefectInfoEntityList.stream().map(DefectInfoEntity::getId).collect(Collectors.toList()));
//        }
//
//        String markPhotoUrl = "";
//        if (ToolUtil.isNotEmpty(defectInfoEntityList)) {
//            // 处理标记的x，x1，y，y1,得到坐标信息
//            String info = recordService.delaMarkInfo(photoId, defectInfoEntityList, null, problemSource);
//            // 生成标记图片
//            markPhotoUrl = recordService.createOtherPhoto(info, photoId);
//
//            // 保存问题详细信息
//            defectInfoEntityList.forEach(e -> {
//                e.setRecordId(dataProblemEntity.getId());
//            });
//            defectInfoService.saveBatch(defectInfoEntityList);
//        }
//        // 保存illegal表的照片路径
//        this.lambdaUpdate().set(IllegalPointEntity::getMarkPhotoUrl, markPhotoUrl).eq(IllegalPointEntity::getId, illegalPointId).update();
//        // 保存dataProblem表路径
//        dataProblemService.lambdaUpdate().set(DataProblemEntity::getProblemUrl, markPhotoUrl).eq(DataProblemEntity::getDataId, illegalPointId)
//                .eq(DataProblemEntity::getProblemSource, problemSource).eq(DataProblemEntity::getDeleted, 0).update();
//    }

    @Override
    public void deleteDataList(DeleteDataDTO deleteDataDTO) {
        List<Integer> dataIdList = deleteDataDTO.getDataIdList();
        Integer problemSource = deleteDataDTO.getProblemSource();
        Integer dataSource = problemSource == 8 ? 3 : (problemSource == 17 ? 2 : null);
        if (dataSource != null) {
            // 删除推送分析关系, 违建点,问题，图片标记
            dataTagTypeService.lambdaUpdate().in(DataTagTypeEntity::getDataId, dataIdList).eq(DataTagTypeEntity::getType, problemSource).set(DataTagTypeEntity::getDeleted, 1).update();

            List<IllegalPointEntity> illegalPointEntityList = this.lambdaQuery().in(IllegalPointEntity::getBeforeFileId, dataIdList)
                    .or().in(IllegalPointEntity::getAfterFileId, dataIdList).eq(IllegalPointEntity::getSource, dataSource).list();
            if (ToolUtil.isNotEmpty(illegalPointEntityList)) {
                List<Integer> illegalPointIdList = illegalPointEntityList.stream().map(IllegalPointEntity::getId).collect(Collectors.toList());
                this.removeByIds(illegalPointIdList);

                List<Integer> dataProblemIdList = dataProblemService.lambdaQuery().in(DataProblemEntity::getDataId, illegalPointIdList).list().stream().map(DataProblemEntity::getId).collect(Collectors.toList());
                dataProblemService.removeByIds(dataProblemIdList);
                defectInfoService.lambdaUpdate().in(DefectInfoEntity::getRecordId, dataProblemIdList).set(DefectInfoEntity::getDeleted, 1).update();
            }
        }
    }



    @Override
    public Map<String, Object> getTagOrthoRecordList(Integer problemSource,String recordMonth,String recordDay) {

        // 查询用户单位信息
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        Map<String, Object> map = new HashMap<>();
        // 获取标签正射列表
        List<TagOrthoDTO> tagOrthoList = baseMapper.getTagOrthoList(orgCode, problemSource);
        List<TagOrthoDTO> tagOrthoRecordList = new ArrayList<>();
        if(problemSource==41){
            List<DataMultispectralDirectorEntity> list = dataMultispectralDirectorService.list();
            for (DataMultispectralDirectorEntity dataMultispectralDirectorEntity:list){
                List<TagOrthoDTO> tagOrthoDTOS = tagOrthoList.stream().filter(e -> e.getOrthoId().equals(dataMultispectralDirectorEntity.getMultispectralId())).collect(Collectors.toList());
                if(tagOrthoDTOS.size()>0){
                    LocalDateTime createTime = dataMultispectralDirectorEntity.getCreateTime();
                    String month = createTime.format(DateTimeFormatter.ofPattern("yyyyMM"));
                    String day = createTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                    if(recordMonth==null||recordMonth.equals(month)) {
                        if(recordDay==null||recordDay.equals(day)) {
                            TagOrthoDTO tagOrthoDTO = tagOrthoDTOS.get(0);
                            tagOrthoDTO.setShpUrl(dataMultispectralDirectorEntity.getDirectorUrl());
                            tagOrthoRecordList.add(tagOrthoDTO);
                        }
                    }
                }
            }
        }
        if(problemSource==42){
            List<DataOrthoJsonEntity> list = dataOrthoJsonService.list();
            for (DataOrthoJsonEntity dataOrthoJsonEntity:list){
                List<TagOrthoDTO> tagOrthoDTOS = tagOrthoList.stream().filter(e -> e.getOrthoId().equals(dataOrthoJsonEntity.getOrthoId())).collect(Collectors.toList());
                if(tagOrthoDTOS.size()>0){
                    LocalDateTime createTime = dataOrthoJsonEntity.getCreateTime();
                    String month = createTime.format(DateTimeFormatter.ofPattern("yyyyMM"));
                    String day = createTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                    if(recordMonth==null||recordMonth.equals(month)) {
                        if(recordDay==null||recordDay.equals(day)) {
                            TagOrthoDTO tagOrthoDTO = tagOrthoDTOS.get(0);
                            tagOrthoDTO.setShpUrl(dataOrthoJsonEntity.getJsonUrl());
                            tagOrthoRecordList.add(tagOrthoDTO);
                        }
                    }
                }
            }
        }
        map.put("list", tagOrthoRecordList);
        return map;
    }

    @Override
    public RestRes analysFeatures(Integer id,Integer type) {
        DataOrthoEntity dataOrthoEntity = dataOrthoService.getById(id);
        String dataTimeFormater = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        String tifUrl = dataOrthoEntity.getTifUrl();
        StoreConfig store = geoaiUosProperties.getStore();
        String tifRelUrl = tifUrl != null ? store.getOriginRelPath() + tifUrl.substring(store.getOriginPath().length()) : " ";
        String jsonRelUrl = tifRelUrl.substring(0,tifRelUrl.lastIndexOf("."))+"_"+ dataTimeFormater +".geojson";
        if (ToolUtil.isNotEmpty(tifUrl)) {
            String command = "cd /usr/anaconda3/bin && source activate mm_cd && "+"cd /mnt/water/code/inference_pack && "+"python3 do_inference_image.py -i "+tifRelUrl+" -t /mnt/water/data/temp -o "+jsonRelUrl+" -p 8";
            sshHelper.exec(command);
        }
        String jsonUrl = tifUrl.substring(0,tifUrl.lastIndexOf("."))+"_" + dataTimeFormater + ".geojson";
        DataOrthoJsonEntity dataOrthoJsonEntity = new DataOrthoJsonEntity();
        dataOrthoJsonEntity.setOrthoId(dataOrthoEntity.getId());
        dataOrthoJsonEntity.setJsonUrl(jsonUrl);
        dataOrthoJsonService.save(dataOrthoJsonEntity);
        return RestRes.ok();
    }

    @Override
    public RestRes analysWater(Integer id) {
        DataMultispectralEntity dataMultispectralEntity = dataMultispectralService.getById(id);
        String dataTimeFormater = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        String tifUrl = dataMultispectralEntity.getTifUrl();
        StoreConfig store = geoaiUosProperties.getStore();
        String tifRelUrl = tifUrl != null ? store.getOriginRelPath() + tifUrl.substring(store.getOriginPath().length()) : " ";
        String directorRelUrl = tifRelUrl.substring(0,tifRelUrl.lastIndexOf("/")+1)+ dataTimeFormater;
        if (ToolUtil.isNotEmpty(tifUrl)) {
            String command = "cd /usr/anaconda3/bin && source activate mm_cd && "+"python /mnt/water/ortho/water_quality.py -i "+tifRelUrl+" -d "+directorRelUrl + " && cd "+directorRelUrl
//                    + " 转颜色"
                    + " && python3 /usr/bin/GrayToColorImage.py nh4.tif nh4copy.tif"
                    + " && gdal2tiles.py -p mercator -z 0-15 -r average nh4copy.tif nh4"
                    + " && python3 /usr/bin/GrayToColorImage.py  od.tif odcopy.tif"
                    + " && gdal2tiles.py -p mercator -z 0-15 -r average odcopy.tif od"
                    + " && python3 /usr/bin/GrayToColorImage.py  ss.tif sscopy.tif"
                    + " && gdal2tiles.py -p mercator -z 0-15 -r average sscopy.tif ss"
                    + " && python3 /usr/bin/GrayToColorImage.py  tn.tif tncopy.tif"
                    + " && gdal2tiles.py -p mercator -z 0-15 -r average tncopy.tif tn"
                    + " && python3 /usr/bin/GrayToColorImage.py  tp.tif tpcopy.tif"
                    + " && gdal2tiles.py -p mercator -z 0-15 -r average tpcopy.tif tp";
            sshHelper.exec(command);
        }
        String directorUrl = tifUrl.substring(0,tifUrl.lastIndexOf("/")+1)+ dataTimeFormater;
        DataMultispectralDirectorEntity dataMultispectralDirectorEntity = new DataMultispectralDirectorEntity();
        dataMultispectralDirectorEntity.setMultispectralId(dataMultispectralEntity.getId());
        dataMultispectralDirectorEntity.setDirectorUrl(directorUrl);
        dataMultispectralDirectorService.save(dataMultispectralDirectorEntity);
        return RestRes.ok();
    }

    @Override
    public RestRes analysTest() {
        String command = "cd /usr/anaconda3/bin && source activate mm_cd && "+"cd /mnt/water/code/inference_pack && "+"python3 do_inference_image.py -i /mnt/water/data/gf2_gz_nrg/E113D3_N22D9_20191227_GF2_DOM_4_fus/E113D3_N22D9_20191227_GF2_DOM_4_fus_nrg.tif -t /mnt/water/data/temp -o /mnt/water/data/E113D3_N22D9_20191227_GF2_DOM_4_fus.shp -p 8";
        String exec = sshHelper.exec(command);
        log.info("结果:"+exec);
        return RestRes.ok();
    }

    @Override
    public void downLoadWater(Integer id, HttpServletResponse response) {
        DataMultispectralDirectorEntity dataMultispectralDirectorEntity = dataMultispectralDirectorService.getById(id);
        String directorUrl = dataMultispectralDirectorEntity.getDirectorUrl();
        String name = directorUrl.substring(directorUrl.lastIndexOf("/") + 1);
        //DownLoadZipUtil.downloadZip(name, fileUrl, response);
    }

//    @Override
//    public void delFeatures(Integer id) {
//        DataOrthoJsonEntity dataOrthoJsonEntity = dataOrthoJsonService.getById(id);
//        String jsonUrl = dataOrthoJsonEntity.getJsonUrl();
//        dataOrthoJsonService.removeById(id);
//        File file = new File(jsonUrl);
//        if (file.isFile() && file.exists()) {
//            file.delete();
//        }
//    }
//
//    @Override
//    public void delWater(Integer id) {
//        DataMultispectralDirectorEntity dataMultispectralDirectorEntity = dataMultispectralDirectorService.getById(id);
//        String directorUrl = dataMultispectralDirectorEntity.getDirectorUrl();
//        dataMultispectralDirectorService.removeById(id);
//        File file = new File(directorUrl);
//        if (file.isFile() && file.exists()) {
//            file.delete();
//        }
//    }

//    private RestRes AiTifToKml(String rootPath, String folderName) throws Exception {
//        File dir = new File(rootPath);
//        String[] tifArray = dir.list();
//        List<String> list1 = Arrays.asList(tifArray);
//        int tifSize = tifArray.length;
////            int totalTifNum = tifSize;
//        for (int i = 1; i < tifSize + 1; i++) {
//            String tifRelUrl = rootPath + "/" + tifArray[i - 1];
//            if (!(tifRelUrl.endsWith("tif") || tifRelUrl.endsWith("tiff"))) {
////                        totalTifNum--;
////                        continue;
//                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RECOGNITION_FAILED_THERE_ARE_NONTIF_FILES_IN_THE_DIRECTORY.getContent()));
//            }
//            File file = new File(tifRelUrl);
//            String str = "";
//            String sshUrl = geoaiUosProperties.getAi().getSshUrl();
//            synchronized (this) {
//                // 上传tiff到AI服务器，并进行AI识别
//                if (isUsedHCM()) {
//                    String encode = URLEncoder.encode(tifRelUrl, "utf-8");
//                    str = SSHHelper.doPostGateWay(sshUrl + "/record/construction?current=" + i + "&total=" + tifSize + "&path=" + folderName+"&filePath="+ encode);
//                }else {
//                    str = SSHHelper.doPostGateWay(file, sshUrl + "/record/construction?current=" + i + "&total=" + tifSize + "&path=" + folderName);
//                }
//                if (str.contains("haNum")) {
//                    String hasNum = str.substring(str.lastIndexOf(":") + 1);
//                    log.info("i: " + i + "; haNum: " + hasNum);
//                    i = Integer.parseInt(hasNum);
//                } else if (!str.contains("ok")) {
//                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_IDENTIFICATION_ERROR.getContent()));
//                }
//            }
//        }
//        return null;
//    }

//    /**
//     *  此方法弃用在v2.2.3后弃用，若后续版本无异常则可以删除，若有异常则去掉该注释并写明方法
//     */
//    @Deprecated
//    private void dealKmlFile(Integer afterFileId, List<IllegalPointEntity> illegalPointEntityList, String kmlUrl) {
////        String kmlUrl = "D:\\data\\正射\\test.kml";
//        if (ToolUtil.isNotEmpty(kmlUrl)) {
//
//            try {
//                @Cleanup
//                InputStreamReader inputStream = new InputStreamReader(new FileInputStream(kmlUrl));
//                BufferedReader bufferedReader = new BufferedReader(inputStream);
//                String line = "";
//                log.info("开始处理kml文件...");
//                while ((line = bufferedReader.readLine()) != null) {
//                    if (line.contains("coordinates")) {
//                        // 获取到经纬度
//                        String rule = "(?<=<coordinates>)[^<]+";
//                        Matcher m = Pattern.compile(rule).matcher(line);
//                        while (m.find()) {
//                            String pointInfo = m.group(0);
//                            String[] dataSplit = pointInfo.split(" ");
//
//                            List<Double> lonList = new ArrayList();
//                            List<Double> latList = new ArrayList();
//                            for (String info : dataSplit) {
//                                String[] latAndLng = info.split(",");
//                                lonList.add(Double.valueOf(latAndLng[0]));
//                                latList.add(Double.valueOf(latAndLng[1]));
//                            }
//
//                            Double minLon = Collections.min(lonList);
//                            Double maxLon = Collections.max(lonList);
//                            Double minLat = Collections.min(latList);
//                            Double maxLat = Collections.min(latList);
//
//
//                            Double lon = (minLon + maxLon) / 2;
//                            Double lat = (minLat + maxLat) / 2;
////                        Double area = (maxLon-minLon) * (maxLat-minLat);
//
//                            IllegalPointEntity illegalPointEntity = new IllegalPointEntity();
//                            illegalPointEntity.setBeforeFileId(0);
//                            illegalPointEntity.setAfterFileId(afterFileId);
//                            illegalPointEntity.setName("疑似违建");
//                            illegalPointEntity.setLongitude(lon);
//                            illegalPointEntity.setLatitude(lat);
////                            illegalPointEntity.setPointInfo(pointInfo);
//                            illegalPointEntity.setType(2);
//                            illegalPointEntity.setSource(2);
//                            illegalPointEntity.setProblemSource(24);
//                            illegalPointEntityList.add(illegalPointEntity);
//                        }
//                    }
//                }
//
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        }
//    }


    @Transactional(rollbackFor = Exception.class)
    public void saveResult(Integer beforeFileId, Integer afterFileId, List<IllegalPointEntity> illegalPointEntityList) {
        Integer dataType = illegalPointEntityList.get(0).getSource();
        Integer state = null;
        if (DataTypeEnum.ORTHO.getValue() == dataType) {
            DataOrthoEntity dataOrthoEntity = dataOrthoService.getOne(new QueryWrapper<DataOrthoEntity>().eq("id", afterFileId));
            state = dataOrthoEntity.getState();

        } else if (DataTypeEnum.POINTCLOUD.getValue() == dataType) {
            DataPointCloudEntity dataPointCloudEntity = dataPointCloudService.getOne(new QueryWrapper<DataPointCloudEntity>().eq("id", afterFileId));
            state = dataPointCloudEntity.getState();

        }
        // 删除旧数据，保存新数据
        saveDataToDB(beforeFileId, afterFileId, illegalPointEntityList, state);
    }

    /**
     * 删除旧数据，保存新数据
     *
     * @param beforeFileId
     * @param afterFileId
     * @param illegalPointEntityList
     * @param state
     */
    private void saveDataToDB(Integer beforeFileId, Integer afterFileId, List<IllegalPointEntity> illegalPointEntityList, Integer state) {
        if (state == 1) {
            List<Integer> illegalIdList = this.list(new QueryWrapper<IllegalPointEntity>().eq("before_file_id", beforeFileId).eq("after_file_id", afterFileId)).stream().map(IllegalPointEntity::getId).collect(Collectors.toList());
            if (ToolUtil.isNotEmpty(illegalIdList)) {
                // 删除这2个点云已分析过的违建点
                this.lambdaUpdate().set(IllegalPointEntity::getDeleted, 1).in(IllegalPointEntity::getId, illegalIdList).update();
                // 删除这2个点云已存入dataProblem表的违建点
                dataProblemService.lambdaUpdate().set(DataProblemEntity::getDeleted, 1).in(DataProblemEntity::getDataId, illegalIdList).update();
            }
            // 保存新的违建点
            for (IllegalPointEntity illegalPointEntity : illegalPointEntityList) {
                this.save(illegalPointEntity);
                setDataProblem(illegalPointEntity);
            }
            log.info("智能分析保存入库");
        } else {
            log.info("已被终止");
        }
    }

    /**
     * 保存违建点到问题表
     *
     * @param illegalPointEntity
     */
    public void setDataProblem(IllegalPointEntity illegalPointEntity) {
        Integer dataType = illegalPointEntity.getSource();
        Integer problemSource = illegalPointEntity.getProblemSource();
//        Integer problemSource = DataTypeEnum.ORTHO.getValue() == dataType ? 17 : 8;
        DataProblemEntity dataProblemEntity = dataProblemService.getOne(new QueryWrapper<DataProblemEntity>().eq("data_id", illegalPointEntity.getId())
                .eq("problem_source", problemSource).eq("deleted", 0));
        if (dataProblemEntity == null) {
            dataProblemEntity = new DataProblemEntity();
            dataProblemEntity.setDataId(illegalPointEntity.getId().longValue());
            dataProblemEntity.setProblemStatus(ProblemConstant.ProblemStatus.HAS_PROBLEM);
            dataProblemEntity.setLat(illegalPointEntity.getLatitude());
            dataProblemEntity.setLng(illegalPointEntity.getLongitude());
            dataProblemEntity.setDataSource(dataType);
            dataProblemEntity.setProblemName(illegalPointEntity.getName());
            dataProblemEntity.setFlag(1);

            if (DataTypeEnum.ORTHO.getValue() == dataType) {
                DataOrthoEntity dataOrthoEntity = dataOrthoService.getById(illegalPointEntity.getAfterFileId());
                if (dataOrthoEntity != null) {
                    dataProblemEntity.setMissionRecordId(dataOrthoEntity.getMissionRecordsId());
                    dataProblemEntity.setMissionRecordTime(dataOrthoEntity.getMissionRecordTime());
                    dataProblemEntity.setTaskId(dataOrthoEntity.getTaskId());
                    dataProblemEntity.setTaskName(dataOrthoEntity.getTaskName());
                    dataProblemEntity.setTagId(dataOrthoEntity.getTagId());
                    dataProblemEntity.setProblemSource(problemSource);
//                    LocalDateTime orthoCreateTime = fileInfoService.getOne(new QueryWrapper<FileInfoEntity>().eq("id", dataOrthoEntity.getFileInfoId())).getCreateTime();
                    dataProblemEntity.setPhotoTime(dataOrthoEntity.getMissionRecordTime());
                }
            } else if (DataTypeEnum.POINTCLOUD.getValue() == dataType) {
                DataPointCloudEntity dataPointCloudEntity = dataPointCloudService.getById(illegalPointEntity.getAfterFileId());
                if (dataPointCloudEntity != null) {
                    dataProblemEntity.setMissionRecordId(dataPointCloudEntity.getMissionRecordsId());
                    dataProblemEntity.setMissionRecordTime(dataPointCloudEntity.getMissionRecordTime());
                    dataProblemEntity.setTaskId(dataPointCloudEntity.getTaskId());
                    dataProblemEntity.setTaskName(dataPointCloudEntity.getTaskName());
                    dataProblemEntity.setTagId(dataPointCloudEntity.getTagId());
                    dataProblemEntity.setProblemSource(8);
//                    LocalDateTime pointCouldCreateTime = fileInfoService.getOne(new QueryWrapper<FileInfoEntity>().eq("id", dataPointCloudEntity.getFileInfoId())).getCreateTime();
                    dataProblemEntity.setPhotoTime(dataPointCloudEntity.getMissionRecordTime());
                }
            }

        } else {
            if (ToolUtil.isNotEmpty(illegalPointEntity.getPhotoUrl())) {
                dataProblemEntity.setProblemUrl(illegalPointEntity.getPhotoUrl());
            }
        }
        dataProblemService.saveOrUpdate(dataProblemEntity);
    }

}
