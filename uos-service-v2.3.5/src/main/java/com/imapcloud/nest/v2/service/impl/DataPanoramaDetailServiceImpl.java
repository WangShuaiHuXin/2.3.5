package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.util.DateUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.NestConstant;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.service.MissionRecordsService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.utils.ZipFileUtils;
import com.imapcloud.nest.v2.dao.entity.DataPanoramaDetailEntity;
import com.imapcloud.nest.v2.dao.mapper.DataPanoramaDetailMapper;
import com.imapcloud.nest.v2.dao.po.in.DataPanoramaDetailCriteriaPO;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileStorageOutDO;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.nest.v2.service.DataPanoramaDetailService;
import com.imapcloud.nest.v2.service.DataPanoramaPointService;
import com.imapcloud.nest.v2.service.converter.DataPanoramaDetailConverter;
import com.imapcloud.nest.v2.service.dto.in.DataPanoramaDetailInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataPanoramaPointInDTO;
import com.imapcloud.nest.v2.service.dto.in.PanoramaDataDetailInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaDetailOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaPointOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaDetailServiceImpl.java
 * @Description DataPanoramaDetailServiceImpl
 * @createTime 2022年09月27日 16:35:00
 */
@Slf4j
@Service
public class DataPanoramaDetailServiceImpl implements DataPanoramaDetailService {

    @Resource
    private DataPanoramaDetailMapper dataPanoramaDetailMapper;

    @Resource
    private MissionRecordsService missionRecordsService;

    @Resource
    @Lazy
    private DataPanoramaPointService dataPanoramaPointService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private UploadManager uploadManager;

