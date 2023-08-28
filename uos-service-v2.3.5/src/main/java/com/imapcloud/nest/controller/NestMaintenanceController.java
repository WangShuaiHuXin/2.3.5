package com.imapcloud.nest.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.model.NestMaintenanceEntity;
import com.imapcloud.nest.model.NestMaintenanceMessageUserRelEntity;
import com.imapcloud.nest.model.NestMaintenanceProjectEntity;
import com.imapcloud.nest.pojo.dto.FlightStatisticsDto;
import com.imapcloud.nest.pojo.dto.NestMaintenanceDTO;
import com.imapcloud.nest.pojo.dto.SaveNestMaintenanceProjectDto;
import com.imapcloud.nest.service.NestMaintenanceMessageUserRelService;
import com.imapcloud.nest.service.NestMaintenanceProjectService;
import com.imapcloud.nest.service.NestMaintenanceService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.data.entity.FlightStatisticsEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p>
 * 机巢维保记录表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-05-14
 */
@RestController
@RequestMapping("/maintenance")
public class NestMaintenanceController {

    @Autowired
    private NestMaintenanceService nestMaintenanceService;

    @Autowired
    private NestMaintenanceProjectService nestMaintenanceProjectService;

    @Autowired
    private NestMaintenanceMessageUserRelService nestMaintenanceMessageUserRelService;

    @Autowired
    private NestService nestService;

    @Autowired
    private BaseNestService baseNestService;

    @Autowired
    private RedisService redisService;

    /**
     * 维护项目记录分页查询
     *
     * @param params 查询参数
     */
    @GetMapping("/nest/page")
    public RestRes queryPage(@RequestParam Map<String, Object> params) {
        return nestMaintenanceService.queryPage(params);
    }

    @PostMapping("/nest/save")
    public RestRes saveNestMaintenance(@RequestBody NestMaintenanceEntity entity) {
        nestMaintenanceService.save(entity);
        return RestRes.ok();
    }

//    /**
//     * 下载附件
//     * @deprecated 2.2.3，前端确认未用到，将在后续版本删除
//     */
//    @Deprecated
//    @PostMapping("/download/Attachment")
//    public RestRes downloadAttachment(HttpServletResponse response, String filePath) {
//        return nestMaintenanceService.downloadAttachment(response, filePath);
//    }

