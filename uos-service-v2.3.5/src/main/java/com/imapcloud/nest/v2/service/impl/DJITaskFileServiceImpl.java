package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.AirLineEntity;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.model.SysTaskTagEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.service.AirLineService;
import com.imapcloud.nest.service.MissionService;
import com.imapcloud.nest.service.SysTaskTagService;
import com.imapcloud.nest.service.TaskService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.dao.entity.TaskFileEntity;
import com.imapcloud.nest.v2.dao.mapper.TaskFileMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileStorageOutDO;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.nest.v2.service.AirLineProxy;
import com.imapcloud.nest.v2.service.DJITaskFileService;
import com.imapcloud.nest.v2.service.converter.DJITaskFileConverter;
import com.imapcloud.nest.v2.service.dto.in.DJITaskFileInDTO;
import com.imapcloud.nest.v2.service.dto.out.DJITaskOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJITaskFileServiceImpl.java
 * @Description DJITaskFileServiceImpl
 * @createTime 2022年10月19日 15:53:00
 */
@Slf4j
@Service
public class DJITaskFileServiceImpl implements DJITaskFileService {

    @Resource
    private TaskFileMapper taskFileMapper;

    @Resource
    private TaskService taskService;

    @Resource
    private AirLineService airLineService;

    @Resource
    private MissionService missionService;

    @Resource
    private SysTaskTagService sysTaskTagService;

    @Resource
    private UploadManager uploadManager;

    @Resource
    private FileManager fileManager;

