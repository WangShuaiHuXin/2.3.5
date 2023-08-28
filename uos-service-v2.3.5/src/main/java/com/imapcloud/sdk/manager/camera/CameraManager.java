package com.imapcloud.sdk.manager.camera;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.camera.enums.H20SeriesLensModeEnum;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wmin
 * 飞机相机管理器
 */

@Deprecated
public class CameraManager {
    private final static String FUNCTION_TOPIC = Constant.AIRCRAFT_CAMERA_FUNCTION_TOPIC;
    private Client client;

    public CameraManager(Client client) {
        this.client = client;
    }

    /**
     * 获取相机名字
     *
     * @param handle
     */
    public void getAircraftCameraName(UserHandle<String> handle) {
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
        getResultStr(Constant.AIRCRAFT_CAMERA_MANAGER_C0, null, ph, "cameraName");
    }

    /**
     * 获取相机状态
     *
     * @param handle
     */
    public void getAircraftCameraMode(UserHandle<CameraModeEnum> handle) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String s, String msg) {
                handle.handle(CameraModeEnum.getInstance(s), true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }

            }
        };
        getResultStr(Constant.AIRCRAFT_CAMERA_MANAGER_C1, null, ph, "cameraMode");
    }

    /**
     * 设置相机曝光模式
     *
     * @param exposureMode
     * @param handle
     */
    public void setAircraftExposureMode(CameraExposureEnum exposureMode, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("exposureMode", exposureMode.getValue());
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C2, handle, param);
    }

    /**
     * 获取相机曝光模式
     *
     * @param handle
     */
    public void getAircraftExposureMode(UserHandle<CameraExposureEnum> handle) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String s, String msg) {
                handle.handle(CameraExposureEnum.valueOf(s), true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        getResultStr(Constant.AIRCRAFT_CAMERA_MANAGER_C3, null, ph, "exposureMode");
    }

    /**
     * 设置相机ISO
     *
     * @param iso
     * @param handle
     */
    public void setCameraIso(CameraISOEnum iso, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("iso", iso.getValue());
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C4, handle, param);
    }

    /**
     * 获取相机ISO
     *
     * @param handle
     */
    public void getCameraIso(UserHandle<CameraISOEnum> handle) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String s, String msg) {
                handle.handle(CameraISOEnum.getInstance(s), true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        getResultStr(Constant.AIRCRAFT_CAMERA_MANAGER_C5, null, ph, "iso");
    }

    /**
     * 设置相机快门速度
     *
     * @param shutterSpeed
     * @param handle
     */
    public void setCameraShutterSpeed(CameraShutterSpeedEnum shutterSpeed, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("shutterSpeed", shutterSpeed.getValue());
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C6, handle, param);
    }

    /**
     * 获取相机快门速度
     *
     * @param handle
     */
    public void getCameraShutterSpeed(UserHandle<CameraShutterSpeedEnum> handle) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String s, String msg) {
                handle.handle(CameraShutterSpeedEnum.getInstance(s), true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        getResultStr(Constant.AIRCRAFT_CAMERA_MANAGER_C7, null, ph, "shutterSpeed");
    }

    /**
     * 设置相机测光模式
     *
     * @param meteringMode
     * @param handle
     */
    public void setCameraMeterMode(CameraMeterModeEnum meteringMode, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("meteringMode", meteringMode.getValue());
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C8, handle, param);
    }

    /**
     * 获取相机测光模式
     *
     * @param handle
     */
    public void getCameraMeterMode(UserHandle<CameraMeterModeEnum> handle) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String s, String msg) {
                handle.handle(CameraMeterModeEnum.getInstance(s), true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        getResultStr(Constant.AIRCRAFT_CAMERA_MANAGER_C9, null, ph, "meteringMode");
    }

    /**
     * 设置相机曝光补偿
     *
     * @param exposureCompensation
     * @param handle
     */
    public void setCameraExposureCompensation(CameraExposureCompensationEnum exposureCompensation, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("exposureCompensation", exposureCompensation.getValue());
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C10, handle, param);
    }

    /**
     * 获取相机曝光补偿
     *
     * @param handle
     */
    public void getCameraExposureCompensation(UserHandle<CameraExposureCompensationEnum> handle) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String s, String msg) {
                handle.handle(CameraExposureCompensationEnum.getInstance(s), true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        getResultStr(Constant.AIRCRAFT_CAMERA_MANAGER_C11, null, ph, "exposureCompensation");
    }

    /**
     * 设置锁定自动曝光
     *
     * @param aeLock
     * @param handle
     */
    public void setCameraAELock(boolean aeLock, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("AELock", aeLock);
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C12, handle, param);
    }

    /**
     * 获取锁定自动曝光
     *
     * @param handle
     */
    public void getCameraAELock(UserHandle<Boolean> handle) {
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
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, Constant.AIRCRAFT_CAMERA_MANAGER_C13, null, ph, "AELock",Boolean.class);
    }

    /**
     * 设置相机照片AEB数量
     *
     * @param photoAEBCount
     * @param handle
     */
    public void setCameraPhotoAEBCount(CameraAEBCountEnum photoAEBCount, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("photoAEBCount", photoAEBCount.getValue());
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C14, handle, param);
    }

    /**
     * 获取相机照片AEB数量
     *
     * @param handle
     */
    public void getCameraPhotoAEBCount(UserHandle<CameraAEBCountEnum> handle) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String s, String msg) {
                handle.handle(CameraAEBCountEnum.getInstance(s), true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };

        getResultStr(Constant.AIRCRAFT_CAMERA_MANAGER_C15, null, ph, "photoAEBCount");
    }

    /**
     * 设置相机白平衡
     *
     * @param whiteBalancePreset
     * @param handle
     */
    public void setCameraWhiteBalance(CameraWhiteBalanceEnum whiteBalancePreset, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("whiteBalancePreset", whiteBalancePreset.getValue());
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C16, handle, param);
    }

    /**
     * 获取相机白平衡
     *
     * @param handle
     */
    public void getCameraWhiteBalance(UserHandle<CameraWhiteBalanceEnum> handle) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String s, String msg) {
                handle.handle(CameraWhiteBalanceEnum.getInstance(s), true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        getResultStr(Constant.AIRCRAFT_CAMERA_MANAGER_C17, null, ph, "whiteBalancePreset");
    }

    /**
     * 相机初始化
     *
     * @param handle
     */
    public void cameraRestoreFactorySet(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C18, handle, null);
    }

    /**
     * 设置相机为FPV模式
     * 支持mini跟m300
     */
    public void setCameraFpvMode(CameraFpvModeEnum fpvMode, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("mode", fpvMode.name());
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C19, handle, param);
    }

    /**
     * 开始拍照
     */
    public void startPhotograph(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C20, handle, null);
    }

    /**
     * 开始录制视频
     *
     * @param handle
     */
    public void startRecord(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C21, handle, null);
    }

    /**
     * 停止录制视频
     *
     * @param handle
     */
    public void stopRecord(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C22, handle, null);
    }

    /**
     * 相机聚焦中心
     */
    public void cameraFocusCenter(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C23, handle, null);
    }

    /**
     * 设置相机拍照模式
     */
    public void setCameraPhotoMode(PhotoModeEnum photoMode, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("shootPhotoMode", photoMode.name());
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C24, handle, param);
    }

    /**
     * 设置相机MSX等级
     *
     * @param level
     * @param handle
     */
    public void setCameraMsxLevel(Integer level, UserHandle<Boolean> handle) {
        //等级只支持1-100
        if (level < 1 || level > 100) {
            return;
        }
        Map<String, Object> param = new HashMap<>(2);
        param.put("msxLevel", level);
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C31, handle, param);
    }

    /**
     * 设置视频拍摄参数，仅支持御2航线双光版
     *
     * @param handle
     */
    public void selectVideoShotParam(UserHandle<Map<String, String>> handle) {
        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
            @Override
            public void success(BaseResult3 baseResult3, String msg) {
                String param = baseResult3.getParam();
                JSONObject jsonObject = JSONObject.parseObject(param);

                Map<String, String> map = new HashMap<>(2);
                map.put("resolution", jsonObject.getString("resolution"));
                map.put("frameRate", jsonObject.getString("frameRate"));
                handle.handle(map, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, Constant.AIRCRAFT_CAMERA_MANAGER_C32, null, ph);
    }

    /**
     * 设置视频拍摄参数
     *
     * @param resolution
     * @param frameRate
     * @param handle
     */
    public void setVideoShotParam(String resolution, String frameRate, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("resolution", resolution);
        param.put("frameRate", frameRate);
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C33, handle, param);
    }

    /**
     * 设置云台偏航角度
     *
     * @param angle -90° ----> 0°
     */
    public void setGimbalYawAngle(int angle, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("angle", angle);
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C6, handle, param);
    }


    /**
     * 切换推流视频，4:3或者16:9
     *
     * @param photoRatio43
     * @param handle
     */
    public void switchPhotoRatio(Boolean photoRatio43, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("photoRatio4", photoRatio43);
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C30, handle, param);
    }

    /**
     * 拍照变焦
     *
     * @param ratio  变焦倍数
     * @param handle
     */
    public void setPhotoZoomRatio(float ratio, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("ratio", ratio);
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C35, handle, param);
    }

    /**
     * 获取相机变焦倍数
     *
     * @param handle
     */
    public void getPhotoZoomRatio(UserHandle<Double> handle) {
        ProxyHandle<Double> ph = new ProxyHandle<Double>() {
            @Override
            public void success(Double ratio, String msg) {
                handle.handle(ratio, true, msg);
            }
            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client,FUNCTION_TOPIC,Constant.AIRCRAFT_CAMERA_MANAGER_C36,null,ph,"ratio",Double.class);
    }

    /**
     * 重置变焦
     *
     * @param handle
     */
    public void resetPhotoZoomRatio(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C37, handle, null);
    }

    /**
     * 云台控制：控制云台的俯仰、朝向
     *
     * @param pitchAngle
     * @param yawAngle
     * @param handle
     */
    public void setCameraAngle(Float pitchAngle, Float yawAngle, UserHandle<Boolean> handle) {
        if (pitchAngle == null && yawAngle == null) {
            return;
        }
        Map<String, Object> param = new HashMap<>(2);
        if (pitchAngle != null) {
            param.put("pitchAngle", pitchAngle);
        }
        if (yawAngle != null) {
            param.put("yawAngle", yawAngle);
        }
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C38, handle, param);
    }


    /**
     * 云台回中并朝下
     *
     * @param handle
     */
    public void resetCameraAngle(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C40, handle, null);
    }

    /**
     * 设置相机拍照/录像模式，设置多种模式时，拍照/录像会使用多种模式（不同镜头）进行拍摄，仅支持{G900}挂载禅思H20系列相机
     *
     * @param eList
     * @param handle
     */
    public void h20selectPhotoVideoSource(List<H20SeriesLensModeEnum> eList, UserHandle<Boolean> handle) {
        List<Integer> values = H20SeriesLensModeEnum.listValues(eList);
        if (CollectionUtil.isEmpty(values)) {
            return;
        }
        Map<String, Object> param = new HashMap<>(2);
        param.put("formats", values);
        getResultBoolean(Constant.AIRCRAFT_CAMERA_MANAGER_C41, handle, param);
    }


    private void getResultStr(String code, Map<String, Object> param, ProxyHandle<String> ph, String key) {
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
