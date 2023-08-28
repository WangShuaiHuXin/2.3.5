package com.imapcloud.nest.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.DataConstant;
import com.imapcloud.nest.enums.PersonTypeEnum;
import com.imapcloud.nest.utils.MinIoUnit;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.PersonManageEntity;
import com.imapcloud.nest.v2.dao.mapper.PersonManageMapper;
import com.imapcloud.nest.v2.dao.po.in.PersonManageCriteriaPO;
import com.imapcloud.nest.v2.service.PersonManageService;
import com.imapcloud.nest.v2.service.dto.in.PersonManageInDTO;
import com.imapcloud.nest.v2.service.dto.in.PersonManagePageInDTO;
import com.imapcloud.nest.v2.service.dto.out.PersonManageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.PersonManagePageOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Classname PersonManageServiceImpl
 * @Description 人员管理实现类
 * @Date 2023/3/28 14:04
 * @Author Carnival
 */
@Slf4j
@Service
public class PersonManageServiceImpl implements PersonManageService {

    @Resource
    private PersonManageMapper personManageMapper;

    private static final Integer PHOTO_MAX_FILE_SIZE = 50 * 1024 * 1024;

    private static final Integer Fail = 0;

    @Override
    public String addPerson(PersonManageInDTO inDTO) {
        PersonManageEntity entity = new PersonManageEntity();
        entity.setPersonId(BizIdUtils.snowflakeIdStr());
        setPersonEntity(entity, inDTO);
        int res = personManageMapper.insert(entity);
        if (res != Fail) {
            return entity.getPersonId();
        }
        return null;
    }

    @Override
    public Boolean deletePerson(String personId) {
        Optional<PersonManageEntity> personById = findPersonById(personId);
        if (personById.isPresent()) {
            PersonManageEntity entity = personById.get();
            int res = personManageMapper.deleteById(entity.getId());
            return res != Fail;
        }
        return false;
    }

    @Override
    public Boolean modifyPerson(String personId, PersonManageInDTO inDTO) {
        Optional<PersonManageEntity> personById = findPersonById(personId);
        if (personById.isPresent()) {
            PersonManageEntity entity = personById.get();
            setPersonEntity(entity, inDTO);
            int res = personManageMapper.updateById(entity);
            return res != Fail;
        }
        return false;
    }

    @Override
    public PersonManageOutDTO queryPersonInfo(String personId) {
        Optional<PersonManageEntity> personById = findPersonById(personId);
        if (personById.isPresent()) {
            PersonManageEntity entity = personById.get();
            PersonManageOutDTO outDTO = new PersonManageOutDTO();
            BeanUtils.copyProperties(entity, outDTO);
            return outDTO;
        }
        return null;
    }

    @Override
    public PageResultInfo<PersonManagePageOutDTO> pagePersonInfo(PersonManagePageInDTO inDTO) {
        String orgCodeFromAccount = TrustedAccessTracerHolder.get().getOrgCode();
        PersonManageCriteriaPO po = buildPersonCriteriaPO(inDTO, orgCodeFromAccount);
        long total = personManageMapper.countByCondition(po);
        List<PersonManagePageOutDTO> res = null;
        if (total > 0) {
            List<PersonManageEntity> rows = personManageMapper.selectByCondition(po, PagingRestrictDo.getPagingRestrict(inDTO));
            res = rows.stream()
                    .map(r -> {
                        PersonManagePageOutDTO pageOutDTO = new PersonManagePageOutDTO();
                        BeanUtils.copyProperties(r, pageOutDTO);
                        return pageOutDTO;
                    }).collect(Collectors.toList());
            return PageResultInfo.of(total, res);
        }
        return PageResultInfo.of(total, res);
    }

