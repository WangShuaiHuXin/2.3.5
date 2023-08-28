package com.imapcloud.nest.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;

/**
 * @author wmin
 */
@Configuration
@Slf4j
@EnableAsync
public class HttpRequestConfig {
    /**
     * 5秒超时时间
     */
    private int outtime = 3000;

    @Bean("restTemplate")
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) {
                log.info("some error!");
            }
        });
        return restTemplate;
    }

    @Bean("factory")
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        //客户端与服务端建立连接超时时间
        factory.setConnectTimeout(1000);
        //客户端从服务端读取数据的超时时间
        factory.setReadTimeout(6000);
        return factory;
    }

    @Bean
    public WebClient webClient() {
        //配置固定大小连接池
        ConnectionProvider provider = ConnectionProvider
                .builder("webClientPool")
                // 没有连接可用时，请求等待的最长时间
                .pendingAcquireTimeout(Duration.ofSeconds(1))
                // 最大连接数
                .maxConnections(10)
                // 连接最大闲置时间
                .maxIdleTime(Duration.ofSeconds(30))
                // 等待队列大小
                .pendingAcquireMaxCount(5)
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .doOnError((req, err) -> {
                    log.error("err on request:{}", req.uri(), err);
                }, (res, err) -> {
                    log.error("err on response:{}", res.uri(), err);
                });
        // 使用Reactor
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }


    @Bean
    public RestTemplate restTemplateHttps() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();
        SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(scsf)
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        requestFactory.setReadTimeout(outtime);
        requestFactory.setConnectTimeout(outtime);
        requestFactory.setConnectionRequestTimeout(outtime);
        return new RestTemplate(requestFactory);
    }
}
