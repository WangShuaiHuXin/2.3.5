package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import com.geoai.common.core.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 表计数据查询请求信息
 * @author Vastfy
 * @date 2022/11/29 10:59
 * @since 2.1.5
 */
@ApiModel("表计数据查询请求信息")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MeterDataQueryReqVO extends PageInfo {

    @ApiModelProperty(value = "单位编码", position = 1, example = "000010")
    private String orgCode;

    @ApiModelProperty(value = "架次飞行开始时间-起始", position = 2, example = "2022-11-29")
    @DateTimeFormat(pattern = DateUtils.DATE_PATTERN_OF_CN)
    private LocalDate fromTime;

    @ApiModelProperty(value = "架次飞行开始时间-截止", position = 3, example = "2022-12-29")
    @DateTimeFormat(pattern = DateUtils.DATE_PATTERN_OF_CN)
    private LocalDate toTime;

    @ApiModelProperty(value = "识别类型", position = 3, example = "101")
    private Integer idenValue;

    /**
     * 关键字搜索
     */
    private String keyword;

}
