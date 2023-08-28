package com.imapcloud.nest;

import com.google.common.base.Stopwatch;
import com.imapcloud.nest.common.netty.ws.WsServer;
import com.imapcloud.nest.service.UdpService;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.properties.WebsocketConfig;
import com.imapcloud.nest.v2.manager.rest.DingTalkManager;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author wmin
 */
@Slf4j
@MapperScans({
        @MapperScan("com.imapcloud.nest.v2.dao"),
        @MapperScan("com.imapcloud.nest.mapper")
})
@EnableFeignClients
@EnableDiscoveryClient
@Configuration
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
@SpringBootApplication
@EnableRetry
public class  NestCommonApplication implements CommandLineRunner {

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private WsServer wsServer;

    @Resource
    private UdpService udpService;

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            SpringApplication.run(NestCommonApplication.class, args);
            try {
                DingTalkManager.sendStart("geoai-uos-service", stopwatch.elapsed(TimeUnit.SECONDS), true);
            } catch (Exception e) {
                log.warn("send ding talk info error");
            }
        } catch (Exception e) {
            try {
                DingTalkManager.sendStart("geoai-uos-service", stopwatch.elapsed(TimeUnit.SECONDS), false);
            } catch (Exception ex) {
                log.warn("send ding talk info error");
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        WebsocketConfig websocket = geoaiUosProperties.getWebsocket();
        wsServer.start(new InetSocketAddress(websocket.getPort()));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> wsServer.destory()));
        Executors.newFixedThreadPool(1, r -> new Thread(r, "udp-monitor-thread"))
                .execute(() -> udpService.reserveMsg());
    }
}
