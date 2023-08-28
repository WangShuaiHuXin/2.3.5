package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.LimitVal;
import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
public class G503AutoMissionQueueDTO {

    @NotNull
    @NestId
    private String nestId;

    @LimitVal(values = {"0", "1", "2"}, message = "gainDataMode只能是0、1、2")
    private Integer gainDataMode;

    @LimitVal(values = {"0", "1"}, message = "gainVideo只能是0、1")
    private Integer gainVideo;

    //只有G900需要
    private Integer positionStrategy = 1;

    @NotEmpty
    private List<Mission> missionList;

    /**
     * 账号ID
     * @since 2.2.5
     */
    private String accountId;

    @Data
    public static class Mission {
        private Integer uavWhich;
        private Integer missionId;
    }
}
