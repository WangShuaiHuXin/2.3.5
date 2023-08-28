package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIMediaStreamOutDTO.java
 * @Description DJIMediaStreamOutDTO
 * @createTime 2022年09月16日 11:23:00
 */
@Data
@Accessors(chain = true)
public class DJIMediaStreamOutDTO {

    /**
     * 巢内/机身推流地址
     */
    private String innerStreamPushUrl;

    /**
     * 巢外/无人机摄像头推流地址
     */
    private String outerStreamPushUrl;

}
