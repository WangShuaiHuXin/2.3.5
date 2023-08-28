package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.out.DJICommonResultOutDTO;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJICommonService.java
 * @Description DJICommonService
 * @createTime 2022年10月19日 15:52:00
 */
public interface DJIMissionService {

    /**
     * 取消任务
     * @param nestId
     * @param fightIds
     * @return
     */
    DJICommonResultOutDTO.CommonResultOutDTO flightTaskUndo(String nestId , List<String> fightIds);

}
