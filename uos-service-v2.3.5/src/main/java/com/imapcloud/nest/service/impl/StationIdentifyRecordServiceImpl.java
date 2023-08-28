package com.imapcloud.nest.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.MissionConstant;
import com.imapcloud.nest.common.constant.ProblemConstant;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.common.validator.Assert;
import com.imapcloud.nest.enums.ProblemStatusEnum;
import com.imapcloud.nest.mapper.StationIdentifyRecordMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TaskPhotoDTO;
import com.imapcloud.nest.pojo.dto.reqDto.StationIdentifyDefectReqDto;
import com.imapcloud.nest.pojo.dto.respDto.StationIdentifyDefectAIListRespDto;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.ExcelUtil;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.manager.nacos.NacosConfigurationService;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 * 变电站的设备出现的缺陷记录 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-12-29
 */
@Service
@Slf4j
public class StationIdentifyRecordServiceImpl extends ServiceImpl<StationIdentifyRecordMapper, StationIdentifyRecordEntity> implements StationIdentifyRecordService {


    @Autowired
    private NestService nestService;

    @Autowired
    private CommonNestStateService commonNestStateService;


    @Override
    public IPage<StationIdentifyPhotoDTO> getPhotoListPage(IPage<StationIdentifyPhotoDTO> page, Integer tagId, String photoName,String missionRecordId,Integer defectStatus) {
        return baseMapper.getPhotoListPage(page, tagId, photoName,missionRecordId,defectStatus);
    }

    @Override
    public List<StationDevicePhotoDTO> getAllPhotoMeterNum(Integer tagId, String photoName, String startTime, String endTime) {
        return baseMapper.getAllPhotoMeterNum(tagId, photoName, startTime, endTime);
    }


    public Double getTemperature(Integer nestId) {
        //初始化机巢
//        nestService.initNest(nestId);

        String nestUuid = nestService.getUuidById(nestId);
        // 哈尔滨机巢的气象信息
        AerographyInfoDto aerographyInfoDto = commonNestStateService.getAerographyInfoDto(nestUuid);
        Double temperature = aerographyInfoDto.getTemperature();
        for (int i = 1; i < 10; i++) {
            if (temperature == 0.0) {
                log.info("重试次数： " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                temperature = commonNestStateService.getAerographyInfoDto(nestUuid).getTemperature();
            }
        }

        Double value = 0.02 * temperature + 1;
        return value;
    }


}
