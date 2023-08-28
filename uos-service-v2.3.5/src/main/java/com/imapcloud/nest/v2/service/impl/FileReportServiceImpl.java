package com.imapcloud.nest.v2.service.impl;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.util.DateUtils;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.dataobj.MinioDO;
import com.imapcloud.nest.v2.manager.dataobj.in.FileReportInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileReportInfoOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.nest.v2.manager.sql.FileReportInfoManager;
import com.imapcloud.nest.v2.service.FileReportService;
import com.imapcloud.nest.v2.service.dto.in.FileReportInDTO;
import com.imapcloud.nest.v2.service.dto.out.FileReportOutDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件服务实现
 *
 * @author boluo
 * @date 2022-11-02
 */
@Service
public class FileReportServiceImpl implements FileReportService {

    @Resource
    private FileReportInfoManager fileReportInfoManager;

    @Resource
    private BaseNestManager baseNestManager;

    @Override
    public FileReportOutDTO.FileInfoOutDTO fileTotal(String orgCode) {

        FileReportInfoOutDO.FileOutDO fileOutDO = fileReportInfoManager.queryFileReportByOrgCode(orgCode);
        FileReportOutDTO.FileInfoOutDTO fileInfoOutDTO = new FileReportOutDTO.FileInfoOutDTO();
        fileInfoOutDTO.setPicture(fileOutDO.getPictureSize());
        fileInfoOutDTO.setVideo(fileOutDO.getVideoSize());
        fileInfoOutDTO.setVideoPicture(fileOutDO.getVideoPictureSize());
        fileInfoOutDTO.setTotal(fileOutDO.getTotalSize());
        return fileInfoOutDTO;
    }

    @Override
    public List<FileReportOutDTO.FileTrendOutDTO> trend(String orgCode, String time, String nestId) {

        List<FileReportOutDTO.FileTrendOutDTO> fileTrendOutDTOList = Lists.newLinkedList();
        List<String> timeList = Lists.newLinkedList();
        int type;
        if (time.length() == 4) {
            type = 1;
            // 按月查询 查询12个月
            for (int i = 1; i < 13; i++) {
                if (i < 10) {
                    timeList.add(String.format("%s-0%s", time, i));
                } else {
                    timeList.add(String.format("%s-%s", time, i));
                }
            }


        } else if (time.length() == 7) {
            type = 0;
            // 按日查询
            LocalDate localDate = DateUtils.toLocalDate(String.format("%s-01", time), DateUtils.DATE_FORMATTER_OF_CN);
            LocalDate plusMonths = localDate.plusMonths(1);

            while (localDate.isBefore(plusMonths)) {
                timeList.add(localDate.format(DateUtils.DATE_FORMATTER_OF_CN));
                localDate = localDate.plusDays(1);
            }
        } else {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FILEREPORTSERVICEIMPL_01.getContent()));
        }
        List<FileReportInfoOutDO.FileTrendOutDO> fileTrendOutDOList = fileReportInfoManager.trend(orgCode, nestId, timeList, type);
        Map<String, FileReportInfoOutDO.FileTrendOutDO> fileTrendOutDOMap = fileTrendOutDOList.stream()
                .collect(Collectors.toMap(FileReportInfoOutDO.FileTrendOutDO::getTime, bean -> bean, (key1, key2) -> key1));

        for (String str : timeList) {

            FileReportOutDTO.FileTrendOutDTO fileTrendOutDTO = new FileReportOutDTO.FileTrendOutDTO();

            FileReportInfoOutDO.FileTrendOutDO fileTrendOutDO = fileTrendOutDOMap.get(str);
            if (fileTrendOutDO != null) {
                fileTrendOutDTO.setTime(fileTrendOutDO.getTime());
                fileTrendOutDTO.setPicture(fileTrendOutDO.getPictureSize());
                fileTrendOutDTO.setVideo(fileTrendOutDO.getVideoSize());
                fileTrendOutDTO.setVideoPicture(fileTrendOutDO.getVideoPictureSize());
                fileTrendOutDTO.setTotal(fileTrendOutDO.getTotalSize());
            } else {
                fileTrendOutDTO.setTime(str);
                fileTrendOutDTO.setPicture(0L);
                fileTrendOutDTO.setVideo(0L);
                fileTrendOutDTO.setVideoPicture(0L);
                fileTrendOutDTO.setTotal(0L);
            }
            fileTrendOutDTOList.add(fileTrendOutDTO);
        }
        return fileTrendOutDTOList;
    }

    @Override
    public PageResultInfo<FileReportOutDTO.FileListOutDTO> list(FileReportInDTO.FileListInDTO fileListInDTO) {

        FileReportInfoInDO.ListInDO listInDO = new FileReportInfoInDO.ListInDO();
        BeanUtils.copyProperties(fileListInDTO, listInDO);
        // 查询总数
        long listCount = fileReportInfoManager.listCount(listInDO);
        if (listCount == 0) {
            return PageResultInfo.of(0, Collections.emptyList());
        }
        List<FileReportInfoOutDO.ListOutDO> outDOList = fileReportInfoManager.list(listInDO);
        // 查询基站信息
        List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectListByNestIdList(outDOList.stream().map(FileReportInfoOutDO.ListOutDO::getNestId).collect(Collectors.toList()));
        Map<String, String> stringStringMap = baseNestEntityOutDOList.stream()
                .collect(Collectors.toMap(BaseNestOutDO.BaseNestEntityOutDO::getNestId, BaseNestOutDO.BaseNestEntityOutDO::getName, (key1, key2) -> key1));

        List<FileReportOutDTO.FileListOutDTO> fileListOutDTOList = Lists.newLinkedList();
        for (FileReportInfoOutDO.ListOutDO listOutDO : outDOList) {
            stringStringMap.get(listOutDO.getNestId());
            FileReportOutDTO.FileListOutDTO fileListOutDTO = new FileReportOutDTO.FileListOutDTO();
            fileListOutDTO.setNestId(listOutDO.getNestId());
            fileListOutDTO.setNestName(MinioDO.getNestName(listOutDO.getNestId(), stringStringMap));
            fileListOutDTO.setPicture(listOutDO.getPictureSize());
            fileListOutDTO.setVideo(listOutDO.getVideoSize());
            fileListOutDTO.setVideoPicture(listOutDO.getVideoPictureSize());
            fileListOutDTO.setTotal(listOutDO.getTotalSize());
            fileListOutDTOList.add(fileListOutDTO);
        }
        return PageResultInfo.of(listCount, fileListOutDTOList);
    }
}
