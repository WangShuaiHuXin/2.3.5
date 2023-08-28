package com.imapcloud.nest.v2.web.download.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.bytearray.ByteArrayImageConverter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.utils.excel.EasyExcelStyleUtils;
import com.imapcloud.nest.utils.excel.InspectionReportStyleStrategy;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.*;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.sql.PowerInspectionReportManager;
import com.imapcloud.nest.v2.service.PowerInspectionService;
import com.imapcloud.nest.v2.service.dto.in.InspectionQueryPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.InspectionQueryPageOutDTO;
import com.imapcloud.nest.v2.web.download.AbstractDownloadHandler;
import com.imapcloud.nest.v2.web.download.DownloadHandler;
import com.imapcloud.nest.v2.web.download.HandlerIn;
import com.imapcloud.nest.v2.web.vo.req.PowerInspectionExportReqVO;
import com.imapcloud.nest.v2.web.vo.resp.InspectionExportRespVO;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 巡检报告-批量导出
 *
 * @author boluo
 * @date 2023-05-08
 */
@Slf4j
@Component
@DownloadAnnotation(key = "download_power_inspection")
public class PowerInspectionDownloadHandler extends AbstractDownloadHandler implements DownloadHandler {

    @Resource
    private PowerInspectionService powerInspectionService;

    @Resource
    private PowerInspectionReportManager powerInspectionReportManager;

    @Resource
    private FileManager fileManager;

    @Resource
    private RedisService redisService;

    private PowerInspectionExportReqVO getPowerInspectionExportReqVO(String param) {

        return JSONUtil.toBean(param, PowerInspectionExportReqVO.class);
    }

    @Override
    protected boolean checkParam(HandlerIn handlerIn) {

        PowerInspectionExportReqVO powerInspectionExportReqVO = getPowerInspectionExportReqVO(handlerIn.getParam());
        return !StringUtils.isEmpty(powerInspectionExportReqVO.getItems());
    }

    @Override
    protected RedisService getRedisService() {
        return redisService;
    }

    @Override
    protected boolean check(HandlerIn handlerIn) {
        PowerInspectionExportReqVO powerInspectionExportReqVO = getPowerInspectionExportReqVO(handlerIn.getParam());

        if (StringUtil.isEmpty(powerInspectionExportReqVO.getIds())) {
            return true;
        }
        List<String> list = Lists.newLinkedList();
        String[] split = powerInspectionExportReqVO.getIds().split(",");
        list.addAll(Arrays.asList(split));

        int selectNum = powerInspectionReportManager.selectNum(list, handlerIn.getOrgCode());
        return selectNum == list.size();
    }

