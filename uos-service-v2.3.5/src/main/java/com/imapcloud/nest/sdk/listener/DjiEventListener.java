package com.imapcloud.nest.sdk.listener;

import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.pojo.vo.DjiHmsVO;
import com.imapcloud.nest.pojo.vo.RemoteDebugVO;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.v2.common.utils.DjiHmsUtils;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.dji.DjiDockManagerCf;
import com.imapcloud.sdk.pojo.constant.dji.DjiErrorCodeEnum;
import com.imapcloud.sdk.pojo.constant.dji.DjiMethodEnum;
import com.imapcloud.sdk.pojo.djido.HmsEventDO;
import com.imapcloud.sdk.pojo.djido.RemoteDebugProgressDO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wmin
 */

@Component
public class DjiEventListener {

    public void refreshNestState(String nestUuid) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (Objects.nonNull(cm)) {
            DjiDockManagerCf djiDockManagerCf = cm.getDjiDockManagerCf();

            listenHmsEvents(nestUuid, djiDockManagerCf);
            listenRemoteDebugProgressOfEvents(nestUuid, djiDockManagerCf);
        }
    }

    /**
     * 监听hms事件将其通过ws传给前端
     */
    public void listenHmsEvents(String nestUuid, DjiDockManagerCf djiDockManagerCf) {
        djiDockManagerCf.listenHmsEvents((djiCommonDO, isSuccess, errMsg) -> {
            HmsEventDO data = djiCommonDO.getData();
            if (Objects.isNull(data)) {
                return;
            }
            List<DjiHmsVO> voList;
            List<HmsEventDO.Item> list = data.getList();
            if (CollectionUtils.isEmpty(list)) {
                voList = Collections.emptyList();
            } else {
                voList = list.stream().map(item -> {
                    DjiHmsUtils.HmsEvent hmsEvent = new DjiHmsUtils.HmsEvent();
                    hmsEvent.setModule(item.getModule());
                    hmsEvent.setCode(item.getCode());
                    hmsEvent.setDomainType(item.getDomainType());
                    hmsEvent.setInTheSky(item.getInTheSky());
                    DjiHmsUtils.HmsEventArgs hmsEventArgs = new DjiHmsUtils.HmsEventArgs();
                    hmsEventArgs.setSensorIndex(item.getArgs().getSensorIndex());
                    hmsEventArgs.setComponentIndex(item.getArgs().getComponentIndex());
                    hmsEventArgs.setAlarmid(item.getArgs().getAlarmid());
                    hmsEvent.setArgs(hmsEventArgs);
                    String hmsZhTip = DjiHmsUtils.getHmsZhTip(hmsEvent);

                    DjiHmsVO djiHmsVO = new DjiHmsVO();
                    djiHmsVO.setLevel(item.getLevel());
                    djiHmsVO.setTip(hmsZhTip);
                    djiHmsVO.setCode(item.getCode());
                    djiHmsVO.setModule(item.getModule());
                    return djiHmsVO;
                }).collect(Collectors.toList());
            }

            Map<String, Object> map = new HashMap<>(2);
            map.put("hmsTips", voList);
            String msg = WebSocketRes.ok().msg("").topic(WebSocketTopicEnum.DJI_HMS_TIPS).data(map).uuid(nestUuid).toJSONString();
            ChannelService.sendMessageByType3Channel(nestUuid, msg);
        });
    }

    /**
     * 监听远程调试单步指令事件
     * 只提示报错问题
     *
     * @param nestUuid
     * @param djiDockManagerCf
     */
    public void listenRemoteDebugProgressOfEvents(String nestUuid, DjiDockManagerCf djiDockManagerCf) {
        djiDockManagerCf.listenRemoteDebugProgressOfEvents((djiCommonDO, isSuccess, errMsg) -> {
            RemoteDebugProgressDO data = djiCommonDO.getData();
            if (Objects.isNull(data)) {
                return;
            }

            Integer result = data.getResult();
            if (DjiErrorCodeEnum.isSuccess(result)) {
                return;
            }
            String tip = DjiErrorCodeEnum.getMsg(data.getResult());
            DjiMethodEnum zhMethodEnum = DjiMethodEnum.getInstance(djiCommonDO.getMethod());
            RemoteDebugVO remoteDebugVO = new RemoteDebugVO();
            remoteDebugVO.setCode(String.valueOf(data.getResult()));
            remoteDebugVO.setTip(tip);
            remoteDebugVO.setMethod(zhMethodEnum.getZh());

            Map<String, Object> map = new HashMap<>(2);
            map.put("remoteDebugTip", remoteDebugVO);
            String msg = WebSocketRes.ok().msg("").topic(WebSocketTopicEnum.DJI_REMOTE_DEBUG_TIPS).data(map).uuid(nestUuid).toJSONString();
            ChannelService.sendMessageByType3Channel(nestUuid, msg);
        });
    }
}
