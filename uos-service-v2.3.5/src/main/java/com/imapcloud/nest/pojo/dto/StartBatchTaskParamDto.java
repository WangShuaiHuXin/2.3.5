package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.CollectionNotEmpty;
import com.imapcloud.nest.common.annotation.LimitVal;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wmin
 *
 */
@Data
public class StartBatchTaskParamDto {
    @CollectionNotEmpty
    private List<Integer> taskIdList;
    @LimitVal(values = {"0", "1", "2"})
    private Integer gainDataMode;
    @LimitVal(values = {"0", "1"})
    private Integer gainVideo;
    @LimitVal(values = {"0", "1"}, message = "flightStrategy只能是0、1")
    private Integer flightStrategy;
}