    /**
     * 新增全景明细
     *
     * @param addDetailInDTO
     * @return
     */
    @Override
    public String addDetail(DataPanoramaDetailInDTO.AddDetailInDTO addDetailInDTO) {
        log.info("addDetail: addDetailInDTO -> {} ", addDetailInDTO);
        //校验
        if(addDetailInDTO==null){
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_IN_SAVING_PANORAMA_DETAILS__THE_PANORAMA_DETAILS_DATA_CANNOT_BE_EMPTY.getContent()));
        }
        validate(addDetailInDTO);
        fillData(addDetailInDTO);
        DataPanoramaDetailEntity dataPanoramaDetailEntity = DataPanoramaDetailConverter.INSTANCES.convert(addDetailInDTO);
        //插入
        int count = this.dataPanoramaDetailMapper.insert(dataPanoramaDetailEntity);
        if(log.isDebugEnabled()){
            log.debug("addDetail: 成功插入 {} 条 ",count);
        }
        return dataPanoramaDetailEntity.getDetailId();
    }

    /**
     * 上传全景明细
     *
     * @param detailUploadInDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public DataPanoramaDetailOutDTO.QueryOutDTO uploadDetail(DataPanoramaDetailInDTO.DetailUploadInDTO detailUploadInDTO , MultipartFile fileData) {
        String zipType = "zip", panoramaPath = "";
        //2、先解压，后逐个上传
        try (InputStream inputStream = fileData.getInputStream()) {
            if (!fileData.getOriginalFilename().endsWith(zipType)) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLEASE_UPLOAD_ZIP_FORMAT_FILE.getContent()));
            }
            List<File> fileList = ZipFileUtils.extractFiles(inputStream);
            File[] files = fileList.stream()
                    .filter(f -> f.isFile() && f.getParent().endsWith(".tiles"))
                    .peek(r ->  log.info("解压全景图片文件：{}", r))
                    .toArray(File[]::new);
            Path panoramaZipPath = Files.createTempFile("panorama_", ".zip");
            ZipUtil.zip(panoramaZipPath.toFile(), false, files);
            log.info("全景文件压缩成功 ==> {}", panoramaZipPath);
            CommonFileInDO commonFileInDO = new CommonFileInDO();
            commonFileInDO.setFileName(fileData.getOriginalFilename());
            commonFileInDO.setInputStream(Files.newInputStream(panoramaZipPath));
            commonFileInDO.setAutoDecompress(true);
            Optional<FileStorageOutDO> optional = uploadManager.uploadFile(commonFileInDO);
            if(optional.isPresent()){
                panoramaPath = optional.get().getDecompressedPath();
                log.info("uploadDetail: fileName -> {} , wholePath -> {}", fileData.getOriginalFilename(), panoramaPath);
            }
        } catch (IOException e) {
            log.error("上传全景明细失败", e);
            throw new BusinessException(e.getMessage());
        }

        if (StringUtils.isEmpty(panoramaPath)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_UPLOAD_ZIP_FILE.getContent()));
        }
        //插入数据
        DataPanoramaDetailOutDTO.QueryOutDTO queryOutDTO = new DataPanoramaDetailOutDTO.QueryOutDTO();
        DataPanoramaDetailInDTO.AddDetailInDTO addDetailInDTO = new DataPanoramaDetailInDTO.AddDetailInDTO();
        addDetailInDTO.setPointId(detailUploadInDTO.getPointId());
        addDetailInDTO.setMissionRecordsId(detailUploadInDTO.getMissionRecordsId());
        addDetailInDTO.setAcquisitionTime(detailUploadInDTO.getAcquisitionTime());
        // 前端无杠会无法访问
        addDetailInDTO.setDetailUrl(panoramaPath + SymbolConstants.SLASH_LEFT);
        String detailId = this.addDetail(addDetailInDTO);
        List<DataPanoramaDetailOutDTO.QueryOutDTO> queryOutDTOList = this.queryDetail(new DataPanoramaDetailInDTO.QueryOneInDTO().setDetailId(detailId));
        if (CollectionUtil.isEmpty(queryOutDTOList)) {
            return queryOutDTO;
        }
        return queryOutDTOList.get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public DataPanoramaDetailOutDTO.QueryOutDTO savePanoramaDetail(PanoramaDataDetailInDTO detailData) {
        DataPanoramaDetailInDTO.AddDetailInDTO addDetailInDTO = new DataPanoramaDetailInDTO.AddDetailInDTO();
        addDetailInDTO.setPointId(detailData.getPointId());
        addDetailInDTO.setMissionRecordsId(detailData.getMissionRecordsId());
        addDetailInDTO.setAcquisitionTime(detailData.getAcquisitionTime());
        // 全景解压目录，/store/2023/03/AF/2358b126646f33972dcb58f570eec938.zip ==> /store/2023/03/AF/2358b126646f33972dcb58f570eec938
        String detailPath = detailData.getDetailPath();
        detailPath = detailPath.substring(0, detailPath.lastIndexOf(SymbolConstants.POINT));
        addDetailInDTO.setDetailUrl(detailPath);
        String detailId = this.addDetail(addDetailInDTO);
        List<DataPanoramaDetailOutDTO.QueryOutDTO> queryOutDTOList = this.queryDetail(new DataPanoramaDetailInDTO.QueryOneInDTO().setDetailId(detailId));
        if (CollectionUtil.isEmpty(queryOutDTOList)) {
            return new DataPanoramaDetailOutDTO.QueryOutDTO();
        }
        return queryOutDTOList.get(0);
    }

    /**
     * 校验
     * @return
     */
    public void validate(DataPanoramaDetailInDTO.AddDetailInDTO addDetailInDTO){
        if(StringUtils.isEmpty(addDetailInDTO.getPointId())){
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_WHEN_ADDING_PANORAMA_DETAILS_THE_PANORAMA_ID_CANNOT_BE_EMPTY.getContent()));
        }
    }

    /**
     *填充字段
     *
     * @param addDetailInDTO
     */
    public void fillData(DataPanoramaDetailInDTO.AddDetailInDTO addDetailInDTO) {
        if (StringUtils.isEmpty(addDetailInDTO.getOrgCode())) {
            addDetailInDTO.setOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
        }
        //MissionRecords 有值的情况下
        if (StringUtils.hasText(addDetailInDTO.getMissionRecordsId())) {
            //手工上传应该都没有该字段
            //后续如果有这个字段，需要补充其他字段
            if (judgeField(addDetailInDTO)) {
                //查询MissionRecords表
                MissionRecordsEntity missionRecordsEntity = this.missionRecordsService.getMissionRecordById(Integer.parseInt(addDetailInDTO.getMissionRecordsId()));
                if (StringUtils.isEmpty(addDetailInDTO.getMissionId())) {
                    addDetailInDTO.setMissionId(missionRecordsEntity.getMissionId().toString());
                }
                if (StringUtils.isEmpty(addDetailInDTO.getMissionFlyIndex())) {
                    addDetailInDTO.setMissionFlyIndex(missionRecordsEntity.getFlyIndex().toString());
                }
                if (StringUtils.isEmpty(addDetailInDTO.getMissionRecordTime())) {
                    addDetailInDTO.setMissionRecordTime(missionRecordsEntity.getStartTime());
                }
            }
        }
        if (StringUtils.isEmpty(addDetailInDTO.getMissionRecordTime())) {
            addDetailInDTO.setMissionRecordTime(LocalDateTime.parse("2022-01-01 00:00:00", DateUtils.DATE_TIME_FORMATTER_OF_CN));
        }
        List<DataPanoramaPointOutDTO.QueryOutDTO> outDTOS = this.dataPanoramaPointService.queryPoint(new DataPanoramaPointInDTO.QueryOneInDTO().setPointId(addDetailInDTO.getPointId()));
        if (CollectionUtil.isEmpty(outDTOS)) {
            return;
        }
        addDetailInDTO.setAirLineId(outDTOS.get(0).getAirLineId());
        addDetailInDTO.setAirPointId(outDTOS.get(0).getAirPointId());
    }

    /**
     * 判断是否要补充字段
     * @return
     */
    public Boolean judgeField(DataPanoramaDetailInDTO.AddDetailInDTO addDetailInDTO){
        if(StringUtils.isEmpty(addDetailInDTO.getMissionId())){
            return Boolean.TRUE;
        }
        if(StringUtils.isEmpty(addDetailInDTO.getMissionFlyIndex())){
            return Boolean.TRUE;
        }
        if(StringUtils.isEmpty(addDetailInDTO.getMissionRecordTime())){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 删除全景明细
     *
     * @param detailIds
     * @return
     */
    @Override
    public Boolean deleteDetails(List<String> detailIds) {
        if(log.isDebugEnabled()){
            log.debug("deleteDetails:DetailIds -> {}", detailIds);
        }
        if(CollectionUtil.isEmpty(detailIds)){
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_DATA_TO_BE_DELETED_IS_EMPTY_PLEASE_CHECK.getContent()));
        }
        LambdaUpdateWrapper updateWrapper = Wrappers.lambdaUpdate(DataPanoramaDetailEntity.class)
                .set(DataPanoramaDetailEntity::getDeleted, NestConstant.DeleteType.DELETED)
                .in(DataPanoramaDetailEntity::getDetailId,detailIds);
        int count = this.dataPanoramaDetailMapper.delete(updateWrapper);
        if(log.isDebugEnabled()){
            log.debug("deleteDetails: 成功删除 {} 条 ",count);
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
    public PageResultInfo<DataPanoramaDetailOutDTO.QueryPageOutDTO> queryDetailPage(DataPanoramaDetailInDTO.QueryPageInDTO queryPageInDTO) {
        List<DataPanoramaDetailOutDTO.QueryPageOutDTO> results = new ArrayList<>();
        long total = 0L;
        //获取该用户拥有的单位全
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        String startTime = ObjectUtil.isEmpty(queryPageInDTO.getStartTime())?"":LocalDateTime.of(queryPageInDTO.getStartTime()
                , LocalTime.of(00, 00, 00)).toString(),
                endTime = ObjectUtil.isEmpty(queryPageInDTO.getEndTime())?"":LocalDateTime.of(queryPageInDTO.getEndTime()
                        , LocalTime.of(23, 59, 59)).toString();
        //拼装数据
        DataPanoramaDetailCriteriaPO po = DataPanoramaDetailCriteriaPO.builder()
                .startTime(startTime)
                .endTime(endTime)
                .visibleOrgCode(orgCode)
                .missionRecordsId(queryPageInDTO.getMissionRecordsId())
                .pointId(queryPageInDTO.getPointId())
                .detailId(queryPageInDTO.getDetailId())
                .missionId(queryPageInDTO.getMissionId())
                .build();
        //查询条件下总数
        total = this.dataPanoramaDetailMapper.countByCondition(po);
        if(total > 0){
            //分页查询基础数据
            List<DataPanoramaDetailEntity> outPOList = this.dataPanoramaDetailMapper.selectByCondition(po, PagingRestrictDo.getPagingRestrict(queryPageInDTO));
            results = outPOList.stream().map(DataPanoramaDetailConverter.INSTANCES::convertPage).collect(Collectors.toList());
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
    public List<DataPanoramaDetailOutDTO.QueryLessOutDTO> queryAllDetails(DataPanoramaDetailInDTO.QueryInDTO queryInDTO) {
        List<DataPanoramaDetailOutDTO.QueryLessOutDTO> queryLessOutDTOList = new ArrayList<>();
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataPanoramaDetailEntity.class)
                .eq(StringUtils.hasText(queryInDTO.getDetailId()),DataPanoramaDetailEntity::getDetailId,queryInDTO.getDetailId())
                .eq(StringUtils.hasText(queryInDTO.getPointId()),DataPanoramaDetailEntity::getPointId,queryInDTO.getPointId())
                .eq(StringUtils.hasText(queryInDTO.getMissionId()),DataPanoramaDetailEntity::getMissionId,queryInDTO.getMissionId())
                .eq(StringUtils.hasText(queryInDTO.getMissionRecordsId()),DataPanoramaDetailEntity::getMissionRecordsId,queryInDTO.getMissionRecordsId())
                .eq(StringUtils.hasText(queryInDTO.getAirPointId()),DataPanoramaDetailEntity::getAirPointId,queryInDTO.getAirPointId())
                .eq(StringUtils.hasText(queryInDTO.getAirLineId()),DataPanoramaDetailEntity::getAirLineId,queryInDTO.getAirLineId())
                .orderByDesc(DataPanoramaDetailEntity::getAcquisitionTime,DataPanoramaDetailEntity::getId);
        List<DataPanoramaDetailEntity> dataPanoramaDetailEntities = this.dataPanoramaDetailMapper.selectList(queryWrapper);
        if(CollectionUtil.isEmpty(dataPanoramaDetailEntities)){
            return queryLessOutDTOList;
        }
        queryLessOutDTOList = dataPanoramaDetailEntities.stream()
                .map(DataPanoramaDetailConverter.INSTANCES::convertLess)
                .collect(Collectors.toList());
        return queryLessOutDTOList;
    }

    /**
     * 查询单条
     * @param queryOneInDTO
     * @return
     */
    @Override
    public List<DataPanoramaDetailOutDTO.QueryOutDTO> queryDetail(DataPanoramaDetailInDTO.QueryOneInDTO queryOneInDTO) {
        List<DataPanoramaDetailOutDTO.QueryOutDTO> queryOutDTOList = new ArrayList<>();
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataPanoramaDetailEntity.class)
                .eq(StringUtils.hasText(queryOneInDTO.getDetailId()),DataPanoramaDetailEntity::getDetailId,queryOneInDTO.getDetailId())
                .eq(StringUtils.hasText(queryOneInDTO.getPointId()),DataPanoramaDetailEntity::getPointId,queryOneInDTO.getPointId())
                .orderByDesc(DataPanoramaDetailEntity::getAcquisitionTime,DataPanoramaDetailEntity::getId);
        List<DataPanoramaDetailEntity> dataPanoramaDetailEntities = this.dataPanoramaDetailMapper.selectList(queryWrapper);
        if(CollectionUtil.isEmpty(dataPanoramaDetailEntities)){
            return queryOutDTOList;
        }
        queryOutDTOList = dataPanoramaDetailEntities.stream()
                .map(DataPanoramaDetailConverter.INSTANCES::convert)
                .collect(Collectors.toList());
        return queryOutDTOList;
    }

    /**
     * 根据pointId获取detailURL
     *
     * @param pointIdList
     * @return
     */
    @Override
    public Map<String, DataPanoramaDetailOutDTO.QueryOutDTO> getPointToURL(List<String> pointIdList, String missionId ,String missionRecordsId) {
        Map<String,DataPanoramaDetailOutDTO.QueryOutDTO> pointIdToURLMap = new HashMap<>();
        if(CollectionUtil.isEmpty(pointIdList)){
            return pointIdToURLMap;
        }
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataPanoramaDetailEntity.class)
                .in(DataPanoramaDetailEntity::getPointId,pointIdList)
                .select(DataPanoramaDetailEntity::getPointId
                        ,DataPanoramaDetailEntity::getDetailUrl
                        ,DataPanoramaDetailEntity::getMissionId
                        ,DataPanoramaDetailEntity::getMissionRecordsId
                        ,DataPanoramaDetailEntity::getAcquisitionTime)
                .orderByDesc(DataPanoramaDetailEntity::getAcquisitionTime,DataPanoramaDetailEntity::getId);
        List<DataPanoramaDetailEntity> dataPanoramaDetailEntities = this.dataPanoramaDetailMapper.selectList(queryWrapper);
        if(CollectionUtil.isNotEmpty(dataPanoramaDetailEntities)){
            //先分组再取分组第一个数
            pointIdToURLMap = dataPanoramaDetailEntities.stream()
                    .filter(x-> StringUtils.isEmpty(missionId)? ObjectUtil.isNotNull(x):missionId.equals(x.getMissionId()))
                    .filter(x-> StringUtils.isEmpty(missionRecordsId)? ObjectUtil.isNotNull(x):missionRecordsId.equals(x.getMissionRecordsId()))
                    .collect(Collectors.groupingBy(DataPanoramaDetailEntity::getPointId
                            ,Collectors.collectingAndThen(Collectors.toList(),x->{
                                DataPanoramaDetailOutDTO.QueryOutDTO queryOutDTO = new DataPanoramaDetailOutDTO.QueryOutDTO();
                                queryOutDTO.setDetailUrl(x.get(0).getDetailUrl());
                                queryOutDTO.setAcquisitionTime(x.get(0).getAcquisitionTime());
                                return queryOutDTO;
                            }))
                    );
        }
        return pointIdToURLMap;
    }
}
