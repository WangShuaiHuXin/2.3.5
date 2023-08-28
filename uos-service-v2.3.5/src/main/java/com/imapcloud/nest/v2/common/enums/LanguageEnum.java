package com.imapcloud.nest.v2.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 语言枚举
 *
 * @author boluo
 * @date 2023-02-17
 */
@Getter
@AllArgsConstructor
public enum LanguageEnum {

    /**
     * cn
     */
    CN("zh_CN", "中文"),
    US("en-US", "英文"),
    ;
    private String code;

    private String msg;
}
