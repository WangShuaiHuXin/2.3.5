package com.imapcloud.nest.v2.manager.sql.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.util.SqlEscapeUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.BaseNestEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseNestMapper;
import com.imapcloud.nest.v2.dao.po.in.BaseNestInPO;
import com.imapcloud.nest.v2.dao.po.out.BaseNestOutPO;
import com.imapcloud.nest.v2.manager.dataobj.in.BaseNestInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 基站表
 *
 * @author boluo
 * @date 2022-08-25
 */
@Component
public class BaseNestManagerImpl implements BaseNestManager {

    @Resource
    private BaseNestMapper baseNestMapper;

    @Resource
    private RedisService redisService;

    public String getUuidByNestId(String nestId) {

        return baseNestMapper.selectUuidByNestId(nestId);
    }

    private List<BaseNestOutDO.BaseNestEntityOutDO> toBaseNestEntityOutDOList(List<BaseNestEntity> baseNestEntityList) {
        List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = Lists.newArrayList();
        for (BaseNestEntity baseNestEntity : baseNestEntityList) {
            BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO = new BaseNestOutDO.BaseNestEntityOutDO();
            baseNestEntityOutDO.setNestId(baseNestEntity.getNestId());
            baseNestEntityOutDO.setUuid(baseNestEntity.getUuid());
            baseNestEntityOutDO.setName(baseNestEntity.getName());
            baseNestEntityOutDO.setNumber(baseNestEntity.getNumber());
            baseNestEntityOutDO.setType(baseNestEntity.getType() == null || baseNestEntity.getType() == -1 ? null : baseNestEntity.getType());
            baseNestEntityOutDO.setLatitude(baseNestEntity.getLatitude());
            baseNestEntityOutDO.setLongitude(baseNestEntity.getLongitude());
            baseNestEntityOutDO.setAltitude(baseNestEntity.getAltitude());
            baseNestEntityOutDO.setAglAltitude(baseNestEntity.getAglAltitude());
            baseNestEntityOutDO.setAddress(baseNestEntity.getAddress());
            baseNestEntityOutDO.setRegionId(baseNestEntity.getRegionId());
            baseNestEntityOutDO.setDescription(baseNestEntity.getDescription());
            baseNestEntityOutDO.setShowStatus(baseNestEntity.getShowStatus());
            baseNestEntityOutDO.setMaintenanceStatus(baseNestEntity.getMaintenanceStatus());
            baseNestEntityOutDO.setDeployTime(baseNestEntity.getDeployTime());
            baseNestEntityOutDO.setMqttBrokerId(baseNestEntity.getMqttBrokerId());
            baseNestEntityOutDO.setInnerStreamId(baseNestEntity.getInnerStreamId());
            baseNestEntityOutDO.setOuterStreamId(baseNestEntity.getOuterStreamId());
            baseNestEntityOutDOList.add(baseNestEntityOutDO);
        }
        return baseNestEntityOutDOList;
    }

    @Override
    public List<BaseNestOutDO.BaseNestEntityOutDO> selectByNestId(String nestId) {

        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                .eq(BaseNestEntity::getNestId, nestId);
        List<BaseNestEntity> baseNestEntityList = baseNestMapper.selectList(wrapper);
        return toBaseNestEntityOutDOList(baseNestEntityList);
    }

    @Override
    public List<BaseNestOutDO.BaseNestEntityOutDO> selectListByNestIdList(List<String> nestIdList) {
        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                .in(BaseNestEntity::getNestId, nestIdList);
        List<BaseNestEntity> baseNestEntityList = baseNestMapper.selectList(wrapper);
        return toBaseNestEntityOutDOList(baseNestEntityList);
    }

    @Override
    public List<BaseNestOutDO.BaseNestEntityOutDO> selectByUuid(String uuid) {
        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                .eq(BaseNestEntity::getUuid, uuid);
        List<BaseNestEntity> baseNestEntityList = baseNestMapper.selectList(wrapper);
        return toBaseNestEntityOutDOList(baseNestEntityList);
    }

