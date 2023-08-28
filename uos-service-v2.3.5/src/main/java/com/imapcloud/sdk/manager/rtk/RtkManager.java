package com.imapcloud.sdk.manager.rtk;

import com.imapcloud.nest.model.NestRtkEntity;
import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.constant.RtkConnTypeEnum;
import com.imapcloud.sdk.pojo.entity.RtkParam;


import java.util.HashMap;
import java.util.Map;

@Deprecated
public class RtkManager {
    private final static String FUNCTION_TOPIC = Constant.RTK_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public RtkManager(Client client) {
        this.client = client;
    }

    /**
     * 打开RTK
     *
     * @param handle
     */
    public void openRtk(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_RTK_OPEN, handle, null);
    }

    /**
     * 关闭RTK
     *
     * @param handle
     */
    public void closeRtk(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_RTK_CLOSE, handle, null);
    }

    /**
     * 检查RTK是否可用
     */
    public void isRtkEnable(UserHandle<Boolean> handle) {

        ProxyHandle<Boolean> ph = new ProxyHandle<Boolean>() {
            @Override
            public void success(Boolean isEnable, String msg) {
                handle.handle(isEnable, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, Constant.EACC_RTK_ENABLE, null, ph, "isEnable",Boolean.class);
    }

    /**
     * 重启RTK
     *
     * @param handle
     */
    public void restartRtk(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_RTK_RECONNECT, handle, null);
    }

    /**
     * 获取RTK连接类型
     *
     * @param handle
     */
    public void getRtkConnectType(UserHandle<Integer> handle) {
        ProxyHandle<Integer> ph = new ProxyHandle<Integer>() {
            @Override
            public void success(Integer rtkType, String msg) {
                handle.handle(rtkType, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, Constant.EACC_RTK_GET_TYPE, null, ph, "rtkType",Integer.class);
    }

    /**
     * 设置RTK连接类型
     *
     * @param connectType
     * @param handle
     */
    public void setRTKConnectType(Integer connectType, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("rtkType", connectType);
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
        ClientProxy.proxyPublishBool(this.client, FUNCTION_TOPIC, Constant.EACC_RTK_SET_TYPE, param, ph);
    }

    /**
     * 获取RTK账号参数
     *
     * @param handle
     */
    public void getRtkAccountParam(UserHandle<NestRtkEntity> handle) {
        ProxyHandle<NestRtkEntity> ph = new ProxyHandle<NestRtkEntity>() {
            @Override
            public void success(NestRtkEntity rtkParam, String msg) {
                handle.handle(rtkParam, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublish(this.client, FUNCTION_TOPIC, Constant.EACC_RTK_GET_ACCOUNT, null, ph, NestRtkEntity.class);
    }

    /**
     * 设置 RTK账号参数
     * @param rtkParam
     * @param handle
     */
    public void setRtkAccountParam(NestRtkEntity rtkParam, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(8);
        param.put("ip", rtkParam.getIp());
        param.put("port", rtkParam.getPort());
        param.put("mountPoint", rtkParam.getMountPoint());
        param.put("userName", rtkParam.getUserName());
        param.put("password", rtkParam.getPassword());

        getResultBoolean(Constant.EACC_RTK_SET_ACCOUNT, handle, param);
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
