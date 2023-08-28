package com.imapcloud.nest.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.validator.Assert;
import com.imapcloud.nest.mapper.EarlyWarningMapper;
import com.imapcloud.nest.model.EarlyWarningEntity;
import com.imapcloud.nest.model.EarlyWarningKeyEntity;
import com.imapcloud.nest.model.EarlyWarningUnitEntity;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.pojo.DO.EarlyWarningDo;
import com.imapcloud.nest.pojo.dto.NestDto;
import com.imapcloud.nest.pojo.dto.reqDto.EarlyWarningAddDto;
import com.imapcloud.nest.pojo.vo.EarlyWarningVO;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.Query;
import com.imapcloud.nest.utils.WeatherUtils;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.properties.WeatherConfig;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.OrgAccountService;
import com.imapcloud.nest.v2.service.dto.out.BaseNestInfoOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 天气/区域警告配置 服务层实现
 */
@Service("earlyWarningService")
@Slf4j
public class EarlyWarningServiceImpl extends ServiceImpl<EarlyWarningMapper, EarlyWarningEntity> implements EarlyWarningService {

    @Resource
    private EarlyWarningUnitService earlyWarningUnitService;

    @Resource
    private WeatherUtils weatherUtils;

    @Resource
    private NestService nestService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private SysUnitService sysUnitService;

    @Resource
    private CommonNestStateService commonNestStateService;

    @Resource
    private RedisService redisService;

    @Resource
    private EarlyWarningKeyService earlyWarningKeyService;

    @Resource
    private OrgAccountService orgAccountService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private UosOrgManager uosOrgManager;

    private static final Integer EARLY_WARNING_STEP = 14;

    @Override
    public IPage<EarlyWarningVO> queryPage(Map<String, Object> params) {
        String unitId = (String) params.get("unitId");
        if (StringUtils.isNotBlank(unitId)) {
            List<EarlyWarningUnitEntity> earlyWarningUnitEntities = earlyWarningUnitService.
                    lambdaQuery().eq(EarlyWarningUnitEntity::getOrgCode, unitId).list();
            List<Integer> earlyWarningUnitIds = earlyWarningUnitEntities.stream().
                    distinct().
                    map(EarlyWarningUnitEntity::getEarlyWarningId).
                    collect(Collectors.toList());
            params.put("earlyWarningUnitIds", earlyWarningUnitIds.size() == 0 ? null : earlyWarningUnitIds);
            if(CollectionUtils.isEmpty(earlyWarningUnitIds)){
                 return new Page<>();
            }
        }
        IPage<EarlyWarningVO> iPage = getBaseMapper().queryPage(new Query<EarlyWarningEntity>().getPage(params, "id", false), params);
        iPage.getRecords().forEach(e -> e.setUnitList(earlyWarningUnitService.getUnitByEarlyWarningId(e.getId())));
        return iPage;
    }

    @Override
    public EarlyWarningVO ById(Integer id) {
        EarlyWarningVO vo = getBaseMapper().byId(id);
        vo.setUnitList(earlyWarningUnitService.getUnitByEarlyWarningId(vo.getId()));
        return vo;
    }

    @Override
    @Transactional
    public void saveEntity(EarlyWarningAddDto dto) {
        List<String> unitIds = dto.getUnitIds();
        save(dto);//保存天气预警对象
        saveList(unitIds, dto);
    }

    @Override
    @Transactional
    public void updateEntity(EarlyWarningAddDto dto) {
        List<String> unitIds = dto.getUnitIds();
        updateById(dto);//保存天气预警对象

        //清空原本的单位关联数据
        QueryWrapper<EarlyWarningUnitEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("early_warning_id", dto.getId());
        earlyWarningUnitService.remove(queryWrapper);

        saveList(unitIds, dto);

    }

    /**
     * 批量保存预警单位关联对象
     *
     * @param unitIds 单位id数组
     * @param dto     天气/区域警告配置 dto
     */
    private void saveList(List<String> unitIds, EarlyWarningAddDto dto) {
        List<EarlyWarningUnitEntity> earlyWarningUnitEntities = new ArrayList<>();
        if (unitIds != null) {
            for (String unitId : unitIds) {
                EarlyWarningUnitEntity earlyWarningUnitEntity = new EarlyWarningUnitEntity();
                earlyWarningUnitEntity.setEarlyWarningId(dto.getId());
                earlyWarningUnitEntity.setOrgCode(unitId);
                earlyWarningUnitEntities.add(earlyWarningUnitEntity);
            }
            earlyWarningUnitService.saveBatch(earlyWarningUnitEntities);//批量保存预警-单位关联
        }
    }

    @Override
    public JSONObject FirstEarlyWarningByNestId(String nestId) {
        if (nestId == null) { // 如果机巢id为空则返回空结果集
            return new JSONObject();
        }
        BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(nestId);
        return getEarlyWarningByNest(baseNestInfo);
    }

