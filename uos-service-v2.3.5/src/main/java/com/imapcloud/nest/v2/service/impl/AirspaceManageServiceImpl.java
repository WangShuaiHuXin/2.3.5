package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.DataConstant;
import com.imapcloud.nest.enums.AircraftCodeEnum;
import com.imapcloud.nest.utils.MinIoUnit;
import com.imapcloud.nest.utils.ParseVectorFileUtil;
import com.imapcloud.nest.utils.WordUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.dao.entity.AirspaceManageEntity;
import com.imapcloud.nest.v2.dao.mapper.AirspaceManageMapper;
import com.imapcloud.nest.v2.dao.po.in.AirspaceManageCriteriaPO;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.service.AirspaceManageService;
import com.imapcloud.nest.v2.service.dto.in.AirspaceManageInDTO;
import com.imapcloud.nest.v2.service.dto.in.AirspacePageInDTO;
import com.imapcloud.nest.v2.service.dto.in.AirspaceUploadFileInDTO;
import com.imapcloud.nest.v2.service.dto.out.AirspaceManageAirCoorOutDTO;
import com.imapcloud.nest.v2.service.dto.out.AirspaceManageOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Classname AirspaceManageServiceImpl
 * @Description 空域管理实现类
 * @Date 2023/3/8 18:13
 * @Author Carnival
 */
@Slf4j
@Service
public class AirspaceManageServiceImpl implements AirspaceManageService {

    @Resource
    private AirspaceManageMapper airspaceManageMapper;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private FileManager fileManager;

    private static final Integer Fail = 0;

    private static final Integer PHOTO_MAX_FILE_SIZE = 50 * 1024 * 1024;
    private static final Integer FILE_MAX_FILE_SIZE = 50 * 1024 * 1024;

    @Override
    public String addAirspace(AirspaceManageInDTO inDTO) {
        AirspaceManageEntity entity = new AirspaceManageEntity();
        entity.setAirspaceId(BizIdUtils.snowflakeIdStr());
        entity.setAirspaceName(inDTO.getAirspaceName());
        entity.setAddress(inDTO.getAddress());
        entity.setAglAltitude(inDTO.getAglAltitude());
        entity.setAirCoor(inDTO.getAirCoor());
        entity.setAltitude(inDTO.getAltitude());
        entity.setAirCoorCount(inDTO.getAirCoorCount());
        entity.setIsApproval(0);
        entity.setStartTime(strTranLocalDateTime(inDTO.getStartTime()));
        entity.setEndTime(strTranLocalDateTime(inDTO.getEndTime()));
        entity.setOrgCode(inDTO.getOrgCode());
        entity.setOrgName(inDTO.getOrgName());
        entity.setUavCode(inDTO.getUavCode());
        entity.setUavCount(inDTO.getUavCount());
        entity.setPhotoUrl(inDTO.getPhotoUrl());
        entity.setApplicantType(inDTO.getApplicantType());
        entity.setMissionType(inDTO.getMissionType());
        int res = airspaceManageMapper.insert(entity);
        if (res != Fail) {
            return entity.getAirspaceId();
        }
        return null;
    }

    @Override
    public Boolean deleteBatchAirspace(List<String> airspaceIds) {
        if (!CollectionUtils.isEmpty(airspaceIds)) {
            LambdaQueryWrapper<AirspaceManageEntity> con = Wrappers.lambdaQuery(AirspaceManageEntity.class)
                    .in(AirspaceManageEntity::getAirspaceId, airspaceIds);
            List<AirspaceManageEntity> airspaceManageEntities = airspaceManageMapper.selectList(con);
            if (CollectionUtils.isEmpty(airspaceManageEntities)) {
                return false;
            }
            List<Long> ids = airspaceManageEntities.stream().map(AirspaceManageEntity::getId).collect(Collectors.toList());
            int res = airspaceManageMapper.deleteBatchIds(ids);
            return res != Fail;
        }
        return false;
    }

