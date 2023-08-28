package com.imapcloud.nest.pojo.dto.autoMissionQueueDTO;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.*;

@Data
@Accessors(chain = true)
public class AutoMissionQueueBody implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int ENABLE_MAX_MISSION_SIZE = 8;

    /**
     * mission_records中的exec_id
     */
    private String execMissionId;

    /**
     * 总架次数
     */
    private Integer totalMissions;

    /**
     * 完成架次
     */
    private Integer completeMissions;

    /**
     * 总时间，单位s
     */
    private Long totalTime;

    /**
     * 队列架次状态
     */
    private Integer queueState;

    /**
     * 基站id
     */
    private String nestId;

    /**
     * 基站UUID
     */
    private String nestUuid;

    private Extra extra;

    private Integer lastMissionIndex;

    private List<MissionBody> missionList;

    public void sumTime() {
        this.totalTime = this.missionList.stream().mapToLong(MissionBody::getFlyTime).sum();
    }

    public int obtainReadyMissionId() {
        if (lastMissionIndex > missionList.size() - 1) {
            return -1;
        }
        if (this.lastMissionIndex >= 0) {
            return missionList.get(this.lastMissionIndex).getId();
        }
        return -1;
    }

    /**
     * 获取未执行完成的架次id
     *
     * @return
     */
    public int obtainUnExecutedMissionId() {
        if (lastMissionIndex > missionList.size() - 1) {
            return -1;
        }
        if (this.lastMissionIndex >= 0) {
            MissionBody missionBody = missionList.get(this.lastMissionIndex);
            if (missionBody.getState().equals(MissionState.COMPLETE.getValue())) {
                if (this.lastMissionIndex + 1 > missionList.size() - 1) {
                    return -1;
                }
                return missionList.get(this.lastMissionIndex + 1).getState();
            }
            return missionList.get(this.lastMissionIndex).getId();
        }
        return -1;
    }

    public long obtainReadyMissionFlyTime() {
        if (lastMissionIndex > missionList.size() - 1) {
            return 0L;
        }
        if (this.lastMissionIndex >= 0) {
            return missionList.get(this.lastMissionIndex).getFlyTime();
        }
        return 0L;
    }

    public String obtainReadyMissionName() {
        if (lastMissionIndex > missionList.size() - 1) {
            return null;
        }
        if (this.lastMissionIndex >= 0) {
            return missionList.get(this.lastMissionIndex).getName();
        }
        return null;
    }

    public void updateLastMissionIndex() {
        if (this.lastMissionIndex > this.getMissionList().size() - 1) {
            return;
        }
        if (this.lastMissionIndex > -1) {
            MissionBody missionBody = this.missionList.get(this.lastMissionIndex);
            if (MissionState.COMPLETE.getValue() == missionBody.getState()) {
                this.lastMissionIndex++;
            }
        } else {
            this.lastMissionIndex++;
        }
    }

    public void init() {
        this.completeMissions = 0;
        this.totalTime = 0L;
        this.queueState = QueueState.STOP.getValue();
        this.missionList.forEach(m -> {
            m.setProgress(0.0);
            m.setState(MissionState.TODO.getValue());
        });
    }

    public List<MissionBody> addMissionBodyList(List<MissionBody> missionList) {
        if (this.missionList == null) {
            this.missionList = missionList;
            return Collections.emptyList();
        }
        List<MissionBody> existMissionBodyList = new ArrayList<>(missionList.size());
        for (MissionBody mb : missionList) {
            if (!checkContainsMissionBody(mb)) {
                this.missionList.add(mb);
            } else {
                existMissionBodyList.add(mb);
            }
        }
        updateTotalMissions();
        return existMissionBodyList;
    }


    public boolean removeMissionBody(Integer missionId) {
        if (this.missionList != null) {
            int missionBodyIndex = obtainMissionBodyIndex(missionId);
            if (missionBodyIndex != -1) {
                MissionBody missionBody = this.missionList.get(missionBodyIndex);
                if (MissionState.TODO.getValue() == missionBody.getState()) {
                    this.missionList.remove(missionBodyIndex);
                    updateTotalMissions();
                    return true;
                }
            }

        }
        return false;
    }

    public boolean topMission(Integer missionId) {
        int missionBodyIndex = obtainMissionBodyIndex(missionId);
        if (missionBodyIndex == -1) {
            return false;
        }
        if (this.lastMissionIndex < this.missionList.size() - 1) {
            this.missionList.add(this.lastMissionIndex + 1, this.missionList.remove(missionBodyIndex));
            return true;
        }
        return false;
    }

    public boolean completeFinish() {
        if (this.lastMissionIndex > this.missionList.size() - 1) {
            this.setQueueState(AutoMissionQueueBody.QueueState.FINISH.getValue());
            return true;
        }

        if (this.lastMissionIndex == this.missionList.size() - 1 && (
                MissionState.COMPLETE.getValue() == this.missionList.get(this.lastMissionIndex).getState()
        )) {
            this.setQueueState(AutoMissionQueueBody.QueueState.FINISH.getValue());
            return true;
        }
        return false;
    }

    public int completeRedisExpire() {
        GeoaiUosProperties bean = SpringContextUtils.getBean(GeoaiUosProperties.class);
        if (QueueState.PAUSE.getValue() == this.queueState || QueueState.TODO.getValue() == this.queueState) {
            if (bean == null) {
                return 30;
            }
            return bean.getTaskQueue().getPauseRetentionDuration();
        }
        if (QueueState.FINISH.getValue() == this.queueState || QueueState.STOP.getValue() == this.queueState) {
            if (bean == null) {
                return 10;
            }
            return bean.getTaskQueue().getEndRetentionDuration();
        }

        if (QueueState.RUNNING.getValue() == this.queueState) {
            if (this.totalMissions - this.completeMissions > 0) {
                return (this.totalMissions - this.completeMissions) * 60;
            }
        }

        return 10;
    }

    public boolean isFinish() {
        return AutoMissionQueueBody.QueueState.FINISH.getValue() == this.getQueueState();
    }

    public int obtainEnableMissionSize() {
        if (this.missionList == null) {
            return 0;
        }
        return ENABLE_MAX_MISSION_SIZE - this.getMissionList().size();
    }

    public void updateCompleteMissionsAndQueueState() {
        this.completeMissions = (int) this.missionList.stream().filter(mb -> MissionState.COMPLETE.getValue() == mb.getState()).count();
        if (this.completeMissions.equals(this.totalMissions) && QueueState.RUNNING.getValue() == this.queueState) {
            this.setQueueState(QueueState.FINISH.getValue());
        }
    }

    public boolean checkHasMissionId(Integer missionId) {
        if (this.missionList != null) {
            for (MissionBody missionBody : this.missionList) {
                if (missionBody.getId().equals(missionId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateTotalMissions() {
        this.totalMissions = this.missionList.size();
    }

    private int obtainMissionBodyIndex(Integer missionId) {
        if (this.missionList != null) {
            for (int i = 0; i < this.missionList.size(); i++) {
                MissionBody missionBody = this.missionList.get(i);
                if (missionBody != null &&
                        missionBody.getId().equals(missionId) &&
                        MissionState.TODO.getValue() == missionBody.getState()) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean checkContainsMissionBody(MissionBody missionBody) {
        for (MissionBody mb : this.missionList) {
            if (mb.getId().equals(missionBody.getId())) {
                return true;
            }
        }
        return false;
    }


    @Data
    @Accessors(chain = true)
    public static class MissionBody implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * 架次id
         */
        private Integer id;
        /**
         * 架次名称
         */
        private String name;
        /**
         * 执行进度
         */
        private Double progress;
        /**
         * 架次状态
         */
        private Integer state;

        private Long flyTime = 0L;

        /**
         * 账号ID
         * @since 2.2.3
         */
        private String accountId;

        /**
         * 任务开始执行时间戳
         */
        private Long taskStartTime;
    }


    @Data
    @Accessors(chain = true)
    public static class Extra implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer gainDataMode;
        private Integer gainVideo;
        private String account;
        private Integer type;
        private Integer driveType;
        private String nestName;
        private Integer planRecordId;
        private Integer planId;
        private Integer positionStrategy = 1;
        /**
         * missionId -> planRecordMissionId
         */
        private Map<String, Integer> planRecordMissionIdMap = new HashMap<>(8);

        /**
         * missionId -> missionRecordId
         */
        private Map<String, Integer> missionRecordIdMap = new HashMap<>(8);

        private Boolean multiTask;
        private String planName;
        private Long userId;
        private boolean planAuto;


        public void putPlanRecordMissionId(Map<Integer, Integer> map) {
            if (CollectionUtil.isNotEmpty(map)) {
                Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, Integer> next = iterator.next();
                    this.planRecordMissionIdMap.put(String.valueOf(next.getKey()), next.getValue());
                }
            }
        }

        public Integer obtainPlanRecordMissionId(Integer missionId) {
            return this.planRecordMissionIdMap.get(String.valueOf(missionId));
        }

        public void putMissionRecordId(Integer missionId, Integer missionRecordId) {
            this.missionRecordIdMap.put(String.valueOf(missionId), missionRecordId);
        }

        public Integer obtainMissionRecordId(Integer missionId) {
            return this.missionRecordIdMap.get(String.valueOf(missionId));
        }

        public List<Integer> listMissionRecordIds() {
            Collection<Integer> missionRecordIdSet = this.missionRecordIdMap.values();
            if (CollectionUtil.isNotEmpty(missionRecordIdSet)) {
                return new ArrayList<>(missionRecordIdSet);
            }
            return Collections.emptyList();
        }
    }

    public enum QueueState {
        UNKNOWN(-1),
        TODO(0),
        RUNNING(1),
        PAUSE(2),
        STOP(3),
        FINISH(4);

        private int value;

        QueueState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum DriveTypeEnum {
        BATCH_TASK(1),
        PLAN_TASK(2);
        private int type;

        DriveTypeEnum(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }


    public enum MissionState {
        UNKNOWN(-1),
        TODO(0),
        EXECUTING(1),
        COMPLETE(2),
        ERROR(3);
        private int value;

        MissionState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static MissionState getInstance(Integer value) {
            if (value != null) {
                for (MissionState e : MissionState.values()) {
                    if (e.getValue() == value) {
                        return e;
                    }
                }
            }
            return UNKNOWN;
        }
    }

}
