package com.imapcloud.sdk.manager.system;

import com.imapcloud.sdk.manager.*;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.entity.CpsVersionCode;

import java.util.HashMap;
import java.util.Map;

public class SystemManagerCf {
    private final static String FUNCTION_TOPIC = SystemManagerCode.SYSTEM_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public SystemManagerCf(Client client) {
        this.client = client;
    }

    public MqttResult<String> getMpsVersion(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(SystemManagerCode.SYSTEM_MANAGER_C01)
                .clazz(String.class)
                .key("version")
                .which(which);
        return ClientProxy.getMqttResult(mrp);

    }

    public MqttResult<String> getCpsVersion(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(SystemManagerCode.SYSTEM_MANAGER_C02)
                .clazz(String.class)
                .key("version")
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    public MqttResult<CpsVersionCode> getCpsVersionCode() {
        MqttResParam<CpsVersionCode> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(SystemManagerCode.SYSTEM_MANAGER_C02)
                .clazz(CpsVersionCode.class)
                .which(AirIndexEnum.DEFAULT);
        return ClientProxy.getMqttResult(mrp);
    }

    public MqttResult<String> getNestMode(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(SystemManagerCode.SYSTEM_MANAGER_C03)
                .clazz(String.class)
                .key("nest_model")
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取MPS错误信息回调
     *
     * @param which
     */
    public MqttResult<String> getMpsError(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(SystemManagerCode.SYSTEM_MANAGER_C06)
                .clazz(String.class)
                .key("errorInfo")
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 基站日志上传
     *
     * @param module        指定上传的文件夹
     * @param serverAddress Http上传文件地址
     * @param filename      文件名称
     * @deprecated 2.2.3，
     * 使用新接口{@link SystemManagerCf#uploadLogFile(java.lang.String, java.lang.String, java.lang.String, com.imapcloud.sdk.manager.UploadV2Param, com.imapcloud.sdk.pojo.constant.AirIndexEnum...)}替代，
     * 将在后续版本删除
     */
    @Deprecated
    public MqttResult<NullParam> uploadLogFile(String module, String serverAddress, String filename, AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("module", module);
        // @deprecated 2.2.3，将在所有基站升级至相应版本后删除
        param.put("serverAddress", serverAddress);
        param.put("fileName", filename);
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(SystemManagerCode.SYSTEM_MANAGER_C04)
                .param(param)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 基站日志上传
     * @param module        上传模块
     * @param uploadV2Param 上传参数
     * @param which      无人机位置
     */
    public MqttResult<NullParam> uploadLogFile(String module, String serverAddress, String filename, UploadV2Param uploadV2Param, AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("module", module);
        // @deprecated 2.2.3，将在所有基站升级至相应版本后删除
        param.put("serverAddress", serverAddress);
        param.put("fileName", filename);
        // v2版
        param.put("v2SmallFileUploadUrl", uploadV2Param.getV2SmallFileUploadUrl());
        param.put("v2ChunkUpload", uploadV2Param.getV2ChunkUpload());
        param.put("v2ChunkInitUrl", uploadV2Param.getV2ChunkInitUrl());
        param.put("v2ChunkUploadUrl", uploadV2Param.getV2ChunkUploadUrl());
        param.put("v2ChunkCombineCallbackUrl", uploadV2Param.getV2ChunkCombineCallbackUrl());
        param.put("v2ChunkSize", uploadV2Param.getV2ChunkSize());
        param.put("v2ChunkSyncUrl", uploadV2Param.getV2ChunkSyncUrl());
        param.put("v2AuthUrl", uploadV2Param.getV2AuthUrl());
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(SystemManagerCode.SYSTEM_MANAGER_C04)
                .param(param)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 更新cps
     * 通过输入参数apkUrl到cps，让cps到指定的地方拉取apk包
     */
    public MqttResult<NullParam> updateCps(String apkUrl, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(SystemManagerCode.SYSTEM_MANAGER_C07)
                .param("apk", apkUrl)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 更新mps
     * 通过输入参数apkUrl到mps，让mps到指定的地方拉取apk包
     * 返回结果太久，忽略返回结果
     */
    public void updateMps(String apkUrl, String apkVersion, String apkName, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        Map<String, Object> params = new HashMap<>(3);
        params.put("version", apkVersion);
        params.put("url", apkUrl);
        params.put("fileName", apkName);
        mrp.type(MqttResParam.Type.NUL)
                .code(SystemManagerCode.SYSTEM_MANAGER_C13)
                .param(params)
                .which(which);
        ClientProxy.ignoreMqttResult(mrp);
    }

    /**
     * 清空基站日志
     */
    public MqttResult<NullParam> clearNestLog(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(SystemManagerCode.SYSTEM_MANAGER_C08)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 查看系统时间
     *
     * @param which
     */
    public MqttResult<String> selectSysTime(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(SystemManagerCode.SYSTEM_MANAGER_C09)
                .clazz(String.class)
                .key("time")
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 更新系统时间
     *
     * @param which
     */
    public MqttResult<NullParam> updateSysTime(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(SystemManagerCode.SYSTEM_MANAGER_C10)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 清理DJI缓存文件
     */
    public MqttResult<NullParam> clearDjiCacheFile(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(SystemManagerCode.SYSTEM_MANAGER_C12)
                .maxWaitTime(15)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 取消CPS安装
     */
    public MqttResult<NullParam> cancelCpsInstall(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(SystemManagerCode.SYSTEM_MANAGER_C16)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 取消MPS安装
     */
    public MqttResult<NullParam> cancelMpsInstall(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(SystemManagerCode.SYSTEM_MANAGER_C14)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 重连USB
     *
     * @param which
     * @return
     */
    public MqttResult<NullParam> reconnectUsb(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(SystemManagerCode.SYSTEM_MANAGER_C18)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }
}
