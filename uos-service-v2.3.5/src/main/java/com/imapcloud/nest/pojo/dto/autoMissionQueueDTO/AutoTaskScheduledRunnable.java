package com.imapcloud.nest.pojo.dto.autoMissionQueueDTO;

import com.imapcloud.nest.utils.CountDowner;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.ScheduledFuture;

@Data
@Accessors(chain = true)
public class AutoTaskScheduledRunnable {
    private ScheduledFuture<?> schedule;
    private String nestUuid;
    private Integer missionId;
    private Integer type;
    private String account;
    private CountDowner countDowner;

    public boolean cancelSchedule() {
        if (this.schedule == null) {
            return false;
        }
        if (this.countDowner == null || this.countDowner.isExpire() || this.countDowner.getScheduledFuture() == null) {
            return false;
        }

        this.schedule.cancel(true);
        this.countDowner.getScheduledFuture().cancel(true);
        return true;
    }

}