    @Override
    public PageResultInfo<AirspaceManageOutDTO> pageAirspaceList(AirspacePageInDTO inDTO) {
        String orgCodeFromAccount = TrustedAccessTracerHolder.get().getOrgCode();
        AirspaceManageCriteriaPO po = buildAirspaceCriteria(inDTO, orgCodeFromAccount);
        long total = airspaceManageMapper.countByCondition(po);
        List<AirspaceManageOutDTO> res = null;
        if (total > 0) {
            List<AirspaceManageEntity> rows = airspaceManageMapper.selectByCondition(po, PagingRestrictDo.getPagingRestrict(inDTO));
            res = rows.stream()
                    .map(r -> {
                        AirspaceManageOutDTO outDTO = new AirspaceManageOutDTO();
                        BeanUtils.copyProperties(r, outDTO);
                        AircraftCodeEnum aircraftCodeEnum = AircraftCodeEnum.getInstance(r.getUavCode());
                        outDTO.setUavName(aircraftCodeEnum.getCode());
                        outDTO.setStartTime(localDateTimeTranStr(r.getStartTime()));
                        outDTO.setEndTime(localDateTimeTranStr(r.getEndTime()));
                        return outDTO;
                    }).collect(Collectors.toList());
            return PageResultInfo.of(total, res);
        }
        return PageResultInfo.of(total, res);
    }

//    @Override
//    public String uploadApprovalFile(AirspaceUploadFileInDTO inDTO) {
//        MultipartFile file = inDTO.getFile();
//        if (!ObjectUtils.isEmpty(file)) {
//            if (file.getSize() > FILE_MAX_FILE_SIZE) {
//                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_04.getContent()));
//            }
//            String originalFileName = file.getOriginalFilename();
//            String airspaceId = inDTO.getAirspaceId();
//            if (StringUtils.hasText(originalFileName) && originalFileName.contains(".")) {
//                String[] split = originalFileName.split("\\.");
//                String filename = split[0];
//                String suffix = split[1];
//
//                if (".pdf".equals(suffix) || ".docx".equals(suffix) || ".doc".equals(suffix)) {
//                    throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_01.getContent()));
//                }
//
//                String fileType = getFileContentType(originalFileName);
//                Optional<AirspaceManageEntity> airspaceById = findAirspaceById(airspaceId);
//                if (airspaceById.isPresent()) {
//                    try (InputStream in = file.getInputStream()) {
//                        String newFileName = String.format("%s%s", filename, fileType);
//                        String approvalFilePath = String.format("%s%s", UploadTypeEnum.AIRSPACE_APPROVAL_FILE.getPath(), newFileName);
//                        if (!MinIoUnit.putObject(approvalFilePath, in)) {
//                            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_UPLOAD_FILE.getContent()));
//                        }
//                        AirspaceManageEntity entity = airspaceById.get();
//                        entity.setIsApproval(1);
//                        entity.setApprovalFileUrl(approvalFilePath);
//                        airspaceManageMapper.updateById(entity);
//                        return approvalFilePath;
//                    } catch (Exception e) {
//                        log.error("#Airspace upload approvalFile error:", e);
//                    }
//                }
//            } else {
//                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_01.getContent()));
//            }
//        }
//
//        return null;
//    }

    @Override
    public String saveApprovalFile(AirspaceUploadFileInDTO inDTO) {
        String filePath = inDTO.getFilePath();
        if (StringUtils.hasText(filePath)) {
            Boolean fileExists = fileManager.checkFileExists(filePath);
            if (!Boolean.TRUE.equals(fileExists)) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_04.getContent()));
            }
            String suffix = StringUtils.getFilenameExtension(filePath);
            if (!"pdf".equals(suffix) && !"docx".equals(suffix) && !"doc".equals(suffix)) {
                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_01.getContent()));
            }
            Optional<AirspaceManageEntity> airspaceById = findAirspaceById(inDTO.getAirspaceId());
            if (airspaceById.isPresent()) {
                AirspaceManageEntity entity = airspaceById.get();
                entity.setIsApproval(1);
                entity.setApprovalFileUrl(filePath);
                airspaceManageMapper.updateById(entity);
                return filePath;
            }
        }
        return null;
    }

    @Override
    public AirspaceManageAirCoorOutDTO uploadAirCoor(MultipartFile file) {
        if (!ObjectUtils.isEmpty(file)) {
            if (file.getSize() > FILE_MAX_FILE_SIZE) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_04.getContent()));
            }
            String fileName = file.getOriginalFilename();
            if (StringUtils.hasText(fileName)) {
                String suffixName = fileName.substring(fileName.lastIndexOf("."));
                String parseContent = null;
                try {
                    if (".kml".equals(suffixName)) {
                        parseContent = ParseVectorFileUtil.parseKmlFile(file);
                    } else if (".json".equals(suffixName) || ".geojson".equals(suffixName)) {
                        parseContent = ParseVectorFileUtil.parseJsonFile(file);
                    } else {
                        throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_02.getContent()));
                    }
                } catch (Exception e) {
                    throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_03.getContent()), e);
                }

                if (StringUtils.hasText(parseContent)) {
                    String[] split = parseContent.split(" ");
                    AirspaceManageAirCoorOutDTO outDTO = new AirspaceManageAirCoorOutDTO();
                    outDTO.setAirCoor(parseContent);
                    outDTO.setAirCoorCount(split.length);
                    return outDTO;
                }
            }
        }
        return null;
    }

