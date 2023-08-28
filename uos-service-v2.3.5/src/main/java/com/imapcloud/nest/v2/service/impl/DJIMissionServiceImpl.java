package com.imapcloud.nest.v2.service.impl;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.DJIMissionService;
import com.imapcloud.nest.v2.service.dto.out.DJICommonResultOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.DjiMqttResult;
import com.imapcloud.sdk.manager.dji.DjiDockManagerCf;
import com.imapcloud.sdk.pojo.constant.dji.DjiErrorCodeEnum;
import com.imapcloud.sdk.pojo.djido.DjiCommonDO;
import com.imapcloud.sdk.pojo.djido.DjiCommonDataDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJICommonServiceImpl.java
 * @Description DJICommonServiceImpl
 * @createTime 2022年10月19日 15:53:00
 */
@Slf4j
@Service
public class DJIMissionServiceImpl implements DJIMissionService {

    @Resource
    private BaseNestService baseNestService;

    /**
     * 获取Manager
     * @param nestId
     * @return
     */
    public DjiDockManagerCf getDjiDockManagerCf(String nestId){
        String uuid = this.baseNestService.getNestUuidByNestIdInCache(nestId);
        ComponentManager cm = ComponentManagerFactory.getInstance(uuid);
        if (cm == null) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_DOCK_OFFLINE.getContent() , new Object[]{uuid},""));
        }
        DjiDockManagerCf djiDockManagerCf = cm.getDjiDockManagerCf();
        return djiDockManagerCf;
    }

    /**
     * 组装返回DTO
     * @param result
     * @return
     */
    public DJICommonResultOutDTO.CommonResultOutDTO getResult(DjiMqttResult<DjiCommonDataDO> result){
        DJICommonResultOutDTO.CommonResultOutDTO resultOut = new DJICommonResultOutDTO.CommonResultOutDTO();
        resultOut.setResult(result.isSuccess()?1:0);
        Integer resultCode = Optional.ofNullable(result)
                .map(DjiMqttResult::getCommonDO)
                .map(DjiCommonDO::getData)
                .map(DjiCommonDataDO::getResult)
                .orElseGet(()->-1);
        resultOut.setStatusCode(String.valueOf(resultCode));
        resultOut.setStatusStr(DjiErrorCodeEnum.getMsg(resultCode));
        return resultOut;
    }

    /**
     * 取消任务
     * @param nestId
     * @param fightIds
     * @return
     */
    @Override
    public DJICommonResultOutDTO.CommonResultOutDTO flightTaskUndo(String nestId, List<String> fightIds) {
        DJICommonResultOutDTO.CommonResultOutDTO outDTO =  new DJICommonResultOutDTO.CommonResultOutDTO();
        outDTO.setResult(0);
        DjiMqttResult<DjiCommonDataDO> outDO  = new DjiMqttResult<DjiCommonDataDO>();
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(nestId);
        outDO = djiDockManagerCf.flightTaskUndo(fightIds);
        if(!outDO.isSuccess()){
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_COMMON_ACTION_ERR.getContent() , new Object[]{outDO.getErrMsg()},""));
        }
        outDTO = this.getResult(outDO);
        return outDTO;
    }
}
