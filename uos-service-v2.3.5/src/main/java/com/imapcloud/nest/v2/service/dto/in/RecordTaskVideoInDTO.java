package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

/**
 * 视频录像文件信息
 *
 * @author Vastfy
 * @date 2023/4/25 18:45
 * @since 2.3.2
 */
@Data
public class RecordTaskVideoInDTO {

    /**
     * 录像任务ID
     */
    private String recordTaskId;
    /**
     * 录像视频索引
     */
    private Integer index;
    /**
     * 录像视频名称
     */
    private String videoName;
    /**
     * 录像视频大小，单位：字节
     */
    private Long videoSize;
    /**
     * 录像视频存储地址
     */
    private String videoUri;

}
