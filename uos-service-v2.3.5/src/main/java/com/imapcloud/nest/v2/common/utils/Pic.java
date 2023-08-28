package com.imapcloud.nest.v2.common.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName Pic.java
 * @Description Pic
 * @createTime 2022年07月18日 13:47:00
 */
@Data
@Builder
@AllArgsConstructor
public class Pic{
    //w -> width
    /**
     * width
     */
    private BigDecimal w;
    //h -> height
    /**
     * height
     */
    private BigDecimal h;
    //标记坐标 x1 -> 横坐标 ， y1 -> 纵坐标 ， w1 -> 宽度 ， h1 -> 高度
    /**
     * 标注横坐标
     */
    private BigDecimal x1 ;

    /**
     * 标注纵坐标
     */
    private BigDecimal y1 ;

    /**
     * 标注宽度
     */
    private BigDecimal w1 ;

    /**
     * 标注高度
     */
    private BigDecimal h1 ;

    //外框  缩放裁剪坐标 x2为偏移横坐标比例，y2为偏移纵坐标  w2为截图框宽度，h2为截图框高度
    /**
     * 裁剪框原图偏移横坐标
     */
    private BigDecimal x2 ;

    /**
     * 裁剪框原图偏移纵坐标
     */
    private BigDecimal y2 ;

    /**
     * 裁剪框宽度
     */
    private BigDecimal  w2 ;

    /**
     * 裁剪框高度
     */
    private BigDecimal h2;

    /**
     * 倍率
     */
    private BigDecimal scale;

    /**
     * 描述
     */
    private String desc;

    /**
     * 来源类型
     */
    private Integer srcDataType;

    /**
     * 照片创建时间
     */
    private LocalDateTime photoCreateTime;

    public Pic(){}

    /**
     *
     * @param x1
     * @param y1
     * @param w1
     * @param h1
     * @param x2
     * @param y2
     * @param w2
     * @param h2
     * @param scale
     */
    public Pic(BigDecimal x1 , BigDecimal  y1 , BigDecimal w1 , BigDecimal h1
            , BigDecimal x2 , BigDecimal y2 ,BigDecimal w2 , BigDecimal h2
            , BigDecimal scale ,String desc,Integer srcDataType){
        this.scale = scale;
        //内框
        this.x1 = x1;
        this.y1 = y1;
        this.w1 = (w1);
        this.h1 = (h1);

        //外框
        this.x2 = x2;
        this.y2 = y2;
        this.w2 = (w2);
        this.h2 = (h2);

        //描述
        this.desc = desc;
        this.srcDataType = srcDataType;
    }

    /**
     *  计算属性
     * @param w
     * @param h
     */
    public void computeValue(BigDecimal w , BigDecimal h){
        this.w = w;
        this.h = h;

        this.x1 = x1.multiply(w);
        this.y1 = y1.multiply(h);
        this.w1 = (w1).multiply(w);
        this.h1 = (h1).multiply(h);

        //外框
        this.x2 = x2.multiply(w);
        this.y2 = y2.multiply(h);
        this.w2 = (w2).multiply(w);
        this.h2 = (h2).multiply(h);
    }

    /**
     *
     * @param x1
     * @param y1
     * @param w1
     * @param h1
     * @param x2
     * @param y2
     * @param w2
     * @param h2
     * @param scale
     * @param w
     * @param h
     */
    public Pic(BigDecimal x1 , BigDecimal  y1 , BigDecimal w1 , BigDecimal h1
            , BigDecimal x2 , BigDecimal y2 ,BigDecimal w2 , BigDecimal h2
            , BigDecimal scale,BigDecimal w , BigDecimal h){
        this.scale = scale;
        this.w = w;
        this.h = h;
        //内框
        this.x1 = x1.multiply(w);
        this.y1 = y1.multiply(h);
        this.w1 = (w1).multiply(w);
        this.h1 = (h1).multiply(h);

        //外框
        this.x2 = x2.multiply(w);
        this.y2 = y2.multiply(h);
        this.w2 = (w2).multiply(w);
        this.h2 = (h2).multiply(h);
    }



}
