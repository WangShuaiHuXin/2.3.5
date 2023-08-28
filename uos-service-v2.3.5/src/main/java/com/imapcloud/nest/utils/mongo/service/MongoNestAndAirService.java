package com.imapcloud.nest.utils.mongo.service;

import com.imapcloud.nest.utils.mongo.pojo.AppAirMsgEntity;
import com.imapcloud.nest.utils.mongo.pojo.MongoPage;
import com.imapcloud.nest.utils.mongo.pojo.NestAndAirEntity;
import com.imapcloud.nest.utils.mongo.pojo.NestAndAirParam;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;

import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基站和无人机消息服务类
 *
 * @Author: wmin
 * @Date: 2021/3/22 14:24
 */
@Slf4j
@Component
public class MongoNestAndAirService {
    private final static String USE_COLL = "nest_logs";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisService redisService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    public void save(NestAndAirEntity nestAndAirEntity) {
        if (geoaiUosProperties.getMongo().isAllowSavingLogs()) {
            mongoTemplate.save(nestAndAirEntity, USE_COLL);
        }
    }

    private void save(Object object) {
        if (geoaiUosProperties.getMongo().isAllowSavingLogs()) {
            mongoTemplate.save(object, USE_COLL);
        }
    }


    public List<NestAndAirEntity> findByMissionRecordsId(Integer missionRecordsId) {
        if (geoaiUosProperties.getMongo().isAllowSavingLogs()) {
            Criteria criteria = Criteria.where("missionRecordsId").is(missionRecordsId);
            Query query = new Query(criteria);
            return mongoTemplate.find(query, NestAndAirEntity.class, USE_COLL);
        }
        return Collections.emptyList();
    }

    public MongoPage<NestAndAirEntity> findByMissionRecordsIdPage(Integer missionRecordsId, Integer currentPage, Integer pageSize) {
        if (geoaiUosProperties.getMongo().isAllowSavingLogs()) {
            Criteria criteria = Criteria.where("missionRecordsId").is(missionRecordsId);
            Query query = new Query(criteria);

            long total = mongoTemplate.count(query, USE_COLL);

            //获取总页数
            long pages = (long) Math.ceil(total / pageSize) + ((total % pageSize) > 0 ? 1 : 0);
            //获取总页数
            long skip = (currentPage - 1) * pageSize;
            query.skip(skip).limit(pageSize);
            List<NestAndAirEntity> records = mongoTemplate.find(query, NestAndAirEntity.class, USE_COLL);
            MongoPage<NestAndAirEntity> mongoPage = new MongoPage<>();
            mongoPage.setPages(pages);
            mongoPage.setPageSize(pageSize);
            mongoPage.setTotal(total);
            mongoPage.setRecords(records);
            mongoPage.setCurrentPage(currentPage);
            return mongoPage;
        }
        MongoPage<NestAndAirEntity> mongoPage = new MongoPage<>();
        mongoPage.setPages(0L);
        mongoPage.setPageSize(0);
        mongoPage.setTotal(0L);
        mongoPage.setRecords(Collections.emptyList());
        mongoPage.setCurrentPage(currentPage);
        return mongoPage;
    }

    public List<AppAirMsgEntity> findAppMsgByMissionRecordIdsId(Integer missionRecordsId) {
        if (geoaiUosProperties.getMongo().isAllowSavingLogs()) {
            Criteria criteria = Criteria.where("missionRecordsId").is(missionRecordsId);
            Query query = new Query(criteria);
            return mongoTemplate.find(query, AppAirMsgEntity.class, USE_COLL);
        }
        return Collections.emptyList();
    }

    public long batchDeleteByMissionRecordsIdList(List<Integer> missionRecordsIdList) {
        if (geoaiUosProperties.getMongo().isAllowSavingLogs() && !CollectionUtils.isEmpty(missionRecordsIdList)) {
            Criteria criteria = Criteria.where("missionRecordsId").in(missionRecordsIdList);
            Query query = new Query(criteria);
            DeleteResult remove = mongoTemplate.remove(query, USE_COLL);
            return remove.getDeletedCount();
        }
        return 0L;
    }

