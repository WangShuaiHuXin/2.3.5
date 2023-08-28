package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * AI识别配置信息
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 */
@Data
public class AIConfig {

    /**
     * AI服务主机
     */
    private String host;

    /**
     * AI服务端口
     */
    private int port;

    /**
     * AI服务用户名
     */
    private String username;

    /**
     * AI服务密码
     */
    private String password;

    /**
     * AI服务源文件路径
     */
    private String originPath;

    /**
     * AI服务已识别处理过的红外测温(Infrared Temperature Measurement, ITM)文件路径
     */
    private String itmFilePath;

    /**
     * AI服务已识别过的表计读数(Meter reading, mr)文件路径
     */
    private String mrFilePath;

    /**
     * AI服务已识别过的其他文件路径
     */
    private String otherFilePath;

    /**
     * 不知道干啥的...
     */
    private String otherFilePathNginx;

    /**
     * SSH URL
     */
    private String sshUrl;

    /**
     * AI服务已识别处理过的红外测温(Infrared Temperature Measurement, ITM)文件nginx路径
     */
    private String itmNginxUrl;

    /**
     * AI服务已识别过的表计读数(Meter reading, mr)文件nginx路径
     */
    private String mrNginxUrl;

    /**
     * 不知道是啥...，猜测是违建点（Illegal Construction Site，ics）文件nginx路径
     */
    private String icsNginxUrl;

    /**
     * 是否使用华为云主机
     */
    private boolean useHuawei;

}
