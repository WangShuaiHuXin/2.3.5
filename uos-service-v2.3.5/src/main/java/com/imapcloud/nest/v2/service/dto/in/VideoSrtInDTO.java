package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 视频飞行轨迹信息
 * @author Vastfy
 * @date 2023/02/21 09:52
 * @since 2.2.3
 */
@Data
public class VideoSrtInDTO implements Serializable {

    private Integer videoId;

    private InputStream inputStream;

    private String srtFilename;

}
