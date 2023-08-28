package com.imapcloud.nest.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 机巢更新状态
 *
 * @author wmin
 */
@Data
@Accessors(chain = true)
public class NestCpsUpdateDto {
    /**
     * 基站名称
     */
    private String nestName;
    /**
     * 安装包名称
     */
    private String packageName;
    /**
     * 安装包大小，单位MB
     */
    private Double packageSize;

    /**
     * 下载进度
     */
    private Integer downloadProgress;

    /**
     * 更新状态
     */
    private String updateState;

    /**
     * 无人机标识
     */
    private Integer uavWhich;

}
