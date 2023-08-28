package com.imapcloud.nest.enums;

import com.geoai.common.core.enums.IEnum;
import lombok.AllArgsConstructor;

/**
 * minio对象类型枚举
 *
 * @author boluo
 * @date 2022-10-27
 */
@AllArgsConstructor
public enum MinioObjectTypeEnum implements IEnum<String> {

    /**
     * minio文件类型
     */
    PHOTO("uos-photo", "图片"),
    VIDEO("uos-video", "视频"),
    VIDEO_PICTURE("uos-video-picture", "视频抽帧"),
    ;

    /**
     * 代码
     */
    private String code;

    /**
     * 消息
     */
    private String message;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
