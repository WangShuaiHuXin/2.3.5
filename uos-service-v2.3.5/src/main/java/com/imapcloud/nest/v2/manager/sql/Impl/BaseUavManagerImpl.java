package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.enums.AircraftCodeEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.BaseUavEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseUavMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.BaseUavInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseUavOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseUavManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 无人机信息
 *
 * @author boluo
 * @date 2022-08-25
 */
@Slf4j
@Component
public class BaseUavManagerImpl implements BaseUavManager {

    @Resource
    private BaseUavMapper baseUavMapper;

    @Override
    public BaseUavOutDO.BaseUavEntityOutDO selectOneByUavId(String uavId) {
        LambdaQueryWrapper<BaseUavEntity> queryWrapper = Wrappers.lambdaQuery(BaseUavEntity.class)
                .eq(BaseUavEntity::getUavId, uavId);
        List<BaseUavEntity> baseUavEntityList = baseUavMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(baseUavEntityList)) {

            return toBaseUavEntityOutDO(baseUavEntityList.get(0));
        }
        return null;
    }

    private BaseUavOutDO.BaseUavEntityOutDO toBaseUavEntityOutDO(BaseUavEntity baseUavEntity) {
        BaseUavOutDO.BaseUavEntityOutDO baseUavOutDO = new BaseUavOutDO.BaseUavEntityOutDO();
        baseUavOutDO.setUavId(baseUavEntity.getUavId());
        baseUavOutDO.setUavNumber(baseUavEntity.getUavNumber());
        baseUavOutDO.setRcNumber(baseUavEntity.getRcNumber());
        baseUavOutDO.setCameraName(baseUavEntity.getCameraName());
        baseUavOutDO.setStreamId(baseUavEntity.getStreamId());
        baseUavOutDO.setType(baseUavEntity.getType());
        baseUavOutDO.setWhich(baseUavEntity.getWhich());
        baseUavOutDO.setRegisterCode(baseUavEntity.getRegisterCode());
        baseUavOutDO.setTakeoffWeight(baseUavEntity.getTakeoffWeight());


        /*中科天网*/
        baseUavOutDO.setUavPro(baseUavEntity.getUavPro());
        baseUavOutDO.setUavName(baseUavEntity.getUavName());
        baseUavOutDO.setUavType(baseUavEntity.getUavType());
        baseUavOutDO.setUavPattern(baseUavEntity.getUavPattern());
        baseUavOutDO.setUavSn(baseUavEntity.getUavSn());
        return baseUavOutDO;
    }

    private BaseUavEntity toBaseUavEntity(BaseUavInDO.BaseUavEntityInDO baseUavEntityInDO) {
        BaseUavEntity baseUavEntity = new BaseUavEntity();
        baseUavEntity.setUavId(baseUavEntityInDO.getUavId());
        baseUavEntity.setUavNumber(baseUavEntityInDO.getUavNumber() == null ? "" : baseUavEntityInDO.getUavNumber());
        baseUavEntity.setRcNumber(baseUavEntityInDO.getRcNumber() == null ? "" : baseUavEntityInDO.getRcNumber());
        baseUavEntity.setCameraName(baseUavEntityInDO.getCameraName());
        baseUavEntity.setStreamId(baseUavEntityInDO.getStreamId());
        baseUavEntity.setType(baseUavEntityInDO.getType());
        baseUavEntity.setWhich(baseUavEntityInDO.getWhich());
        baseUavEntity.setRegisterCode(baseUavEntityInDO.getRegisterCode());
        baseUavEntity.setTakeoffWeight(baseUavEntityInDO.getTakeoffWeight());

        /*中科天网*/
        baseUavEntity.setUavPro(baseUavEntityInDO.getUavPro());
        baseUavEntity.setUavName(baseUavEntityInDO.getUavName());
        baseUavEntity.setUavType(baseUavEntityInDO.getUavType());
        baseUavEntity.setUavPattern(baseUavEntityInDO.getUavPattern());
        baseUavEntity.setUavSn(baseUavEntityInDO.getUavSn());
        return baseUavEntity;
    }

    /**
     * 检测无人机型号是否合法
     */
    private void checkType(String type) {
        if (!NumberUtil.isNumber(type)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_LEGAL_DRONE_MODEL.getContent()));
        }
        if (!AircraftCodeEnum.checkCode(Integer.parseInt(type))) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_LEGAL_DRONE_MODEL.getContent()));
        }

    }

    @Override
    public int updateByUavId(BaseUavInDO.BaseUavEntityInDO baseUavEntityInDO) {

        if (StringUtils.isBlank(baseUavEntityInDO.getUavId())) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_DRONE_ID.getContent()));
        }
        BaseUavEntity baseUavEntity = toBaseUavEntity(baseUavEntityInDO);
        baseUavEntityInDO.setUpdateAccount(baseUavEntity);
        log.info("#BaseUavManagerImpl.updateByUavId# baseUavEntity={}", baseUavEntity);
        checkType(baseUavEntity.getType());
        return baseUavMapper.updateByUavId(baseUavEntity);
    }

    @Override
    public int insert(BaseUavInDO.BaseUavEntityInDO baseUavEntityInDO) {

        BaseUavEntity baseUavEntity = toBaseUavEntity(baseUavEntityInDO);
        baseUavEntityInDO.setInsertAccount(baseUavEntity);
        log.info("#BaseUavManagerImpl.insert# baseUavEntity={}", baseUavEntity);
        checkType(baseUavEntity.getType());
        return baseUavMapper.insert(baseUavEntity);
    }

    @Override
    public int deleteByUavIdList(List<String> uavIdList, String accountId) {
        return baseUavMapper.deleteByUavIdList(uavIdList, accountId);
    }

    @Override
    public List<BaseUavOutDO.BaseUavEntityOutDO> selectListByUavIdList(List<String> uavIdList) {
        LambdaQueryWrapper<BaseUavEntity> queryWrapper = Wrappers.lambdaQuery(BaseUavEntity.class)
                .in(BaseUavEntity::getUavId, uavIdList);
        List<BaseUavEntity> baseUavEntityList = baseUavMapper.selectList(queryWrapper);

        List<BaseUavOutDO.BaseUavEntityOutDO> result = Lists.newLinkedList();
        if (CollUtil.isNotEmpty(baseUavEntityList)) {

            for (BaseUavEntity uavEntity : baseUavEntityList) {
                result.add(toBaseUavEntityOutDO(uavEntity));
            }
            return result;
        }
        return null;
    }
}
