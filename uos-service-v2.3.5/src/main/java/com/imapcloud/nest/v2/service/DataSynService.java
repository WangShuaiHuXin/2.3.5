package com.imapcloud.nest.v2.service;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisBaseService.java
 * @Description DataAnalysisBaseService
 * @createTime 2022年07月13日 14:39:00
 */
public interface DataSynService{

    boolean synDataList(String nestId ,List<Integer> recordIdList);


}
