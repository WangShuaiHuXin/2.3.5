package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaRecordsReqVO.java
 * @Description DataPanoramaRecordsReqVO
 * @createTime 2022年09月16日 11:23:00
 */
@Data
public class DataPanoramaRecordsReqVO {

    @Data
    public static class RecordsReqVO extends PageInfo implements Serializable {
        /**
         * 单位编码
         */
        @ApiModelProperty(value = "单位编码", position = 1, example = "")
        private String orgCode;

        /**
         * 基站id
         */
        @ApiModelProperty(value = "基站id", position = 1, example = "")
        private String baseNestId;

        /**
         * 开始时间
         */
        @ApiModelProperty(value = "开始时间", position = 1, example = "")
//        @NotNull(message = "开始时间不能为空")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startTime;

        /**
         * 结束时间
         */
        @ApiModelProperty(value = "结束时间", position = 1, example = "")
//        @NotNull(message = "结束时间不能为空")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endTime;
    }

    @Data
    public static class PicReqVO implements Serializable {
        /**
         * 架次记录ID
         */
        @ApiModelProperty(value = "架次记录ID", position = 1, example = "")
        @NotNull(message = "架次记录ID不能为空")
        private String missionRecordsId;

        /**
         * 航点id
         */
        @ApiModelProperty(value = "航点id", position = 1, example = "")
        private String airPointId;

        /**
         * 航点序号
         */
        @ApiModelProperty(value = "航点序号", position = 1, example = "0")
        @Min(value = 1, message = "最小航点序号为1")
        @NotNull(message = "航点序号不能为空")
        private Integer airPointIndex;

    }

}
