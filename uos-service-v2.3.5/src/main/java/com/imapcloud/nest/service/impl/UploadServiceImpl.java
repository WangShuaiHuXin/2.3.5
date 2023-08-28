package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.enums.UploadTypeEnum;
import com.imapcloud.nest.mapper.UploadMapper;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.UploadEntity;
import com.imapcloud.nest.pojo.dto.UploadDto;
import com.imapcloud.nest.service.MissionRecordsService;
import com.imapcloud.nest.service.UploadService;
import com.imapcloud.nest.utils.MinIoUnit;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.properties.StoreConfig;
import com.imapcloud.nest.v2.service.RocketmqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hc
 *
 * @date 2021/11/04
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UploadServiceImpl extends ServiceImpl<UploadMapper, UploadEntity> implements UploadService {
    /**
     * 上传成功
     */
    private final Integer UPLOAD_SUCCESS = 0;
    /**
     * 部分上传
     */
    private final Integer UPLOAD_PART = 1;
    /**
     * 需要合并
     */
    private final String NEED_MERGE = "0";
    /**
     * 不需要合并
     */
    private final String NO_MERGE = "1";
    /**
     * 不需要合并，抛错
     */
    private final String NO_MERGE_ERR = "2";

    @Autowired
    private MissionRecordsService missionRecordsService;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private UploadMapper uploadMapper;

    @Resource
    private RocketmqService rocketmqService;

    /**
     * 返回状态0,1,2
     * 0->分片为1的返回状态
     * 1->部分分片上传的返回状态
     * 2->全部分片上传了的返回状态
     * 3->秒传的返回状态
     */
    private final Integer resposeState0 = 0;
    private final Integer resposeState1 = 1;
    private final Integer resposeState2 = 2;
    private final Integer resposeState3 = 3;



//    @Override
//    public RestRes initMultiPartUpload(UploadEntity uploadEntity){
//        String path = getTotalPath(uploadEntity);
//        //上传文件时，需要过滤非对应格式的文件
//        //上传文件时，过滤大小超过5G的文件
//        this.applicationContext.publishEvent(new UploadFileBeforeEvent(uploadEntity));
//        if(uploadEntity.getChunkCount()==1){
//            //获取绝对路径
//            String fileName = uploadEntity.getFileName();
//            String changeName = MinIoUnit.renameFile(fileName);
//            path += changeName;
//            String uploadUrl = MinIoUnit.createUploadUrl(uploadEntity.getBucketName(), path, 60);
//            Map objectMap = new HashMap();
//            objectMap.put("uploadUrl",uploadUrl);
//            objectMap.put("filePath", geoaiUosProperties.getStore().getOriginPath() + path);
//            objectMap.put("fileName",fileName);
//            //保存文件
//            Map resMap = new HashMap();
//            resMap.put("object",objectMap);
//            resMap.put("state",resposeState0);
//            return RestRes.ok(resMap);
//        }
//       //校验文件md5，该文件是否上传过
//        List<UploadEntity> uploadEntitys = this.lambdaQuery().eq(UploadEntity::getFileMd5, uploadEntity.getFileMd5()).list();
//        path += uploadEntity.getFileMd5();
//        if(ToolUtil.isNotEmpty(uploadEntitys)){
//            //UploadDto mysqlFileData = mysql.get(uploadDto.getFileMd5());
//            UploadEntity mysqlFileData = uploadEntitys.get(0);
//            //秒传
//            if(UPLOAD_SUCCESS.equals(mysqlFileData.getUploadStatus())){
//                Map resMap = new HashMap();
//                resMap.put("state",resposeState3);
//                resMap.put("object",mysqlFileData);
//                return RestRes.ok(resMap);
//            }
//            //续传
//            //获取到该文件已上传分片
//            Map<Integer,String> okChunkMap = MinIoUnit.mapChunkObjectNames(uploadEntity.getBucketName(),path);
//            List<UploadDto> chunkUploadUrls = new ArrayList<>();
//            if (okChunkMap!=null&&okChunkMap.size() > 0){
//                for (int i = 1; i <= uploadEntity.getChunkCount(); i++) {
//                    //判断当前分片是否已经上传过了
//                    if(!okChunkMap.containsKey(i)){
//                        //生成分片上传url
//                        UploadDto url = new UploadDto();
//                        url.setPartNumber(i);
//                        url.setUploadUrl(MinIoUnit.createUploadChunkUrl(uploadEntity.getBucketName(),path,i));
//                        chunkUploadUrls.add(url);
//                    }
//                }
//                if (chunkUploadUrls.size() == 0){
//                    Map resMap = new HashMap();
//                    resMap.put("state",resposeState2);
//                    return RestRes.ok(resMap);
//                }
//                Map resMap = new HashMap();
//                resMap.put("state",resposeState1);
//                resMap.put("list",chunkUploadUrls);
//                return RestRes.ok(resMap);
//            }
//        }
//        //初次上传和已有文件信息但未上传任何分片的情况下则直接生成所有上传url
//        List<String> uploadUrls = MinIoUnit.createUploadChunkUrlList(uploadEntity.getBucketName(),path,uploadEntity.getChunkCount());
//        List<UploadDto> chunkUploadUrls = new ArrayList<>();
//        for (int i = 1; i <= uploadUrls.size(); i++) {
//            UploadDto url = new UploadDto();
//            url.setPartNumber(i);
//            url.setUploadUrl(MinIoUnit.createUploadChunkUrl(uploadEntity.getBucketName(),path,i));
//            chunkUploadUrls.add(url);
//        }
//        //向数据库中记录该文件的上传信息
//        uploadEntity.setUploadStatus(UPLOAD_PART);
//
//        //mysql.put(uploadDto.getFileMd5(),uploadDto);
//        if(ToolUtil.isEmpty(uploadEntitys)) {
//            save(uploadEntity);
//        }
//        Map resMap = new HashMap();
//        resMap.put("state",resposeState1);
//        resMap.put("list",chunkUploadUrls);
//        return RestRes.ok(resMap);
//    }

    private String getTotalPath(UploadEntity uploadDto) {
        String path = UploadTypeEnum.enmuMap.get(uploadDto.getCode());
        if (uploadDto.getUnitId()!=null) {
            path += uploadDto.getUnitId() + "/";
        }
        if (uploadDto.getMissionId()!=null) {
            path += uploadDto.getMissionId() + "/";
        }
        if (uploadDto.getMissionRecordsId()!=null) {
            path += uploadDto.getMissionRecordsId() + "/";
        }
        return path;
    }

//    @Override
//    public RestRes mergeMultipartUpload(UploadEntity uploadEntity){
//        StoreConfig store = geoaiUosProperties.getStore();
//        String path = store.getOriginPath() + getTotalPath(uploadEntity);
//        String chunckPath = path + uploadEntity.getFileMd5()+"/";
//        //根据md5获取所有分片文件名称(minio的文件名称 = 文件path)
//        //List<String> chunks = MinIoUnit.listObjectNames(uploadEntity.getBucketName(),chunckPath);
//        //MinIoUnit.composeObject(uploadEntity.getBucketName(),chunks,mergePath)
//
//        //自定义文件名称
//        String fileName = uploadEntity.getFileName();
//        String changeName = MinIoUnit.renameFile(fileName);
//        String mergePath = path + changeName;
//
//        //合并文件
//        if(merge(chunckPath,mergePath)){
//
//            //获取文件访问外链(1小时过期)
//            String url = MinIoUnit.getObjectUrl(uploadEntity.getBucketName(),fileName,60);
//            //获取数据库里记录的文件信息，修改数据并返回文件信息
//            //mysql.put(uploadDto.getFileMd5(), dbData);
//            this.lambdaUpdate().eq(UploadEntity::getFileMd5,uploadEntity.getFileMd5())
//                    .set(UploadEntity::getUploadStatus,UPLOAD_SUCCESS)
//                    .set(UploadEntity::getFilePath,mergePath)
//                    .set(UploadEntity::getFileName,fileName)
//                    .update();
//            List<UploadEntity> uploadDtos = this.lambdaQuery().eq(UploadEntity::getFileMd5, uploadEntity.getFileMd5()).list();
//            UploadEntity dbData = uploadDtos.get(0);
//            Map resMap = new HashMap();
//            resMap.put("object",dbData);
//            return RestRes.ok(resMap);
//        }
//        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_MERGE_FILE.getContent()));
//    }

    private boolean merge(String folder, String file) {
        try {
            //不存在的话，进行合并
            Path filepath = Paths.get(file);
            File sysFile = filepath.toFile();
            if (sysFile.exists()) {
                sysFile.delete();
            }
            Files.createFile(filepath);
            List<String> objectList = Lists.newArrayList();
            List<Path> collect = Files.list(Paths.get(folder))
                    .filter(path -> path.getFileName().toString().contains(".chunk"))
                    .sorted((o1, o2) -> {
                        String p1 = o1.getFileName().toString();
                        String p2 = o2.getFileName().toString();
                        int i1 = p1.lastIndexOf(".");
                        int i2 = p2.lastIndexOf(".");
                        return Integer.valueOf(p1.substring(0, i1)).compareTo(Integer.valueOf(p2.substring(0, i2)));
                    }).collect(Collectors.toList());
            for (Path path : collect) {
                //以追加的形式写入文件
                Files.write(filepath, Files.readAllBytes(path), StandardOpenOption.APPEND);
                //合并后删除该块
                String url = folder + path.getFileName().toString();
                log.info("#PathUploadFileUtils.merge# url={}", url);
                objectList.add(url);
            }
            log.info("文件合并成功");
            rocketmqService.sendFileDelete(objectList);
            return true;
        } catch (IOException e) {
            log.error("合并失败：{},{}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public RestRes initMultiPartUploadCps(UploadEntity uploadEntity){
        this.consumerUploadData(uploadEntity);
        String path = getTotalPath(uploadEntity);
        if(uploadEntity.getChunkCount()==1){
            //获取绝对路径
            StoreConfig store = geoaiUosProperties.getStore();
            String fileName = uploadEntity.getFileName();
            String changeName = MinIoUnit.renameFile(fileName);
            path += changeName;
            String uploadUrl = MinIoUnit.createUploadUrl(uploadEntity.getBucketName(), path, 60);
            List<Map> listMap = new ArrayList<>();
            Map objectMap = new HashMap();
            objectMap.put("uploadUrl",uploadUrl);
            objectMap.put("filePath", store.getOriginPath() + path);
            objectMap.put("fileName",fileName);
            objectMap.put("partNumber",1);
            listMap.add(objectMap);
            List<UploadEntity> uploadEntities = this.lambdaQuery().eq(UploadEntity::getFileMd5, uploadEntity.getFileMd5()).list();
            if(ToolUtil.isEmpty(uploadEntities)) {
                uploadEntity.setFilePath(store.getOriginPath() + path);
                uploadEntity.setUploadStatus(UPLOAD_PART);
                save(uploadEntity);
            }
            //保存文件
            Map resMap = new HashMap();
            resMap.put("list",listMap);
            resMap.put("state",resposeState0);
            return RestRes.ok(resMap);
        }
        //校验文件md5，该文件是否上传过
        List<UploadEntity> uploadEntitys = this.lambdaQuery().eq(UploadEntity::getFileMd5, uploadEntity.getFileMd5()).list();
        path += uploadEntity.getFileMd5();
        if(ToolUtil.isNotEmpty(uploadEntitys)){
            UploadEntity mysqlFileData = uploadEntitys.get(0);
            List<UploadDto> chunkUploadUrls = new ArrayList<>();
            //秒传
            if(UPLOAD_SUCCESS.equals(mysqlFileData.getUploadStatus())){
                Map resMap = new HashMap();
                UploadDto url = new UploadDto();
//                url.setPartNumber(i);
//                url.setUploadUrl(MinIoUnit.createUploadChunkUrl(uploadEntity.getBucketName(),path,i));
                chunkUploadUrls.add(url);
                resMap.put("state",resposeState3);
//                resMap.put("object",mysqlFileData);
                resMap.put("list",chunkUploadUrls);
                return RestRes.ok(resMap);

            }
            //续传
            //获取到该文件已上传分片
            Map<Integer,String> okChunkMap = MinIoUnit.mapChunkObjectNames(uploadEntity.getBucketName(),path);
            if (okChunkMap!=null&&okChunkMap.size() > 0){
                for (int i = 1; i <= uploadEntity.getChunkCount(); i++) {
                    //判断当前分片是否已经上传过了
                    if(!okChunkMap.containsKey(i)){
                        //生成分片上传url
                        UploadDto url = new UploadDto();
                        url.setPartNumber(i);
                        url.setUploadUrl(MinIoUnit.createUploadChunkUrl(uploadEntity.getBucketName(),path,i));
                        chunkUploadUrls.add(url);
                    }
                }
                if (chunkUploadUrls.size() == 0){
                    Map resMap = new HashMap();
                    resMap.put("state",resposeState2);
                    return RestRes.ok(resMap);
                }
                Map resMap = new HashMap();
                resMap.put("state",resposeState1);
                resMap.put("list",chunkUploadUrls);
                return RestRes.ok(resMap);
            }
        }
        //初次上传和已有文件信息但未上传任何分片的情况下则直接生成所有上传url
        List<String> uploadUrls = MinIoUnit.createUploadChunkUrlList(uploadEntity.getBucketName(),path,uploadEntity.getChunkCount());
        List<UploadDto> chunkUploadUrls = new ArrayList<>();
        for (int i = 1; i <= uploadUrls.size(); i++) {
            UploadDto url = new UploadDto();
            url.setPartNumber(i);
            url.setUploadUrl(MinIoUnit.createUploadChunkUrl(uploadEntity.getBucketName(),path,i));
            chunkUploadUrls.add(url);
        }
        //向数据库中记录该文件的上传信息
        uploadEntity.setUploadStatus(UPLOAD_PART);

        //mysql.put(uploadDto.getFileMd5(),uploadDto);
        if(ToolUtil.isEmpty(uploadEntitys)) {
            save(uploadEntity);
        }
        Map resMap = new HashMap();
        resMap.put("state",resposeState1);
        resMap.put("list",chunkUploadUrls);
        return RestRes.ok(resMap);
    }

    /**
     * 针对Cps提供合并接口
     * @param uploadEntity
     * @return
     */
    @Override
    public RestRes mergeMultipartUploadCps(UploadEntity uploadEntity) {
        //初始化信息
        log.info("【mergeMultipartUploadCps】uploadEntity->{}",uploadEntity.toString());
        this.consumerUploadData(uploadEntity);
        StoreConfig store = geoaiUosProperties.getStore();
        String path = store.getOriginPath() + getTotalPath(uploadEntity);
        String chunkPath = path + uploadEntity.getFileMd5() + "/";
        //自定义文件名称
        String fileName = uploadEntity.getFileName();
        String changeName = MinIoUnit.renameFile(fileName);
        String mergePath = path + changeName;
        //判断是否需要进行合并动作
        String[] results = this.isNeedMerge(chunkPath, uploadEntity);
        switch (results[0]) {
            case NO_MERGE: {
                Map resMap = this.composeResult(uploadEntity);
                return RestRes.ok(resMap);
            }
            case NO_MERGE_ERR:
                return RestRes.err(results[1]);
            case NEED_MERGE:
                ;
            default:
                break;
        }
        //合并文件
        if (merge(chunkPath, mergePath)) {
            //获取文件访问外链(1小时过期)
            String url = MinIoUnit.getObjectUrl(uploadEntity.getBucketName(), fileName, 60);
            //获取数据库里记录的文件信息，修改数据并返回文件信息
            //mysql.put(uploadDto.getFileMd5(), dbData);
            this.lambdaUpdate().eq(UploadEntity::getFileMd5, uploadEntity.getFileMd5())
                    .set(UploadEntity::getUploadStatus, UPLOAD_SUCCESS)
                    //TODO 上线要去掉拼接
//                    .set(UploadEntity::getFilePath, "/"+mergePath)
                    .set(UploadEntity::getFilePath, mergePath)
                    .set(UploadEntity::getFileName, fileName)
                    .update();
            Map resMap = this.composeResult(uploadEntity);
            //TODO 判断是否合并后的文件可用。

            return RestRes.ok(resMap);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_MERGE_FILE.getContent()));
    }

    /**
     * 完善初始值
     * @param uploadEntity
     */
    public void consumerUploadData(UploadEntity uploadEntity) {
        log.info("UploadService-[consumerUploadData]-{}", uploadEntity);
        String execMissionID = uploadEntity.getExecMissionID();
        List<MissionRecordsEntity> missionRecordsEntityList = this.missionRecordsService.lambdaQuery().eq(MissionRecordsEntity::getExecId, uploadEntity.getExecMissionID()).list();
        if (!CollectionUtil.isEmpty(missionRecordsEntityList)) {
            MissionRecordsEntity recordsEntity = missionRecordsEntityList.get(0);
            uploadEntity.setMissionId(recordsEntity.getMissionId());
            uploadEntity.setMissionRecordsId(recordsEntity.getId());
            uploadEntity.setUnitId(recordsEntity.getOrgCode());
        }
    }

    /**
     * 判断是否需要进行合并动作
     * 0-需要合并
     * 1-不需要合并，不报错
     * 2-不需要合并，报错
     * @param chunkPath
     * @param uploadEntity
     * @return
     */
    public String[] isNeedMerge(String chunkPath,UploadEntity uploadEntity){
        String[] result = new String[2];
        String msg = "";
        result[0] = NEED_MERGE;
        result[1] = "需要进行合并";
        String md5 = uploadEntity.getFileMd5();
        //查询upload数据表数据
        List<UploadEntity> uploadEntityList = this.lambdaQuery().eq(UploadEntity::getFileMd5,md5).list();
        if(CollectionUtil.isEmpty(uploadEntityList)){
            result[0] = NO_MERGE_ERR;
            result[1] = "没有进行初始化动作";
            log.info("UploadService-[isNeedMerge]-NO_MERGE_ERR-{}",result[1]);
            return result;
        }
        //查询切片数据
        Integer chunkCount = Optional.ofNullable(uploadEntityList.get(0))
                                    .map(entity -> entity.getChunkCount())
                                    .orElseGet(() -> 0);
        if(chunkCount == 1 || chunkCount == 0 || MinIoUnit.objectExists(uploadEntityList.get(0).getFilePath())){
            result[0] = NO_MERGE;
            result[1] = "不需要进行切片动作";
            log.info("UploadService-[isNeedMerge]-NO_MERGE-{}",result[1]);
            return result;
        }

        //查询缺失切片数据
        List<Integer> lostFile = new ArrayList<>();
        try {
            List<Integer> fileNameList = Files.list(Paths.get(chunkPath))
                    .filter(path -> path.getFileName().toString().contains("chunk"))
                    .map(path1 -> path1.getFileName().toString())
                    .map(path2 ->Integer.parseInt(path2.substring(0,path2.lastIndexOf("."))))
                    .collect(Collectors.toList());
            for(int i = 1;i<=chunkCount;i++){
                if(fileNameList==null || !fileNameList.contains(i)){
                    lostFile.add(i);
                }
            }
            if(lostFile.size()>0){
                result[0] = NO_MERGE_ERR;
                result[1] = String.format("无法进行合并，缺失切片文件序号：【%s】",
                        lostFile.stream().map(index->index.toString()).collect(Collectors.joining(",")));
                log.info("UploadService-[isNeedMerge]-NO_MERGE_ERR-{}",result[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
            msg = e.getMessage();
        }finally {
            if(StringUtils.isNotEmpty(msg)){
                result[0] = NO_MERGE_ERR;
                result[1] = msg;
            }
        }
        return result;
    }

    /**
     * 拼装返回数据
     * @param uploadEntity
     * @return
     */
    public Map composeResult(UploadEntity uploadEntity){
        List<UploadEntity> uploadEntityList = this.lambdaQuery().eq(UploadEntity::getFileMd5, uploadEntity.getFileMd5()).list();
        UploadEntity dbData = uploadEntityList.get(0);
        Map resMap = new HashMap();
        resMap.put("object",dbData);
        return resMap;
    }

    @Override
    public void deleteByFilePath(String filePath) {

        if (StringUtils.isBlank(filePath)) {
            return;
        }
        uploadMapper.deleteByFilePath(filePath);
    }
}
