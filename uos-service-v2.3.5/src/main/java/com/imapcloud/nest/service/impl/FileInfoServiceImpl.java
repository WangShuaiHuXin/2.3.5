package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.common.async.PartUploadFileAsync;
import com.imapcloud.nest.mapper.FileInfoMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.FileInfoDto;
import com.imapcloud.nest.service.FileInfoService;
import com.imapcloud.nest.service.MissionRecordsService;
import com.imapcloud.nest.service.MissionService;
import com.imapcloud.nest.service.SysTaskTagService;
import com.imapcloud.nest.service.event.dataCenter.UploadFileExistCheckEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 文件实现类
 *
 * @author: zhengxd
 * @create: 2020/12/15
 **/
@Service
@Slf4j
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfoEntity> implements FileInfoService {
    @Autowired
    private PartUploadFileAsync partUploadFileAsync;
    @Autowired
    private MissionService missionService;
    @Autowired
    private MissionRecordsService missionRecordsService;
    @Autowired
    private SysTaskTagService sysTaskTagService;
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public Integer uploadSrt(String fileName,String filePath,Integer videoId) {
        FileInfoSrtEntity fileInfoSrtEntity = partUploadFileAsync.uploadSrt(filePath, fileName,videoId);
        return fileInfoSrtEntity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long save(FileInfoDto fileInfoDto) {
        Integer missionRecordsId = fileInfoDto.getMissionRecordsId();
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(missionRecordsId);
        if (missionRecordsEntity != null) {
            fileInfoDto.setMissionRecordTime(missionRecordsEntity.getCreateTime());
        }
        Integer missionId = fileInfoDto.getMissionId();
        MissionEntity missionEntity = missionService.getById(missionId);
        if (missionEntity != null) {
            fileInfoDto.setTaskId(missionEntity.getTaskId());
            fileInfoDto.setTaskName(missionEntity.getName());
            fileInfoDto.setUnitId(missionEntity.getOrgCode());
            SysTaskTagEntity sysTaskTagEntity = sysTaskTagService.getOne(new QueryWrapper<SysTaskTagEntity>().eq("task_id", missionEntity.getTaskId()).eq("deleted", 0));
            if (sysTaskTagEntity != null) {
                fileInfoDto.setTagId(sysTaskTagEntity.getTagId());
            }
        }
        if(fileInfoDto.getType()!=0||fileInfoDto.getType()!=1) {
            FileInfoEntity fileInfoEntity = new FileInfoEntity();
            fileInfoEntity.setFilePath(fileInfoDto.getFilePath());
            fileInfoEntity.setFileName(fileInfoDto.getFileName());
            fileInfoEntity.setUploadPath(fileInfoDto.getUploadPath());
            fileInfoEntity.setFileSize(fileInfoDto.getFileSize());
            this.save(fileInfoEntity);
            fileInfoDto.setFileInfoId(fileInfoEntity.getId());
        }
        Long id = null;
        //校验minIO是否存在对应文件
        this.applicationContext.publishEvent(new UploadFileExistCheckEvent(fileInfoDto));
        switch (fileInfoDto.getType()) {
            case 0:
                //图片
                id = partUploadFileAsync.savePhoto(fileInfoDto);
                break;
            case 1:
                // 视频
                id = partUploadFileAsync.saveVideo(fileInfoDto);
                break;
            case 2:
                // 正射
                // 异步 合并分片，并解压文件
                id = partUploadFileAsync.saveOrtho(fileInfoDto);
                break;
            case 3:
                // 点云
                id = partUploadFileAsync.savePointCloud(fileInfoDto);
                break;
            case 4:
                // 倾斜
                id = partUploadFileAsync.saveTitl(fileInfoDto);
                break;
            case 5:
                //矢量
                id = partUploadFileAsync.saveVector(fileInfoDto);
                break;
//            case 6:
//                // 全景
//                /*
//                 * 此方法弃用在v2.2.3后弃用，若后续版本无异常则可以删除，若有异常则去掉该注释并写明方法
//                 */
//                id = partUploadFileAsync.savePanorama(fileInfoDto);
//                break;
//            case 8:
//                // 污染网格
//                partUploadFileAsync.savePollution(fileInfoDto);
//                break;
//            case 9:
//                // 多光谱
//                /*
//                 * 此方法弃用在v2.2.3后弃用，若后续版本无异常则可以删除，若有异常则去掉该注释并写明方法
//                 */
//                id = partUploadFileAsync.saveMultispectral(fileInfoDto);
            default:
                break;
        }
        return id;
    }

//    /**
//     * 此方法弃用在v2.2.3后弃用，若后续版本无异常则可以删除，若有异常则去掉该注释并写明方法
//     */
//    @Deprecated
//    @Override
//    public void unPack(String uploadPath, String fileName, String unPackPath) {
//        partUploadFileAsync.getZipFile(uploadPath,fileName,unPackPath);
//    }

}