    private String getFileName(HandlerIn handlerIn) {
        return String.format("%s_%s.xlsx", MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_009
                .getContent()).trim(), System.currentTimeMillis());
    }

    private List<List<String>> getHead(HandlerIn handlerIn) {
        PowerInspectionExportReqVO powerInspectionExportReqVO = getPowerInspectionExportReqVO(handlerIn.getParam());
        List<List<String>> result = new ArrayList<>();

        String[] itemSplit = powerInspectionExportReqVO.getItems().split(",");
        List<String> itemNos = Arrays.asList(itemSplit);
        itemNos.forEach(item -> {
            List<String> head = new ArrayList<>();
            head.add(PowerInspectionExcelItemEnum.getValueById(item));
            result.add(head);
        });
        return result;
    }

    private List<List<Object>> getData(HandlerIn handlerIn, List<List<String>> head) throws Exception {
        PowerInspectionExportReqVO powerInspectionExportReqVO = getPowerInspectionExportReqVO(handlerIn.getParam());

        List<InspectionExportRespVO> respVOS = new ArrayList<>();

        String ids = powerInspectionExportReqVO.getIds();

        if (!StringUtils.hasText(ids)) {
            return Collections.emptyList();
        }

        String[] split = ids.split(",");
        ArrayList<String> arrayList = Lists.newArrayList(split);

        InspectionQueryPageInDTO inDTO = new InspectionQueryPageInDTO();
        inDTO.setIds(arrayList);
        InspectionQueryPageOutDTO dto = powerInspectionService.inspectionQueryPage(inDTO);
        List<InspectionQueryPageOutDTO.InspectionQueryPageOutInfo> infoList = dto.getInfoList();
        if (CollUtil.isEmpty(infoList)) {
            return Collections.emptyList();
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String[] itemSplit = powerInspectionExportReqVO.getItems().split(",");
        List<String> itemNos = Arrays.asList(itemSplit);

        for (InspectionQueryPageOutDTO.InspectionQueryPageOutInfo info : infoList) {
            InspectionExportRespVO vo = new InspectionExportRespVO();
            if (itemNos.contains(PowerInspectionExcelItemEnum.ANA_SCREENSHOTS.getCode().toString())) {
                try(InputStream inputStream = fileManager.getInputSteam(info.getScreenShootUrl())){
                    byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
                    vo.setMeterImg(bytes);
                }
            }
            if (itemNos.contains(PowerInspectionExcelItemEnum.PATROL_PHOTOS.getCode().toString())) {
                try (InputStream inputStream = fileManager.getInputSteam(info.getThumbnailUrl())) {
                    byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
                    vo.setInspectionImg(bytes);
                }
            }
            vo.setComponenName(info.getComponentName());
            vo.setEquipmentName(info.getEquipmentName());
            vo.setInspectionType(PowerDsicernTypesEnum.getValueByCode(info.getAnalysisType()));
            StringBuffer buffer = new StringBuffer();
            List<InspectionQueryPageOutDTO.ReadingInfo> readingInfos = info.getReadingInfos();
            if (CollectionUtil.isNotEmpty(readingInfos)) {
                readingInfos.forEach(item -> {
                    if (StringUtils.isEmpty(item.getValue())) {
                        buffer.append(item.getKey());
                    } else {
                        buffer.append(ObjectUtils.isNotEmpty(item.getKey()) ? item.getKey() + " :" : " " + " :");
                        buffer.append(item.getValue() + " ");
                    }
                });
                vo.setInspectionResult(buffer.toString());
            }
            if (StringUtils.hasText(info.getAlarmReson())) {
                vo.setInspectionConclusion(PowerDeviceStateEnum.getValueByCode(info.getAnalysisConclusion()) + "(" + info.getAlarmReson() + ")");
            } else {
                vo.setInspectionConclusion(PowerDeviceStateEnum.getValueByCode(info.getAnalysisConclusion()));
            }
            vo.setEquipmentType(info.getEquipmentType());
            vo.setSpacUnit(info.getSpacUnit());
            vo.setVoltageLevel(info.getVoltageLevel());
            vo.setPhotoGraphyTime(fmt.format(info.getPhotographyTime()));
            respVOS.add(vo);
        }
        return listData(respVOS, InspectionExportRespVO.class, head);
    }

    private List<List<Object>> listData(List<InspectionExportRespVO> vos, Class<InspectionExportRespVO> clzz, List<List<String>> headers) {
        List<List<Object>> object = new ArrayList<>();
        for (InspectionExportRespVO vo : vos) {
            List<Object> objects = new ArrayList<>();
            for (List<String> header : headers) {
                //获取每个字段
                Field[] declaredFields = clzz.getDeclaredFields();
                //遍历字段上的注解判断名称是否可表头名称一致
                for (Field declaredField : declaredFields) {
                    ExcelProperty annotation = declaredField.getAnnotation(ExcelProperty.class);
                    log.info("#PowerInspectionDownloadHandler.listData# header.get(0),{}", header.get(0));
                    log.info("#PowerInspectionDownloadHandler.listData# annotation.value(),{}", annotation.value());
                    if (annotation.value()[0].equals(header.get(0))) {
                        try {
                            PropertyDescriptor pd = new PropertyDescriptor(declaredField.getName(), clzz);
                            Method getMethod = pd.getReadMethod();
                            Object invoke = getMethod.invoke(vo);
                            if (ObjectUtils.isEmpty(invoke)) {
                                objects.add("");
                            } else {
                                objects.add(invoke);
                            }
                        } catch (Exception e) {
                            log.error("#PowerInspectionDownloadHandler.listData# error:", e);
                        }
                    }
                }
            }
            object.add(objects);
        }
        return object;
    }

    @Override
    protected void realExportImpl(HandlerIn handlerIn, HttpServletResponse response) throws Exception {

        String fileName = getFileName(handlerIn);

        List<List<String>> head = getHead(handlerIn);

        List<List<Object>> lists = getData(handlerIn, head);

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel");

        ExcelWriterBuilder write = EasyExcel.write(response.getOutputStream());
        ExcelWriterSheetBuilder sheet = write.sheet();
        ArrayList<WriteHandler> writeHandlers = Lists.newArrayList(new InspectionReportStyleStrategy.WidthStyleStrategy(),
                EasyExcelStyleUtils.getStyleStrategy(),
                new InspectionReportStyleStrategy.RowHeightStyleStrategy());
        for (WriteHandler writeHandler : writeHandlers) {
            sheet.registerWriteHandler(writeHandler);
        }
        sheet.registerConverter(new ByteArrayImageConverter());
        sheet.sheetName(fileName);
        sheet.head(head);
        sheet.doWrite(lists);
    }
}