    @Async("logExecutor")
    public void saveP4rMsg(NestAndAirParam param) {
        Integer recordsId = getRecordsId(param.getNestUuid(), AirIndexEnum.DEFAULT);
        if (recordsId != -1) {
            NestAndAirEntity nestAndAirEntity = new NestAndAirEntity();
            nestAndAirEntity.setMissionRecordsId(recordsId);
            nestAndAirEntity.setNestType(param.getNestType());
            nestAndAirEntity.setNestUuid(param.getNestUuid());
            nestAndAirEntity.setSaveDateTime(LocalDateTime.now());
            Map<String, Object> msg = new HashMap<>(4);
            msg.put("connectState", param.getNestAndServerConnState());
            msg.put("nestMsg", param.getNestAircraftInfoDto().getNestInfo());
            msg.put("aircraftMsg", param.getNestAircraftInfoDto().getDroneInfo());
            msg.put("aircraftLocation", param.getAircraftLocationDto());
            nestAndAirEntity.setMsg(msg);
            save(nestAndAirEntity);
        }
    }

    @Async("logExecutor")
    public void saveMiniMsg(NestAndAirParam param) {
        Integer recordsId = getRecordsId(param.getNestUuid(), AirIndexEnum.DEFAULT);
        if (recordsId != -1) {
            NestAndAirEntity nestAndAirEntity = new NestAndAirEntity();
            nestAndAirEntity.setMissionRecordsId(recordsId);
            nestAndAirEntity.setNestType(param.getNestType());
            nestAndAirEntity.setNestUuid(param.getNestUuid());
            nestAndAirEntity.setSaveDateTime(LocalDateTime.now());
            Map<String, Object> msg = new HashMap<>(4);
            msg.put("connectState", param.getNestAndServerConnState());
            msg.put("nestMsg", param.getMiniNestAircraftInfoDto().getMiniNestInfoDto());
            msg.put("aircraftMsg", param.getMiniNestAircraftInfoDto().getMiniAircraftInfoDto());
            msg.put("aircraftLocation", param.getAircraftLocationDto());
            nestAndAirEntity.setMsg(msg);
            save(nestAndAirEntity);
        }
    }

    @Async("logExecutor")
    public void saveSimpleMsg(NestAndAirParam param) {
        Integer recordsId = getRecordsId(param.getNestUuid(), AirIndexEnum.DEFAULT);
        if (recordsId != -1) {
            NestAndAirEntity nestAndAirEntity = new NestAndAirEntity();
            nestAndAirEntity.setMissionRecordsId(recordsId);
            nestAndAirEntity.setNestType(param.getNestType());
            nestAndAirEntity.setNestUuid(param.getNestUuid());
            nestAndAirEntity.setSaveDateTime(LocalDateTime.now());
            Map<String, Object> msg = new HashMap<>(4);
            msg.put("connectState", param.getNestAndServerConnState());
            msg.put("nestMsg", param.getSimpleNestAirInfoDto());
            msg.put("aircraftLocation", param.getAircraftLocationDto());
            nestAndAirEntity.setMsg(msg);
            save(nestAndAirEntity);
        }
    }

    @Async("logExecutor")
    public void saveM300Msg(NestAndAirParam param) {
        Integer recordsId = getRecordsId(param.getNestUuid(), AirIndexEnum.DEFAULT);
        if (recordsId != -1) {
            NestAndAirEntity nestAndAirEntity = new NestAndAirEntity();
            nestAndAirEntity.setMissionRecordsId(recordsId);
            nestAndAirEntity.setNestType(param.getNestType());
            nestAndAirEntity.setNestUuid(param.getNestUuid());
            nestAndAirEntity.setSaveDateTime(LocalDateTime.now());
            Map<String, Object> msg = new HashMap<>(4);
            msg.put("connectState", param.getNestAndServerConnState());
            msg.put("nestMsg", param.getM300NestAircraftInfoDto().getM300NestInfoDto());
            msg.put("aircraftMsg", param.getM300NestAircraftInfoDto().getM300AircraftInfoDto());
            msg.put("aircraftLocation", param.getAircraftLocationDto());
            nestAndAirEntity.setMsg(msg);
            save(nestAndAirEntity);
        }
    }

