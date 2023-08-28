package com.imapcloud.sdk.manager.accessory;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wmin
 * 在使用此类指令之前，请确保无人机可以访问附件
 */

@Deprecated
public class AccessoryManager {
    private final static String FUNCTION_TOPIC = Constant.ACCESSORY_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public AccessoryManager(Client client) {
        this.client = client;
    }

    /**
     * 打开聚光灯
     *
     * @param handle
     */
    public void openSpotlight(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.ACCESSORY_MANAGER_C1, handle, null);
    }

    /**
     * 关闭聚光灯
     *
     * @param handle
     */
    public void closeSpotlight(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.ACCESSORY_MANAGER_C2, handle, null);
    }


    /**
     * 设置聚光灯亮度
     *
     * @param brightness 亮度
     * @param handle     回调
     */
    public void setSpotlightBrightness(Integer brightness, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("brightness", brightness);
        getResultBoolean(Constant.ACCESSORY_MANAGER_C3, handle, param);
    }

    /**
     * 打开夜航灯
     *
     * @param handle
     */
    public void openBeacon(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.ACCESSORY_MANAGER_C4, handle, null);
    }

    /**
     * 关闭夜航灯
     *
     * @param handle
     */
    public void closeBeacon(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.ACCESSORY_MANAGER_C5, handle, null);
    }

    /**
     * 设置扬声器音量
     *
     * @param volume 音量
     * @param handle 回调
     */
    public void setSpeakerVolume(Integer volume, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("volume", volume);
        getResultBoolean(Constant.ACCESSORY_MANAGER_C6, handle, param);
    }

    /**
     * 设置扬声器播放模式
     *
     * @param isRepeat 是否重复
     * @param handle
     */
    public void setSpeakerPlayMode(Boolean isRepeat, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("isRepeat", isRepeat);
        getResultBoolean(Constant.ACCESSORY_MANAGER_C7, handle, param);
    }

    /**
     * 扬声器播放音频
     *
     * @param index  序号
     * @param handle
     */
    public void speakerPlayAudio(Integer index, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("index", index);
        getResultBoolean(Constant.ACCESSORY_MANAGER_C8, handle, param);
    }


    /**
     * 扬声器停止播放音频
     *
     * @param handle
     */
    public void speakerStopPlayAudio(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.ACCESSORY_MANAGER_C9, handle, null);
    }

    /**
     * 扬声器播放列表重命名
     *
     * @param index
     * @param name
     * @param handle
     */
    public void speakerPlayListRename(Integer index, String name, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("index", index);
        param.put("name", name);
        getResultBoolean(Constant.ACCESSORY_MANAGER_C10, handle, param);
    }


    /**
     * 获取播放列表
     */
    public void listSpeakerPlay(UserHandle<BaseResult3> handle) {
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
        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, Constant.ACCESSORY_MANAGER_C15, null, ph);
    }


    /**
     * 删除机巢音频
     *
     * @param handle
     */
    public void speakerDeleteAudio(List<Integer> indexList, UserHandle<Boolean> handle) {
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("indexList", indexList);
        getResultBoolean(Constant.ACCESSORY_MANAGER_C12, handle, param);
    }


    /**
     * 播放器停止录音
     */
    public void speakerStopRecord(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.ACCESSORY_MANAGER_C14, handle, null);
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


    /**
     * 上传音频
     */
    public void uploadAudio(String saveFileName, String url, Boolean isMp3,  UserHandle<BaseResult3> handle) {
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
     * @param handle
     */
    public void stopUploadAudio(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.ACCESSORY_MANAGER_C19, handle, null);
    }

    /**
     * 喊话器状态重置
     * @param handle
     */
    public void resetSpeaker(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.ACCESSORY_MANAGER_C20, handle, null);
    }

    /**
     * 喊话器开始录音
     * @param handle
     */
    public void speakerStartRecord(Boolean isPersistent, Boolean isMp3, UserHandle<Boolean> handle) {
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("isPersistent", isPersistent);
        param.put("isMp3", isMp3);
        getResultBoolean(Constant.ACCESSORY_MANAGER_C16, handle, param);
    }

    /**
     * 喊话器结束录音
     * @param handle
     */
    public void speakerEndRecord(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.ACCESSORY_MANAGER_C17, handle, null);
    }

    /**
     * 喊话器传输音频字节
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

}
