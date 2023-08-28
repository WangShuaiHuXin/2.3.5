package com.imapcloud.sdk.manager.accessory;

import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.sdk.CommonNestState;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.nest.v2.dao.po.in.AccessoryInPO;
import com.imapcloud.nest.v2.dao.po.out.AccessoryOutPO;
import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.accessory.AccessoryAuthStatusOutPo;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.entity.NestState;
import com.imapcloud.sdk.utils.StringUtil;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wmin
 * 在使用此类指令之前，请确保无人机可以访问附件
 */
public class AccessoryManagerCf {
    private final static String FUNCTION_TOPIC = Constant.ACCESSORY_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public AccessoryManagerCf(Client client) {
        this.client = client;
    }


    /**
     * 打开聚光灯
     *
     * @param which
     */
    public MqttResult<NullParam> openSpotlight(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C1)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 关闭聚光灯
     *
     * @param which
     */
    public MqttResult<NullParam> closeSpotlight(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C2)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 设置聚光灯亮度
     *
     * @param brightness 亮度
     * @param which      回调
     */
    public MqttResult<NullParam> setSpotlightBrightness(Integer brightness, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C3)
                .param("brightness", brightness)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 打开夜航灯
     *
     * @param which
     */
    public MqttResult<NullParam> openBeacon(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C4)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 关闭夜航灯
     *
     * @param which
     */
    public MqttResult<NullParam> closeBeacon(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C5)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置扬声器音量
     *
     * @param volume 音量
     * @param which  回调
     */
    public MqttResult<NullParam> setSpeakerVolume(Integer volume, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C6)
                .param("volume", volume)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置扬声器播放模式
     *
     * @param isRepeat 是否重复
     * @param which
     */
    public MqttResult<NullParam> setSpeakerPlayMode(Boolean isRepeat, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C7)
                .param("isRepeat", isRepeat)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 扬声器播放音频
     *
     * @param index 序号
     * @param which
     */
    public MqttResult<NullParam> speakerPlayAudio(Integer index, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C8)
                .param("index", index)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 扬声器停止播放音频
     *
     * @param which
     */
    public MqttResult<NullParam> speakerStopPlayAudio(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C9)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 扬声器播放列表重命名
     *
     * @param index
     * @param name
     * @param which
     */
    public MqttResult<NullParam> speakerPlayListRename(Integer index, String name, AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("index", index);
        param.put("name", name);

        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C10)
                .param(param)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 获取播放列表
     */
    public MqttResult<BaseResult3> listSpeakerPlay(AirIndexEnum... which) {
        MqttResParam<BaseResult3> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ORI)
                .code(Constant.ACCESSORY_MANAGER_C15)
                .clazz(BaseResult3.class)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 删除机巢音频
     *
     * @param which
     */
    public MqttResult<NullParam> speakerDeleteAudio(List<Integer> indexList, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C12)
                .param("indexList", indexList)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 播放器停止录音
     */
    public MqttResult<NullParam> speakerStopRecord(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C14)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 上传音频，上传音频时间太长，直接使用回调更加合适，这里不做异步转换同步
     */
//    @Deprecated
//    public MqttResult<BaseResult3> uploadAudio(String saveFileName, String url, Boolean isMp3, AirIndexEnum... which) {
//        HashMap<String, Object> param = new HashMap<>(2);
//        param.put("saveFileName", saveFileName);
//        param.put("url", url);
//        param.put("isMp3", isMp3);
//
//        MqttResParam<BaseResult3> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
//        mrp.type(MqttResParam.Type.NUL)
//                .code(Constant.ACCESSORY_MANAGER_C18)
//                .clazz(BaseResult3.class)
//                .param(param)
//                .which(which);
//        return ClientProxy.getMqttResult(mrp);
//    }

    /**
     * 上传音频
     */
    public void uploadAudio(String saveFileName, String url, Boolean isMp3, UserHandle<BaseResult3> handle) {
        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
            @Override
            public void success(BaseResult3 baseResult3, String msg) {
                handle.handle(baseResult3, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("saveFileName", saveFileName);
        param.put("url", url);
        param.put("isMp3", isMp3);
        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, Constant.ACCESSORY_MANAGER_C18, param, ph);
    }

    /**
     * 取消音频传输（包含实时语音、上传音频）
     *
     * @param which
     */
    public MqttResult<NullParam> stopUploadAudio(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C19)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 喊话器状态重置
     *
     * @param which
     */
    public MqttResult<NullParam> resetSpeaker(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C20)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 喊话器开始录音
     *
     * @param which
     */
    public MqttResult<NullParam> speakerStartRecord(Boolean isPersistent, Boolean isMp3, AirIndexEnum... which) {
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("isPersistent", isPersistent);
        param.put("isMp3", isMp3);

        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C16)
                .param(param)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 喊话器结束录音
     *
     * @param which
     */
    public MqttResult<NullParam> speakerEndRecord(AirIndexEnum... which){
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C17)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 喊话器传输音频字节
     *
     * @param handle
     */
    public void sendAudioBytes(byte[] bytes, UserHandle<Boolean> handle) {
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
        ClientProxy.proxyPublishBytes(this.client, FUNCTION_TOPIC + "/speaker/realtimeData", bytes, ph);
    }

    /**
     * 喊话器传输音频字节
     */
    public void sendAudioBytes(byte[] bytes) {
        ClientProxy.proxyPublishBytes(this.client, FUNCTION_TOPIC + "/speaker/realtimeData", bytes);
    }


    /**
     * 获取认证状态
     */
    public MqttResult<AccessoryAuthStatusOutPo> getLteAhtuStatus() {
        MqttResParam<AccessoryAuthStatusOutPo> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .clazz(AccessoryAuthStatusOutPo.class)
                .code(Constant.ACCESSORY_MANAGER_C52)
                .which(AirIndexEnum.DEFAULT);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 发送验证码
     *
     * @param inPO
     * @return
     */
    public MqttResult<NullParam> sendCaptcha(AccessoryInPO.AccessoryCaptchaInPO inPO) {
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("phoneNumber", inPO.getLteaPhoneNumber());
        param.put("areaCode", inPO.getLteaAreaCode());
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C50)
                .param(param)
                .which(AirIndexEnum.DEFAULT);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 发起实名认证请求
     *
     * @param inPO
     * @return
     */
    public MqttResult<NullParam> sendCertification(AccessoryInPO.AccessoryCaptchaInPO inPO) {
        HashMap<String, Object> param = new HashMap<>(3);
        param.put("phoneNumber", inPO.getLteaPhoneNumber());
        param.put("areaCode", inPO.getLteaAreaCode());
        param.put("verificationCode", inPO.getVerificationCode());
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C51)
                .param(param)
                .which(AirIndexEnum.DEFAULT);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 指令-设置增强图传状态
     *
     * @param enable
     * @return
     */
    public MqttResult<NullParam> setTransmission(Boolean enable) {
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("enable", enable);
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ACCESSORY_MANAGER_C53)
                .param(param);
        return ClientProxy.getMqttResult(mrp);
    }

    public MqttResult<AccessoryOutPO.AccessoryEnableOutPO> getTransmission() {
        MqttResParam<AccessoryOutPO.AccessoryEnableOutPO> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.ACCESSORY_MANAGER_C54)
                .clazz(AccessoryOutPO.AccessoryEnableOutPO.class);
        MqttResult<AccessoryOutPO.AccessoryEnableOutPO> mqttResult = ClientProxy.getMqttResult(mrp);
        return mqttResult;
    }
}
