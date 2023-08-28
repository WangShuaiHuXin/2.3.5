package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.VideoSrtInDTO;
import com.imapcloud.nest.v2.service.dto.out.FpiAirlinePackageParseOutDTO;

import java.io.InputStream;

/**
 * 数据解析业务接口实现
 *
 * @author Vastfy
 * @date 2023/2/22 9:33
 * @since 2.2.3
 */
public interface DataParseService {

    /**
     * 解析精细巡检数据压缩包
     * @param filename   精细巡检数据压缩包名称
     * @param inputStream   精细巡检数据压缩包文件流
     * @return  解析结果
     */
    FpiAirlinePackageParseOutDTO parseFinePatrolInspectionData(String filename, InputStream inputStream);

    String parseFlightTrackData(VideoSrtInDTO data);
}
