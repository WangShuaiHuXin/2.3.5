package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PicVO.java
 * @Description TODO
 * @createTime 2022年07月13日 10:23:00
 */
@Data
@Slf4j
public class PicVO {

    private BigDecimal ww , hh ;
    //标记坐标
    private BigDecimal x1 , y1 , w1 , h1  ;
    //外框  w2  当前分辨率 原图 h2 缩放裁剪坐标 x2为偏移横坐标比例，y2为偏移纵坐标  w2为截图框宽度，h2为截图框高度
    private BigDecimal x2 , y2 , w2 , h2;

    private BigDecimal scale;

    public PicVO(){}

    //传跟原图的比例
    public PicVO(BigDecimal x1 , BigDecimal  y1 , BigDecimal w1 , BigDecimal h1 , BigDecimal x2 , BigDecimal y2 ,BigDecimal w2 , BigDecimal h2 , BigDecimal scale,BigDecimal ww , BigDecimal hh){
        this.scale = scale;
        this.ww = ww;
        this.hh = hh;
        //内框
        this.x1 = x1 ;
        this.y1 = y1 ;
        this.w1 = (w1) ;
        this.h1 = (h1) ;

        //外框
        this.x2 = x2 ;
        this.y2 = y2 ;
        this.w2 = (w2) ;
        this.h2 = (h2) ;
        //内框
//        this.x1 = x1 * w;
//        this.y1 = y1 * h;
//        this.w1 = (w1) * w;
//        this.h1 = (h1) * h;
//
//        //外框
//        this.x2 = x2 * w;
//        this.y2 = y2 * h;
//        this.w2 = (w2) * w;
//        this.h2 = (h2) * h;
    }
}