    @Override
    public List<BaseNestOutDO.BaseNestEntityOutDO> selectByNumber(String number) {
        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                .eq(BaseNestEntity::getNumber, number);
        List<BaseNestEntity> baseNestEntityList = baseNestMapper.selectList(wrapper);
        return toBaseNestEntityOutDOList(baseNestEntityList);
    }

    @Override
    public int updateByNestId(BaseNestInDO.BaseNestEntityInDO baseNestEntityInDO) {
        if (StringUtils.isBlank(baseNestEntityInDO.getNestId())) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_IS_EXIST_NESTID.getContent()));
        }
        BaseNestEntity baseNestEntity = toBaseNestEntity(baseNestEntityInDO);

        baseNestEntityInDO.setUpdateAccount(baseNestEntity);
        return baseNestMapper.updateByNestId(baseNestEntity);
    }

    private BaseNestEntity toBaseNestEntity(BaseNestInDO.BaseNestEntityInDO baseNestEntityInDO) {
        BaseNestEntity baseNestEntity = new BaseNestEntity();
        baseNestEntity.setNestId(baseNestEntityInDO.getNestId());
        baseNestEntity.setUuid(baseNestEntityInDO.getUuid());
        baseNestEntity.setName(baseNestEntityInDO.getName());
        baseNestEntity.setNumber(baseNestEntityInDO.getNumber());
        baseNestEntity.setType(baseNestEntityInDO.getType());
        baseNestEntity.setLatitude(baseNestEntityInDO.getLatitude());
        baseNestEntity.setLongitude(baseNestEntityInDO.getLongitude());
        baseNestEntity.setAltitude(baseNestEntityInDO.getAltitude());
        baseNestEntity.setAddress(baseNestEntityInDO.getAddress());
        baseNestEntity.setRegionId(baseNestEntityInDO.getRegionId());
        baseNestEntity.setDescription(baseNestEntityInDO.getDescription());
        baseNestEntity.setShowStatus(baseNestEntityInDO.getShowStatus());
        baseNestEntity.setMaintenanceStatus(baseNestEntityInDO.getMaintenanceStatus());
        baseNestEntity.setDeployTime(baseNestEntityInDO.getDeployTime());
        baseNestEntity.setMqttBrokerId(baseNestEntityInDO.getMqttBrokerId());
        baseNestEntity.setInnerStreamId(baseNestEntityInDO.getInnerStreamId());
        baseNestEntity.setOuterStreamId(baseNestEntityInDO.getOuterStreamId());
        baseNestEntity.setAglAltitude(baseNestEntityInDO.getAglAltitude());
        return baseNestEntity;
    }

    @Override
    public int insertOne(BaseNestInDO.BaseNestEntityInDO baseNestEntityInDO) {

        BaseNestEntity baseNestEntity = toBaseNestEntity(baseNestEntityInDO);
        baseNestEntityInDO.setInsertAccount(baseNestEntity);
        return baseNestMapper.insertNestBase(baseNestEntity);
    }

    @Override
    public long countByCondition(BaseNestInDO.ListInDO listInDO) {
        BaseNestInPO.ListInPO listInPo = BaseNestInPO.ListInPO.builder()
                .userOrgCode(listInDO.getUserOrgCode())
                .orgCode(listInDO.getOrgCode())
                .name(listInDO.getName())
                .number(listInDO.getNumber())
                .uuid(listInDO.getUuid())
                .type(listInDO.getType())
                .regionId(listInDO.getRegionId())
                .keyword(SqlEscapeUtils.escapeSql(listInDO.getKeyword()))
                .types(listInDO.getTypes())
                .showStatus(listInDO.getShowStatus())
                .uavCate(listInDO.getUavCate())
                .uavModel(listInDO.getUavModel())
                .uavType(listInDO.getUavType())
                .build();
        return baseNestMapper.countByCondition(listInPo);
    }

    @Override
    public List<BaseNestOutDO.ListOutDO> selectByCondition(BaseNestInDO.ListInDO listInDO) {
        BaseNestInPO.ListInPO listInPo = BaseNestInPO.ListInPO.builder()
                .userOrgCode(listInDO.getUserOrgCode())
                .orgCode(listInDO.getOrgCode())
                .name(listInDO.getName())
                .number(listInDO.getNumber())
                .uuid(listInDO.getUuid())
                .type(listInDO.getType())
                .regionId(listInDO.getRegionId())
                .keyword(SqlEscapeUtils.escapeSql(listInDO.getKeyword()))
                .showStatus(listInDO.getShowStatus())
                .types(listInDO.getTypes())
                .uavType(listInDO.getUavType())
                .uavCate(listInDO.getUavCate())
                .uavModel(listInDO.getUavModel())
                .build();
        List<BaseNestOutPO.ListOutPO> listOutPOList = baseNestMapper.selectByCondition(listInPo, PagingRestrictDo.getPagingRestrict(listInDO));
        List<BaseNestOutDO.ListOutDO> listOutDOList = Lists.newLinkedList();
        for (BaseNestOutPO.ListOutPO listOutPO : listOutPOList) {
            BaseNestOutDO.ListOutDO listOutDO = new BaseNestOutDO.ListOutDO();
            listOutDO.setNestId(listOutPO.getNestId());
            listOutDO.setUuid(listOutPO.getUuid());
            listOutDO.setName(listOutPO.getName());
            listOutDO.setNumber(listOutPO.getNumber());
            listOutDO.setType(listOutPO.getType());
            listOutDO.setRegionId(listOutPO.getRegionId());
            listOutDO.setLatitude(listOutPO.getLatitude());
            listOutDO.setLongitude(listOutPO.getLongitude());
            listOutDO.setAltitude(listOutPO.getAltitude());
            listOutDOList.add(listOutDO);
        }
        return listOutDOList;
    }

    @Override
    public int deleteByNestId(String nestId, String accountId) {
        return baseNestMapper.deleteByNestId(nestId, accountId);
    }

    @Override
    public void clearNestListRedisCache() {
        redisService.del(redisService.keys("NESTSERVICEIMPL:LISTNESTANDREGION:NESTLISTDTOLIST:*"));
        redisService.del(redisService.keys("NESTSERVICEIMPL:LISTNESTANDREGION:ACCOUNT:MAP:NESTID:LIST:*"));
        //由于超级管理员可以修改所有的基站信息，因此只能在推出的时候去掉缓存
        String pattern = "NESTSERVICEIMPL:LISTNESTANDREGION:NESTLIST:*";
        Set<String> keys = redisService.keys(pattern);
        redisService.del(keys);
    }

    @Override
    public void clearRedisCache(String nestId, String accountId) {
        //在查询机巢列表的时候将列表缓存了，因此在这里要清空redis缓存
        clearNestListRedisCache();
        String nestUuid = this.getUuidByNestId(nestId);
        redisService.hSet(RedisKeyConstantList.NEST_UUID_TYPE_MAP_KEY, nestUuid, null);
        redisService.hSet(RedisKeyConstantList.NEST_UUID_NAME_MAP_KEY, nestUuid, null);
        redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_AIRCRAFT_LIST_KEY, accountId));
        redisService.hSet(RedisKeyConstantList.NEST_UUID_AIR_CODE_MAP_KEY, nestUuid, null);
        redisService.hSet(RedisKeyConstantList.NEST_UUID_AIR_CODE_MAP_KEY, nestUuid + "#0", null);
        redisService.hSet(RedisKeyConstantList.NEST_UUID_AIR_CODE_MAP_KEY, nestUuid + "#1", null);
        redisService.hSet(RedisKeyConstantList.NEST_UUID_AIR_CODE_MAP_KEY, nestUuid + "#2", null);
        redisService.hSet(RedisKeyConstantList.NEST_UUID_AIR_CODE_MAP_KEY, nestUuid + "#3", null);
        redisService.del(RedisKeyConstantList.NEST_ALT_CACHE);

        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_MAP_NEST_ID_LIST_KEY, accountId);
        redisService.del(redisKey);
        String allNestUuidRedisKey = RedisKeyConstantList.ALL_NEST_UUID;
        redisService.del(allNestUuidRedisKey);
        //清空基站类型
        String uuidRedisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.UUID_BY_ID_CACHE_NEW, nestId);
        redisService.del(uuidRedisKey);
        //清空基站类型
        redisService.hDel(RedisKeyConstantList.NEST_TYPE, nestUuid);
    }

    @Override
    public void clearDeleteRedisCache(String userName) {
        clearNestListRedisCache();
        redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_AIRCRAFT_LIST_KEY, userName));
        redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_REGION_LIST_KEY, userName));
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_MAP_NEST_ID_LIST_KEY, userName);
        redisService.del(redisKey);
        redisService.del(RedisKeyConstantList.ALL_NEST_UUID);
    }

    @Override
    public List<BaseNestOutDO.BaseNestUavInfoOutDO> getNestUavInfoByIds(List<String> nestIdList) {

        List<BaseNestOutDO.BaseNestUavInfoOutDO> outDO = baseNestMapper.getNestUavInfoByIds(nestIdList);

        return outDO;
    }

    @Override
    public long typeCountByCondition(BaseNestInDO.ListInDO listInDO) {
        BaseNestInPO.ListInPO listInPo = BaseNestInPO.ListInPO.builder()
                .userOrgCode(listInDO.getUserOrgCode())
                .orgCode(listInDO.getOrgCode())
                .name(listInDO.getName())
                .number(listInDO.getNumber())
                .uuid(listInDO.getUuid())
                .type(listInDO.getType())
                .regionId(listInDO.getRegionId())
                .keyword(SqlEscapeUtils.escapeSql(listInDO.getKeyword()))
                .uavCate(listInDO.getUavCate())
                .uavModel(listInDO.getUavModel())
                .uavType(listInDO.getUavType())
                .build();
        return baseNestMapper.countByCondition(listInPo);
    }

    @Override
    public List<BaseNestOutDO.ListOutDO> selectAllCondition(BaseNestInDO.ListInDO listInDO) {
        BaseNestInPO.ListInPO listInPo = BaseNestInPO.ListInPO.builder()
                .userOrgCode(listInDO.getUserOrgCode())
                .orgCode(listInDO.getOrgCode())
                .name(listInDO.getName())
                .number(listInDO.getNumber())
                .uuid(listInDO.getUuid())
                .type(listInDO.getType())
                .regionId(listInDO.getRegionId())
                .keyword(SqlEscapeUtils.escapeSql(listInDO.getKeyword()))
                .uavType(listInDO.getUavType())
                .uavCate(listInDO.getUavCate())
                .uavModel(listInDO.getUavModel())
                .build();
        List<BaseNestOutPO.ListOutPO> listOutPOList = baseNestMapper.selectAllCondition(listInPo);
        List<BaseNestOutDO.ListOutDO> listOutDOList = Lists.newLinkedList();
        for (BaseNestOutPO.ListOutPO listOutPO : listOutPOList) {
            BaseNestOutDO.ListOutDO listOutDO = new BaseNestOutDO.ListOutDO();
            listOutDO.setNestId(listOutPO.getNestId());
            listOutDO.setUuid(listOutPO.getUuid());
            listOutDO.setName(listOutPO.getName());
            listOutDO.setNumber(listOutPO.getNumber());
            listOutDO.setType(listOutPO.getType());
            listOutDO.setRegionId(listOutPO.getRegionId());
            listOutDO.setLatitude(listOutPO.getLatitude());
            listOutDO.setLongitude(listOutPO.getLongitude());
            listOutDO.setAltitude(listOutPO.getAltitude());
            listOutDOList.add(listOutDO);
        }
        return listOutDOList;
    }

}
