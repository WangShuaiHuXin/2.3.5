package com.imapcloud.nest.v2.common.utils;

import com.imapcloud.nest.v2.common.enums.DataAnalysisWebPicStatusEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName WebPicStatusConverterUtils.java
 * @Description WebPicStatusConverterUtils
 * @createTime 2022年07月15日 10:51:00
 */
public class WebPicStatusConverterUtils {

    /**
     * 根据后台状态转化为前台状态
     * @param pushState
     * @param photoState
     * @return
     */
    public static Integer getWebPicStatus(Integer pushState, Integer photoState) {
        DataAnalysisWebPicStatusEnum dataAnalysisWebPicStatusEnum = DataAnalysisWebPicStatusEnum.findMatch(pushState,photoState).orElseThrow(()->new BusinessException(String.format("photoState:%d,pushState:%d,无法转换状态",photoState,pushState)));
        return dataAnalysisWebPicStatusEnum.getCode();
    }

    /**
     * 根据前台状态，获取照片状态
     * @param picStatus
     * @return
     */
    public static Integer getPhotoState(Integer picStatus) {
        DataAnalysisWebPicStatusEnum dataAnalysisWebPicStatusEnum = DataAnalysisWebPicStatusEnum.findMatch(picStatus).orElseThrow(()->new BusinessException(String.format("picStatus:%d,无法转换状态",picStatus)));
        return dataAnalysisWebPicStatusEnum.getPhotoState() == -1? null : dataAnalysisWebPicStatusEnum.getPhotoState();
    }

    /**
     * 根据前台状态，获取照片推送状态
     * @param picStatus
     * @return
     */
    public static Integer getPushState(Integer picStatus) {
        DataAnalysisWebPicStatusEnum dataAnalysisWebPicStatusEnum = DataAnalysisWebPicStatusEnum.findMatch(picStatus).orElseThrow(()->new BusinessException(String.format("picStatus:%d,无法转换状态",picStatus)));
        return dataAnalysisWebPicStatusEnum.getPushState() == -1 ? null : dataAnalysisWebPicStatusEnum.getPushState();
    }
}