    @Override
    public PersonManageOutDTO.PersonManageCountOutDTO getTypeCount(String orgCode, String keyWord) {
        PersonManageOutDTO.PersonManageCountOutDTO outDTO = new PersonManageOutDTO.PersonManageCountOutDTO();
        if(StringUtils.isEmpty(orgCode)){
            orgCode=TrustedAccessTracerHolder.get().getOrgCode();
        }
        PersonManageCriteriaPO po = PersonManageCriteriaPO.builder().orgCode(orgCode).name(keyWord).build();
        long total = personManageMapper.countByCondition(po);
        if (total < 0) {
            return outDTO;
        }
        List<PersonManageEntity> personManageEntities = personManageMapper.selectByCondition(po, null);
        outDTO.setTotal(personManageEntities.size());
        Long count = personManageEntities.stream().filter(i -> PersonTypeEnum.driver.getCode().toString().equals(i.getPersonType())).count();
        outDTO.setDriTotal(count.intValue());
        count = personManageEntities.stream().filter(i -> PersonTypeEnum.cateGory.getCode().toString().equals(i.getPersonType())).count();
        outDTO.setOpeTotal(count.intValue());
        return outDTO;
    }

    //    @Override
//    public String uploadPhoto(PersonUploadFileInDTO inDTO) {
//        MultipartFile file = inDTO.getFile();
//        String imageType = checkPhotoType(file);
//        if (StringUtils.hasText(imageType)) {
//            Integer uploadType = inDTO.getUploadType();
//            String path = "";
//            if (0 == uploadType) {
//                path = UploadTypeEnum.ID_CARD_FRONT_URL.getPath();
//            } else if (1 == uploadType) {
//                path = UploadTypeEnum.ID_CARD_BACK_URL.getPath();
//            } else if (2 == uploadType) {
//                path = UploadTypeEnum.ID_CARD_DRIVE_URL.getPath();
//            }
//            String newFileName = String.format("%s.%s", BizIdUtils.snowflakeIdStr(), imageType);
//            String imagePath = String.format("%s%s", path, newFileName);
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

    /**
     * 查询人员信息
     */
    private Optional<PersonManageEntity> findPersonById(String personId) {
        if (StringUtils.hasText(personId)) {
            LambdaQueryWrapper<PersonManageEntity> con = Wrappers.lambdaQuery(PersonManageEntity.class)
                    .eq(PersonManageEntity::getPersonId, personId);
            return Optional.ofNullable(personManageMapper.selectOne(con));
        }
        return Optional.empty();
    }

    /**
     * 创建实体类信息
     */
    private void setPersonEntity(PersonManageEntity entity, PersonManageInDTO inDTO) {
        String personType = inDTO.getPersonType();
        entity.setPersonType(personType);
        entity.setOrgCode(inDTO.getOrgCode());
        entity.setOrgName(inDTO.getOrgName());
        entity.setName(inDTO.getName());
        entity.setGender(inDTO.getGender());
        entity.setMobile(inDTO.getMobile());
        entity.setIdCardType(inDTO.getIdCardType());
        entity.setIdCard(inDTO.getIdCard());
        if ("0".equals(personType)) {
            entity.setUserType(inDTO.getUserType());
            entity.setCreditCode(inDTO.getCreditCode());
            entity.setIdCardFrontUrl(null);
            entity.setIdCardBackUrl(null);
            entity.setDriveType(null);
            entity.setDriveLicenseType(null);
            entity.setDriveLicenseCode(null);
            entity.setIdCardDriveUrl(null);
        } else if ("1".equals(personType)) {
            entity.setUserType(null);
            entity.setCreditCode(null);
            entity.setIdCardFrontUrl(inDTO.getIdCardFrontUrl());
            entity.setIdCardBackUrl(inDTO.getIdCardBackUrl());
            entity.setDriveType(inDTO.getDriveType());
            entity.setDriveLicenseType(inDTO.getDriveLicenseType());
            entity.setDriveLicenseCode(inDTO.getDriveLicenseCode());
            entity.setIdCardDriveUrl(inDTO.getIdCardDriveUrl());
        }
    }

    private PersonManageCriteriaPO buildPersonCriteriaPO(PersonManagePageInDTO inDTO, String orgCodeFromAccount) {
        return PersonManageCriteriaPO.builder()
                .orgCodeFromAccount(orgCodeFromAccount)
                .orgCode(inDTO.getOrgCode())
                .name(inDTO.getName())
                .build();
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
}
