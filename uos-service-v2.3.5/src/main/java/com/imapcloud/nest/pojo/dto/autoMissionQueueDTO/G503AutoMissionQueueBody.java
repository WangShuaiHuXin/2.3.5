package com.imapcloud.nest.pojo.dto.autoMissionQueueDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class G503AutoMissionQueueBody implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int ENABLE_MAX_MISSION_SIZE = 3;

    /**
     * 队列架次状态
     * 1 - 完成
     * 0 - 未完成
     */
    private Integer queueState;
    /**
     * 完成架次
     */
    private Integer completeMissions;

    /**
     * 总时间，单位s
     */
    private Long totalTime;

    private String nestId;
    private String nestUuid;
    private Integer lastMissionIndex;
    private List<Mission> missionList;

    public boolean isFinish() {
        long count = this.missionList.stream().filter(mission -> Objects.equals(MissionStateEnum.TODO.getValue(), mission.state) || Objects.equals(MissionStateEnum.EXECUTING.getValue(), mission.state)).count();
        return count == 0;
    }

    public Integer getMissionId(Integer uavWhich) {
        if (Objects.nonNull(uavWhich)) {
            Optional<Mission> first = this.missionList.stream().filter(mission -> mission.getUavWhich().equals(uavWhich)).findFirst();
            if (first.isPresent()) {
                return first.get().getId();
            }
        }
        return null;
    }

    public boolean containsMission(Integer uavWhich) {
        Mission mission = getMission(uavWhich);
        return Objects.nonNull(mission);
    }

    public Mission getMission(Integer uavWhich) {
        if (Objects.nonNull(uavWhich)) {
            Optional<Mission> first = this.missionList.stream().filter(mission -> mission.getUavWhich().equals(uavWhich)).findFirst();
            if (first.isPresent()) {
                return first.get();
            }
        }
        return null;
    }

    public void addMissions(List<Mission> missionList) {
        if (!CollectionUtils.isEmpty(missionList)) {
            if (CollectionUtils.isEmpty(this.getMissionList())) {
                this.setMissionList(new ArrayList<>());
            }
            for (Mission m : missionList) {
                Integer uavWhich = m.getUavWhich();
                if (!containsMission(uavWhich)) {
                    this.missionList.add(m);
                }
            }
        }
    }

    public void clearTodoMissions() {
        this.missionList = this.missionList.stream().filter(m -> !Objects.equals(MissionStateEnum.TODO.getValue(), m.getState())).collect(Collectors.toList());
    }

    public boolean rmMission(Integer missionId) {
        Optional<Mission> first = this.missionList.stream().filter(m -> Objects.equals(m.getId(), missionId)).findFirst();
        if (first.isPresent()) {
            Mission mission = first.get();
            if (Objects.equals(MissionStateEnum.FINISH.getValue(), mission.state) || Objects.equals(MissionStateEnum.ERROR.getValue(), mission.getState())) {
                return this.missionList.removeIf(m -> Objects.equals(m.getId(), missionId));
            }
        }
        return false;
    }

    public boolean isQueueEmpty() {
        return this.missionList.isEmpty();
    }

    public void updateMissionStateToDo2Error() {
        this.missionList.forEach(m -> {
            if (MissionStateEnum.TODO.getValue() == m.getState()) {
                m.setState(MissionStateEnum.ERROR.getValue());
            }
        });
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Mission {
        private Integer gainDataMode;
        private Integer gainVideo;
        private Integer taskId;
        private Integer taskType;
        private String taskName;
        private String tagName;
        private Integer id;
        private String name;
        private Double progress;
        private Integer state;
        private Long flyTime;
        private Integer uavWhich;
        /**
         * 账号ID
         * @since 2.2.5
         */
        private String accountId;
    }

    public enum MissionStateEnum {
        UNKNOWN(-1),
        TODO(0),
        EXECUTING(1),
        FINISH(2),
        ERROR(3);
        private int value;

        MissionStateEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static MissionStateEnum getInstance(Integer value) {
            if (value != null) {
                for (MissionStateEnum e : MissionStateEnum.values()) {
                    if (e.getValue() == value) {
                        return e;
                    }
                }
            }
            return UNKNOWN;
        }
    }

    public enum QueueStateEnum {
        NO_FINISH(0),
        FINISH(1);
        private int value;

        QueueStateEnum(int value) {
            this.value = value;
        }
    }
}
