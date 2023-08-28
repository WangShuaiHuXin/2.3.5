package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.enums.TaskModeEnum;
import com.imapcloud.nest.mapper.TransmissionTowerMapper;
import com.imapcloud.nest.model.TransmissionLineEntity;
import com.imapcloud.nest.model.TransmissionTowerEntity;
import com.imapcloud.nest.pojo.dto.TaskDetailsDto;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.service.TaskService;
import com.imapcloud.nest.service.TransmissionLineService;
import com.imapcloud.nest.service.TransmissionTowerService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 输电线路杆塔表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2021-08-17
 */
@Service
public class TransmissionTowerServiceImpl extends ServiceImpl<TransmissionTowerMapper, TransmissionTowerEntity> implements TransmissionTowerService {

    @Autowired
    private TransmissionLineService transmissionLineService;

    @Autowired
    private NestService nestService;

    @Autowired
    private BaseNestService baseNestService;

    @Autowired
    private TaskService taskService;

    @Override
    public RestRes createTaskByTowerAndNestUuid(String towerUuid, String nestUuid) {
        TransmissionTowerEntity towerEntity = this.lambdaQuery()
                .eq(TransmissionTowerEntity::getUuid, towerUuid)
                .eq(TransmissionTowerEntity::getDeleted, false)
                .one();

        if (towerEntity == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_CORRESPONDING_TOWER.getContent()));
        }

        TransmissionLineEntity transmissionLineEntity = transmissionLineService.lambdaQuery()
                .eq(TransmissionLineEntity::getId, towerEntity.getTransLineId())
                .eq(TransmissionLineEntity::getDeleted, false)
                .one();

        if (transmissionLineEntity == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_CORRESPONDING_TRANSMISSION_ROUTE.getContent()));
        }


        String nestId = transmissionLineEntity.getBaseNestId();
//        NestEntity nestEntity = nestService.lambdaQuery().eq(NestEntity::getDeleted, false)
//                .eq(NestEntity::getId, nestId)
//                .select(NestEntity::getUuid)
//                .one();
        String nestUuid1 = baseNestService.getNestUuidByNestIdInCache(nestId);

        if (Objects.isNull(nestUuid1)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_CORRESPONDING_NEST.getContent()));
        }

        if (!nestUuid1.equals(nestUuid)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MISMATCH_NEST_AND_TOWER.getContent()));
        }

        //生成TaskDetailsDto，然后调用taskService.saveTaskDetailsDto();
        Map<Integer, String> airLineMap = new HashMap<>(2);
        airLineMap.put(0, towerEntity.getRoute());

        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();

        String taskName = transmissionLineEntity.getName() + (transmissionLineEntity.getPrimary() ? "(主)" : "(次)") + "之" + towerEntity.getName() + "任务";
        TaskDetailsDto taskDetailsDto = new TaskDetailsDto().setName(taskName)
                .setMold(0)
                .setUnitId(orgCode)
                .setTaskType(TaskModeEnum.LOCAL.getValue())
                .setPhotoCount(0)
                .setPredictFlyTime(0L)
                .setPredictMiles(0.0)
                .setPointCount(0)
                .setAircraftType("大疆 精灵4 RTK")
                .setHeadingMode("AUTO")
                .setStartStopAlt(100.0)
                .setAutoFlightSpeed(4)
                .setSpeed(4)
                .setDescription("这是杆塔航线任务")
                .setNestId(nestId)
                .setAirLineType(2)
                .setAirLineMap(airLineMap);

        return taskService.saveTaskDetailsDto(taskDetailsDto);
    }
}
