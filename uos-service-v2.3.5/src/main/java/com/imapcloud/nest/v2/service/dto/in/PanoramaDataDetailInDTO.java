package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 全景数据明细信息
 * @author Vastfy
 * @date 2023/02/21 09:52
 * @since 2.2.3
 */
@Data
public class PanoramaDataDetailInDTO implements Serializable {

    /**
     * 全景点主键
     */
    private String pointId;

    /**
     * 任务架次记录id
     */
    private String missionRecordsId;

    /**
     * 照片采集时间
     */
    private LocalDateTime acquisitionTime;

    /**
     * 明细存储路径
     */
    private String detailPath;

}
