package com.imapcloud.sdk.mqttclient;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MqttClientFactory {
    private static final Map<String, IMqttAsyncClient> CLIENT_MAP = new ConcurrentHashMap<>();

    public static IMqttAsyncClient getInstance(String mqttServerUri) throws MqttException {
        IMqttAsyncClient client = CLIENT_MAP.get(mqttServerUri);
        if (client == null) {
            client = new MqttAsyncClient(mqttServerUri, "nest-web-" + System.currentTimeMillis(), new MemoryPersistence());
            CLIENT_MAP.put(mqttServerUri, client);
        }
        return client;
    }
}
