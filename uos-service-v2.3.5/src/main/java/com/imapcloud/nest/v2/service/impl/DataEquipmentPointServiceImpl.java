package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.DataEquipmentPointEntity;
import com.imapcloud.nest.v2.manager.dataobj.in.DataEquipmentPintRelQueryInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.DataEquipmentPointInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.DataEquipmentPointQueryInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DataEquipmentPintRelQueryOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DataEquipmentPointQueryOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerEquipmentInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerWaypointListInfoOutDO;
import com.imapcloud.nest.v2.manager.sql.DataEquipmentPointManager;
import com.imapcloud.nest.v2.manager.sql.PowerEquipmentLegerInfoManager;
import com.imapcloud.nest.v2.manager.sql.PowerWaypointLedgerInfoManager;
import com.imapcloud.nest.v2.service.DataEquipmentPointService;
import com.imapcloud.nest.v2.service.dto.in.DataEquipmentPointInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataEquipmentPointQueryInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataEquipmentPointListOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataEquipmentPointQueryOutDTO;
import com.imapcloud.sdk.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataEquipmentPointServiceImpl implements DataEquipmentPointService {


    @Resource
    private DataEquipmentPointManager dataEquipmentPointManager;

    @Resource
    private PowerEquipmentLegerInfoManager powerEquipmentLegerInfoManager;

    @Resource
    private PowerWaypointLedgerInfoManager powerWaypointLedgerInfoManager;

    @Override
    public boolean addEquipmentPoint(DataEquipmentPointInDTO dto) {
        //初始化实体类数据
        if (CollectionUtil.isEmpty(dto.getEquipmentList())) {
            throw new BusinessException("关联设备不允许为空");
        }
        DataEquipmentPointInDO inDO = new DataEquipmentPointInDO();
        BeanUtils.copyProperties(dto, inDO);
        inDO.setEquipmentList(dto.getEquipmentList());
        return dataEquipmentPointManager.save(inDO);
    }

    @Override
    public boolean editEquipmentPoint(DataEquipmentPointInDTO dto) {
        if (CollectionUtil.isEmpty(dto.getEquipmentList())) {
            throw new BusinessException("关联设备不允许为空");
        }
        DataEquipmentPointInDO inDO = new DataEquipmentPointInDO();
        BeanUtils.copyProperties(dto, inDO);
        inDO.setEquipmentList(dto.getEquipmentList());
        return dataEquipmentPointManager.update(inDO);
    }

    @Override
    public boolean deleteBatchEquipmentPoint(List<String> deletes) {
        boolean flag = dataEquipmentPointManager.deleteBatch(deletes);
        return flag;
    }

    @Override
    public DataEquipmentPointQueryOutDTO queryPageEquipmentPoint(DataEquipmentPointQueryInDTO inDto) {
        DataEquipmentPointQueryInDO build = DataEquipmentPointQueryInDO.builder().keyWord(inDto.getKeyWord())
                .pageNo(inDto.getPageNo())
                .pageSize(inDto.getPageSize())
                .tagId(inDto.getTagId())
                .pointId(inDto.getPointId())
                .orgCode(inDto.getOrgCode()).build();
        DataEquipmentPointQueryOutDO outDO = dataEquipmentPointManager.queryByCondition(build);
        DataEquipmentPointQueryOutDTO dto = new DataEquipmentPointQueryOutDTO();
        if (outDO.getTotal() > 0) {
            List<DataEquipmentPointQueryOutDO.DataEquipmentPointQueryInfoOutDO> dos = outDO.getDtos();
            List<DataEquipmentPointQueryOutDTO.DataEquipmentPointQueryInfoDTO> collect = dos.stream().map(e -> {
                DataEquipmentPointQueryOutDTO.DataEquipmentPointQueryInfoDTO infoDTO = new DataEquipmentPointQueryOutDTO.DataEquipmentPointQueryInfoDTO();
                BeanUtils.copyProperties(e, infoDTO);
                List<DataEquipmentPointQueryOutDO.DataEquipmentInfoOutDO> equipmentList = e.getEquipmentList();
                if (CollectionUtil.isNotEmpty(equipmentList)) {
                    List<DataEquipmentPointQueryOutDTO.DataEquipmentQueryInfoDTO> equipmentQueryInfoDTOS = equipmentList.stream().map(item -> {
                        DataEquipmentPointQueryOutDTO.DataEquipmentQueryInfoDTO queryInfoDTO = new DataEquipmentPointQueryOutDTO.DataEquipmentQueryInfoDTO();
                        queryInfoDTO.setEquipmentId(item.getEquipmentId());
                        queryInfoDTO.setEquipmentName(item.getEquipmentName());
                        return queryInfoDTO;
                    }).collect(Collectors.toList());
                    infoDTO.setEquipmentList(equipmentQueryInfoDTOS);
                }
                return infoDTO;
            }).collect(Collectors.toList());
            dto.setDtos(collect);
        }
        dto.setTotal(outDO.getTotal());
        return dto;
    }

    @Override
    public List<DataEquipmentPointListOutDTO> queryEquipPointList(String orgCode) {
        //查询单位下所有的设备信息
        List<DataEquipmentPointListOutDTO> result =new ArrayList<>();
        List<PowerEquipmentInfoOutDO> dos = powerEquipmentLegerInfoManager.queryListByOrg(orgCode);
        if(CollectionUtil.isEmpty(dos)){
            return result;
        }
        //查询当前单位下所有已匹配的设备信息
        DataEquipmentPintRelQueryInDO build = DataEquipmentPintRelQueryInDO.builder().orgCode(orgCode).build();
        List<DataEquipmentPintRelQueryOutDO> dataEquipmentPintRelQueryOutDOS = dataEquipmentPointManager.queryRelByCondition(build);
        List<String> matchedIds = dataEquipmentPintRelQueryOutDOS.stream().map(DataEquipmentPintRelQueryOutDO::getEquipmentId).collect(Collectors.toList());

        //查询根据设备ID查询所有航点信息
        PowerWaypointListInfoOutDO powerWaypointListInfoOutDO = powerWaypointLedgerInfoManager.queryWaypointListByOrg(orgCode);
        Map<String, List<PowerWaypointListInfoOutDO.PowerWaypointInfoDO>> collect = new HashMap<>();
        if (CollectionUtil.isNotEmpty(powerWaypointListInfoOutDO.getInfoDTOList())) {
            collect = powerWaypointListInfoOutDO.getInfoDTOList().stream().filter(e -> StringUtils.isNotEmpty(e.getEquipmentId())).collect(Collectors.groupingBy(PowerWaypointListInfoOutDO.PowerWaypointInfoDO::getEquipmentId));
        }
        Map<String, List<PowerWaypointListInfoOutDO.PowerWaypointInfoDO>> finalCollect = collect;
        result = dos.stream().map(item -> {
            DataEquipmentPointListOutDTO dto = new DataEquipmentPointListOutDTO();
            dto.setEquipmentId(item.getEquipmentId());
            dto.setEquipmentName(item.getEquipmentName());
            if (matchedIds.contains(item.getEquipmentId())) {
                dto.setAffiliateStatus(true);
            } else {
                dto.setAffiliateStatus(false);
            }
            List<PowerWaypointListInfoOutDO.PowerWaypointInfoDO> powerWaypointInfoDOS = finalCollect.get(item.getEquipmentId());
            if (CollectionUtil.isNotEmpty(powerWaypointInfoDOS)) {
                PowerWaypointListInfoOutDO.PowerWaypointInfoDO powerWaypointInfoDO = powerWaypointInfoDOS.get(0);
                dto.setLng(powerWaypointInfoDO.getLongitude());
                dto.setLat(powerWaypointInfoDO.getLatitude());
                dto.setHeight(powerWaypointInfoDO.getAltitude());
            }
            return dto;
        }).collect(Collectors.toList());
        return result;
    }
}