    //获取年初时间
    private Date getYearStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear(Calendar.MONTH);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    //获取年末时间
    private Date getYearEndDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear(Calendar.MONTH);
        calendar.set(Calendar.MONTH, 12);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    private JSONObject getEarlyWarningByNest(BaseNestInfoOutDTO baseNestInfo) {
        JSONObject result = new JSONObject();

        if (baseNestInfo == null) { //如果nest为空,返回空结果集
            return new JSONObject();
        }
        result.put("nestId", baseNestInfo.getNestId());
        result.put("nestName", baseNestInfo.getName());
        Double longitude = baseNestInfo.getLongitude();
        Double latitude = baseNestInfo.getLatitude();
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        List<EarlyWarningDo> warningDoList = getEarlyWarningByUnitId(baseNestInfo.getNestId(), null);

        Integer earlyWarningDay = 0;//预警天数
        Map<Object, Object> caiYunMap = redisService.hmGet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_EARLY_WARNING_CACHE, baseNestInfo.getUuid()));

//        Assert.isNull(caiYunMap,"未获取到天气数据,可能是秘钥已经失效,或没有找到在线机巢");
        if (caiYunMap == null) {
            log.info("未获取到天气数据,试图请求第三方天气预报接口");
            WeatherConfig weather = geoaiUosProperties.getWeather();
            if (weather.isActivate()) {
                return null;
            } else {
                getWeather();
            }
            caiYunMap = redisService.hmGet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_EARLY_WARNING_CACHE, baseNestInfo.getUuid()));
            if (caiYunMap == null) {
                log.info("未获取到天气数据,可能是秘钥已经失效,或没有找到在线机巢");
                Assert.failure(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_WEATHER_DATA_WAS_NOT_RETRIEVED.getContent()));
            }
        }
        JSONArray temperatureArray = (JSONArray) caiYunMap.get("temperatureArray");//温度预警
        JSONArray precipitationArray = (JSONArray) caiYunMap.get("precipitationArray");//降雨量
        JSONArray windArray = (JSONArray) caiYunMap.get("windArray");//风速预警
        JSONArray skyconArray = (JSONArray) caiYunMap.get("skyconArray");//天气状态
        if (temperatureArray == null || precipitationArray == null || windArray == null || skyconArray == null) {
            return result;
        }
        for (int i = 0; i < EARLY_WARNING_STEP; i++) {
            JSONObject temperature = temperatureArray.getJSONObject(i);
            JSONObject precipitation = precipitationArray.getJSONObject(i);
            JSONObject wind = windArray.getJSONObject(i);
            temperature.put("warningStatus", 0);
            precipitation.put("warningStatus", 0);
            wind.put("warningStatus", 0);
            JSONObject skycon = skyconArray.getJSONObject(i);
            String weatherDateStr = skycon.getString("date");
            Date weatherDate;
            try {
                weatherDate = dealDateFormat(weatherDateStr);
            } catch (ParseException e) {
                log.error("转换日期字符串时出现错误", e);
                continue;
            }
            Integer earlyWarningTime = 0;//预警次数
            long weatherDateLong = weatherDate.getTime();
            for (EarlyWarningDo warningDo : warningDoList) {
                Date earlyWarningDate = warningDo.getEarlyWarningDate();
                Date earlyWarningDateEnd = warningDo.getEarlyWarningDateEnd();
                Byte waringType = warningDo.getWaringType();
                if (warningDo.getIsEveryYear() == 1) {//如果该条规则为每年生效
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(weatherDate);
                    int year = calendar.get(Calendar.YEAR);//获取需要预警的年份
                    calendar.setTime(earlyWarningDate == null ? getYearStartDate() : earlyWarningDate);
                    calendar.set(Calendar.YEAR, year);//重置为当前的年份
                    earlyWarningDate = calendar.getTime();//日期重新赋值
                    calendar.setTime(earlyWarningDateEnd == null ? getYearEndDate() : earlyWarningDateEnd);
                    calendar.set(Calendar.YEAR, year);//重置为当前的年份
                    earlyWarningDateEnd = calendar.getTime();//日期重新赋值
                }
                Long earlyWarningDateLong = earlyWarningDate == null ? null : earlyWarningDate.getTime();
                Long earlyWarningDateEndLong = earlyWarningDateEnd == null ? null : earlyWarningDateEnd.getTime();
                if ((earlyWarningDateEndLong != null && weatherDateLong >= earlyWarningDateEndLong) ||
                        earlyWarningDateLong != null && weatherDateLong <= earlyWarningDateLong) {//如果该日期不在预警时间内,则跳过此次循环
                    continue;
                }
                switch (waringType) {
                    case 1://降水预警
                        earlyWarningTime = putJson(precipitation, waringType, warningDo, earlyWarningTime);
                        break;
                    case 2://风速预警
                        earlyWarningTime = putJson(wind, waringType, warningDo, earlyWarningTime);
                        break;
                    case 3://温度预警
                        earlyWarningTime = putJson(temperature, waringType, warningDo, earlyWarningTime);
                        break;
                }
            }

            //移除不必要的字段
            temperature.remove("avg");
            temperature.remove("date");
            precipitation.remove("min");
            precipitation.remove("max");
            precipitation.remove("date");
            wind.remove("min");
            wind.remove("max");
            wind.remove("date");

            if (earlyWarningTime > 0) {
                ++earlyWarningDay;
            }
        }
        result.put("temperatureArray", temperatureArray);
        result.put("windArray", windArray);
        result.put("precipitationArray", precipitationArray);
        result.put("skyconArray", skyconArray);
        result.put("earlyWarningDay", earlyWarningDay);
        return result;
    }

    public static Date dealDateFormat(String oldDateStr) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX");
        return df.parse(oldDateStr);
    }

    private Integer putJson(JSONObject jsonObject, Byte waringType, EarlyWarningDo warningDo, Integer earlyWarningTime) {
        Double waringLeft = warningDo.getWaringLeft();
        Double waringRight = warningDo.getWaringRight();
        Double min;
        Double max;
        Double avg;
        if (waringType == 2) {//如果预警类型为风速
            min = jsonObject.getJSONObject("min").getDouble("speed");
            max = jsonObject.getJSONObject("max").getDouble("speed");
            avg = jsonObject.getJSONObject("avg").getDouble("speed");
        } else {
            min = jsonObject.getDouble("min");
            max = jsonObject.getDouble("max");
            avg = jsonObject.getDouble("avg");
        }
        if (waringType == 3) {//如果预警类型为温度
            if ((waringLeft == null && waringRight == null) ||
                    (waringRight != null && max >= waringRight && min <= waringRight) ||
                    (waringLeft != null && min <= waringLeft && max >= waringLeft) ||
                    (waringRight == null && min >= waringLeft) ||
                    (waringLeft == null && max <= waringRight)) {//如果符合预警条件,则增加预警状态码
                jsonObject.put("warningStatus", 1);
                earlyWarningTime++;
            }
        } else {
            if ((waringLeft == null && waringRight == null) ||
                    (waringLeft == null && avg <= waringRight) ||
                    (waringRight == null && avg >= waringLeft) ||
                    ((waringLeft != null && waringRight != null) && avg <= waringRight && avg >= waringLeft)) {
                jsonObject.put("warningStatus", 1);
                earlyWarningTime++;
            }
        }
        return earlyWarningTime;
    }

    /**
     * 获取所有非离线机巢所在位置的天气信息  并添加至缓存
     */
    @Override
    public void getWeather() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date());
        log.info("从第三方天气接口获取机巢所在位置天气信息, 当前时间:" + now);
        List<OrgSimpleOutDO> orgInfos = uosOrgManager.listAllOrgInfos();
        for (int i = 0; orgInfos != null && i < orgInfos.size(); i++) {
            getWeather(orgInfos.get(i));
        }
    }

    /**
     * 根据单位信息获取所有非离线机巢所在位置的天气信息 并添加至缓存
     *
     * @param unit 单位信息
     */
    private void getWeather(OrgSimpleOutDO unit) {
        List<NestDto> nestEntities;
        if (unit == null) {
            return;
        }
        nestEntities = nestService.listNestByOrgCode(unit.getOrgCode());
        if (nestEntities == null || nestEntities.size() == 0) {
            return;
        }
        for (NestEntity nest : nestEntities) {
            if (nest == null) {
                continue;
            }
//            System.out.println(nest.getUuid());
            int state = commonNestStateService.getNestCurrentState(nest.getUuid());
//            System.out.println("state:"+state);
            if (state != -1) {//只有非离线机巢才可以查询天气情况
                log.info("获取" + nest.getName() + "所在位置的天气信息");
                getWeather(nest);
            }
        }
    }

    /**
     * 根据机巢信息获取所有非离线机巢所在位置的天气信息 并添加至缓存
     *
     * @param nest 机巢信息
     */
    private void getWeather(NestEntity nest) {

        if (nest == null) { //如果nest为空
            return;
        }
        Double longitude = nest.getLongitude();
        Double latitude = nest.getLatitude();

        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);

        List<EarlyWarningKeyEntity> earlyWarningKeyEntities = earlyWarningKeyService.list();
        if (earlyWarningKeyEntities != null && earlyWarningKeyEntities.size() > 0) { //这个是为了预留以后可能会有多个秘钥的情况
            for (EarlyWarningKeyEntity entity : earlyWarningKeyEntities) {
                if (StringUtils.isBlank(entity.getToken())) continue;
                String caiYunData = weatherUtils.getCaiYunData(nf.format(longitude), nf.format(latitude), EARLY_WARNING_STEP, entity.getToken());
                JSONObject caiYunJson = JSONObject.parseObject(caiYunData);
                String caiYunStatus = caiYunJson.getString("status");
                if (caiYunStatus.equals("ok")) {
                    setWeatherCache(caiYunJson, nest.getUuid());
                    log.info("已成功获取" + nest.getName() + "所在位置的天气信息");
                    break;
                } else {
                    log.info(entity.getToken() + ", 该秘钥已失效");
                }
            }
        } else {
            log.info("未在数据库找到第三方天气接口秘钥, 使用默认秘钥");
            WeatherConfig weather = geoaiUosProperties.getWeather();
            String caiYunData = weatherUtils.getCaiYunData(nf.format(longitude), nf.format(latitude), EARLY_WARNING_STEP, weather.getAccessKey());
            JSONObject caiYunJson = JSONObject.parseObject(caiYunData);
            String caiYunStatus = caiYunJson.getString("status");
            if (caiYunStatus.equals("ok")) {
                setWeatherCache(caiYunJson, nest.getUuid());
                log.info("已成功获取" + nest.getName() + "所在位置的天气信息");
            } else {
                log.info(weather.getAccessKey() + ", 该秘钥已失效");
            }
        }
    }

    //将天气信息写入缓存
    private void setWeatherCache(JSONObject caiYunJson, String nestUuid) {
        JSONObject caiYunResult = caiYunJson.getJSONObject("result");
        JSONObject dailyJsonObject = caiYunResult.getJSONObject("daily");
        Map<String, Object> map = new HashMap<>();
        map.put("temperatureArray", dailyJsonObject.getJSONArray("temperature"));//温度预警
        map.put("precipitationArray", dailyJsonObject.getJSONArray("precipitation"));//降雨量
        map.put("windArray", dailyJsonObject.getJSONArray("wind"));//风速预警
        map.put("skyconArray", dailyJsonObject.getJSONArray("skycon"));//天气状态
        String key = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_EARLY_WARNING_CACHE, nestUuid);
        redisService.hmSet(key, map);
    }

    @Override
    public JSONArray FirstEarlyWarningByUserId(Long userId) {
        WeatherConfig weather = geoaiUosProperties.getWeather();
        if (!weather.isActivate()) {
            return null;
        }
        JSONArray result = new JSONArray();
        // 查询当前用户所在单位
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        Optional<OrgSimpleOutDO> optional = uosOrgManager.getOrgInfo(orgCode);
        optional.ifPresent(r -> result.add(FirstEarlyWarningByUnitId(r)));
        return result;
    }

    public JSONObject FirstEarlyWarningByUnitId(OrgSimpleOutDO unit) {
        JSONObject result = new JSONObject();
        List<NestDto> nestEntities;
        if (unit == null) {
            return result;
        }
        nestEntities = nestService.listNestByOrgCode(unit.getOrgCode());
        List<BaseNestInfoOutDTO> baseNestInfoOutDTOS = baseNestService.listBaseNestInfosByOrgCode(unit.getOrgCode());
        result.put("unitId", unit.getOrgCode());
        result.put("unitName", unit.getOrgName());
        ArrayList<JSONObject> nestEarlyWarnings = new ArrayList<>();
        if (nestEntities == null || nestEntities.size() == 0) return result;
        for (BaseNestInfoOutDTO nest : baseNestInfoOutDTOS) {
            if (nest == null) continue;
            int state = commonNestStateService.getNestCurrentState(nest.getUuid());
            if (state != -1) {//只有非离线机巢才可以查询天气情况
//                log.info("获取机巢所在地天气信息, " +nest.getUuid() + ":" + state);
                nestEarlyWarnings.add(getEarlyWarningByNest(nest));
            }
        }
        result.put("nestEarlyWarnings", nestEarlyWarnings);
        return result;
    }

    @Override
    public List<EarlyWarningDo> getEarlyWarningByUnitId(String nestId, String orgCode) {
        return getBaseMapper().getEarlyWarningByUnitId(nestId, orgCode);
    }

    @Transactional
    @Override
    public void deleteByIds(Integer[] ids) {
        //删除单位关联数据
        QueryWrapper<EarlyWarningUnitEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("early_warning_id", Arrays.asList(ids));
        earlyWarningUnitService.remove(queryWrapper);
        //删除预警规则
        this.removeByIds(Arrays.asList(ids));
    }

    @Override
    public void updateState(Byte state, Integer id) {
        this.lambdaUpdate().eq(EarlyWarningEntity::getId, id).set(EarlyWarningEntity::getState, state).update();
    }

}
