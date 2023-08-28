package com.imapcloud.nest.v2.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisWebPicStatusEnum.java
 * @Description DataAnalysisWebPicStatusEnum
 * @createTime 2022年07月08日 15:41:00
 */
@Getter
public enum DataAnalysisWebPicStatusEnum {

    /**
     * -1：所有
     */
    ALL_STATUSES(-1,-1,-1,"所有","所有"),

    /**
     * 0：待分析
     */
    NEED_ANALYZE(0,0,0,"待分析","【提交态】【待分析】"),

    /**
     * 1: 待确认有问题
     */
    CONFIRM_PROBLEM(1,0,1,"待确认有问题","【提交态】【有问题】"),

    /**
     * 2: 待确认无问题
     */
    CONFIRM_NO_PROBLEM(2,0,2,"待确认无问题","【提交态】【无问题】"),

    /**
     * 3: 有问题
     */
    PROBLEM(3,1,1,"有问题","【核实态】【有问题】"),

    /**
     * 4: 无问题
     */
    NO_PROBLEM(4,1,2,"无问题","【核实态】【无问题】");



    private int code;

    private int photoState;

    private int pushState;

    private String webState;

    private String backState;

    DataAnalysisWebPicStatusEnum(int code, int pushState , int photoState,String webState , String backState){
        this.code = code;
        this.photoState = photoState;
        this.pushState = pushState;
        this.webState = webState;
        this.backState = backState;
    }

    public static Optional<DataAnalysisWebPicStatusEnum> findMatch(int pushState , int photoState){
        return Arrays.stream(DataAnalysisWebPicStatusEnum.values())
                .filter(e -> Objects.equals(pushState, e.getPushState()) && Objects.equals(photoState,e.getPhotoState()))
                .findFirst();
    }

    public static Optional<DataAnalysisWebPicStatusEnum> findMatch(int code){
        return Arrays.stream(DataAnalysisWebPicStatusEnum.values())
                .filter(e -> Objects.equals(code, e.getCode()))
                .findFirst();
    }
}
