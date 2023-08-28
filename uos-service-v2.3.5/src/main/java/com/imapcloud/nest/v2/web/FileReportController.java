package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.service.FileReportService;
import com.imapcloud.nest.v2.service.dto.in.FileReportInDTO;
import com.imapcloud.nest.v2.service.dto.out.FileReportOutDTO;
import com.imapcloud.nest.v2.web.vo.req.FileReportReqVO;
import com.imapcloud.nest.v2.web.vo.resp.FileReportRespVO;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 文件报表
 *
 * @author boluo
 * @date 2022-11-02
 */
@RequestMapping("v2/report/store/")
@RestController
public class FileReportController {

    @Resource
    private FileReportService fileReportService;

    @GetMapping("fileTotal/{orgCode}")
    public Result<Object> fileTotal(@PathVariable("orgCode") String orgCode) {

        FileReportOutDTO.FileInfoOutDTO fileInfoOutDTO = fileReportService.fileTotal(orgCode);

        FileReportRespVO.FileTotalRespVO fileTotalRespVO = new FileReportRespVO.FileTotalRespVO();
        BeanUtils.copyProperties(fileInfoOutDTO, fileTotalRespVO);
        return Result.ok(fileTotalRespVO);
    }

    @GetMapping("trend/{orgCode}")
    public Result<Object> trend(@PathVariable("orgCode") String orgCode, @Valid FileReportReqVO.FileTrendReqVO fileTrendReqVO) {

        List<FileReportOutDTO.FileTrendOutDTO> fileTrendOutDTOList = fileReportService.trend(orgCode, fileTrendReqVO.getTime(), fileTrendReqVO.getNestId());

        List<FileReportRespVO.FileTrendRespVO> fileTrendRespVOList = Lists.newLinkedList();
        for (FileReportOutDTO.FileTrendOutDTO fileTrendOutDTO : fileTrendOutDTOList) {
            FileReportRespVO.FileTrendRespVO fileTrendRespVO = new FileReportRespVO.FileTrendRespVO();
            BeanUtils.copyProperties(fileTrendOutDTO, fileTrendRespVO);
            fileTrendRespVOList.add(fileTrendRespVO);
        }
        return Result.ok(fileTrendRespVOList);
    }

    @GetMapping("list")
    public Result<Object> list(@Valid FileReportReqVO.FileListReqVO fileListReqVO) {

        FileReportInDTO.FileListInDTO fileListInDTO = new FileReportInDTO.FileListInDTO();
        BeanUtils.copyProperties(fileListReqVO, fileListInDTO);
        PageResultInfo<FileReportOutDTO.FileListOutDTO> resultInfo = fileReportService.list(fileListInDTO);

        List<FileReportRespVO.FileListRespVO> fileTrendOutDTOList = Lists.newLinkedList();
        for (FileReportOutDTO.FileListOutDTO fileListOutDTO : resultInfo.getRecords()) {
            FileReportRespVO.FileListRespVO fileListRespVO = new FileReportRespVO.FileListRespVO();
            BeanUtils.copyProperties(fileListOutDTO, fileListRespVO);
            fileTrendOutDTOList.add(fileListRespVO);
        }
        return Result.ok(PageResultInfo.of(resultInfo.getTotal(), fileTrendOutDTOList));
    }
}
