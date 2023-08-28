package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.service.SysUnitService;
import com.imapcloud.nest.v2.common.utils.PageConverterUtils;
import com.imapcloud.nest.v2.manager.dataobj.in.OrgPageQueryDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.service.dto.UnitEntityDTO;
import com.imapcloud.nest.v2.service.dto.UnitNodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 单位信息表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Service
@Slf4j
public class SysUnitServiceImpl implements SysUnitService {

    /**
     * 单位编码切割长度
     */
    private static final int SPLICE_LENGTH = 3;

    @Resource
    private UosOrgManager uosOrgManager;

    @Override
    public List<String> getSuperiorOrgCodes(String orgCode) {
        if(StringUtils.hasText(orgCode) && orgCode.length() > SPLICE_LENGTH){
            // 000001002
            List<String> superiorOrgCodes = new ArrayList<>();
            // 2
            int size = orgCode.length() / SPLICE_LENGTH - 1;
            for (int i = 0; i < size; i++){
                // 000
                // 000001
                String superiorOrgCode = orgCode.substring(0, (i + 1) * SPLICE_LENGTH);
                superiorOrgCodes.add(superiorOrgCode);
            }
            return superiorOrgCodes;
        }
        return Collections.emptyList();
    }

    @Override
    public List<UnitNodeDTO> getUnitNodeInfos(List<String> unitIds) {
        if(!CollectionUtils.isEmpty(unitIds)){
            return unitIds.stream()
                    .filter(StringUtils::hasText)
                    .map(r -> {
                        UnitNodeDTO unitNodeDTO = new UnitNodeDTO();
                        unitNodeDTO.setUnitId(r);
                        if(r.length() > SPLICE_LENGTH){
                            unitNodeDTO.setParentUnitId(r.substring(0, r.length() - SPLICE_LENGTH));
                        }
                        return unitNodeDTO;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public UnitEntityDTO getById(String orgCode) {
        Optional<OrgSimpleOutDO> optional = uosOrgManager.getOrgInfo(orgCode);
        if(optional.isPresent()){
            return optional.map(r -> {
                        UnitEntityDTO ue = new UnitEntityDTO();
                        ue.setId(r.getOrgCode());
                        ue.setName(r.getOrgName());
                        ue.setLatitude(r.getLatitude());
                        ue.setLongitude(r.getLongitude());
                        return ue;
                     })
                    .get();
        }
        return null;
    }

    @Override
    public IPage<UnitEntityDTO> listSysUnitByPages(Integer pageNo, Integer pageSize, String name) {
        if (pageNo != null && pageSize != null) {
            try {
                OrgPageQueryDO condition = new OrgPageQueryDO();
                condition.setOrgName(name);
                condition.setPageNo(pageNo);
                condition.setPageSize(pageSize);
                PageResultInfo<UnitEntityDTO> page = uosOrgManager.queryOrgInfo(condition)
                        .map(r -> {
                            UnitEntityDTO entity = new UnitEntityDTO();
                            entity.setId(r.getOrgCode());
                            entity.setName(r.getOrgName());
                            // FIXME vastfy：确认前端是否只用到了这些值
                            if(StringUtils.hasText(r.getLongitude())){
                                entity.setLongitude(Double.valueOf(r.getLongitude()));
                            }
                            if(StringUtils.hasText(r.getLatitude())){
                                entity.setLatitude(Double.valueOf(r.getLatitude()));
                            }
                            return entity;
                        });

                return PageConverterUtils.convertToOld(page);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("分页出现问题： {}", e.getMessage());
                return null;
            }
        }
        return null;
    }

}
