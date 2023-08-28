package com.imapcloud.sdk.manager.data;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.data.entity.FlightMissionPageEntity;
import com.imapcloud.sdk.manager.data.entity.FlightStatisticsEntity;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据管理
 *
 * @author wmin
 */
public class DataManagerCf {
    private final static String FUNCTION_TOPIC = DataManagerCode.DATA_MANAGER_FUNCTION_TOPIC;
    private final Client client;

    public DataManagerCf(Client client) {
        this.client = client;
    }

    /**
     * 查询飞行架次列表
     */
    public MqttResult<FlightStatisticsEntity> queryFlightStatistics(AirIndexEnum... which) {
        MqttResParam<FlightStatisticsEntity> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(DataManagerCode.DATA_MANAGER_C01)
                .clazz(FlightStatisticsEntity.class)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 查询飞行架次信息列表
     */
    public MqttResult<FlightMissionPageEntity> queryFlightMissionMsgList(Integer page, Integer pageSize, AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("page", page);
        param.put("pageSize", pageSize);
        MqttResParam<FlightMissionPageEntity> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(DataManagerCode.DATA_MANAGER_C02)
                .clazz(FlightMissionPageEntity.class)
                .param(param)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 新接口，新加时间范围查询
     * 查询飞行架次信息列表
     */
    public MqttResult<FlightMissionPageEntity> queryFlightMissionMsgListByDate(Integer page, Integer pageSize, long startTime , long endTime , AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("page", page);
        param.put("pageSize", pageSize);
        param.put("startTime",startTime);
        param.put("endTime",endTime);
        MqttResParam<FlightMissionPageEntity> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(DataManagerCode.DATA_MANAGER_C02)
                .clazz(FlightMissionPageEntity.class)
                .param(param)
                .maxWaitTime(6)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 上传飞行轨迹
     *
     * @param execMissionID
     * @param uploadUrl
     */
    public MqttResult<NullParam> uploadFlightPath(String execMissionID, String uploadUrl, AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("execMissionID", execMissionID);
        param.put("uploadUrl", uploadUrl);
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(DataManagerCode.DATA_MANAGER_C03)
                .param(param)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


}