    /**
     * 本地大疆航线上传使用接口
     *
     * @param fileName
     * @param fileMD5
     * @param file
     * @return
     */
    @Override
    public DJITaskOutDTO.DJITaskFileInfoOutDTO uploadFile(String fileName, String fileMD5, MultipartFile file) {
        DJITaskOutDTO.DJITaskFileInfoOutDTO djiTaskFileInfoOutDTO = new DJITaskOutDTO.DJITaskFileInfoOutDTO();
        //解析航线包为newJson
        String name = file.getOriginalFilename();
        String djiAirLine = null;
        if (name.endsWith(".kmz")) {
            djiAirLine = new AirLineProxy(DjiAirLineServiceImpl.class)
                    .proxyTransformKmzToJsonMainImpl(file);
        }
        if (name.endsWith(".kml")) {
            djiAirLine = new AirLineProxy(DjiOldAirLineServiceImpl.class)
                    .proxyTransformKmzToJsonMainImpl(file);
        }
        if (Objects.isNull(djiAirLine)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DJI_PARSE_ERR.getContent()));
        }

        // 上传文件
        CommonFileInDO commonFileInDO = new CommonFileInDO();
        try {
            commonFileInDO.setFileName(name);
            commonFileInDO.setInputStream(file.getInputStream());
        } catch (IOException e) {
            log.error("获取文件流失败", e);
            // FIXME 定义错误码，无法读取{0}文件
            throw new BizException("无法读取大疆航线文件");
        }
        Optional<FileStorageOutDO> optional = uploadManager.uploadFile(commonFileInDO);
        if(!optional.isPresent()){
            // FIXME 定义错误码，{0}文件上传失败
            throw new BizException("大疆航线文件上传失败");
        }

        DJITaskFileInDTO.DJITaskFileAddInDTO addInDTO = new DJITaskFileInDTO.DJITaskFileAddInDTO();
        addInDTO.setFileUrl(optional.get().getStoragePath() + SymbolConstants.SLASH_LEFT + optional.get().getFilename());
        try {
            addInDTO.setFileMd5(MD5.create().digestHex(file.getInputStream()));
        } catch (IOException e) {
            log.error("uploadFile -> 获取MD5失败", e);
        }
        addInDTO.setFileName(file.getOriginalFilename());
        addInDTO.setTaskId("");
        addInDTO.setMissionId("");
        String taskFileId = this.insertTaskFile(addInDTO);
        djiTaskFileInfoOutDTO.setTaskFileId(taskFileId);
        djiTaskFileInfoOutDTO.setDjiAirLine(djiAirLine);
        return djiTaskFileInfoOutDTO;
    }

    /**
     * 新增
     *
     * @param taskFileAddInDTO
     * @author double
     * @date 2022/10/24
     */
    @Override
    public String insertTaskFile(DJITaskFileInDTO.DJITaskFileAddInDTO taskFileAddInDTO) {
        TaskFileEntity taskFileEntity = DJITaskFileConverter.INSTANCES.convert(taskFileAddInDTO);
        this.taskFileMapper.insertTaskFile(taskFileEntity);
        return taskFileEntity.getTaskFileId();
    }

    /**
     * 刪除
     *
     * @param taskFileId
     * @author double
     * @date 2022/10/24
     */
    @Override
    public int deleteTaskFile(String taskFileId) {
        if (StringUtils.isEmpty(taskFileId)) {
            return 0;
        }
        return this.taskFileMapper.deleteTaskFile(taskFileId);
    }

    /**
     * 刪除
     *
     * @param taskId
     * @param missionIds
     * @author double
     * @date 2022/10/24
     */
    @Override
    public int deleteByTaskIdMissionIds(String taskId, List<String> missionIds) {
        if (StringUtils.isEmpty(taskId) && CollectionUtil.isEmpty(missionIds)) {
            return 0;
        }
        return this.taskFileMapper.deleteByTaskIdMissionIds(taskId, missionIds);
    }

    /**
     * 更新
     *
     * @param taskFileUpdateInDTO
     * @author double
     * @date 2022/10/24
     */
    @Override
    public String updateTaskFile(DJITaskFileInDTO.DJITaskFileUpdateInDTO taskFileUpdateInDTO) {
        TaskFileEntity taskFileEntity = DJITaskFileConverter.INSTANCES.convert(taskFileUpdateInDTO);
        this.taskFileMapper.updateTaskFile(taskFileEntity);
        return taskFileEntity.getTaskFileId();
    }

    /**
     * 查询数据
     *
     * @param queryInDTO
     * @return
     */
    @Override
    public DJITaskOutDTO.DJITaskFileQueryOutDTO queryOutDTO(DJITaskFileInDTO.DJITaskFileQueryInDTO queryInDTO) {
        DJITaskOutDTO.DJITaskFileQueryOutDTO outDTO = new DJITaskOutDTO.DJITaskFileQueryOutDTO();
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(TaskFileEntity.class)
                .eq(StringUtils.hasText(queryInDTO.getTaskId()), TaskFileEntity::getTaskId, queryInDTO.getTaskId())
                .eq(StringUtils.hasText(queryInDTO.getTaskFileId()), TaskFileEntity::getTaskFileId, queryInDTO.getTaskFileId())
                .eq(StringUtils.hasText(queryInDTO.getMissionId()), TaskFileEntity::getMissionId, queryInDTO.getMissionId())
                .orderByDesc(TaskFileEntity::getId);
        TaskFileEntity taskFileEntity = (TaskFileEntity) this.taskFileMapper.selectList(queryWrapper)
                .stream()
                .findFirst()
                .orElseGet(() -> new TaskFileEntity());
        outDTO = DJITaskFileConverter.INSTANCES.convert(taskFileEntity);
        if (log.isDebugEnabled()) {
            log.debug("【queryOutDTO】查询的航线包关联信息为：{}", outDTO.toString());
        }
        return outDTO;
    }

    /**
     * 查询dji航线
     *
     * @param taskId
     * @return
     */
    @Override
    public DJITaskOutDTO.DJITaskInfoOutDTO queryDJIAirLine(String taskId) {
        DJITaskOutDTO.DJITaskInfoOutDTO djiTaskInfoOutDTO = new DJITaskOutDTO.DJITaskInfoOutDTO();
        TaskEntity taskEntity = this.taskService.getById(taskId);
        List<MissionEntity> missionEntities = this.missionService.listMissionByTaskId(Integer.parseInt(taskId));
        if (CollectionUtil.isEmpty(missionEntities)) {
            log.info("根据taskId：{},查询不到对应的mission数据", taskId);
            return djiTaskInfoOutDTO;
        }
        LambdaQueryWrapper wrapper = Wrappers.lambdaQuery(AirLineEntity.class)
                .in(AirLineEntity::getId, missionEntities.stream().map(MissionEntity::getAirLineId).collect(Collectors.toList()))
                .select(AirLineEntity::getId, AirLineEntity::getDjiWaypoints);
        List<AirLineEntity> airLineEntities = this.airLineService.list(wrapper);
        Map<Integer, String> airLineIdToDjiAirLineMap = airLineEntities.stream()
                .collect(Collectors.toMap(AirLineEntity::getId, AirLineEntity::getDjiWaypoints, (o, n) -> n));
        SysTaskTagEntity sysTaskTagEntity = this.sysTaskTagService.getOne(new QueryWrapper<SysTaskTagEntity>().eq("task_id", taskId));
        djiTaskInfoOutDTO.setNestId(taskEntity.getBaseNestId());
        djiTaskInfoOutDTO.setTagId(sysTaskTagEntity.getTagId());
        djiTaskInfoOutDTO.setShowInfo(airLineEntities.get(0).getShowInfo());
        //适配多架次
        Map<Integer, String> djiAirLineMap = new HashMap<>();
        //TODO 这里djiAirLineMap用成了missionId
        missionEntities.stream().forEach(mission -> {
            djiAirLineMap.put(mission.getId(), airLineIdToDjiAirLineMap.get(mission.getAirLineId()));
        });
        djiTaskInfoOutDTO.setDjiAirLineMap(djiAirLineMap);
        return djiTaskInfoOutDTO;
    }

    /**
     * 物理删除多余kmz包
     *
     * @return
     */
    @Override
    public boolean physicsDeleteKmz() {
        List<TaskFileEntity> taskFileEntities = this.taskFileMapper.getAllByDeletedTaskFile();
        List<String> pathList = taskFileEntities.stream().map(TaskFileEntity::getFileUrl).collect(Collectors.toList());
        List<String> taskFileIds = taskFileEntities.stream().map(TaskFileEntity::getTaskFileId).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(taskFileIds)){
            fileManager.deleteFiles(pathList);
//        MinIoUnit.rmObjects(pathList);
            LambdaUpdateWrapper<TaskFileEntity> lambdaQueryWrapper = Wrappers.lambdaUpdate(TaskFileEntity.class)
                    .set(TaskFileEntity::getFileMd5,"")
                    .in(TaskFileEntity::getTaskFileId , taskFileIds);
            this.taskFileMapper.update(null,lambdaQueryWrapper);
        }
        return true;
    }

    @Override
    public String analysisKMZ(MultipartFile file) {
        //解析航线包为newJson
        String name = file.getOriginalFilename();
        String djiAirLine = null;
        if(StringUtils.hasText(name) && name.endsWith(".kmz")) {
            djiAirLine = new AirLineProxy(G900AirLineServiceImpl.class)
                    .proxyTransformKmzToJsonMainImpl(file);
        }
        return djiAirLine;
    }
    @Override
    public void updateMissionInfo(Integer missionId, String taskFileId, Integer taskId) {
        LambdaUpdateWrapper<TaskFileEntity> updateWrapper = Wrappers.lambdaUpdate(TaskFileEntity.class)
                .eq(TaskFileEntity::getTaskFileId, taskFileId)
                .eq(TaskFileEntity::getDeleted, false)
                .set(TaskFileEntity::getTaskId, taskId)
                .set(TaskFileEntity::getMissionId, missionId);

        taskFileMapper.update(null, updateWrapper);
    }
}
