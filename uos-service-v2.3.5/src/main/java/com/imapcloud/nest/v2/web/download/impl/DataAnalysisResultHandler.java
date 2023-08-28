package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.util.StringUtils;
import com.geoai.common.core.util.DateUtils;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.utils.excel.EasyExcelUtils;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.config.DataAnalysisConfig;
import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.service.DataAnalysisResultGroupService;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisResultGroupPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisResultGroupOutDTO;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 综合分析 问题统计-导出清单
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "data_analysis_result")
public class DataAnalysisResultHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private RedisService redisService;

    @Resource
    private DataAnalysisConfig dataAnalysisConfig;

    @Resource
    private DataAnalysisResultGroupService dataAnalysisResultGroupService;

    @Resource
    private FileManager fileManager;

    /**
     * 问题组下最多导出10列
     */
    private static final int MAX_COLUMN = 10;

    @Override
    protected boolean check(HandlerIn handlerIn) {

        Bean bean = toBean(handlerIn.getParam());
        String[] resultIds = bean.problemResultList.split(",");
        List<String> list = Arrays.asList(resultIds);
        String orgCode = handlerIn.getOrgCode();
        int num = dataAnalysisResultGroupService.selectNum(list, orgCode);
        return num == list.size();
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {
        String param = handlerIn.getParam();
        if (StringUtils.isEmpty(param)) {
            return false;
        }
        Bean bean = toBean(handlerIn.getParam());
        String resultIdList = bean.problemResultList;
        if (StringUtils.isEmpty(resultIdList)) {
            return false;
        }
        return true;
    }

    private Bean toBean(String param) {
        return JSONUtil.toBean(param, Bean.class);
    }

    @Data
    private static class Bean {
        private String problemResultList;
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {

        Bean bean = toBean(handlerIn.getParam());
        String resultIdList = bean.getProblemResultList();
        String redisKey = RedisKeyConstantList.DATA_ANALYSIS_RESULT_GROUP_EXPORT;
        try {
            long increment = redisService.incr(redisKey);
            log.info("#DataAnalysisResultHandler.realExportImpl# increment={}", increment);
            if (increment > dataAnalysisConfig.getMaxExportNum()) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TOO_MANY_PEOPLE_EXPORTED.getContent()));
            }

            List<String> problemResultList = new ArrayList<>(Arrays.asList(resultIdList.split(",")));
            if (problemResultList.size() > 20) {
                throw new BusinessException("MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_UP_TO_20_PIECES_OF_DATA.getContent())");
            }

            DataAnalysisResultGroupOutDTO.ResultGroupExportResultOutDTO dto = dataAnalysisResultGroupService.resultGroupExportList(problemResultList);
            Integer maxImageNum = dto.getMaxImageNum();
            List<DataAnalysisResultGroupOutDTO.ResultGroupExportOutDTO> resultList = dto.getResultList();

            String fileName = String.format("分析统计-问题列表-%s", LocalDateTime.now().format(DateUtils.DATE_TIME_FORMATTER));
            List<List<String>> head = head(maxImageNum);
            List<List<Object>> dataList = dataList(resultList);

            // 问题列表导出
            EasyExcelUtils.exportResultGroup(response, dataList, head, fileName);
        } finally {
            redisService.decr(redisKey);
        }
    }

    private List<List<String>> head(int maxImageNums) {
        List<List<String>> list = ListUtils.newArrayList();
        List<String> head01 = ListUtils.newArrayList();
        head01.add(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_INDUSTRY.getContent()));
        List<String> head02 = ListUtils.newArrayList();
        head02.add(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_QUESTION.getContent()));
        List<String> head0 = ListUtils.newArrayList();
        head0.add(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_NUMBER_OF_DISCOVERIES.getContent()));
        List<String> head1 = ListUtils.newArrayList();
        head1.add(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_FIRST_SHOT.getContent()));
        List<String> head2 = ListUtils.newArrayList();
        head2.add(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_LATEST_SHOT.getContent()));
        List<String> head3 = ListUtils.newArrayList();
        head3.add(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_ADDRESS.getContent()));
        List<String> head4 = ListUtils.newArrayList();
        head4.add(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_LOCATION.getContent()));
        List<String> head5 = ListUtils.newArrayList();
        head5.add(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_LONGITUDE_AND_LATITUDE.getContent()));
        List<String> head6 = ListUtils.newArrayList();
        head6.add(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_MAP.getContent()));
        List<String> head7 = ListUtils.newArrayList();
        head7.add(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_TAG.getContent()));
        List<String> head8 = ListUtils.newArrayList();
        head8.add(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_TASK.getContent()));
        List<String> head9 = ListUtils.newArrayList();
        head9.add(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_ORG.getContent()));

        list.add(head01);
        list.add(head02);
        list.add(head0);
        list.add(head1);
        list.add(head2);
        list.add(head3);
        list.add(head4);
        list.add(head5);
        list.add(head6);
        list.add(head7);
        list.add(head8);
        list.add(head9);

        maxImageNums = Math.min(maxImageNums, MAX_COLUMN);
        for (int i = 1; i <= maxImageNums; i++) {
            List<String> head = ListUtils.newArrayList();
            head.add(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXPORT_IMAGE.getContent()) + i);
            list.add(head);
        }

        return list;
    }

    /**
     * 获取问题统计列表数据
     */
    private List<List<Object>> dataList(List<DataAnalysisResultGroupOutDTO.ResultGroupExportOutDTO> resultList) {
        List<List<Object>> list = ListUtils.newArrayList();
        for (DataAnalysisResultGroupOutDTO.ResultGroupExportOutDTO result : resultList) {
            List<Object> data = ListUtils.newArrayList();
            data.add(result.getTopicIndustryName());
            data.add(result.getTopicProblemName());
            data.add(result.getFoundNums());
            data.add(result.getEarliestTime());
            data.add(result.getLatestTime());
            data.add(result.getAddr());
            data.add(result.getUrlAddr());
            data.add(result.getLngAndLat());
            data.add(getImageData(result.getAddrImagePath()));
            data.add(result.getTagName());
            data.add(result.getTaskName());
            data.add(result.getOrgName());
            List<String> resultImages = result.getResultImages();
            int size = Math.min(resultImages.size(), MAX_COLUMN);
            for (int i = 0; i < size; i++) {
                data.add(getImageData(resultImages.get(i)));
            }
            list.add(data);
        }

        return list;
    }


    private byte[] getImageData(String url) {
        try (InputStream inputStream = fileManager.getInputSteam(url);) {
            if (inputStream == null) {
                return new byte[0];
            }
            return IoUtil.readBytes(inputStream);
        } catch (IORuntimeException | IOException e) {
            log.error("#DataAnalysisResultController.getImageByte#", e);
        }
        return new byte[0];
    }
}
