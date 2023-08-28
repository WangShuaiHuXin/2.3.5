package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJICommonResultRespVO.java
 * @Description DJICommonResultRespVO
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@ApiModel("响应信息")
@EqualsAndHashCode(callSuper = false)
public class DJITaskRespVO implements Serializable {

    @Data
    public static class DJITaskInfoRespVO {

        /**
         * key -> 航线架次id
         * val -> 航线的JSON
         */
        @ApiModelProperty(value = "航线json", position = 1, example = "")
        private Map<Integer, String> airLineMap;

        /**
         * 机巢id
         */
        @ApiModelProperty(value = "基站", position = 1, example = "")
        private String nestId;

        @ApiModelProperty(value = "标签", position = 1, example = "0")
        private Integer tagId;

        /**
         * 显示参数
         */
        @ApiModelProperty(value = "showinfo", position = 1, example = "")
        private String showInfo;

        /**
         * key -> 航线架次id
         * val -> 航线的JSON
         */
        @ApiModelProperty(value = "大疆json新格式", position = 1, example = "")
        private Map<Integer, String> djiAirLineMap;

    }

    @Data
    public static class DJITaskFileInfoRespVO {

        @ApiModelProperty(value = "航线文件id", position = 1, example = "")
        private String taskFileId;

        /**
         * key -> 航线架次id
         * val -> 航线的JSON
         */
        @ApiModelProperty(value = "大疆json新格式", position = 1, example = "")
        private String djiAirLine;

    }

}
