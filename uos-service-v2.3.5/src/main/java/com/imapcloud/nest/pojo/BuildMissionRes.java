package com.imapcloud.nest.pojo;

import com.imapcloud.sdk.pojo.entity.Mission;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 启动任务时构建任务结果
 *
 * @author wmin
 */
@Data
@Accessors(chain = true)
public class BuildMissionRes {
    private Mission mission;
    private String errMsg;
}
