package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import com.imapcloud.nest.enums.TaskModeEnum;
import lombok.Data;

import java.util.Map;

/**
 * Created by wmin on 2020/11/16 17:17
 * 易飞云平台统一航线格式
 *
 * @author wmin
 */
@Data
public class UnifyAirLineFormatDto {
    /**
     * 任务模式
     */
    private TaskModeEnum mode;
    /**
     * 地图绘制参数
     */
    private Map<String, Object> mapConfigs;
    /**
     * 航线设置参数
     */
    private Map<String, Object> lineConfigs;
}
