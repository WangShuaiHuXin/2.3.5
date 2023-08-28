package com.imapcloud.nest.v2.service.impl;

import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisOperationTipEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisOperationTipMapper;
import com.imapcloud.nest.v2.service.DataAnalysisOperationTipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisOperationTipServiceImpl.java
 * @Description DataAnalysisOperationTipServiceImpl
 * @createTime 2022年07月26日 19:41:00
 */
@Slf4j
@Service
public class DataAnalysisOperationTipServiceImpl implements DataAnalysisOperationTipService {

    @Resource
    private DataAnalysisOperationTipMapper dataAnalysisOperationTipMapper;
    /**
     * 设置是否要展示提示
     *
     * @param enable
     * @return
     */
    @Override
    public boolean setOperationTip(Integer enable) {
        Long creatorId = Long.valueOf(TrustedAccessTracerHolder.get().getAccountId());
        DataAnalysisOperationTipEntity operationTipEntity = this.dataAnalysisOperationTipMapper.query(creatorId);
        if(operationTipEntity!=null){
            DataAnalysisOperationTipEntity updateEntity = new DataAnalysisOperationTipEntity()
                    .setId(operationTipEntity.getId())
                            .setTip(enable);
            this.dataAnalysisOperationTipMapper.update(updateEntity);
        }else{
            DataAnalysisOperationTipEntity entity = new DataAnalysisOperationTipEntity()
                    .setCreatorId(creatorId)
                    .setTip(enable);
            this.dataAnalysisOperationTipMapper.insert(entity);
        }
        return true;
    }

    /**
     * 获取是否要展示提示
     *
     * @return
     */
    @Override
    public int getOperationTip() {
        int enable = 1;
        Long creatorId = Long.valueOf(TrustedAccessTracerHolder.get().getAccountId());
        DataAnalysisOperationTipEntity operationTipEntity = this.dataAnalysisOperationTipMapper.query(creatorId);
        if(operationTipEntity!=null){
            enable = operationTipEntity.getTip();
        }
        return enable;
    }
}
