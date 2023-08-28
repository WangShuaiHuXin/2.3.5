package com.imapcloud.nest.v2.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * UOS配置信息
 * @author Vastfy
 * @date 2022/6/30 17:11
 * @since 1.9.5
 */
@Data
@Component
@ConfigurationProperties(prefix = "geoai.uos")
public class GeoaiUosProperties {

    /**
     * mqtt外部域
     */
    private boolean mqttOuterDomain;

    /**
     * UOS系统版本
     */
    private String version;

    /**
     * 磁盘使用告警阈值(单位：G)
     */
    private Integer diskUsageThreshold = 100;

    /**
     * 系统日志路径
     */
    private String logPath;

    /**
     * 区分系统部署内外网环境，内网：1
     */
    private Integer insideStatus;

    /**
     * websocket配置信息
     */
    @NestedConfigurationProperty
    private WebsocketConfig websocket = new WebsocketConfig();

    /**
     * 天气服务配置信息
     */
    @NestedConfigurationProperty
    private WeatherConfig weather = new WeatherConfig();

    /**
     * minio配置信息
     */
    @NestedConfigurationProperty
    private MinioConfig minio = new MinioConfig();

    /**
     * 流媒体配置信息
     * @deprecated 2.3.2，已废弃NMS，后续版本将移除
     */
    @Deprecated
    @NestedConfigurationProperty
    private IptvConfig iptv = new IptvConfig();

    /**
     * 上传配置信息
     */
    @NestedConfigurationProperty
    private UploadConfig upload = new UploadConfig();

    /**
     * 存储配置信息
     */
    @NestedConfigurationProperty
    private StoreConfig store = new StoreConfig();

    /**
     * 域名配置信息
     */
    @NestedConfigurationProperty
    private DomainConfig domain = new DomainConfig();

    /**
     * 气体配置信息
     */
    @NestedConfigurationProperty
    private GasConfig gas = new GasConfig();

    /**
     * mongo 配置信息
     */
    @NestedConfigurationProperty
    private MongoConfig mongo = new MongoConfig();

    /**
     * 基站日志 配置信息
     */
    @NestedConfigurationProperty
    private NestLogConfig nestLog = new NestLogConfig();

    /**
     * CPS日志 配置信息
     * @deprecated 2.2.3，将在后续版本删除
     */
    @Deprecated
    @NestedConfigurationProperty
    private CpsLogConfig cpsLog = new CpsLogConfig();

    /**
     * 任务队列 配置信息
     */
    @NestedConfigurationProperty
    private TaskQueueConfig taskQueue = new TaskQueueConfig();

    /**
     * AI识别 配置信息
     */
    @NestedConfigurationProperty
    private AIConfig ai = new AIConfig();

    /**
     * MQTT 配置信息
     */
    @NestedConfigurationProperty
    private MqttConfig mqtt = new MqttConfig();

    /**
     * IOT 配置信息
     */
    @NestedConfigurationProperty
    private IOTConfig iot = new IOTConfig();

    /**
     * 分析应用 配置信息
     */
    @NestedConfigurationProperty
    private AnalysisConfig analysis = new AnalysisConfig();

    /**
     * 分析应用 配置信息
     */
    @NestedConfigurationProperty
    private DingTalkConfig dingTalk = new DingTalkConfig();

    /**
     * 大疆基站 配置信息
     */
    @NestedConfigurationProperty
    private DjiConfig dji = new DjiConfig();

    /**
     * 消息 配置信息
     */
    @NestedConfigurationProperty
    private ImConfig im = new ImConfig();

    /**
     * 数据同步 配置信息
     */
    @NestedConfigurationProperty
    private DataSynConfig dataSyn = new DataSynConfig();

    /**
     * pilot 配置信息
     */
    @NestedConfigurationProperty
    private DjiPilotConfig djiPilot = new DjiPilotConfig();


    private DataSynConfig dataSynConfig = new DataSynConfig();

    /**
     * 对象存储 配置信息
     * @since 2.2.3
     */
    @NestedConfigurationProperty
    private OssConfig oss = new OssConfig();

    /**
     * 字体 配置信息
     * @since 2.2.3
     */
    @NestedConfigurationProperty
    private FontConfig font = new FontConfig();

    /**
     * 民航 配置信息
     * @since 2.2.5
     */
    @NestedConfigurationProperty
    private CaacConfig caac = new CaacConfig();

    /**
     * 内网穿透 配置信息
     * @since 2.3.2
     */
    @NestedConfigurationProperty
    private FrpcConfig frpc = new FrpcConfig();


    @NestedConfigurationProperty
    private RocketmqConfig rocketmq = new RocketmqConfig();

}
