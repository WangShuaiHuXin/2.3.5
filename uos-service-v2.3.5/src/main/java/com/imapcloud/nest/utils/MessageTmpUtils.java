package com.imapcloud.nest.utils;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.List;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;

/**
 * 临时处理socket的国际化工具
 */
public class MessageTmpUtils {
    private static final String PATH_PARAMETER = "Accept-Language";
    private static final String VALIDATION_MESSAGES = "ValidationMessages";
    private static final Boolean DEFAULT_MESSAGE = true;
    private static final String PATH_PARAMETER_SPLIT = "-";
    private static volatile ResourceBundleMessageSource resourceBundleMessageSource;

    public MessageTmpUtils() {
    }

    public static String getMessage(String message) {
        Locale locale = LocaleContextHolder.getLocale();
        if (resourceBundleMessageSource == null) {
            Class var2 = ResourceBundleMessageSource.class;
            synchronized (ResourceBundleMessageSource.class) {
                if (resourceBundleMessageSource == null) {
                    resourceBundleMessageSource = new ResourceBundleMessageSource();
                }
            }
        }

        resourceBundleMessageSource.setBasename("ValidationMessages_en_US");
        resourceBundleMessageSource.setUseCodeAsDefaultMessage(DEFAULT_MESSAGE);
        return resourceBundleMessageSource.getMessage(message, (Object[]) null, locale);
    }

    public static String getMessage(String message, ServerHttpRequest request) {
        List<String> languages = request.getHeaders().get("Accept-Language");
        Locale locale = LocaleContextHolder.getLocale();
        if (!CollectionUtils.isEmpty(languages)) {
            String lang = (String) languages.get(0);
            String[] split = lang.split("-");
            locale = new Locale(split[0], split[1]);
        }

        if (resourceBundleMessageSource == null) {
            Class var8 = ResourceBundleMessageSource.class;
            synchronized (ResourceBundleMessageSource.class) {
                if (resourceBundleMessageSource == null) {
                    resourceBundleMessageSource = new ResourceBundleMessageSource();
                }
            }
        }

        resourceBundleMessageSource.setBasename("ValidationMessages_en_US");
        resourceBundleMessageSource.setUseCodeAsDefaultMessage(DEFAULT_MESSAGE);
        return resourceBundleMessageSource.getMessage(message, (Object[]) null, locale);
    }
}