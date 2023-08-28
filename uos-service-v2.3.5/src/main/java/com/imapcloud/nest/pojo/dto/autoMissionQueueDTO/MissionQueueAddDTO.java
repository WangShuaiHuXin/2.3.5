package com.imapcloud.nest.pojo.dto.autoMissionQueueDTO;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MissionQueueAddDTO {
    @NotNull
    private String nestId;
    @NotEmpty
    private List<Integer> missionIdList;
    /**
     * 账号ID
     * @since 2.2.5
     */
    private String accountId;
}
