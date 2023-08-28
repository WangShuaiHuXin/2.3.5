package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.service.PowerDefectService;
import com.imapcloud.nest.v2.service.dto.in.PowerDefectInDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerDefectOutDTO;
import com.imapcloud.nest.v2.web.vo.req.PowerDefectReqVO;
import com.imapcloud.nest.v2.web.vo.resp.PowerDefectRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 缺陷识别
 *
 * @author boluo
 * @date 2023-03-07
 */
@RequestMapping("v2/power/defect/")
@RestController
@Slf4j
public class PowerDefectController {

    @Resource
    private PowerDefectService powerDefectService;

    @GetMapping("list/{dataId}")
    public Result<PageResultInfo<PowerDefectRespVO.ListRespVO>> list(@PathVariable String dataId, PowerDefectReqVO.ListReqVO listReqVO) {

        PowerDefectInDTO.ListInDTO listInDTO = new PowerDefectInDTO.ListInDTO();
        BeanUtils.copyProperties(listReqVO, listInDTO);
        listInDTO.setVerificationStatus(listReqVO.getVerificationState());
        listInDTO.setDataId(dataId);

        PageResultInfo<PowerDefectOutDTO.ListOutDTO> pageResultInfo = powerDefectService.list(listInDTO);

        List<PowerDefectRespVO.ListRespVO> respVOList = Lists.newLinkedList();
        for (PowerDefectOutDTO.ListOutDTO record : pageResultInfo.getRecords()) {
            PowerDefectRespVO.ListRespVO listRespVO = new PowerDefectRespVO.ListRespVO();
            BeanUtils.copyProperties(record, listRespVO);
            respVOList.add(listRespVO);
        }
        return Result.ok(PageResultInfo.of(pageResultInfo.getTotal(), respVOList));
    }

    @GetMapping("detail/{detailId}")
    public Result<PowerDefectRespVO.DetailRespVO> detail(@PathVariable String detailId) {

        PowerDefectOutDTO.DetailOutDTO detailOutDTO = powerDefectService.detail(detailId);
        PowerDefectRespVO.DetailRespVO detailRespVO = new PowerDefectRespVO.DetailRespVO();
        BeanUtils.copyProperties(detailOutDTO, detailRespVO);

        return Result.ok(detailRespVO);
    }

    @PostMapping("/{detailId}/mark/edit")
    public Result<Object> editMark(@PathVariable String detailId,
                                   @Valid @RequestBody PowerDefectReqVO.AddMarkReqVO addMarkReqVO) {

        PowerDefectInDTO.AddMarkInDTO addMarkInDTO = new PowerDefectInDTO.AddMarkInDTO();
        BeanUtils.copyProperties(addMarkReqVO, addMarkInDTO);
        addMarkInDTO.setDetailId(detailId);
        addMarkInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        return Result.ok(powerDefectService.addMark(addMarkInDTO));
    }

    @DeleteMapping("/mark/delete")
    public Result<Object> deleteMark(@Valid @RequestBody List<String> defectMarkIdList) {

        String accountId = TrustedAccessTracerHolder.get().getAccountId();
        powerDefectService.deleteMark(defectMarkIdList, accountId);
        return Result.ok();
    }

    @DeleteMapping("/delete")
    public Result<Object> delete(@Valid @RequestBody List<String> detailIdList) {

        String accountId = TrustedAccessTracerHolder.get().getAccountId();
        powerDefectService.delete(detailIdList, accountId);
        return Result.ok();
    }

    @GetMapping("/statistics/{dataId}")
    public Result<Object> statistics(@PathVariable String dataId) {

        return Result.ok(powerDefectService.statistics(dataId));
    }
}
