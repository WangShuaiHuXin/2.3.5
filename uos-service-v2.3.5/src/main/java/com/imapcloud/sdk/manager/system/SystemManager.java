package com.imapcloud.sdk.manager.system;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class SystemManager {
    private final static String FUNCTION_TOPIC = SystemManagerCode.SYSTEM_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public SystemManager(Client client) {
        this.client = client;
    }

    public void getMpsVersion(UserHandle<String> handle) {
        getResultStr(SystemManagerCode.SYSTEM_MANAGER_C01, handle, null, "version");
    }

    public void getCpsVersion(UserHandle<String> handle) {
        getResultStr(SystemManagerCode.SYSTEM_MANAGER_C02, handle, null, "version");
    }

    public void getNestMode(UserHandle<String> handle) {
        getResultStr(SystemManagerCode.SYSTEM_MANAGER_C03, handle, null, "nest_model");
    }

    /**
     * 获取MPS错误信息回调
     *
     * @param handle
     */
    public void getMpsError(UserHandle<String> handle) {
        getResultStr(SystemManagerCode.SYSTEM_MANAGER_C06, handle, null, "errorInfo");
    }

    /**
     * 基站日志上传
     *
     * @param module        指定上传的文件夹
     * @param serverAddress Http上传文件地址
     * @param filename      文件名称
     */
    public void uploadLogFile(String module, String serverAddress, String filename, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("module", module);
        param.put("serverAddress", serverAddress);
        param.put("fileName", filename);
        getResultBoolean(SystemManagerCode.SYSTEM_MANAGER_C04, handle, param);
    }

    /**
     * 更新cps
     * 通过输入参数apkUrl到cps，让cps到指定的地方拉取apk包
     *
     * @param apkUrl
     * @param handle
     */
    public void updateCps(String apkUrl, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("apk", apkUrl);
        getResultBoolean(SystemManagerCode.SYSTEM_MANAGER_C07, handle, param);
    }

    /**
     * 清空基站日志
     *
     * @param handle
     */
    public void clearNestLog(UserHandle<Boolean> handle) {
        getResultBoolean(SystemManagerCode.SYSTEM_MANAGER_C08, handle, null);
    }

    /**
     * 查看系统时间
     *
     * @param handle
     */
    public void selectSysTime(UserHandle<String> handle) {
        getResultStr(SystemManagerCode.SYSTEM_MANAGER_C09, handle, null, "time");
    }


    /**
     * 更新系统时间
     *
     * @param handle
     */
    public void updateSysTime(UserHandle<Boolean> handle) {
        getResultBoolean(SystemManagerCode.SYSTEM_MANAGER_C10, handle, null);
    }

    /**
     * 清理DJI缓存文件
     */
    public void clearDjiCacheFile(UserHandle<Boolean> handle) {
        getResultBoolean(SystemManagerCode.SYSTEM_MANAGER_C12, handle, null);
    }

    private void getResultMap(String code, UserHandle<Map> handle) {
        ProxyHandle<Map> ph = new ProxyHandle<Map>() {
            @Override
            public void success(Map map, String msg) {
                handle.handle(map, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                handle.handle(null, false, msg);
            }
        };
        ClientProxy.proxyPublishParamMap(client, FUNCTION_TOPIC, code, ph);
    }

    private void getResultStr(String code, UserHandle<String> handle, Map<String, Object> param, String key) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String str, String msg) {
                handle.handle(str, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, code, param, ph, key,String.class);
    }

    private void getResultBoolean(String code, UserHandle<Boolean> handle, Map<String, Object> param) {
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
        ClientProxy.proxyPublishBool(this.client, FUNCTION_TOPIC, code, param, ph);
    }
}
