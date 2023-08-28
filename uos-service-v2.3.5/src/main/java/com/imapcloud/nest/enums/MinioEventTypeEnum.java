package com.imapcloud.nest.enums;

import com.geoai.common.core.enums.IEnum;
import lombok.AllArgsConstructor;

/**
 * minio事件类型枚举
 *
 * @author boluo
 * @date 2022-10-28
 */
@AllArgsConstructor
public enum MinioEventTypeEnum implements IEnum<Integer> {

    /**
     * minio事件类型
     */
    DELETE(1, "删除"),
    PUT_TAGGING(2, "设置tag"),
    ;

    /**
     * 代码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
