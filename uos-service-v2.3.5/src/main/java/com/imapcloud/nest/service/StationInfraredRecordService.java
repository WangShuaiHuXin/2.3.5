package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.StationInfraredRecordEntity;
import com.imapcloud.nest.model.StationInfraredRecordRectangleEntity;
import com.imapcloud.nest.pojo.dto.AIRedRecognitionDto;
import com.imapcloud.nest.pojo.dto.reqDto.RecordDto;
import com.imapcloud.nest.pojo.vo.StationInfraredRecordRectangleVO;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 变电站的设备出现的红外测温记录 服务类
 * </p>
 *
 * @author hc
 * @since 2020-12-29
 */
public interface StationInfraredRecordService extends IService<StationInfraredRecordEntity> {

    RestRes queryRecordByTagId(Integer tagId);

    RestRes queryPhotosByPhotoName(Map<String, Object> params);

    RestRes queryPicByMonth(Integer tagId, String photoName, String startTime, String endTime);

    RestRes getAiRecognitionPic(AIRedRecognitionDto aiRedRecognitionDto);

    RestRes getAllList();

    RestRes setThreshold(String value);

    /**
     * 红外的阈值
     *
     * @return
     */
    Map getThreshold();

    RestRes getTem(StationInfraredRecordRectangleEntity stationInfraredRecordRectangleEntity);

    RestRes saveInfrared(StationInfraredRecordRectangleVO stationInfraredRecordRectangleVO);

    RestRes updateInfraredState(RecordDto recordDto);
}
