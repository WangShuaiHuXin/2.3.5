package com.imapcloud.nest.common.netty.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class G503TaskProgressDTO {
    /**
     * 基站uuid
     */
    private String nestUuid;
    /**
     * 基站nestId
     */
    private String nestId;

    private List<MissionDTO> missionDTOList;

    public MissionDTO getMissionDTO(Integer uavWhich) {
        if (Objects.nonNull(uavWhich)) {
            if (!CollectionUtils.isEmpty(this.missionDTOList)) {
                Optional<MissionDTO> first = this.missionDTOList.stream()
                        .filter(m -> Objects.equals(m.getUavWhich(), uavWhich))
                        .findFirst();
                return first.orElse(null);
            }
        }
        return null;
    }

    public boolean containsNoFinishMission(Integer uavWhich) {
        if (Objects.nonNull(uavWhich)) {
            if (!CollectionUtils.isEmpty(this.missionDTOList)) {
                Optional<MissionDTO> first = this.missionDTOList.stream()
                        .filter(m -> Objects.equals(m.getUavWhich(), uavWhich))
                        .findFirst();
                if (first.isPresent()) {
                    MissionDTO missionDTO = first.get();
                    if (MissionStateEnum.TODO.value == missionDTO.getMissionState() ||
                            MissionStateEnum.EXECUTING.value == missionDTO.getMissionState()
                    ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void rmMissionDTO(Integer uavWhich) {
        if (Objects.nonNull(uavWhich)) {
            if (!CollectionUtils.isEmpty(this.missionDTOList)) {
                this.missionDTOList = this.missionDTOList.stream().filter(m -> !Objects.equals(m.getUavWhich(), uavWhich)).collect(Collectors.toList());
            }
        }
    }

    public boolean addMissionDTO(MissionDTO dto) {
        if (Objects.nonNull(dto)) {
            if (CollectionUtils.isEmpty(this.missionDTOList)) {
                this.missionDTOList = new ArrayList<>();
                this.missionDTOList.add(dto);
            } else {
                this.missionDTOList.add(dto);
            }
            return true;
        }
        return false;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MissionDTO {

        /**
         * 无人机位置
         */
        private Integer uavWhich;

        /**
         * 上传数据模式
         */
        private Integer gainDataMode;
        /**
         * 是否需要录屏
         */
        private Integer gainVideo;

        /**
         * 是否需要切换变焦镜头
         */
        private Boolean switchZoomCamera;

        /**
         * 是否需要起飞录制
         */
        private Boolean takeOffRecord;

        /**
         * 任务类型
         */
        private Integer taskType;
        /**
         * 架次uuid
         */
        private String missionUuid;
        /**
         * 架次id
         */
        private Integer missionId;

        /**
         * 架次名称
         */
        private String missionName;

        /**
         * 航点数量
         */
        private Integer points;

        private Integer airLineId;

        private Integer missionState;

        private Integer flyIndex;

        private LocalDateTime startTime;

    }

    public enum MissionStateEnum {
        UNKNOWN(-1),
        TODO(0),
        EXECUTING(1),
        COMPLETE(2),
        ERROR(3);
        private int value;

        MissionStateEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
