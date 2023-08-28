package com.imapcloud.nest.controller;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author wmin
 */
public abstract class BaseController {

    private static final String DATE_PATTERN_OF_OF_CN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER_OF_CN = DateTimeFormatter.ofPattern(DATE_PATTERN_OF_OF_CN);

    private static final String TIME_PATTERN_OF_OF_CN = "HH:mm:ss";
    private static final DateTimeFormatter TIME_FORMATTER_OF_CN = DateTimeFormatter.ofPattern(TIME_PATTERN_OF_OF_CN);

    private static final String DATE_TIME_PATTERN_OF_CN = DATE_PATTERN_OF_OF_CN + " " + TIME_PATTERN_OF_OF_CN;
    private static final DateTimeFormatter DATE_TIME_FORMATTER_OF_CN = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_OF_CN);

    /**
     * 自动转换日期类型的字段格式（适用于query类型参数）
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN_OF_CN);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (StringUtils.hasText(text)) {
                    this.setValue(LocalDate.parse(text, DATE_FORMATTER_OF_CN));
                }
            }
        });
        binder.registerCustomEditor(LocalTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (StringUtils.hasText(text)) {
                    this.setValue(LocalTime.parse(text, TIME_FORMATTER_OF_CN));
                }
            }
        });
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (StringUtils.hasText(text)) {
                    this.setValue(LocalDateTime.parse(text, DATE_TIME_FORMATTER_OF_CN));
                }
            }
        });
    }

}
