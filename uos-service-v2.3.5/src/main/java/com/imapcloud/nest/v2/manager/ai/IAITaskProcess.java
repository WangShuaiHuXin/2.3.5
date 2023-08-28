package com.imapcloud.nest.v2.manager.ai;

/**
 * AI识别任务进度信息
 *
 * @author Vastfy
 * @date 2022/11/30 18:33
 * @since 2.1.5
 */
public interface IAITaskProcess {

    /**
     * 获取AI任务ID
     * @return 任务ID
     */
    String getTaskId();

    /**
     * 获取AI任务名称
     * @return 任务名称
     */
    String getTaskName();

    /**
     * 获取AI任务状态值
     * @return  任务状态值
     */
    Integer getTaskState();

    /**
     * 获取AI任务类型
     * @return  任务类型
     */
    Integer getTaskType();

    /**
     * 获取AI任务待识别照片总数量
     * @return  任务待识别照片总数量
     */
    int getTotalPic();

    /**
     * 获取AI任务已识别照片成功数量
     * @return  任务已识别照片成功数量
     */
    int getSuccessPic();

    /**
     * 获取AI任务已识别照片失败数量
     * @return  任务已识别照片失败数量
     */
    int getFailedPic();

    /**
     * 获取AI任务实际执行时间（单位：ms）
     * @return  任务实际执行时间
     */
    long getExecutionTime();

    /**
     * 获取AI任务绑定数据ID
     * @return 任务绑定数据ID
     */
    String getDataId();

    /**
     * 获取AI任务绑定飞行任务ID
     * @return 任务绑定飞行任务ID
     */
    String getFlightTaskId();

    /**
     * 获取AI任务绑定飞行任务的标签信息
     * @return  任务绑定飞行任务的标签信息
     */
    String getFlightTaskTag();

    /**
     * 任务是否完成
     */
    default boolean isCompleted(){
        return getTotalPic() - getSuccessPic() - getFailedPic() <= 0;
    }

    /**
     * 任务是否开始
     */
    default boolean isStart(){
        return (getSuccessPic() == 0 && getFailedPic() == 1) || (getSuccessPic() == 1 && getFailedPic() == 0);
    }

}
