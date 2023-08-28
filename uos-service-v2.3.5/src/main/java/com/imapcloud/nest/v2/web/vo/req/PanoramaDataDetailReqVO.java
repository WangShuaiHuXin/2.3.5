package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 全景数据明细信息
 * @author Vastfy
 * @date 2023/02/21 09:52
 * @since 2.2.3
 */
@ApiModel("全景数据明细信息")
@Data
public class PanoramaDataDetailReqVO implements Serializable {

    /**
     * 全景点主键
     */
    @NotEmpty(message = "全景点主键不能为空!")
    private String pointId;

    /**
     * 任务架次记录id
     */
    private String missionRecordsId;

    /**
     * 照片采集时间
     */
    @NotNull(message = "照片采集时间不能为空!!")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acquisitionTime;

    /**
     * 明细存储路径
     */
    @NotNull(message = "全景明细文件存储路径不能为空")
    private String detailPath;

}
