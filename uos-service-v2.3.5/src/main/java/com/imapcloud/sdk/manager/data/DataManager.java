package com.imapcloud.sdk.manager.data;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.data.entity.FlightMissionPageEntity;
import com.imapcloud.sdk.manager.data.entity.FlightStatisticsEntity;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据管理
 * @author wmin
 */
@Deprecated
public class DataManager {
    private final static String FUNCTION_TOPIC = DataManagerCode.DATA_MANAGER_FUNCTION_TOPIC;
    private final Client client;

    public DataManager(Client client) {
        this.client = client;
    }

    /**
     * 查询飞行架次列表
     */
    public void queryFlightStatistics(UserHandle<FlightStatisticsEntity> handle){
        ProxyHandle<FlightStatisticsEntity> ph = new ProxyHandle<FlightStatisticsEntity>() {
            @Override
            public void success(FlightStatisticsEntity fse, String msg) {
                handle.handle(fse, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublish(this.client,FUNCTION_TOPIC, DataManagerCode.DATA_MANAGER_C01,null,ph,FlightStatisticsEntity.class);
    }

    /**
     * 查询飞行架次信息列表
     */
    public void queryFlightMissionMsgList(Integer page, Integer pageSize, UserHandle<FlightMissionPageEntity> handle) {

        ProxyHandle<FlightMissionPageEntity> ph = new ProxyHandle<FlightMissionPageEntity>() {
            @Override
            public void success(FlightMissionPageEntity fmpe, String msg) {
                handle.handle(fmpe, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        Map<String,Object> param = new HashMap<>(2);
        param.put("page",page);
        param.put("pageSize",pageSize);
        ClientProxy.proxyPublish(this.client,FUNCTION_TOPIC, DataManagerCode.DATA_MANAGER_C02,param,ph,FlightMissionPageEntity.class);
    }

    /**
     * 上传飞行轨迹
     * @param execMissionID
     * @param uploadUrl
     */
    public void uploadFlightPath(String execMissionID,String uploadUrl,UserHandle<Boolean> handle){
        ProxyHandle<Boolean> ph = new ProxyHandle<Boolean>() {
            @Override
            public void success(Boolean aBoolean, String msg) {
                handle.handle(aBoolean, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        Map<String,Object> param = new HashMap<>(2);
        param.put("execMissionID",execMissionID);
        param.put("uploadUrl",uploadUrl);
        ClientProxy.proxyPublishBool(this.client, FUNCTION_TOPIC, DataManagerCode.DATA_MANAGER_C03, param, ph);
    }


}
