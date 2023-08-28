package com.imapcloud.sdk.mqttclient;


import lombok.ToString;

@ToString
public class MqttOptions {

    private String username;
    private String password;
    private String serverUri;
    private String clientId;
    private int keepAliveInterval = 30;
    private int connectionTimeout = 30;
    private int nestType;

    public static MqttOptions instance() {
        return new MqttOptions();
    }

    public MqttOptions username(String username) {
        this.username = username;
        return this;
    }

    public MqttOptions password(String password) {
        this.password = password;
        return this;
    }

    public MqttOptions serverUri(String serverUri) {
        this.serverUri = serverUri;
        return this;
    }

    public MqttOptions clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public MqttOptions keepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
        return this;
    }

    public MqttOptions connectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public MqttOptions nestType(int nestType) {
        this.nestType = nestType;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getServerUri() {
        return serverUri;
    }

    public String getClientId() {
        return clientId;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getNestType() {
        return nestType;
    }
}
