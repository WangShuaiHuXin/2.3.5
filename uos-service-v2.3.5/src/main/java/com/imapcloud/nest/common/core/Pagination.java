package com.imapcloud.nest.common.core;

import lombok.Data;

import javax.validation.constraints.Max;
import java.io.Serializable;

/**
 * 分页查询条件
 * @author Vastfy
 * @date 2022/04/18 15:11
 * @since 1.8.9
 */
@Data
public class Pagination implements Serializable {

    /**
     * 当前页面【默认值1】
     */
//    @ApiModelProperty(value = "当前页面【默认值1】", example = "1", position = 1001)
    private int curPage = 1;

    /**
     * 分页大小【默认值10】
     */
    @Max(value = 100, message = "{geoai_uos__number_of_single_query_cannot_exceed_100}")
//    @ApiModelProperty(value = "分页大小【默认值10】", example = "10", position = 1002)
    private int pageSize = 10;

    public Pagination() {
    }

    public final void setCurPage(int curPage) {
        this.curPage = Math.max(curPage, 0);
    }

}
