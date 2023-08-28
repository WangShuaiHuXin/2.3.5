package com.imapcloud.nest.utils;

import com.imapcloud.nest.utils.NodeMediaUtil;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.utils.nms.NmsRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * 请求其他接口
 *
 * @author: daolin
 * @create: 2020/10/26
 **/
@Slf4j
@Component
@EnableAsync
public class HttpRequestUtil {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private WebClient webClient;

    /**
     * 1）url: 请求地址；
     * 2）method: 请求类型(如：POST,PUT,DELETE,GET)；
     * 3）requestEntity: 请求实体，封装请求头，请求内容
     * 4）responseType: 响应类型，根据服务接口的返回类型决定
     * 5）uriVariables: url中参数变量值
     */
    public static <T> ResponseEntity sendRequest(String url, HttpMethod method, T requestEntity, Class responseType, Object[] uriVariables, String w3Account, String token) {
        RestTemplate restTemplate = new RestTemplate();
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
        // 设置请求头：ContentType、Token、Cookie、User-Agent
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json");
        headers.setContentType(type);
//        headers.setContentLength();
        //这是请求头里的一些参数配置，结合实际情况填写。
        if (ToolUtil.isNotEmpty(token)) {
            headers.set("Authorization", token);
        }
        HttpEntity<T> entity = new HttpEntity<T>(requestEntity, headers);
        // 响应默认返回类型为String
        responseType = (responseType == null ? String.class : responseType);
        ResponseEntity response = null;
        try {
            if (null == uriVariables) {
                response = restTemplate.exchange(url, method, entity, responseType);
            } else {
                response = restTemplate.exchange(url, method, entity, responseType, uriVariables);
            }
        } catch (RestClientException e) {
            log.error(e.getMessage());
        }
        return response;
    }


    public <T> ResponseEntity sendRequest2(String url, HttpMethod method, T requestEntity, Class resType, Object[] uriVariables, String token) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json");
        headers.setContentType(type);
        //这是请求头里的一些参数配置，结合实际情况填写。
        if (ToolUtil.isNotEmpty(token)) {
            headers.set("Authorization", token);
        }
        HttpEntity<T> entity = new HttpEntity<>(requestEntity, headers);
        // 响应默认返回类型为String
        resType = resType == null ? String.class : resType;
        ResponseEntity response;
        try {
            if (null == uriVariables) {
                response = restTemplate.exchange(url, method, entity, resType);
            } else {
                response = restTemplate.exchange(url, method, entity, resType, uriVariables);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            NmsRes res = new NmsRes();
            res.setCode(503);
            res.setError("请求错误");
            response = ResponseEntity.ok(res);
            return response;
        }
        return response;
    }

    @Async("asyncMedia")
    public void sendRequestAsync(String url, HttpMethod method, Map requestEntity, String token) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json");
        headers.setContentType(type);
        //这是请求头里的一些参数配置，结合实际情况填写。
        if (ToolUtil.isNotEmpty(token)) {
            headers.set("Authorization", token);
        }
        HttpEntity<Map> entity = new HttpEntity<>(requestEntity, headers);
        log.info("异步发送http请求");
        // 响应默认返回类型为String
        ResponseEntity response = restTemplate.exchange(url, method, entity, String.class);
        log.info("异步请求结果：{}", response.getBody().toString());

    }

    /**
     * 发送https请求，需要用这个类工厂方法
     *
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static RestTemplate getHttpsRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext,
                new String[]{"TLSv1"},
                null,
                NoopHostnameVerifier.INSTANCE);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }

    /**
     * 响应式请求
     *
     * @param url
     * @param token
     * @param reqParam
     * @param method
     */
    @Async("asyncMedia")
    public void sendRequestFlux(String url, String token, NodeMediaUtil.CapParam reqParam, HttpMethod method) {
        long start = System.currentTimeMillis();
        log.info("异步响应式请求开始执行");
        Mono<String> mono = webClient.method(method)
                .uri(url)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(s))
                .body(Mono.just(reqParam), NodeMediaUtil.CapParam.class)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("请求错误")
                .timeout(Duration.of(5, ChronoUnit.SECONDS));
        mono.subscribe(result -> {
            long end = System.currentTimeMillis();
            log.info("异步响应式请求时长：{},结果：{}", (end - start) / 1000.0, result);
        });
    }
}
