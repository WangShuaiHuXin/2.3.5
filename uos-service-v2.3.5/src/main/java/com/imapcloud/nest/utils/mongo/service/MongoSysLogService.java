package com.imapcloud.nest.utils.mongo.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.imapcloud.nest.model.SysLogEntity;
import com.imapcloud.nest.pojo.dto.SysLogDelParam;
import com.imapcloud.nest.pojo.dto.SysLogQueryParam;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Author: wmin
 * @Date: 2021/3/22 17:39
 */
@Slf4j
@Component
public class MongoSysLogService {
    private final static String USE_COLL = "sys_logs";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Async("logExecutor")
    public void save(SysLogEntity sysLogEntity) {
        if (geoaiUosProperties.getMongo().isAllowSavingLogs()) {
            mongoTemplate.save(sysLogEntity, USE_COLL);
        }
    }

    public RestRes listQuerySysLogs(SysLogQueryParam param) {
        Query query = new Query();
        setFindQueryCriteria(query, param);

        setFindQuerySort(query, param);

        long total = mongoTemplate.count(query, SysLogEntity.class, USE_COLL);
        PageRequest pageRequest = PageRequest.of(param.getPageNo() - 1, param.getPageSize());
        query.with(pageRequest);

        List<SysLogEntity> sysLogs = mongoTemplate.find(query, SysLogEntity.class, USE_COLL);
        Map<String, Object> res = new HashMap<>(8);
        long pages = (total / param.getPageSize()) + ((total % param.getPageSize()) > 0 ? 1 : 0);
        res.put("pages", pages);
        res.put("current", param.getPageNo());
        res.put("records", sysLogs);
        res.put("size", param.getPageSize());
        res.put("total", total);

        return RestRes.ok(res);
    }

    public RestRes removeSysLogs(SysLogDelParam sysLogDelParam) {
        Query query = new Query();
        long startMilli = sysLogDelParam.getStartExecDate().atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long endMilli = sysLogDelParam.getEndExecDate().atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        query.addCriteria(Criteria.where("execDateMilli").gte(startMilli).lte(endMilli));
        if (CollectionUtil.isNotEmpty(sysLogDelParam.getMongoIdList())) {
            query.addCriteria(Criteria.where("_id").in(sysLogDelParam.getMongoIdList()));
        }
        DeleteResult remove = mongoTemplate.remove(query, SysLogEntity.class, USE_COLL);
        long deletedCount = remove.getDeletedCount();
        Map<String, Object> res = new HashMap<>(2);
        res.put("deletedCount", deletedCount);
        return RestRes.ok(res);
    }


    private void setFindQuerySort(Query query, SysLogQueryParam param) {
        Sort.Order execTimeSort;
        if (param.getExecTimeSort() != null) {
            execTimeSort = param.getExecTimeSort() == 1 ? Sort.Order.asc("execTime") : Sort.Order.desc("execTime");
        } else {
            execTimeSort = Sort.Order.asc("execTime");
        }
        Sort.Order timeLenSort;
        if (param.getTimeLenSort() != null) {
            timeLenSort = param.getTimeLenSort() == 1 ? Sort.Order.asc("timeLength") : Sort.Order.desc("timeLength");
        } else {
            timeLenSort = Sort.Order.asc("timeLength");
        }
        query.with(Sort.by(execTimeSort, timeLenSort));
    }

    private void setFindQueryCriteria(Query query, SysLogQueryParam param) {
        if (param.getStartExecDate() != null && param.getEndExecDate() != null) {
            long startMilli = LocalDate.parse(param.getStartExecDate()).atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            long endMilli = LocalDate.parse(param.getEndExecDate()).atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            query.addCriteria(Criteria.where("execDateMilli").gte(startMilli).lte(endMilli));
        }

        if (StrUtil.isNotEmpty(param.getAccount())) {
            String regex = String.format("%s%s%s", "^.*", param.getAccount(), ".*$");
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("requestAccount").regex(pattern));
        }

        if (StrUtil.isNotEmpty(param.getMethodName())) {
            String regex = String.format("%s%s%s", "^.*", param.getMethodName(), ".*$");
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("methodName").regex(pattern));
        }
        if (StrUtil.isNotEmpty(param.getRequestIp())) {
            query.addCriteria(Criteria.where("requestIp").is(param.getRequestIp()));
        }
    }
}
