package com.imapcloud.nest.pojo.dto;

import lombok.Data;

/**
 * 固定机巢调试面板
 *
 * @author wmin
 */
@Data
public class FixDebugPanelDto {
    /**
     * 舱门状态
     * -1 -> 未知
     * 0 -> 关闭
     * 1 -> 打开
     * 2 -> 错误
     * 3 -> 重置
     */
    private Integer cabin;

    /**
     * 翻折装置
     * -1 -> 未知
     * 0 -> 关闭
     * 1 -> 打开
     * 2 -> 错误
     * 3 -> 重置
     */
    private Integer fold;

    /**
     * 升降平台
     * -1 -> 未知
     * 0 -> 关闭
     * 1 -> 打开
     * 2 -> 错误
     * 3 -> 重置
     */
    private Integer lift;

    /**
     * 机械爪
     * -1 -> 未知
     * 0 -> 关闭
     * 1 -> 打开
     * 2 -> 错误
     * 3 -> 重置
     */
    private Integer claw;

    /**
     * 归中X
     * -1 -> 未知
     * 0 -> 关闭
     * 1 -> 打开
     * 2 -> 错误
     * 3 -> 重置
     */
    private Integer squareX;

    /**
     * 归中Y
     * -1 -> 未知
     * 0 -> 关闭
     * 1 -> 打开
     * 2 -> 错误
     * 3 -> 重置
     */
    private Integer squareY;

    /**
     * 机械臂X
     * -1 -> 未知
     * 0 ->  原点
     * 1 ->  中间点
     * 2 ->  错误
     * 3 ->  重置
     * 4 ->  终点
     */
    private Integer armX;

    /**
     * 机械臂X
     * -1 -> 未知
     * 0 ->  原点
     * 1 ->  中间点
     * 2 ->  错误
     * 3 ->  重置
     * 4 ->  终点
     */
    private Integer armY;

    /**
     * 机械臂Z
     * -1 -> 未知
     * 0 ->  原点
     * 1 ->  中间点
     * 2 ->  错误
     * 3 ->  重置
     * 4 ->  终点
     */
    private Integer armZ;

}
