package com.imapcloud.sdk.pojo.entity;

import com.imapcloud.sdk.pojo.constant.MissionCommonStateEnum;

/**
 * @author daolin
 * 机巢通用任务状态
 */
public class MissionState {

    /**
     * 任务执行状态（单个航点的状态）
     */
    private MissionCommonStateEnum currentState = MissionCommonStateEnum.UNKNOWN;
    /**
     * 当前的流程步骤
     */
    private Process process = new Process();
    /**
     * 错误诊断信息
     */
    private ErrorDiagnosis errorDiagnosis = new ErrorDiagnosis();
    /**
     * 是否遇到错误
     */
    private Boolean errorEncountered = false;

    /**
     * 是否整个任务流程被人为中止
     */
    private Boolean aborted = false;

    public MissionCommonStateEnum getCurrentState() {
        return currentState;
    }

    public void setCurrentState(MissionCommonStateEnum currentState) {
        this.currentState = currentState;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public ErrorDiagnosis getErrorDiagnosis() {
        return errorDiagnosis;
    }

    public void setErrorDiagnosis(ErrorDiagnosis errorDiagnosis) {
        this.errorDiagnosis = errorDiagnosis;
    }

    public Boolean getErrorEncountered() {
        return errorEncountered;
    }

    public void setErrorEncountered(Boolean errorEncountered) {
        this.errorEncountered = errorEncountered;
    }

    public Boolean getAborted() {
        return aborted;
    }

    public void setAborted(Boolean aborted) {
        this.aborted = aborted;
    }

    /**
     * 流程步骤
     */
    public static class Process {
        /**
         * 流程步骤信息
         */
        private String processMessage = "";
        /**
         * 该流程步骤是否已完成
         */
        private Boolean finished = false;
        /**
         * 是否正在倒计时中
         */
        private Boolean countingDown = false;
        /**
         * 倒计时还剩余多少秒步骤超时报错
         */
        private Long countDownToTimeout = 0L;

        public String getProcessMessage() {
            return processMessage;
        }

        public void setProcessMessage(String processMessage) {
            this.processMessage = processMessage;
        }

        public Boolean getFinished() {
            return finished;
        }

        public void setFinished(Boolean finished) {
            this.finished = finished;
        }

        public Boolean getCountingDown() {
            return countingDown;
        }

        public void setCountingDown(Boolean countingDown) {
            this.countingDown = countingDown;
        }

        public Long getCountDownToTimeout() {
            return countDownToTimeout;
        }

        public void setCountDownToTimeout(Long countDownToTimeout) {
            this.countDownToTimeout = countDownToTimeout;
        }
    }

    /**
     * 错误诊断信息
     */
    public static class ErrorDiagnosis {
        /**
         * 错误信息
         */
        private String errorMessage = "";
        /**
         * 解决办法
         */
        private String solution = "";

        private String errorCode = "";

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getSolution() {
            return solution;
        }

        public void setSolution(String solution) {
            this.solution = solution;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }
    }

}
