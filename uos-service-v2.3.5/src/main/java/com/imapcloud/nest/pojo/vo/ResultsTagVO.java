package com.imapcloud.nest.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ResultsTagVO {

    /**
     * tag      : 成果标记
     * tagName  : 成果标记名
     * position : {"x":"成果标记坐标%","y":"成果标记坐标","x1":"成果标记坐标","y1":"成果标记坐标"}
     * uuid     : 成果标记唯一标识
     */
    @TableId
    private Integer id;
    private String tag;
    private String tagName;
    private String uuid;
    @TableField(exist=false)
    private PositionBean position;
    private Integer resultId;
    private Integer defectId;
    private String x;
    private String y;
    private String x1;
    private String y1;

    @Data
    public static class PositionBean {
        /**
         * x  : 成果标记坐标
         * y  : 成果标记坐标
         * x1 : 成果标记坐标
         * y1 : 成果标记坐标
         */
        private String x;
        private String y;
        private String x1;
        private String y1;
    }
}
