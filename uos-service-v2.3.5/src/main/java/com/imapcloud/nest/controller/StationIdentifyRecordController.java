package com.imapcloud.nest.controller;


import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.model.DefectInfoEntity;
import com.imapcloud.nest.pojo.dto.reqDto.StationIdentifyDefectReqDto;
import com.imapcloud.nest.service.StationIdentifyRecordService;
import com.imapcloud.nest.utils.RestRes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 * 变电站的设备出现的缺陷记录 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-12-29
 */
@RestController
@RequestMapping("/stationIdentifyRecord")
public class StationIdentifyRecordController {

}

