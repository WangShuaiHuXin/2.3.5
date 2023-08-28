package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.NestConstant;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.mapper.NestExternalEquipMapper;
import com.imapcloud.nest.model.NestExternalEquipEntity;
import com.imapcloud.nest.service.NestExternalEquipService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.sdk.utils.StringUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName NestExternalEquipServiceImpl.java
 * @Description NestExternalEquipServiceImpl
 * @createTime 2022年06月21日 17:54:00
 */
@Service
public class NestExternalEquipServiceImpl extends ServiceImpl<NestExternalEquipMapper, NestExternalEquipEntity> implements NestExternalEquipService {

    /**
     * 通过设备号、设备类型获取实体
     * @param serialNo - equipNo
     * @param equipType
     * @return
     */
    @Override
    public NestExternalEquipEntity getEntityByNo(String serialNo , Integer equipType) {
        List<NestExternalEquipEntity> nestExternalEquipEntityList = new ArrayList<>();
        if(StringUtil.isNotEmpty(serialNo) && equipType!=null){
            nestExternalEquipEntityList = this.lambdaQuery()
                    .eq(NestExternalEquipEntity::getExternalEquipNo, serialNo)
                    .eq(NestExternalEquipEntity::getExternalEquipType, equipType)
                    .eq(NestExternalEquipEntity::getDeleted, NestConstant.DeleteType.NOT_DELETE)
                    .orderByDesc(NestExternalEquipEntity::getModifyTime)
                    .list();
        }
        return CollectionUtil.isEmpty(nestExternalEquipEntityList)? new NestExternalEquipEntity() : nestExternalEquipEntityList.get(0);
    }

    /**
     * 通过设备号获取实体列表
     *
     * @param serialNo
     * @param equipType
     * @return
     */
    @Override
    public List<NestExternalEquipEntity> getEntityListByNo(String serialNo, Integer equipType , String nestUuid) {
        List<NestExternalEquipEntity> nestExternalEquipEntityList = new ArrayList<>();
        if(StringUtil.isNotEmpty(serialNo) && equipType!=null){
            nestExternalEquipEntityList = this.lambdaQuery()
                    .eq(NestExternalEquipEntity::getExternalEquipNo, serialNo)
                    .eq(NestExternalEquipEntity::getExternalEquipType, equipType)
                    .ne(NestExternalEquipEntity::getNestUuid,nestUuid)
                    .eq(NestExternalEquipEntity::getDeleted, NestConstant.DeleteType.NOT_DELETE)
                    .orderByDesc(NestExternalEquipEntity::getModifyTime)
                    .list();
        }
        return nestExternalEquipEntityList;
    }

    /**
     * 通过基站查询对应绑定信息 -- 设备跟基站默认一对一
     *
     * @param nestUuid
     * @param nestId
     * @param equipType 设备类型
     * @return
     */
    @Override
    public NestExternalEquipEntity getEntityByNest(String nestUuid, String nestId, Integer equipType) {
        List<NestExternalEquipEntity> nestExternalEquipEntityList = new ArrayList<>();
        //nestUUID nestID 两者不能都为空，同时equipType不能为空
        boolean bol =  (StringUtil.isNotEmpty(nestUuid) || nestId!=null ) && equipType!=null;
        if( bol ){
            LambdaQueryChainWrapper<NestExternalEquipEntity> wrapper = this.lambdaQuery();
            Optional.ofNullable(nestUuid).ifPresent(uuid->wrapper.eq(NestExternalEquipEntity::getNestUuid,uuid));
            Optional.ofNullable(nestId).ifPresent(id->wrapper.eq(NestExternalEquipEntity::getBaseNestId,id));

            nestExternalEquipEntityList = wrapper
                    .eq(NestExternalEquipEntity::getExternalEquipType, equipType)
                    .eq(NestExternalEquipEntity::getDeleted, NestConstant.DeleteType.NOT_DELETE)
                    .orderByDesc(NestExternalEquipEntity::getModifyTime)
                    .list();
        }
        return CollectionUtil.isEmpty(nestExternalEquipEntityList)? new NestExternalEquipEntity() : nestExternalEquipEntityList.get(0);
    }

    /**
     * 新增绑定 - 先将当前设备绑定过的所有基站解绑，再添加绑定信息
     *
     * @param entity
     */
    @Override
    public void addEquipEntity(NestExternalEquipEntity entity) {
        Optional.ofNullable(entity)
                .filter(e-> (e.getBaseNestId()!=null || StringUtil.isNotEmpty(e.getNestUuid())) && StringUtil.isNotEmpty(e.getExternalEquipNo()) )
                .orElseThrow(()->new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INPUT_PARAMETER_CANNOT_BE_EMPTY.getContent())));
        //删除绑定关系
        this.delEquipEntity(entity.getNestUuid(),entity.getBaseNestId(),entity.getExternalEquipNo(),entity.getExternalEquipType() , Boolean.FALSE);
        this.saveOrUpdate(entity);
    }

    /**
     * 解除绑定
     *
     * @param nestUuid
     * @param nestId
     * @param serialNo - equipNo
     */
    @Override
    public void delEquipEntity(String nestUuid, String nestId, String serialNo , Integer equipType , boolean realDel) {
        LambdaUpdateChainWrapper<NestExternalEquipEntity> wrapper = this.lambdaUpdate();
        wrapper.eq(NestExternalEquipEntity::getExternalEquipNo,serialNo)
                .eq(NestExternalEquipEntity::getExternalEquipType,equipType);
        //解除所有关联过该气体传感器的记录
        if(!realDel){
            wrapper.set(NestExternalEquipEntity::getDeleted,NestConstant.DeleteType.DELETED).update();
        }else{
            wrapper.update(null);
        }
        //解除当前基站所有挂载的气体传感器
        LambdaUpdateChainWrapper<NestExternalEquipEntity> nestWrapper = this.lambdaUpdate();
        if(StringUtil.isEmpty(nestUuid) && nestId == null ) return;
        Optional.ofNullable(nestUuid).ifPresent(uuid->nestWrapper.eq(NestExternalEquipEntity::getNestUuid,uuid));
        Optional.ofNullable(nestId).ifPresent(id->nestWrapper.eq(NestExternalEquipEntity::getBaseNestId,id));
        if(!realDel){
            nestWrapper.set(NestExternalEquipEntity::getDeleted,NestConstant.DeleteType.DELETED).update();
        }else{
            nestWrapper.update(null);
        }
    }
}
