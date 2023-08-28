package com.imapcloud.nest.v2.service.impl;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.NestConstant;
import com.imapcloud.nest.sdk.CommonNestState;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.UosBatteryService;
import com.imapcloud.nest.v2.service.dto.out.BaseNestInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.BatteryEnableOutDTO;
import com.imapcloud.nest.v2.service.dto.out.BatteryOutDTO;
import com.imapcloud.nest.v2.service.dto.out.StreamOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.power.PowerManagerCf;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.pojo.entity.G503NestBatteryState;
import com.imapcloud.sdk.pojo.entity.M300NestBatteries;
import com.imapcloud.sdk.pojo.entity.M300NestBatteryBoard;
import com.imapcloud.sdk.pojo.entity.M300NestBatteryState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosBatteryServiceImpl.java
 * @Description UosBatteryServiceImpl
 * @createTime 2022年08月19日 10:04:00
 */
@Slf4j
@Service
public class UosBatteryServiceImpl implements UosBatteryService {

    @Resource
    private NestService nestService;

    @Resource
    private BaseNestService baseNestService;

    /**
     *
     * @param nestId
     * @param batteryGroupId
     * @param enable
     * @return
     */
    @Override
    public Boolean enableBatteryGroup(String nestId , Integer batteryGroupId, Integer enable) {
        StreamOutDTO outDTO = null;
        if (nestId == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PERFORM_DEACTIVATE_ENABLE_BATTERY_PACK_ACTION_NESTID_CANNOT_BE_EMPTY.getContent()));
        }

        ComponentManager cm = this.baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            throw new BusinessException(String.format("%s-机巢离线",nestId));
        }
        //校验
        BaseNestInfoOutDTO baseNestInfoOutDTO = this.baseNestService.getBaseNestInfo(nestId);

        if(ObjectUtils.isEmpty(baseNestInfoOutDTO)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXECUTES_THE_GET_BATTERY_PACK_ENABLE_STATUS_ACTION.getContent()) + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_CORRESPONDING_NEST.getContent()));
        }
        if(baseNestInfoOutDTO.getType()!=2){
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PERFORM_THE_GET_BATTERY_PACK_ENABLED_ACTION_ONLY_SUPPORTED_FOR_G900_NESTS.getContent()));
        }

        PowerManagerCf powerManagerCf = cm.getPowerManagerCf();
        MqttResult<NullParam> res = null;
        if(enable == 0){
            res = powerManagerCf.disenableG900BatteryGroup(batteryGroupId);
        }else{
            res = powerManagerCf.enableG900BatteryGroup(batteryGroupId);
        }
        if (!res.isSuccess()) {
            throw new BusinessException(String.format("设置电池停用启用失败-%s",res.getMsg()));
        }
        return Boolean.TRUE;
    }

    /**
     * 获取电池组停用、启用信息
     * @param nestId
     * @return
     */
    @Override
    public List<BatteryEnableOutDTO> getBatteryGroupEnable(String nestId) {
        List<BatteryEnableOutDTO> batteryEnableOutDTOList = new ArrayList<>();
        if (nestId == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXECUTE_THE_ACTION_OF_GETTING_THE_BATTERY_PACK_ENABLED_STATUS_NESTID_CANNOT_BE_EMPTY.getContent()));
        }
        BaseNestInfoOutDTO baseNestInfoOutDTO = this.baseNestService.getBaseNestInfo(nestId);

        if(ObjectUtils.isEmpty(baseNestInfoOutDTO)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXECUTES_THE_GET_BATTERY_PACK_ENABLE_STATUS_ACTION.getContent()) + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_CORRESPONDING_NEST.getContent()));
        }
        if(baseNestInfoOutDTO.getType()!=2){
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PERFORM_THE_GET_BATTERY_PACK_ENABLED_ACTION_ONLY_SUPPORTED_FOR_G900_NESTS.getContent()));
        }
        CommonNestState cns = CommonNestStateFactory.getInstance(baseNestInfoOutDTO.getUuid());
        if (cns != null) {
            M300NestBatteryState m300NestBatteryState = cns.getM300NestBatteryState();
            List<M300NestBatteryBoard> batteryBoards = m300NestBatteryState.getBatteryBoards()
                    .stream()
                    .sorted(Comparator.comparing(M300NestBatteryBoard::getLayer))
                    .collect(Collectors.toList());
            int groupIndex = 1 ;
            for (M300NestBatteryBoard batteryBoard : batteryBoards) {
                List<M300NestBatteries> batteryGroups = batteryBoard.getBatteryGroups()
                        .stream()
                        .sorted(Comparator.comparing(M300NestBatteries::getGroupId))
                        .collect(Collectors.toList());
                Integer layer = batteryBoard.getLayer();
                for (M300NestBatteries batteryGroup : batteryGroups) {
                    BatteryEnableOutDTO batteryEnableOutDTO = new BatteryEnableOutDTO();
                    batteryEnableOutDTO.setLayout(layer)
                            .setGroupId( groupIndex )
                            .setEnable(batteryGroup.getEnable()? NestConstant.CommonNum.ONE : NestConstant.CommonNum.ZREO);
                    batteryEnableOutDTOList.add(batteryEnableOutDTO);
                    groupIndex++;
                }
            }
        }
        return batteryEnableOutDTOList;
    }

    /**
     * 获取G503消息
     *
     * @param nestId
     * @return
     */
    @Override
    public List<BatteryOutDTO> getBatteryUseNums(String nestId) {
        List<BatteryOutDTO> outDTOList = new ArrayList<BatteryOutDTO>();
        BaseNestInfoOutDTO baseNestInfoOutDTO = this.baseNestService.getBaseNestInfo(nestId);
        Integer nestType = baseNestInfoOutDTO.getType();

        if(ObjectUtils.isEmpty(baseNestInfoOutDTO)) {
            throw new BusinessException("执行获取电池组信息，查询不到对应的基站信息");
        }
        if(nestType!=NestTypeEnum.G503.getValue()){
            throw new BusinessException("执行获取电池组使用情况，仅支持G503");
        }
        CommonNestState cns = CommonNestStateFactory.getInstance(baseNestInfoOutDTO.getUuid());
        if (cns != null) {
            G503NestBatteryState g503NestBatteryState = cns.getG503NestBatteryState();
            List<G503NestBatteryState.BatteryGroup> batteryGroups = g503NestBatteryState.getBatteryGroups();
            int which = 0 ;
            //三个无人机
            for(G503NestBatteryState.BatteryGroup batteryGroup : batteryGroups){
                which = ++which;
                BatteryOutDTO batteryOutDTO = new BatteryOutDTO();
                batteryOutDTO.setWhich(which);
                List<G503NestBatteryState.Battery> batteries = batteryGroup.getBatteries();
                List<BatteryOutDTO.BattyUseNumsOutDTO> battyUseNumsOutDTOList = new ArrayList<BatteryOutDTO.BattyUseNumsOutDTO>();
                int batteryIndex = 0;
                for(G503NestBatteryState.Battery battery: batteries){
                    batteryIndex = ++batteryIndex;
                    BatteryOutDTO.BattyUseNumsOutDTO battyUseNumsOutDTO = new BatteryOutDTO.BattyUseNumsOutDTO();
                    battyUseNumsOutDTO.setBatteryNum(String.valueOf(batteryIndex));
                    battyUseNumsOutDTO.setCharges(0);
                    battyUseNumsOutDTOList.add(battyUseNumsOutDTO);
                }
                batteryOutDTO.setBattyUseNumsRespVOList(battyUseNumsOutDTOList);
                outDTOList.add(batteryOutDTO);
            }
        }
        return outDTOList;
    }
}
