package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;
import lombok.ToString;

/**
 * CPS常规参数设置
 *
 * @author boluo
 * @date 2022-08-27
 */
@ToString
public class GeneralManagerInDO {

    private GeneralManagerInDO() {}

    /**
     * 设置基站的巢内巢外监控信息
     *
     * @author boluo
     * @date 2022-08-27
     */
    @Data
    public static class PushStreamInfoInDO {

        private String nestId;

        /**
         * 摄像头ip地址
         */
        private String ip;
        /**
         * 登录用户名
         */
        private String userName;
        /**
         * 登录密码
         */
        private String password;
        /**
         * 推流功能开关
         */
        private Boolean enable;
        /**
         * 推流地址
         */
        private String rtmpUrl;
    }
}
