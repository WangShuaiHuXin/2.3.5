package com.imapcloud.nest.v2.common.config;

import com.geoai.common.core.exception.BizException;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用下载
 *
 * @author boluo
 * @date 2023-05-08
 */
@Component
public class DownloadConfig implements ApplicationListener<ApplicationStartedEvent> {

    @Resource
    private ApplicationContext applicationContext;

    private final Map<String, DownloadHandler> handlerMap = new ConcurrentHashMap<>();

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

        Map<String, DownloadHandler> downloadHandlerMap = applicationContext.getBeansOfType(DownloadHandler.class);

        for (DownloadHandler handler : downloadHandlerMap.values()) {
            DownloadAnnotation annotation = handler.getClass().getAnnotation(DownloadAnnotation.class);
            if (annotation != null) {
                String key = annotation.key();
                if (handlerMap.containsKey(key)) {
                    throw new BizException("DownloadAnnotation key already exists");
                }
                handlerMap.put(key, handler);
            }
        }
    }

    public DownloadHandler getDownloadHandler(String key) {
        DownloadHandler downloadHandler = handlerMap.get(key);
        if (downloadHandler == null) {
            throw new BizException("DownloadAnnotation key not exists");
        }
        return downloadHandler;
    }
}
