package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.NestConstant;
import com.imapcloud.nest.enums.UploadTypeEnum;
import com.imapcloud.nest.v2.common.enums.DataAnalysisMarkNameEnum;
import com.imapcloud.nest.v2.common.enums.DataAnalysisMarkStatusEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.utils.DrawImageUtils;
import com.imapcloud.nest.v2.common.utils.Pic;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkMergeEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisDetailMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMergeMapper;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisMarkQueryCriteriaPO;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisDetailMarkOutPO;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileStorageOutDO;
import com.imapcloud.nest.v2.manager.event.MarkDelEvent;
import com.imapcloud.nest.v2.manager.event.MarkDelResultEvent;
import com.imapcloud.nest.v2.manager.event.WriteBackPicEvent;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.nest.v2.service.DataAnalysisDetailService;
import com.imapcloud.nest.v2.service.DataAnalysisMarkService;
import com.imapcloud.nest.v2.service.DataAnalysisResultService;
import com.imapcloud.nest.v2.service.converter.DataAnalysisMarkConverter;
import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisDetailDrawOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMarkDrawOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMarkOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMarkPageOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisMarkServiceImpl.java
 * @Description DataAnalysisMarkServiceImpl
 * @createTime 2022年07月13日 15:22:00
 */
@Slf4j
@Service
public class DataAnalysisMarkServiceImpl implements DataAnalysisMarkService {
    @Resource
    private DataAnalysisMarkMapper dataAnalysisMarkMapper;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private DataAnalysisDetailService dataAnalysisDetailService;

    @Resource
    private DataAnalysisDetailMapper dataAnalysisDetailMapper;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private DataAnalysisMarkMergeMapper dataAnalysisMarkMergeMapper;

    @Resource
    private DataAnalysisResultService dataAnalysisResultService;

    @Resource
    private FileManager fileManager;

    @Resource
    private UploadManager uploadManager;

    /**
     * 根据Details 分页查询标注数据
     *
     * @param dataAnalysisMarkPageInDTO
     * @return
     */
    @Override
    public PageResultInfo<DataAnalysisMarkPageOutDTO> queryMarkPage(DataAnalysisMarkPageInDTO dataAnalysisMarkPageInDTO) {
        List<DataAnalysisMarkPageOutDTO> results = new ArrayList<>();
        long total = 0L;
        DataAnalysisMarkQueryCriteriaPO po = DataAnalysisMarkQueryCriteriaPO.builder()
                .detailId(dataAnalysisMarkPageInDTO.getDetailId())
                .markState(dataAnalysisMarkPageInDTO.getMarkState())
                .build();
        total = this.dataAnalysisMarkMapper.countByCondition(po);
        if (total > 0) {
            List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = this.dataAnalysisMarkMapper.selectByCondition(po, PagingRestrictDo.getPagingRestrict(dataAnalysisMarkPageInDTO));
            results = dataAnalysisMarkEntities.stream()
                    .map(x -> DataAnalysisMarkConverter.INSTANCES.convert(x))
                    .collect(Collectors.toList());
        }
        return PageResultInfo.of(total, results);
    }

    /**
     * 根据Details 查询标注数据 -返回对应的数据
     *
     * @param dataAnalysisMarkInDTO
     * @return
     */
    @Override
    public List<DataAnalysisMarkOutDTO> queryMark(DataAnalysisMarkInDTO dataAnalysisMarkInDTO) {
        List<DataAnalysisMarkOutDTO> results = new ArrayList<>();

        if (dataAnalysisMarkInDTO != null) {
            DataAnalysisMarkQueryCriteriaPO po = DataAnalysisMarkQueryCriteriaPO.builder()
                    .photoId(dataAnalysisMarkInDTO.getPhotoId())
                    .detailId(dataAnalysisMarkInDTO.getDetailId())
                    .markId(dataAnalysisMarkInDTO.getMarkId())
                    .build();
            List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS = this.dataAnalysisMarkMapper.queryMarks(po);
            //填充专题信息
            this.dataAnalysisDetailService.fillTopicData(dataAnalysisDetailMarkOutPOS);
            //填充是否合并信息
            this.dataAnalysisDetailService.fillMergeState(dataAnalysisDetailMarkOutPOS);
            results = dataAnalysisDetailMarkOutPOS.stream()
                    .map(DataAnalysisMarkConverter.INSTANCES::convertQuery)
                    .collect(Collectors.toList());

        }
        return results;
    }

