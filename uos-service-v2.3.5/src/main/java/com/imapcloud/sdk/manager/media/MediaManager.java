package com.imapcloud.sdk.manager.media;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.entity.CpsSdCareRemainSpace;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class MediaManager {
    private final static String FUNCTION_TOPIC = Constant.MEDIA_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public MediaManager(Client client) {
        this.client = client;
    }

    /**
     * SD卡是否插入
     *
     * @param handle
     */
    public void isSdCardInserted(UserHandle<Boolean> handle) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String inserted, String msg) {
                handle.handle("1".equals(inserted), true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, Constant.MEDIA_MANAGER_C1, null, ph, "inserted",String.class);
    }

    /**
     * SD 卡是否有错误
     *
     * @param handle
     */
    public void isSdCardHasError(UserHandle<Boolean> handle) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String inserted, String msg) {
                handle.handle("1".equals(inserted), true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, Constant.MEDIA_MANAGER_C2, null, ph, "hasError",String.class);
    }

    /**
     * 获取SD卡的总容量
     *
     * @param handle
     */
    public void getSdCardTotalSpace(UserHandle<String> handle) {
        getResultStr(Constant.MEDIA_MANAGER_C3, handle, null, "totalSpaceInMB");
    }

    /**
     * 获取SD剩余容量
     *
     * @param handle
     */
    public void getSdCardRemainSpace(UserHandle<String> handle) {
        getResultStr(Constant.MEDIA_MANAGER_C4, handle, null, "remainingSpaceInMB");
    }
    /**
     * 获取SD剩余容量(可离线查询sd剩余空间)
     *
     * @param handle
     */
    public void getCpsSdCardRemainSpace(UserHandle<CpsSdCareRemainSpace> handle) {
        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
            @Override
            public void success(BaseResult3 baseResult3, String msg) {
                String param = baseResult3.getParam();
                JSONObject jsonObject = JSON.parseObject(param);
                CpsSdCareRemainSpace cpsSdCareRemainSpace = new CpsSdCareRemainSpace();
                if (jsonObject != null) {
                    cpsSdCareRemainSpace.setRemainingSpaceInMB(jsonObject.getLong("remainingSpaceInMB"));
                    cpsSdCareRemainSpace.setCache(jsonObject.getBoolean("cache"));
                }
                handle.handle(cpsSdCareRemainSpace, true, msg);
            }
            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, Constant.MEDIA_MANAGER_C4, null, ph);
    }

    /**
     * 获取SD卡容量可以存的照片数
     *
     * @param handle
     */
    public void getSdCardAvailableCaptureCount(UserHandle<String> handle) {
        getResultStr(Constant.MEDIA_MANAGER_C5, handle, null, "availableCaptureCount");
    }

    /**
     * 获取SD卡容量可以录制视频的长度(单位是秒)
     *
     * @param handle
     */
    public void getSdCardAvailableRecordTimes(UserHandle<String> handle) {
        getResultStr(Constant.MEDIA_MANAGER_C6, handle, null, "availableRecordingTimeInSeconds");
    }

    /**
     * 格式化SD卡
     *
     * @param handle
     */
    public void formatSdCard(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.MEDIA_MANAGER_C7, handle, null);
    }



    /**
     * 获取照片数量
     *
     * @param missionId
     * @return
     */
    @Deprecated
    public void getPatrolPhotoCount(String missionId, UserHandle<Integer> handle) {
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("missionID", missionId);
        getResultInt(Constant.MEDIA_MANAGER_C10, handle, param, "count");
    }


    /**
     * 获取当前指定任务指定的某一张 缩略图
     * 返回的是图片的base64编码
     *
     * @param missionId
     * @param index
     */
    @Deprecated
    public void getThumbPhoto(String missionId, int index, UserHandle<String> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("missionID", missionId);
        param.put("index", index);
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String s, String msg) {
                handle.handle(s, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishBase64(this.client, FUNCTION_TOPIC, Constant.MEDIA_MANAGER_C11, ph, param);
    }


    private void getResultInt(String code, UserHandle<Integer> handle, Map<String, Object> param, String key) {
        ProxyHandle<Integer> ph = new ProxyHandle<Integer>() {
            @Override
            public void success(Integer res, String msg) {
                handle.handle(res, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, code, param, ph, key,Integer.class);
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

    private void getResultStr(String code, UserHandle<String> handle, Map<String, Object> param, String key) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String res, String msg) {
                handle.handle(res, true, msg);
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

}

