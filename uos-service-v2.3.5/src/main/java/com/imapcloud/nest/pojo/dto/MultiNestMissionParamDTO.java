package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.LimitVal;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wmin
 */
@Data
@Accessors(chain = true)
public class MultiNestMissionParamDTO {

    @NotNull
    private String sysUserId;
    @LimitVal(values = {"0", "1", "2"})
    private Integer gainDataMode;
    @LimitVal(values = {"0", "1"})
    private Integer gainVideo;

    @NotNull
    private List<NestMission> nestMissionList;

    @Data
    public static class NestMission {
        private String nestId;
        private String nestUuid;
        private Integer taskId;
        private String taskName;
        private String nestName;
    }
}