    /**
     * 批量核实标注
     *
     * @param markIds
     * @return
     */
    @Override
    public boolean confirmMarks(List<Long> markIds) {
        //更新markIds里所有未核实、且未删除的数据更新为已核实
        LambdaUpdateWrapper<DataAnalysisMarkEntity> wrapper = Wrappers.lambdaUpdate(DataAnalysisMarkEntity.class)
                .set(DataAnalysisMarkEntity::getMarkState, DataAnalysisMarkStatusEnum.CONFIRM.getType())
                .in(DataAnalysisMarkEntity::getMarkId, markIds)
                .eq(DataAnalysisMarkEntity::getMarkState, DataAnalysisMarkStatusEnum.NOT_CONFIRM.getType());

        return this.dataAnalysisMarkMapper.update(null, wrapper) > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 核实标注
     *
     * @param markId
     * @return
     */
    @Override
    public boolean confirmMark(Long markId) {
        //更新markId里未核实、且未删除的数据更新为已核实
        LambdaUpdateWrapper<DataAnalysisMarkEntity> wrapper = Wrappers.lambdaUpdate(DataAnalysisMarkEntity.class)
                .set(DataAnalysisMarkEntity::getMarkState, DataAnalysisMarkStatusEnum.CONFIRM.getType())
                .eq(DataAnalysisMarkEntity::getMarkId, markId)
                .eq(DataAnalysisMarkEntity::getMarkState, DataAnalysisMarkStatusEnum.NOT_CONFIRM.getType());

        return this.dataAnalysisMarkMapper.update(null,wrapper)>0?Boolean.TRUE:Boolean.FALSE;
    }

    /**
     * 核实照片下所有标注
     *
     * @param detailId
     * @return
     */
    @Override
    public boolean confirmMarkForPic(Long detailId) {
        //更新照片下所有标注里“所有未核实、且未删除“的数据更新为已核实
        LambdaUpdateWrapper<DataAnalysisMarkEntity> wrapper = Wrappers.lambdaUpdate(DataAnalysisMarkEntity.class)
                .set(DataAnalysisMarkEntity::getMarkState, DataAnalysisMarkStatusEnum.CONFIRM.getType())
                .eq(DataAnalysisMarkEntity::getDetailId , detailId);
//                .eq(DataAnalysisMarkEntity::getMarkState,DataAnalysisMarkStatusEnum.NOT_CONFIRM.getType())

        return this.dataAnalysisMarkMapper.update(null,wrapper)>0?Boolean.TRUE:Boolean.FALSE;
    }

    /**
     * 根据Mark生成对应的单个标注图片以及缩略图
     *
     * @param markInDTO
     * @return
     */
    @Override
    public List<DataAnalysisMarkDrawOutDTO> drawSingleMarkPic(List<DataAnalysisMarkDrawInDTO> markInDTO) {
        List<DataAnalysisMarkDrawOutDTO> dataAnalysisMarkDrawOutDTOS = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(markInDTO)) {
            markInDTO.stream().forEach(mark -> {
                Pic pic = new Pic(mark.getRecX()
                        ,mark.getRecY()
                        ,mark.getRecWidth()
                        ,mark.getRecHeight()
                        ,mark.getRelX()
                        ,mark.getRelY()
                        ,mark.getCutWidth()
                        ,mark.getCutHeight()
                        ,mark.getPicScale()
                        ,String.format("%s"
                            , StringUtils.isEmpty(mark.getTopicProblemName())?"":mark.getTopicProblemName())
                        ,mark.getSrcDataType()
                );
                pic.setPhotoCreateTime(mark.getPhotoCreateTime());
                String srcPath = mark.getOriginalImagePath(), descPath = this.getMarkName(mark.getPhotoName(), DataAnalysisMarkNameEnum.MARK.getType()), thumDescPath = this.getMarkName(mark.getPhotoName(), DataAnalysisMarkNameEnum.THUM_MARK.getType());

                try(InputStream srcInputStream = fileManager.getInputSteam(srcPath)) {
                    //生成缩略图
                    InputStream inputStream = DrawImageUtils.drawImage(pic, srcPath, srcInputStream);
                    DataAnalysisMarkDrawOutDTO dataAnalysisMarkDrawOutDTO = new DataAnalysisMarkDrawOutDTO();
                    dataAnalysisMarkDrawOutDTO.setId(mark.getId());
                    dataAnalysisMarkDrawOutDTO.setMarkId(mark.getMarkId());
                    if(Objects.nonNull(inputStream)){
                        CommonFileInDO commonFileInDO = new CommonFileInDO();
                        commonFileInDO.setFileName(srcPath);
                        commonFileInDO.setInputStream(inputStream);
                        Optional<FileStorageOutDO> optional = uploadManager.uploadFile(commonFileInDO);
                        if(optional.isPresent()){
                            String imageUrl = optional.get().getStoragePath() + SymbolConstants.SLASH_LEFT + optional.get().getFilename();
                            String thumbnailUrl = fileManager.generateThumbnail(imageUrl, 0.54D, true);
                            dataAnalysisMarkDrawOutDTO.setMarkImagePath(imageUrl);
                            dataAnalysisMarkDrawOutDTO.setThumImagePath(thumbnailUrl);
                        }else{
                            log.warn("上传标注图失败");
                        }
                    }
//                    //画图
//                    DrawImageUtils.drawImage(pic, srcPath, descPath);
//                    //生成缩略图
//                    DrawImageUtils.thumbnailImage(descPath, thumDescPath);
//                    DataAnalysisMarkDrawOutDTO dataAnalysisMarkDrawOutDTO = new DataAnalysisMarkDrawOutDTO();
//                    dataAnalysisMarkDrawOutDTO.setMarkId(mark.getMarkId());
//                    dataAnalysisMarkDrawOutDTO.setMarkImagePath(String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), descPath));
//                    dataAnalysisMarkDrawOutDTO.setThumImagePath(String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), thumDescPath));
//                    dataAnalysisMarkDrawOutDTO.setId(mark.getId());
                    dataAnalysisMarkDrawOutDTOS.add(dataAnalysisMarkDrawOutDTO);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new BusinessException(e.getMessage());
                }
            });
        }
        return dataAnalysisMarkDrawOutDTOS;
    }

