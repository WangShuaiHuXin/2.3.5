package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.v2.dao.entity.SysOrgPilotSpaceEntity;
import com.imapcloud.nest.v2.dao.mapper.SysOrgPilotSpaceMapper;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.service.SysOrgPilotSpaceService;
import com.imapcloud.nest.v2.service.converter.SysOrgPilotSpaceConverter;
import com.imapcloud.nest.v2.service.dto.in.SysOrgPilotSpaceInDTO;
import com.imapcloud.nest.v2.service.dto.out.SysOrgPilotSpaceOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysOrgPilotSpaceServiceImpl implements SysOrgPilotSpaceService {

    @Resource
    private SysOrgPilotSpaceMapper sysOrgPilotSpaceMapper;

    @Resource
    private UosOrgManager uosOrgManager;


    @Override
    public SysOrgPilotSpaceOutDTO getSysOrgPilotSpace(String orgCode , String workSpaceId) {
        String orgCodeContext = TrustedAccessTracerHolder.get().getOrgCode();
        log.info("orgCode -> {} ,orgCodeContext -> {}" , orgCode , orgCodeContext);
        SysOrgPilotSpaceOutDTO outDTO = new SysOrgPilotSpaceOutDTO();
        if(StringUtils.isEmpty(orgCode)){
            orgCode = orgCodeContext;
        }
        List<SysOrgPilotSpaceEntity> sysOrgPilotSpaceEntityList =  this.sysOrgPilotSpaceMapper.query(orgCode , workSpaceId);
        if(!CollectionUtil.isEmpty(sysOrgPilotSpaceEntityList)){
            outDTO = SysOrgPilotSpaceConverter.INSTANCES.convert(sysOrgPilotSpaceEntityList.stream()
                    .findFirst()
                    .orElseGet(()->new SysOrgPilotSpaceEntity()));
            OrgSimpleOutDO orgSimpleOutDO = this.uosOrgManager.getOrgInfo(orgCode).orElseGet(()->new OrgSimpleOutDO());
            outDTO.setOrgName(orgSimpleOutDO.getOrgName());
        }
        return outDTO;
    }

    @Override
    public List<String> querySysOrgPilotList() {
        List<String> orgCodeList = new ArrayList<>();
        List<SysOrgPilotSpaceEntity> sysOrgPilotSpaceEntityList = this.sysOrgPilotSpaceMapper.query("","");
        if(CollectionUtil.isEmpty(sysOrgPilotSpaceEntityList)){
            return orgCodeList;
        }
        orgCodeList = sysOrgPilotSpaceEntityList.stream().map(SysOrgPilotSpaceEntity::getOrgCode).collect(Collectors.toList());
        return orgCodeList;
    }

    @Override
    public boolean insertSysOrgPilot(List<SysOrgPilotSpaceInDTO> sysOrgPilotSpaceInDTOS) {
        List<SysOrgPilotSpaceEntity> sysOrgPilotSpaceEntityList = new ArrayList<>();
        if(CollectionUtil.isEmpty(sysOrgPilotSpaceInDTOS)){
            return true;
        }
        sysOrgPilotSpaceInDTOS.stream().forEach(x->{
            SysOrgPilotSpaceEntity sysOrgPilotSpaceEntity = new SysOrgPilotSpaceEntity();
            sysOrgPilotSpaceEntity.setOrgCode(x.getOrgCode());
            sysOrgPilotSpaceEntity.setWorkSpaceId(UUID.randomUUID().toString());
            sysOrgPilotSpaceEntityList.add(sysOrgPilotSpaceEntity);
        });
        this.sysOrgPilotSpaceMapper.saveBatch(sysOrgPilotSpaceEntityList);
        return true;
    }
}