    @Async("logExecutor")
    public void saveG503Msg(NestAndAirParam param) {
        Integer recordsId1 = getRecordsId(param.getNestUuid(), AirIndexEnum.ONE);
        Integer recordsId2 = getRecordsId(param.getNestUuid(), AirIndexEnum.TWO);
        Integer recordsId3 = getRecordsId(param.getNestUuid(), AirIndexEnum.THREE);
        if (recordsId1 != -1) {
            NestAndAirEntity nestAndAirEntity = new NestAndAirEntity();
            nestAndAirEntity.setMissionRecordsId(recordsId1);
            nestAndAirEntity.setNestType(param.getNestType());
            nestAndAirEntity.setNestUuid(param.getNestUuid());
            nestAndAirEntity.setSaveDateTime(LocalDateTime.now());
            Map<String, Object> msg = new HashMap<>(4);
            msg.put("connectState", param.getNestAndServerConnState());
            msg.put("nestMsg", param.getG503NestTotalDTOMap().get("1"));
            msg.put("aircraftLocation", param.getG503AircraftLocationDtoMap().get("1"));
            nestAndAirEntity.setMsg(msg);
            save(nestAndAirEntity);
        }
        if (recordsId2 != -1) {
            NestAndAirEntity nestAndAirEntity = new NestAndAirEntity();
            nestAndAirEntity.setMissionRecordsId(recordsId2);
            nestAndAirEntity.setNestType(param.getNestType());
            nestAndAirEntity.setNestUuid(param.getNestUuid());
            nestAndAirEntity.setSaveDateTime(LocalDateTime.now());
            Map<String, Object> msg = new HashMap<>(4);
            msg.put("connectState", param.getNestAndServerConnState());
            msg.put("nestMsg", param.getG503NestTotalDTOMap().get("2"));
            msg.put("aircraftLocation", param.getG503AircraftLocationDtoMap().get("2"));
            nestAndAirEntity.setMsg(msg);
            save(nestAndAirEntity);
        }
        if (recordsId3 != -1) {
            NestAndAirEntity nestAndAirEntity = new NestAndAirEntity();
            nestAndAirEntity.setMissionRecordsId(recordsId3);
            nestAndAirEntity.setNestType(param.getNestType());
            nestAndAirEntity.setNestUuid(param.getNestUuid());
            nestAndAirEntity.setSaveDateTime(LocalDateTime.now());
            Map<String, Object> msg = new HashMap<>(4);
            msg.put("connectState", param.getNestAndServerConnState());
            msg.put("nestMsg", param.getG503NestTotalDTOMap().get("3"));
            msg.put("aircraftLocation", param.getG503AircraftLocationDtoMap().get("3"));
            nestAndAirEntity.setMsg(msg);
            save(nestAndAirEntity);
        }
    }

    @Async("logExecutor")
    public void saveDJIMsg(NestAndAirParam param) {
        Integer recordsId = getRecordsId(param.getNestUuid(),AirIndexEnum.DEFAULT);
        if (recordsId != -1) {
            NestAndAirEntity nestAndAirEntity = new NestAndAirEntity();
            nestAndAirEntity.setMissionRecordsId(recordsId);
            nestAndAirEntity.setNestType(param.getNestType());
            nestAndAirEntity.setNestUuid(param.getNestUuid());
            nestAndAirEntity.setSaveDateTime(LocalDateTime.now());
            Map<String, Object> msg = new HashMap<>(4);
            msg.put("connectState", param.getNestAndServerConnState());
            msg.put("nestMsg", param.getDjiDockAircraftInfoOutDTO().getDjiDockInfoDTO());
            msg.put("aircraftMsg", param.getDjiDockAircraftInfoOutDTO().getDjiAircraftInfoDTO());
            msg.put("aircraftLocation", param.getAircraftLocationDto());
            nestAndAirEntity.setMsg(msg);
            save(nestAndAirEntity);
        }
    }

    @Async("logExecutor")
    public void saveAppMsg(String deviceId, Integer recordId, String messageBody) {
        AppAirMsgEntity appAirMsgEntity = new AppAirMsgEntity();
        appAirMsgEntity.setDeviceId(deviceId)
                .setSaveDateTime(LocalDateTime.now())
                .setMissionRecordsId(recordId)
                .setMsg(messageBody);
        save(appAirMsgEntity);
    }

    private Integer getRecordsId(String nestUuid, AirIndexEnum airIndexEnum) {
        String sysLogSaveKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.SYS_LOG_SAVE_KEY, nestUuid, airIndexEnum.getVal());
        Integer recordsId = (Integer) redisService.get(sysLogSaveKey);
        return recordsId == null ? -1 : recordsId;
    }

}