    /**
     * 根据照片ID画完整的所有标注图片以及缩略图
     *
     * @param markInDTOs
     * @return
     */
    @Override
    public DataAnalysisDetailDrawOutDTO drawAllMarkPic(List<DataAnalysisMarkDrawInDTO> markInDTOs) {
        DataAnalysisDetailDrawOutDTO dataAnalysisDetailDrawOutDTO = new DataAnalysisDetailDrawOutDTO();
        if (CollectionUtil.isNotEmpty(markInDTOs)) {
            List<Pic> picList = new ArrayList<>();

            markInDTOs.stream().forEach(mark -> {
                Pic pic = new Pic(mark.getRecX()
                        , mark.getRecY()
                        , mark.getRecWidth()
                        , mark.getRecHeight()
                        , mark.getRelX()
                        , mark.getRelY()
                        , mark.getCutWidth()
                        , mark.getCutHeight()
                        , mark.getPicScale()
                        , String.format("%s"
                        , StringUtils.isEmpty(mark.getTopicProblemName()) ? "" : mark.getTopicProblemName())
                        , mark.getSrcDataType()
                );
                picList.add(pic);
            });
            String srcPath = markInDTOs.get(0).getOriginalImagePath(), descPath = this.getMarkName(markInDTOs.get(0).getPhotoName(), DataAnalysisMarkNameEnum.MARK_ALL.getType()), thumDescPath = this.getMarkName(markInDTOs.get(0).getPhotoName(), DataAnalysisMarkNameEnum.THUM_MARK_ALL.getType());

            try(InputStream srcInputStream = fileManager.getInputSteam(srcPath);
                InputStream inputStream = DrawImageUtils.drawImages(picList, srcPath, srcInputStream)){
                //生成缩略图
//                DrawImageUtils.thumbnailImage(descPath, thumDescPath);
//                dataAnalysisDetailDrawOutDTO.setThumImageMarkPath(String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), thumDescPath));
//                dataAnalysisDetailDrawOutDTO.setImageMarkPath(String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), descPath));
                if(Objects.nonNull(inputStream)){
                    CommonFileInDO commonFileInDO = new CommonFileInDO();
                    commonFileInDO.setFileName(srcPath);
                    commonFileInDO.setInputStream(inputStream);
                    Optional<FileStorageOutDO> optional = uploadManager.uploadFile(commonFileInDO);
                    if(optional.isPresent()){
                        String imageUrl = optional.get().getStoragePath() + SymbolConstants.SLASH_LEFT + optional.get().getFilename();
                        String thumbnailUrl = fileManager.generateThumbnail(imageUrl, 0.54D, true);
                        dataAnalysisDetailDrawOutDTO.setImageMarkPath(imageUrl);
                        dataAnalysisDetailDrawOutDTO.setThumImageMarkPath(thumbnailUrl);
                    }else{
                        log.warn("上传标注图失败");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new BusinessException(e.getMessage());
            }

        }
        return dataAnalysisDetailDrawOutDTO;
    }

    /**
     * 根据照片Id查询标注
     *
     * @param detailIds
     * @return
     */
    @Override
    public List<DataAnalysisMarkEntity> queryMarkByDetails(List<Long> detailIds) {
        List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(detailIds)) {
            LambdaQueryWrapper<DataAnalysisMarkEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisMarkEntity.class)
                    .in(DataAnalysisMarkEntity::getDetailId, detailIds);
            dataAnalysisMarkEntities = this.dataAnalysisMarkMapper.selectList(wrapper);
        }
        return dataAnalysisMarkEntities;
    }

