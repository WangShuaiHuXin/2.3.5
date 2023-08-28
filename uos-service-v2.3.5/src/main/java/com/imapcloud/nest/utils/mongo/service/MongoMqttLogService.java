package com.imapcloud.nest.utils.mongo.service;

import cn.hutool.core.util.StrUtil;
import com.imapcloud.nest.pojo.dto.QueryMqttLogFromMongoDto;
import com.imapcloud.nest.utils.mongo.pojo.MongoPage;
import com.imapcloud.nest.utils.mongo.pojo.MqttLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MongoMqttLogService {
    private final static String USE_COLL = "mqtt_logs";
    @Autowired
    private MongoTemplate mongoTemplate;

    public void batchInsert(List<MqttLogEntity> list) {
        mongoTemplate.insert(list, USE_COLL);
    }

    public void batchInsert2(List<MqttLogEntity> list) {
        BulkOperations operations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, USE_COLL);

        List<Pair<Query, Update>> collect = list.stream().map(record -> {
            Query query = new Query(Criteria.where("timestamp").is(record.getTimestamp())
                    .and("traceId").is(record.getTraceId())
                    .and("nodeId").is(record.getNodeId()));
            Update update = Update.update("nestUuid", record.getNestUuid())
                    .set("logTime", record.getLogTime())
                    .set("traceId", record.getTraceId())
                    .set("nodeId", record.getNodeId())
                    .set("body", record.getBody())
                    .set("timestamp", record.getTimestamp());
            return Pair.of(query, update);
        }).collect(Collectors.toList());
        operations.upsert(collect);
        operations.execute();
    }

    public List<MqttLogEntity> listLogByTimestamp(Long startTime, Long endTime) {
        Criteria criteria = Criteria.where("timestamp").gte(startTime).lte(endTime);
        Query query = new Query(criteria).with(Sort.by(Sort.Order.desc("timestamp")));
        return mongoTemplate.find(query, MqttLogEntity.class, USE_COLL);
    }

    public MongoPage<MqttLogEntity> listMqttLog(QueryMqttLogFromMongoDto queryParam) {
        int pageSize = queryParam.getPageSize() != null && queryParam.getPageSize() < 100 ? queryParam.getPageSize() : 50;
        int currentPage = queryParam.getCurrentPage() != null ? queryParam.getCurrentPage() : 1;
        Criteria criteria = new Criteria();
        if (StrUtil.isNotEmpty(queryParam.getNestUuid())) {
            criteria.orOperator(Criteria.where("nestUuid").is(queryParam.getNestUuid()), Criteria.where("nestUuid").is(queryParam.getNestUuid().replaceAll("-", "")));
        }
        if (StrUtil.isNotEmpty(queryParam.getNodeId())) {
            criteria.and("nodeId").regex(queryParam.getNodeId());
        }
        if (StrUtil.isNotEmpty(queryParam.getTraceId())) {
            criteria.and("traceId").is(queryParam.getTraceId());
        }

        if (queryParam.getStartTime() != null && queryParam.getEndTime() != null) {
            criteria.and("timestamp").gte(queryParam.getStartTime()).lte(queryParam.getEndTime());
        }
        Query query = new Query(criteria);
        long total = mongoTemplate.count(query, USE_COLL);
        //获取总页数
        long pages = (long) Math.ceil(total / pageSize) + ((total % pageSize) > 0 ? 1 : 0);
        //获取总页数
        long skip = (currentPage - 1) * pageSize;
        query.with(Sort.by(Sort.Order.desc("timestamp"))).skip(skip).limit(pageSize);
        List<MqttLogEntity> list = mongoTemplate.find(query, MqttLogEntity.class, USE_COLL);
        MongoPage<MqttLogEntity> page = new MongoPage<>();
        page.setTotal(total)
                .setPages(pages)
                .setCurrentPage(currentPage)
                .setPageSize(pageSize)
                .setRecords(list);
        return page;
    }


    public void clearLog() {
        mongoTemplate.dropCollection(USE_COLL);
    }
}