    /**
     * 创建一条维保记录
     *
     * @param nestMaintenanceDTO
     * @param bindingResult
     * @return
     */
    @PostMapping("/create/nest/maintenance/record")
    public RestRes createNestMaintenanceRecord(@RequestBody @Valid NestMaintenanceDTO nestMaintenanceDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()));
        }
        return nestMaintenanceService.createNestMaintenanceRecord(nestMaintenanceDTO);
    }

    /**
     * 查询维保项目
     *
     * @param type
     * @return
     */
    @GetMapping("/list/nest/maintenance/project/{type}")
    public RestRes listNestMaintenanceProjectById(@PathVariable Integer type) {
        if (type != null) {
            Map<String, Object> resMap = new HashMap<>(2);
            List<NestMaintenanceProjectEntity> nestMaintenanceProjectEntities = nestMaintenanceProjectService.listByType(type);
            resMap.put("projectList", nestMaintenanceProjectEntities);
            return RestRes.ok(resMap);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    /**
     * 新增维保项目
     */
    @PostMapping("/list/nest/maintenance/project/save")
    public RestRes saveNestMaintenanceProject(@RequestBody NestMaintenanceProjectEntity entity) {
        nestMaintenanceProjectService.save(entity);
        return RestRes.ok();
    }

    /**
     * 新增维保项目
     */
    @PostMapping("/list/nest/maintenance/project/saveBatch")
    public RestRes saveBatchNestMaintenanceProject(@RequestBody SaveNestMaintenanceProjectDto dto) {
        return nestMaintenanceProjectService.saveProject(dto);
    }

    /**
     * 更新维保项目
     */
    @PostMapping("/list/nest/maintenance/project/update")
    public RestRes updateNestMaintenanceProject(@RequestBody NestMaintenanceProjectEntity entity) {
        nestMaintenanceProjectService.updateById(entity);
        return RestRes.ok();
    }

    /**
     * 批量更新维保项目
     */
    @PostMapping("/list/nest/maintenance/project/updateBatch")
    public RestRes updateBatchNestMaintenanceProject(@RequestBody List<NestMaintenanceProjectEntity> entities) {
        nestMaintenanceProjectService.updateBatchById(entities);
        return RestRes.ok();
    }

    /**
     * 删除维保项目
     */
    @PostMapping("/list/nest/maintenance/project/delete")
    public RestRes deleteNestMaintenanceProject(@RequestBody List<Integer> ids) {
        nestMaintenanceProjectService.removeByIds(ids);
        return RestRes.ok();
    }

    @PostMapping("/batch/read/maintenance/message")
    public RestRes batchReadMaintenanceMessage(List<Integer> msgIdList, Integer userId) {
        if (CollectionUtil.isNotEmpty(msgIdList) && userId != null) {
            List<NestMaintenanceMessageUserRelEntity> list = new ArrayList<>(msgIdList.size());
            for (Integer msgId : msgIdList) {
                NestMaintenanceMessageUserRelEntity nmmure = new NestMaintenanceMessageUserRelEntity();
                nmmure.setMessageId(msgId);
                nmmure.setUserId(userId);
                nmmure.setReaded(1);
                list.add(nmmure);
            }
            boolean saveBatch = nestMaintenanceMessageUserRelService.saveBatch(list);
            if (saveBatch) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MESSAGE_MARKED_AS_READ.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @PostMapping("/batch/delete/nest/maintenance/record")
    public RestRes batchDeleteNestMaintenanceRecord(@RequestBody List<Integer> recordIdList) {
        if (CollectionUtil.isNotEmpty(recordIdList)) {
            List<NestMaintenanceEntity> nmeList = new ArrayList<>(recordIdList.size());
            for (Integer id : recordIdList) {
                NestMaintenanceEntity nme = new NestMaintenanceEntity();
                nme.setId(id);
                nme.setDeleted(1);
                nmeList.add(nme);
            }
            boolean b = nestMaintenanceService.updateBatchById(nmeList);
            if (b) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DELETE_DATA_MAINTENANCE_RECORD_SUCCESSFULLY.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @GetMapping("/list/nest/maintenance/record/{nestId}")
    public RestRes listNestMaintenanceRecord(@PathVariable @NotNull(message = "{geoai_uos_base_station_Id_cannot_be_empty_clear_the_check}") String nestId
            , @RequestParam("page") @NotNull(message = "{geoai_uos_page_cannot_be_empty_clear_check}") Integer page
            , @RequestParam("limit") @NotNull(message = "{geoai_uos_page_number_cannot_be_empty_clear_check}") Integer limit
            , @RequestParam("startTime") String startTime
            , @RequestParam("endTime") String endTime) {
        if (nestId != null) {
            return nestMaintenanceService.listNestMaintenanceRecordByPage(nestId, page, limit, startTime, endTime);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @PostMapping("/set/nest/maintenance/state")
    public RestRes setNestMaintenanceState(@RequestBody Map<String, String> map) {
        if (map == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        String nestId = map.get("nestId");
        String state = map.get("state");
        if (nestId != null && state != null) {
            int stateInt = Integer.parseInt(state);
//            String nestUuid = nestService.getUuidById(nestId);
//            NestEntity nestEntity = new NestEntity();
//            nestEntity.setId(nestId);
//            nestEntity.setMaintenanceState(state);
//            boolean b = nestService.updateById(nestEntity);
            String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
            Boolean b = baseNestService.setMaintenanceStateByNestId(stateInt, nestId);
            if (b) {
                String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_MAINTENANCE_STATE, nestUuid);
                redisService.del(redisKey);
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SET_THE_BASE_STATION_TO.getContent()) + (stateInt == 1 ?MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MAINTENANCE_STATUS.getContent())
                        : MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NORMAL_STATUS.getContent())));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @GetMapping("/get/nest/maintenance/state/{nestId}")
    public RestRes getNestMaintenanceState(@PathVariable String nestId) {
        if (nestId != null) {
//            NestEntity nestEntity = nestService.lambdaQuery()
//                    .eq(NestEntity::getId, nestId)
//                    .eq(NestEntity::getDeleted, 0)
//                    .select(NestEntity::getMaintenanceState)
//                    .one();
            Integer maintenanceState = baseNestService.getMaintenanceStateByNestId(nestId);
            Map<String, Object> resMap = new HashMap<>(2);
            resMap.put("state", maintenanceState);
            return RestRes.ok(resMap);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @GetMapping("/get/nest/flight/statistics/{nestId}")
    public RestRes getNestFlightStatistics(@PathVariable Integer nestId) {
        if (nestId != null) {
            ComponentManager cm = getComponentManager(nestId);
            if (cm != null) {
                CompletableFuture<FlightStatisticsEntity> future = new CompletableFuture<>();
                cm.getDataManager().queryFlightStatistics((flightStatisticsEntity, isSuccess, errMsg) -> {
                    if (isSuccess) {
                        future.complete(flightStatisticsEntity);
                    }
                });
                try {
                    FlightStatisticsDto flightStatisticsDto = new FlightStatisticsDto();
                    Integer count = nestMaintenanceService.lambdaQuery().eq(NestMaintenanceEntity::getDeleted, 0).count();
                    flightStatisticsDto.setMaintenanceCount(count);
                    FlightStatisticsEntity flightStatisticsEntity = future.get(10, TimeUnit.SECONDS);
                    BeanUtils.copyProperties(flightStatisticsEntity, flightStatisticsDto);

                    return RestRes.ok("flightStatistics", flightStatisticsDto);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GET_TIMEOUT.getContent()));
                }
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_REQUEST_ERROR.getContent()));
    }

    private ComponentManager getComponentManager(int nestId) {
        NestEntity nest = nestService.getNestByIdIsCache(nestId);
        if (nest != null) {
            return ComponentManagerFactory.getInstance(nest.getUuid());
        }
        return null;
    }

}