    /**
     * 删除对应数据 根据detailId
     *
     * @param detailIds
     * @return
     */
    @Override
    public boolean deleteMarksByDetail(List<Long> detailIds) {
        boolean success = true;
        if (CollectionUtil.isNotEmpty(detailIds)) {
            LambdaQueryWrapper wrapper = Wrappers.lambdaQuery(DataAnalysisMarkEntity.class)
                    .in(DataAnalysisMarkEntity::getDetailId, detailIds)
                    .select(DataAnalysisMarkEntity::getMarkId);
            List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = this.dataAnalysisMarkMapper.selectList(wrapper);
            if (CollectionUtil.isNotEmpty(dataAnalysisMarkEntities)) {
                List<Long> markIds = dataAnalysisMarkEntities.stream()
                        .map(DataAnalysisMarkEntity::getMarkId)
                        .collect(Collectors.toList());
                success = this.deleteMarks(markIds, Boolean.FALSE);
            }
            // 删除问题和问题组
            this.applicationContext.publishEvent(new MarkDelResultEvent(detailIds,false));
        }
        return success;
    }

    /**
     * 删除对应数据
     *
     * @param markIds
     * @param fromMark
     * @return
     */
    @Override
    public boolean deleteMarks(List<Long> markIds, boolean fromMark) {
        int count = 0;
        if (CollectionUtil.isNotEmpty(markIds)) {
            if (fromMark) {
                List<Long> hasConfirmed = this.dataAnalysisDetailService.hasConfirmPic(null, markIds);
                if (CollectionUtil.isNotEmpty(hasConfirmed)) {
                    throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DATA_CANNOT_BE_MARKED_BEFORE_OPERATION.getContent()));
                }
            }
            LambdaUpdateWrapper wrapper = Wrappers.lambdaUpdate(DataAnalysisMarkEntity.class)
                    .set(DataAnalysisMarkEntity::getDeleted, NestConstant.DeleteType.DELETED)
                    .in(DataAnalysisMarkEntity::getMarkId, markIds);
            count = this.dataAnalysisMarkMapper.update(null, wrapper);
            //删除标注表信息
            Set<String> collectGroup = this.dataAnalysisMarkMergeMapper.queryDataByMarkId(markIds).stream().map(DataAnalysisMarkMergeEntity::getResultGroupId).collect(Collectors.toSet());
            LambdaUpdateWrapper updateWrapper = Wrappers.lambdaUpdate(DataAnalysisMarkMergeEntity.class)
                    .set(DataAnalysisMarkMergeEntity::getDeleted, NestConstant.DeleteType.DELETED)
                    .in(DataAnalysisMarkMergeEntity::getMarkId, markIds);
            this.dataAnalysisMarkMergeMapper.update(null, updateWrapper);
            //批量调用balance
            this.dataAnalysisResultService.balance(collectGroup);
            this.applicationContext.publishEvent(new MarkDelEvent(markIds));
            this.applicationContext.publishEvent(new WriteBackPicEvent(markIds, fromMark));
        }
        return count > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 批量保存
     *
     * @param dataAnalysisMarkSaveInDTOList
     * @return
     */
    @Override
    public List<Long> saveBatch(List<DataAnalysisMarkSaveInDTO> dataAnalysisMarkSaveInDTOList) {
        List<Long> markIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dataAnalysisMarkSaveInDTOList)) {
            //核实过的不允许新增
            List<Long> hasConfirmed = this.dataAnalysisDetailService.hasConfirmPic(dataAnalysisMarkSaveInDTOList.stream()
                    .map(DataAnalysisMarkSaveInDTO::getDetailId)
                    .collect(Collectors.toList()), null);
            if (CollectionUtil.isNotEmpty(hasConfirmed)) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_VERIFIED_DATA_IS_NOT_ALLOWED_TO_OPERATE_THE_ANNOTATION_PLEASE_WITHDRAW_AND_OPERATE_AGAIN_FIRST.getContent()));
            }
            markIds = saveOrUpdateBatch(dataAnalysisMarkSaveInDTOList);
            this.applicationContext.publishEvent(new WriteBackPicEvent(markIds, Boolean.TRUE));
        }
        return markIds;
    }

    /**
     * 批量更新保存
     *
     * @param dataAnalysisMarkSaveInDTOList
     * @return
     */
    @Override
    public List<Long> saveOrUpdateBatch(List<DataAnalysisMarkSaveInDTO> dataAnalysisMarkSaveInDTOList) {
        List<Long> markIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dataAnalysisMarkSaveInDTOList)) {
            List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = dataAnalysisMarkSaveInDTOList.stream()
                    .map(DataAnalysisMarkConverter.INSTANCES::convert)
                    .collect(Collectors.toList());
            List<Long> markDTOs = dataAnalysisMarkEntities.stream()
                    .map(DataAnalysisMarkEntity::getMarkId)
                    .collect(Collectors.toList());
            LambdaQueryWrapper<DataAnalysisMarkEntity> queryWrapper = Wrappers.lambdaQuery(DataAnalysisMarkEntity.class)
                    .in(DataAnalysisMarkEntity::getMarkId, markDTOs)
                    .select(DataAnalysisMarkEntity::getMarkId);
            List<Long> existMarkIds = this.dataAnalysisMarkMapper.selectList(queryWrapper)
                    .stream()
                    .map(DataAnalysisMarkEntity::getMarkId)
                    .collect(Collectors.toList());
            List<DataAnalysisMarkEntity> insertMarks = new ArrayList<>(), updateMarks = new ArrayList<>();
            for (DataAnalysisMarkEntity entity : dataAnalysisMarkEntities) {
                if (entity.getMarkId() != null && existMarkIds.contains(entity.getMarkId())) {
                    updateMarks.add(entity);
                } else {
                    insertMarks.add(entity);
                }
            }
            if (CollectionUtil.isNotEmpty(insertMarks)) {
                this.dataAnalysisMarkMapper.saveBatch(insertMarks);
                markIds.addAll(insertMarks.stream().map(DataAnalysisMarkEntity::getMarkId).collect(Collectors.toList()));
            }
            if (CollectionUtil.isNotEmpty(updateMarks)) {
                for (DataAnalysisMarkEntity entity : updateMarks) {
                    this.dataAnalysisMarkMapper.updateEntity(entity);
                    markIds.add(entity.getMarkId());
                }
            }
        }
        return markIds;
    }

