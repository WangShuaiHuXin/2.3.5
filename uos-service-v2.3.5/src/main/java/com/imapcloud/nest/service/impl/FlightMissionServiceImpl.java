package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.mapper.FlightMissionMapper;
import com.imapcloud.nest.model.FlightMissionEntity;
import com.imapcloud.nest.pojo.dto.flightMission.FlightMissionDTO;
import com.imapcloud.nest.pojo.vo.FlightMissionAggVO;
import com.imapcloud.nest.pojo.vo.FlightMissionExportVO;
import com.imapcloud.nest.pojo.vo.FlightMissionSyncAggVO;
import com.imapcloud.nest.pojo.vo.FlightMissionVO;
import com.imapcloud.nest.service.FlightMissionService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.service.event.flightMission.FlightMissionSyncEvent;
import com.imapcloud.nest.utils.LocalDateUtil;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.Query;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.excel.CustomCellWidthStyleStrategy;
import com.imapcloud.nest.utils.excel.EasyExcelStyleUtils;
import com.imapcloud.nest.utils.excel.EasyExcelUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.data.DataManagerCf;
import com.imapcloud.sdk.manager.data.entity.FlightMissionPageEntity;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author double
 * @since 2022-03-22
 */
@Slf4j
@Service
public class FlightMissionServiceImpl extends ServiceImpl<FlightMissionMapper, FlightMissionEntity> implements FlightMissionService {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private NestService nestService;

    @Resource
    private BaseNestService baseNestService;

    public final String NO_MORE_DATA = "no more data";

    /**
     * 当前展示数量
     */
    public final Integer pageSize = 50;

