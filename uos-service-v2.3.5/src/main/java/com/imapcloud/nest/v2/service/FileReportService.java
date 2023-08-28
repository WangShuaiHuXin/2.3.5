package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.FileReportInDTO;
import com.imapcloud.nest.v2.service.dto.out.FileReportOutDTO;

import java.util.List;

/**
 * 文件服务
 *
 * @author boluo
 * @date 2022-11-02
 */
public interface FileReportService {
    /**
     * 指定单位使用的存储空间
     *
     * @param orgCode 组织代码
     * @return {@link FileReportOutDTO.FileInfoOutDTO}
     */
    FileReportOutDTO.FileInfoOutDTO fileTotal(String orgCode);

    /**
     * 单位使用存储空间的趋势
     *
     * @param orgCode 组织代码
     * @param time    时间
     * @param nestId  巢id
     * @return {@link List}<{@link FileReportOutDTO.FileTrendOutDTO}>
     */
    List<FileReportOutDTO.FileTrendOutDTO> trend(String orgCode, String time, String nestId);

    /**
     * 列表
     *
     * @param fileListInDTO 在dto文件列表
     * @return {@link List}<{@link FileReportOutDTO.FileListOutDTO}>
     */
    PageResultInfo<FileReportOutDTO.FileListOutDTO> list(FileReportInDTO.FileListInDTO fileListInDTO);
}