//    @Override
//    public String uploadPhoto(MultipartFile file) {
//        String imageType = checkPhotoType(file);
//        if (StringUtils.hasText(imageType)) {
//            String newFileName = String.format("%s.%s", BizIdUtils.snowflakeIdStr(), imageType);
//            String imagePath = String.format("%s%s", UploadTypeEnum.AIRSPACE_PHOTO.getPath(), newFileName);
//            try (InputStream is = file.getInputStream()) {
//                if (!MinIoUnit.putObject(imagePath, is, imageType)) {
//                    throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_UPLOAD_FILE.getContent()));
//                }
//            } catch (Exception e) {
//                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_THE_FILE_STREAM.getContent()), e);
//            }
//            DomainConfig domain = geoaiUosProperties.getDomain();
//            String originPath = geoaiUosProperties.getStore().getOriginPath();
//            return domain.getMedia() == null ? "" : originPath + imagePath;
//        }
//        return null;
//    }

    @Override
    public void exportAirspace(HttpServletResponse response, String airspaceId) {
        log.info("#AirspaceManageServiceImpl.exportAirspace# airspaceId={}", airspaceId);
        Optional<AirspaceManageEntity> airspaceById = findAirspaceById(airspaceId);
        if (airspaceById.isPresent()) {
            AirspaceManageEntity entity = airspaceById.get();
            String airspaceName = entity.getAirspaceName();
            Map<String, Object> dataMap = getAirspaceMap(entity);
            String fileName = airspaceName + ".docx";
            log.info("#AirspaceManageServiceImpl.exportAirspace# fileName={}", fileName);
            WordUtil.exportWord(fileName, dataMap, response);
            log.info("#AirspaceManageServiceImpl.exportAirspace# end airspaceId={}", airspaceId);
        }
    }

    @Override
    public void exportApprovalFile(HttpServletResponse response, String airspaceId) {
        Optional<AirspaceManageEntity> airspaceById = findAirspaceById(airspaceId);
        if (airspaceById.isPresent()) {
            try {
                AirspaceManageEntity entity = airspaceById.get();
                String approvalFileUrl = entity.getApprovalFileUrl();
                if (!StringUtils.hasText(approvalFileUrl)) {
                    return;
                }
                if ((approvalFileUrl.endsWith(".pdf"))) {
                    response.setContentType("application/pdf");
                    response.setHeader("content-disposition", "attachment; filename=" + URLEncoder.encode(entity.getAirspaceName() + "_批复文件.pdf", "UTF-8"));
                } else if ((approvalFileUrl.endsWith(".doc")) || (approvalFileUrl.endsWith(".docx"))) {
                    response.setContentType("application/msword");
                    response.setHeader("content-disposition", "attachment; filename=" + URLEncoder.encode(entity.getAirspaceName() + "_批复文件.doc", "UTF-8"));
                } else {
                    return;
                }
                try (InputStream is = fileManager.getInputSteam(approvalFileUrl)) {
                    ServletOutputStream outputStream = response.getOutputStream();
                    FastByteArrayOutputStream read = IoUtil.read(is);
                    byte[] bytes = read.toByteArray();
                    outputStream.write(bytes);

                } catch (Exception e) {
                    log.error("#AirsapceManageServiceImpl.exportAirspace.inputStreamError:", e);
                }
            } catch (Exception e) {
                log.error("#ArispaceManageServiceImpl.exportApprovalFile error:", e);
            }
        }
    }


    private Optional<AirspaceManageEntity> findAirspaceById(String AirspaceId) {
        if (StringUtils.hasText(AirspaceId)) {
            LambdaQueryWrapper<AirspaceManageEntity> con = Wrappers.lambdaQuery(AirspaceManageEntity.class)
                    .eq(AirspaceManageEntity::getAirspaceId, AirspaceId);
            return Optional.ofNullable(airspaceManageMapper.selectOne(con));
        }
        return Optional.empty();
    }

    /**
     * str -> localDateTime
     */
    private LocalDateTime strTranLocalDateTime(String time) {
        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd[['T'HH][:mm][:ss]]")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .toFormatter();
        return LocalDateTime.parse(time, fmt);
    }

    /**
     * 校验照片合法性
     */
    private String checkPhotoType(MultipartFile file) {
        String fileType;
        try (InputStream is = file.getInputStream()) {
            fileType = MinIoUnit.getFileType(is);
            if (!DataConstant.IMAGE_TYPE_LIST.contains(fileType)) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FILE_TYPE_IS_NOT_AN_IMAGE.getContent()));
            }
            if (file.getSize() > PHOTO_MAX_FILE_SIZE) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRSPACEMANAGESERVICEIMPL_04.getContent()));
            }
        } catch (Exception e) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_THE_FILE_STREAM.getContent()), e);
        }
        return fileType;
    }


    /**
     * localDateTime -> str
     */
    private String localDateTimeTranStr(LocalDateTime data) {
        return data.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * localDateTime -> 2001年8月1日
     */
    private String localDateTimeTranStrCN(LocalDateTime data) {
        String date = data.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String[] split = date.split("-");
        return split[0] + "年" + split[1] + "月" + split[2] + "号";
    }

    /**
     * 构建PO
     */
    private AirspaceManageCriteriaPO buildAirspaceCriteria(AirspacePageInDTO inDTO, String orgCodeFromAccount) {
        return AirspaceManageCriteriaPO.builder()
                .orgCodeFromAccount(orgCodeFromAccount)
                .airspaceName(inDTO.getAirspaceName())
                .orgCode(inDTO.getOrgCode())
                .build();
    }

    /**
     * 获取文件属性
     */
    private String getFileContentType(String filePath) {
        String suffixName = filePath.substring(filePath.lastIndexOf("."));
        return suffixName.replaceAll("//.", "");
    }

    private Map<String, Object> getAirspaceMap(AirspaceManageEntity entity) {
        String photoUrl = entity.getPhotoUrl();


        Map<String, Object> dataMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        String airCoor = entity.getAirCoor();
        String airCoored = dealAirCoor(airCoor);
        String downloadLink = fileManager.getDownloadLink(photoUrl);
//        String fileUrl = geoaiUosProperties.getDomain().getNginx() + photoUrl;
        dataMap.put("photoUrl", downloadLink);
        dataMap.put("orgCode", entity.getOrgName());
        dataMap.put("airCoor", airCoored);
        dataMap.put("year", String.valueOf(calendar.get(Calendar.YEAR)));
        dataMap.put("month", String.valueOf(calendar.get(Calendar.MONTH) + 1));
        dataMap.put("airspaceName", entity.getAirspaceName());
        dataMap.put("address", entity.getAddress());
        dataMap.put("airCoorCount", String.valueOf(entity.getAirCoorCount()));
        dataMap.put("aglAltitude", String.valueOf(entity.getAglAltitude()));
        dataMap.put("startTime", localDateTimeTranStrCN(entity.getStartTime()));
        dataMap.put("endTime", localDateTimeTranStrCN(entity.getEndTime()));
        AircraftCodeEnum aircraftCodeEnum = AircraftCodeEnum.getInstance(entity.getUavCode());
        dataMap.put("uavName", "机型：" + aircraftCodeEnum.getCode());
        if (ObjectUtils.isEmpty(entity.getUavCount())) {
            dataMap.put("uavCount", "");
        } else {
            dataMap.put("uavCount", "数量：" + entity.getUavCount() + "架");
        }
        if (ObjectUtils.isEmpty(entity.getAltitude())) {
            dataMap.put("altitude", "");
        } else {
            dataMap.put("altitude", String.valueOf(entity.getAltitude()));
        }
        return dataMap;
    }

    private String dealAirCoor(String airCoor) {
        StringBuilder sb = new StringBuilder();
        String[] airCoorArr = airCoor.split(" ");
        int index = 0;
        for (String coor : airCoorArr) {
            if (index > 50) break;
            String[] lngAndLat = coor.split(",");
            sb.append("E");
            sb.append(lngAndLat[0]);
            sb.append(",");
            sb.append("N");
            sb.append(lngAndLat[1]);
            sb.append(";");
            sb.append("\n");
            index++;
        }
        return sb.toString();
    }

    private String getFileName(String fileName) {
        String substring = fileName.substring(fileName.lastIndexOf("/")).substring(1);
        if (StringUtils.hasText(substring) && substring.contains(".")) {
            String[] split = substring.split("\\.");
            return split[0];
        }
        return null;
    }


    @Override
    public int selectNum(String airspaceId, String orgCode) {
        LambdaQueryWrapper queryWrapper = Wrappers.<AirspaceManageEntity>lambdaQuery().eq(AirspaceManageEntity::getAirspaceId, airspaceId)
                .likeRight(AirspaceManageEntity::getOrgCode, orgCode).eq(AirspaceManageEntity::getDeleted,false);
        Integer num = airspaceManageMapper.selectCount(queryWrapper);
        return num;
    }
}