//    /**
//     * 上传地址截图
//     *
//     * @param markId
//     * @param file
//     * @return
//     */
//    @Override
//    public String uploadAddrPic(Long markId, BigDecimal longitude, BigDecimal latitude
//            , String addr, MultipartFile file) {
//        //根据markId查询到照片名
//        String photoName = "";
//        LambdaQueryWrapper queryMarkWrapper = Wrappers.lambdaQuery(DataAnalysisMarkEntity.class)
//                .eq(DataAnalysisMarkEntity::getMarkId, markId)
//                .last("limit 1");
//        DataAnalysisMarkEntity dataAnalysisMark = this.dataAnalysisMarkMapper.selectOne(queryMarkWrapper);
//        if (dataAnalysisMark != null) {
//            LambdaQueryWrapper queryDetailWrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
//                    .eq(DataAnalysisCenterDetailEntity::getCenterDetailId, dataAnalysisMark.getDetailId())
//                    .last("limit 1");
//            DataAnalysisCenterDetailEntity detailEntity = this.dataAnalysisDetailMapper.selectOne(queryDetailWrapper);
//            photoName = Optional.ofNullable(detailEntity).map(DataAnalysisCenterDetailEntity::getPhotoName).orElseGet(() -> "");
//        }
//        String wholePath = this.getMarkName(photoName, DataAnalysisMarkNameEnum.THUM_MARK_ADDR.getType());
//        String newFileName = MinIoUnit.getFileName(wholePath.substring(wholePath.lastIndexOf("/") + 1));
//        String photoPath = wholePath.substring(0, wholePath.lastIndexOf("/") + 1);
//        wholePath = MinIoUnit.upload(geoaiUosProperties.getMinio().getBucketName(), file,
//                newFileName, photoPath);
//        String addrImagePath = String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), wholePath);
//        LambdaUpdateWrapper updateWrapper = Wrappers.lambdaUpdate(DataAnalysisMarkEntity.class)
//                .set(DataAnalysisMarkEntity::getAddrImagePath, addrImagePath)
//                .set(longitude != null, DataAnalysisMarkEntity::getLongitude, longitude)
//                .set(latitude != null, DataAnalysisMarkEntity::getLatitude, latitude)
//                .set(StringUtils.hasText(addr), DataAnalysisMarkEntity::getAddr, addr)
//                .eq(DataAnalysisMarkEntity::getMarkId, markId);
//        this.dataAnalysisMarkMapper.update(null, updateWrapper);
//        return addrImagePath;
//    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void savePicAddrInfo(MarkAddrInfoInDTO markAddrInfoInDTO) {
        LambdaUpdateWrapper<DataAnalysisMarkEntity> updateWrapper = Wrappers.lambdaUpdate(DataAnalysisMarkEntity.class)
                .set(DataAnalysisMarkEntity::getAddrImagePath, markAddrInfoInDTO.getAddrPicPath())
                .set(markAddrInfoInDTO.getLongitude() != null, DataAnalysisMarkEntity::getLongitude, markAddrInfoInDTO.getLongitude())
                .set(markAddrInfoInDTO.getLatitude() != null, DataAnalysisMarkEntity::getLatitude, markAddrInfoInDTO.getLatitude())
                .set(StringUtils.hasText(markAddrInfoInDTO.getAddr()), DataAnalysisMarkEntity::getAddr, markAddrInfoInDTO.getAddr())
                .eq(DataAnalysisMarkEntity::getMarkId, markAddrInfoInDTO.getMarkId());
        this.dataAnalysisMarkMapper.update(null, updateWrapper);
    }

    /**
     * 标注地址信息重置
     *
     * @param markId
     * @param longitude
     * @param latitude
     * @return
     */
    @Override
    public Long markAddrReset(Long markId, BigDecimal longitude, BigDecimal latitude) {
        if (markId != null) {
            LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisMarkEntity.class)
                    .eq(DataAnalysisMarkEntity::getMarkId, markId)
                    .select(DataAnalysisMarkEntity::getAddrImagePath);
            List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = this.dataAnalysisMarkMapper.selectList(queryWrapper);
            if (CollectionUtil.isEmpty(dataAnalysisMarkEntities)) {
                return null;
            }
            String addrImagePath = dataAnalysisMarkEntities.get(0).getAddrImagePath();
//            FileUtils.deleteFile(CollectionUtil.newArrayList(addrImagePath), Boolean.TRUE);
            fileManager.deleteFiles(Collections.singletonList(addrImagePath));
            LambdaUpdateWrapper updateWrapper = Wrappers.lambdaUpdate(DataAnalysisMarkEntity.class)
                    .set(DataAnalysisMarkEntity::getLongitude, longitude)
                    .set(DataAnalysisMarkEntity::getLatitude, latitude)
                    .set(DataAnalysisMarkEntity::getAddr, "")
                    .set(DataAnalysisMarkEntity::getAddrImagePath, "")
                    .eq(DataAnalysisMarkEntity::getMarkId, markId);
            this.dataAnalysisMarkMapper.update(null, updateWrapper);

        }
        return markId;
    }

    /**
     * 标注地址截图清空
     *
     * @param markId
     * @return
     */
    @Override
    public Long markAddrDel(Long markId) {
        if (markId != null) {
            LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisMarkEntity.class)
                    .eq(DataAnalysisMarkEntity::getMarkId, markId)
                    .select(DataAnalysisMarkEntity::getAddrImagePath);
            List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = this.dataAnalysisMarkMapper.selectList(queryWrapper);
            if (CollectionUtil.isEmpty(dataAnalysisMarkEntities)) {
                return null;
            }
            String addrImagePath = dataAnalysisMarkEntities.get(0).getAddrImagePath();
//            FileUtils.deleteFile(CollectionUtil.newArrayList(addrImagePath), Boolean.TRUE);
            fileManager.deleteFiles(Collections.singletonList(addrImagePath));
            LambdaUpdateWrapper updateWrapper = Wrappers.lambdaUpdate(DataAnalysisMarkEntity.class)
                    .set(DataAnalysisMarkEntity::getAddrImagePath, "")
                    .eq(DataAnalysisMarkEntity::getMarkId, markId);
            this.dataAnalysisMarkMapper.update(null, updateWrapper);
        }
        return markId;
    }

    /**
     * 命名生成的标注图片
     *
     * @param photoName
     * @param markNameType 0 Mark 1 Mark_ALL 2 Thum_Mark 3 Thum_Mark_All 4 地址缩略图
     * @return
     */
    public String getMarkName(String photoName, Integer markNameType) {
        String returnName = "", prefix = "";
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        DataAnalysisMarkNameEnum nameEnum = DataAnalysisMarkNameEnum
                .findMatch(markNameType)
                .orElseThrow(() -> new BusinessException(String.format(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UNSUPPORTED_TYPES_OF_ANNOTATION_NAMES.getContent())
                        +":%d", markNameType)));
        if (StringUtils.hasText(photoName) && photoName.lastIndexOf(".") != -1) {
            photoName = photoName.substring(0, photoName.lastIndexOf("."));
        }
        switch (markNameType) {
            case 0:
                prefix = UploadTypeEnum.DATA_ANALYSIS_MARK_PATH.getPath();
                break;
            case 1:
                prefix = UploadTypeEnum.DATA_ANALYSIS_ALL_MARK_PATH.getPath();
                break;
            case 2:
                prefix = UploadTypeEnum.DATA_ANALYSIS_MARK_THUMBNAIL_PATH.getPath();
                break;
            case 3:
                prefix = UploadTypeEnum.DATA_ANALYSIS_ALL_MARK_THUMBNAIL_PATH.getPath();
                break;
            case 4:
                prefix = UploadTypeEnum.DATA_ANALYSIS_ADDR_THUMBNAIL_PATH.getPath();
                break;
            default:
                break;
        }
        returnName = String.format("%s%s-%s-%s.jpg"
                , prefix
                , photoName
                , nameEnum.name()
                , DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        return returnName;
    }

}
