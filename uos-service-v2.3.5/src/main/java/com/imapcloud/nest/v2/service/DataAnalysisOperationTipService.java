package com.imapcloud.nest.v2.service;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisOperationTipService.java
 * @Description DataAnalysisOperationTipService
 * @createTime 2022年07月13日 15:21:00
 */
public interface DataAnalysisOperationTipService {

    /**
     *  设置是否要展示提示
     * @param enable
     * @return
     */
    public boolean setOperationTip(Integer enable);

    /**
     *  获取是否要展示提示
     * @param
     * @return
     */
    public int getOperationTip();


}
