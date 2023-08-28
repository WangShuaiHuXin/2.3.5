package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import lombok.Data;

import java.util.List;

/**
 * Created by wmin on 2020/11/16 17:53
 * 全景采集参数
 *
 * @author wmin
 */
@Data
public class PanoramaParam {
    /**
     * 返航模式，直线返航/原路返航
     */
    private ReturnMode returnMode;
    /**
     * 航线高度
     */
    private Integer lineAlt;

    /**
     * 起降航高
     */
    private Integer takeOffLandAlt;

    /**
     * 分辨率
     */
    private Integer resolution;

    /**
     * 速度
     */
    private Integer speed;

    /**
     * 航线航点别名
     */
    private String byname;

    /**
     * 识别类型
     */
    private List<Integer> photoPropList;

    public enum ReturnMode {
        LINE, ORIGINAL
    }
}
