package com.imapcloud.nest.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.util.BizIdUtils;
import com.imapcloud.nest.service.PersonInfoService;
import com.imapcloud.nest.v2.dao.entity.AirspaceManageEntity;
import com.imapcloud.nest.v2.dao.entity.PersonInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.PersonInfoMapper;
import com.imapcloud.nest.v2.service.dto.in.PersonInfoInDTO;
import com.imapcloud.nest.v2.service.dto.out.PersonInfoOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @Classname PersonInfoServiceImpl
 * @Description 个人信息实现类
 * @Date 2023/3/10 9:07
 * @Author Carnival
 */
@Slf4j
@Service
public class PersonInfoServiceImpl implements PersonInfoService {

    @Resource
    private PersonInfoMapper personInfoMapper;

    private static final Integer Fail = 0;

    @Override
    public Boolean savaOrUpdatePersonInfo(PersonInfoInDTO inDTO) {
        String accountId = inDTO.getAccountId();
        Optional<PersonInfoEntity> personInfoByAccountId = findPersonInfoByAccountId(accountId);
        // 修改
        if (personInfoByAccountId.isPresent()) {
            PersonInfoEntity personInfoEntity = personInfoByAccountId.get();
            personInfoEntity.setPersonIp(inDTO.getIP());
            personInfoEntity.setLicenceCode(inDTO.getLicenceCode());
            int res = personInfoMapper.updateById(personInfoEntity);
            return res != Fail;
        }
        // 新增
        else {
            PersonInfoEntity personInfoEntity = new PersonInfoEntity();
            personInfoEntity.setAccountId(inDTO.getAccountId());
            personInfoEntity.setPersonInfoId(BizIdUtils.snowflakeIdStr());
            personInfoEntity.setPersonIp(inDTO.getIP());
            personInfoEntity.setLicenceCode(inDTO.getLicenceCode());
            int res = personInfoMapper.insert(personInfoEntity);
            return res != Fail;
        }
    }

    @Override
    public PersonInfoOutDTO queryPersonInfo(String accountId) {
        PersonInfoOutDTO res = new PersonInfoOutDTO();
        Optional<PersonInfoEntity> personInfoByAccountId = findPersonInfoByAccountId(accountId);
        if (personInfoByAccountId.isPresent()) {
            PersonInfoEntity personInfoEntity = personInfoByAccountId.get();
            res.setAccountId(personInfoEntity.getAccountId());
            res.setIP(personInfoEntity.getPersonIp());
            res.setLicenceCode(personInfoEntity.getLicenceCode());
        }

        return res;
    }

    private Optional<PersonInfoEntity> findPersonInfoByAccountId(String accountId) {
        if (StringUtils.hasText(accountId)) {
            LambdaQueryWrapper<PersonInfoEntity> con = Wrappers.lambdaQuery(PersonInfoEntity.class)
                    .eq(PersonInfoEntity::getAccountId, accountId);
            return Optional.ofNullable(personInfoMapper.selectOne(con));
        }
        return Optional.empty();
    }
}
