package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.enums.NestExternalEquipEnum;
import com.imapcloud.nest.mapper.DataAirMapper;
import com.imapcloud.nest.model.DataAirEntity;
import com.imapcloud.nest.model.NestExternalEquipEntity;
import com.imapcloud.nest.service.DataAirService;
import com.imapcloud.nest.service.NestExternalEquipService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-08-02
 */
@Service
public class DataAirServiceImpl extends ServiceImpl<DataAirMapper, DataAirEntity> implements DataAirService {

    @Resource
    private NestExternalEquipService nestExternalEquipService;

    /**
     * 设置气体传感器设备号ID
     *
     * @param nestUUid
     * @param serialId
     * @return
     */
    @Override
    public void setSerialId(String nestUUid, String serialId) {
        NestExternalEquipEntity nestExternalEquipEntity = NestExternalEquipEntity.builder()
                .externalEquipNo(serialId)
                .externalEquipType(NestExternalEquipEnum.EXTERNAL_EQUIP_1.getCode())
                .nestUuid(nestUUid)
                .build();
        this.nestExternalEquipService.addEquipEntity(nestExternalEquipEntity);
    }

    /**
     * 获取气体传感器设备号
     *
     * @param nestUUid
     * @return
     */
    @Override
    public String getSerialId(String nestUUid) {
        NestExternalEquipEntity nestExternalEquipEntity = this.nestExternalEquipService.getEntityByNest(nestUUid,null,NestExternalEquipEnum.EXTERNAL_EQUIP_1.getCode());
        return nestExternalEquipEntity.getExternalEquipNo();
    }

    /**
     * 通过设备号获取已经绑定的基站
     *
     * @param serialId
     * @return
     */
    @Override
    public List<String> getNestBySerialId(String serialId , String nestUuid) {
        List<NestExternalEquipEntity> nestExternalEquipEntityList = this.nestExternalEquipService.getEntityListByNo(serialId,NestExternalEquipEnum.EXTERNAL_EQUIP_1.getCode() , nestUuid);
        return nestExternalEquipEntityList.stream().map(e->e.getNestUuid()).collect(Collectors.toList());
    }


}