    private static final LocalDateTime TIME_2010 = LocalDateTime.of(2010, 1, 1, 0, 0, 0, 0);
    /**
     * 查询分页接口
     *
     * @param nestId
     * @param page
     * @param limit
     * @param asc
     * @param desc
     * @return
     */
    @Override
    public RestRes queryFlightMissionPage(String nestId, Integer page
            , Integer limit, String asc
            , String desc, String startTime
            , String endTime, Integer uavWhich) {
        Map<String, Object> params = new HashMap<>(2);
        params.put(Query.PAGE, page);
        params.put(Query.LIMIT, limit);
        Date startTimeDate = null, endTimeDate = null;
        String startTimeStr = "startTime";
        if (StringUtil.isNotEmpty(startTime)) {
            startTimeDate = DateUtil.parse(startTime, "yyyy-MM-dd HH:mm:ss");
        }
        if (StringUtil.isNotEmpty(endTime)) {
            endTimeDate = DateUtil.parse(endTime, "yyyy-MM-dd HH:mm:ss");
        }
        //默认初始化排序
        if (StringUtil.isEmpty(asc) && StringUtil.isEmpty(desc)) {
            desc = startTimeStr;
        }
        IPage<FlightMissionVO> totalPage = baseMapper.getFlightMissionPage(new Query<FlightMissionVO>().getPage(params, asc, desc), nestId, startTimeDate, endTimeDate, uavWhich);
        PageUtils pageData = new PageUtils(totalPage);
        Map<String, Object> returnMap = new HashMap<>(2);
        List<FlightMissionVO> flightMissionVOList = totalPage.getRecords();
        // 结束时间小于2010年的数据，时间设置为null，时长为0
        if (CollUtil.isNotEmpty(flightMissionVOList)) {
            for (FlightMissionVO flightMissionVO : flightMissionVOList) {

                if (TIME_2010.isAfter(flightMissionVO.getEndTime())) {
                    flightMissionVO.setEndTime(null);
                    flightMissionVO.setMissionDate(0D);
                }
            }
        }
        //获取总体记录
        FlightMissionAggVO flightMissionAggVO = this.baseMapper.getTotalFlightMissionVO(nestId);
        flightMissionAggVO.setFlightMissionVOList(flightMissionVOList);
        List<FlightMissionAggVO> returnList = new ArrayList<>();
        returnList.add(flightMissionAggVO);
        pageData.setList(returnList);
        returnMap.put("page", pageData);
        return RestRes.ok(returnMap, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SEARCH_SUCCESS.getContent()));
    }

    /**
     * 飞行记录导出
     *
     * @param nestIds
     * @param startTime
     * @param endTime
     * @param response
     */
    @Override
    public void exportFlightMissionRecord(String nestIds, String startTime, String endTime, Integer uavWhich, HttpServletResponse response) {
        List<FlightMissionExportVO> flightMissionExportVOList = new ArrayList<>();
        String format = "yyyy-MM-dd";
        if (nestIds != null) {

            List<String> nestIdsList = StringUtils.commaDelimitedListToSet(nestIds).stream().map(nestId -> String.valueOf(nestId)).collect(Collectors.toList());
            flightMissionExportVOList = this.getBaseMapper().getExportVO(nestIdsList
                    , DateUtil.beginOfDay(StringUtil.isEmpty(startTime) ? DateUtil.lastMonth() : DateUtil.parse(startTime, format))
                    , DateUtil.endOfDay(StringUtil.isEmpty(endTime) ? DateUtil.date() : DateUtil.parse(endTime, format))
                    , uavWhich);
            if (CollectionUtil.isNotEmpty(flightMissionExportVOList)) {
                flightMissionExportVOList.stream().forEach(e -> {
                    e.setNestType(NestTypeEnum.getInstance(e.getNestTypeId()).getMessage());
                    if (TIME_2010.isAfter(e.getEndTime())) {
                        e.setEndTime(null);
                        e.setMissionDate(0D);
                    }
                });
            }
        }
        List handlers = new ArrayList<>();
        handlers.add(new CustomCellWidthStyleStrategy());
        handlers.add(EasyExcelStyleUtils.getStyleStrategy());
        EasyExcelUtils.writeAndResponseForHandler(String.format("无人机飞行记录_%s", DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd"))
                , FlightMissionExportVO.class
                , flightMissionExportVOList
                , response
                , handlers);
    }

    /**
     * 标记删除飞行记录
     *
     * @param flightIds
     * @return
     */
    @Override
    public RestRes deleteBatchRecord(String flightIds) {
        String[] arr = flightIds.split(",");
        for (String idStr : arr) {
            Integer id = Integer.parseInt(idStr);
            this.deleteRecord(id);
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_DELETED.getContent()));
    }

    /**
     * 单条删除
     *
     * @param flightId
     * @return
     */
    public RestRes deleteRecord(Integer flightId) {
        this.lambdaUpdate()
                .setSql(" deleted = 1 ")
                .eq(FlightMissionEntity::getId, flightId)
                .eq(FlightMissionEntity::getDeleted, 0)
                .update();
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_DELETED.getContent()));
    }

    /**
     * 同步飞行记录
     *
     * @param flightMissionDTO
     * @return
     */
    @Override
    public RestRes syncFlightMission(FlightMissionDTO flightMissionDTO) {
        FlightMissionSyncAggVO flightMissionSyncAggVO = this.syncFlightMissionMain(flightMissionDTO);
        if (!flightMissionSyncAggVO.getSuccess()) {
            return RestRes.err(flightMissionSyncAggVO.getMsg());
        }
        FlightMissionAggVO returnVO = new FlightMissionAggVO();
        BeanUtils.copyProperties(flightMissionSyncAggVO, returnVO);
        return RestRes.ok("FlightMissionAggVO", returnVO, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SYNCHRONIZATION.getContent()));
    }

    /**
     * 同步主方法
     *
     * @param fightMissionDTO
     */
    @Override
    public FlightMissionSyncAggVO syncFlightMissionMain(FlightMissionDTO fightMissionDTO) {
        Integer page = 1;
        String nestId = fightMissionDTO.getNestId();
        FlightMissionSyncAggVO flightMissionSyncAggVO = FlightMissionSyncAggVO.builder().nestId(nestId).success(true).build();
        log.info("syncFlightMissionMain同步开始，nestId->{}", nestId);
        if (nestId == null) {
            log.info("syncFlightMissionMain同步结束，nestId为空");
            return FlightMissionSyncAggVO.builder().success(false).msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SYNCFLIGHTMISSIONMAIN_NESTID_EMPTY.getContent())).build();
        }

        LocalDateTime startTime = fightMissionDTO.getStartTime(), endTime = fightMissionDTO.getEndTime();
        if (startTime == null) {
            log.info("syncFlightMissionMain同步结束，开始时间为空");
            return FlightMissionSyncAggVO.builder().success(false).msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SYNCFLIGHTMISSIONMAIN_STARTTIME_EMPTY.getContent())).build();
        }
        if (endTime == null) {
            log.info("syncFlightMissionMain同步结束，结束时间为空");
            return FlightMissionSyncAggVO.builder().success(false).msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SYNCFLIGHTMISSIONMAIN_ENDTIME_EMPTY.getContent())).build();
        }

        AirIndexEnum airIndexEnum = Objects.isNull(fightMissionDTO.getUavWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(fightMissionDTO.getUavWhich());
        flightMissionSyncAggVO.setUavWhich(airIndexEnum.getVal());

        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            log.info("id->{},基槽离线", nestId);
            return FlightMissionSyncAggVO.builder().success(false).msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SYNCFLIGHTMISSIONMAIN_BASE_SLOT_OFFLINE.getContent())).build();
        }
        DataManagerCf dataManagerCf = cm.getDataManagerCf();
        //这里要分页查询，直至所有结果返回。
        List<FlightMissionPageEntity> pageEntities = new ArrayList<>();
        int totalPage = 2;
        do {
            log.info("mqtt-【queryFlightMissionMsgListByDate】传递参数:{}~{}~{}~{}~{}", startTime, endTime, fightMissionDTO.getUavWhich(), page, totalPage);
            MqttResult<FlightMissionPageEntity> res = dataManagerCf.queryFlightMissionMsgListByDate(page
                    , 10
                    , LocalDateUtil.localDateTimeToTimestamp(startTime)
                    , LocalDateUtil.localDateTimeToTimestamp(endTime)
                    , airIndexEnum);
            if (!res.isSuccess() && !NO_MORE_DATA.equals(res.getMsg())) {
                log.info("指令发送失败:{}", res.getMsg());
                return FlightMissionSyncAggVO.builder().success(false).msg(res.getMsg()).build();
            }
            totalPage = res.getRes() == null ? 1 : res.getRes().getTotalPage();
            pageEntities.add(res.getRes());
        } while (totalPage > page++);
        this.applicationContext.publishEvent(new FlightMissionSyncEvent(flightMissionSyncAggVO.setPageEntityList(pageEntities)));
        return flightMissionSyncAggVO;
    }
}
