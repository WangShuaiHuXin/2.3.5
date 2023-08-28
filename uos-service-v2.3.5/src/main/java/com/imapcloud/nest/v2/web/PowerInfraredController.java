package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.service.PowerInfraredService;
import com.imapcloud.nest.v2.service.dto.in.PowerInfraredInDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerInfraredOutDTO;
import com.imapcloud.nest.v2.web.vo.req.PowerInfraredReqVO;
import com.imapcloud.nest.v2.web.vo.resp.PowerInfraredRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 功率红外控制器
 *
 * @author boluo
 * @date 2022-12-28
 */
@RequestMapping("v2/power/infrared/")
@RestController
@Slf4j
public class PowerInfraredController {

    @Resource
    private PowerInfraredService powerInfraredService;

    @PostMapping("picture/get")
    public Result<Object> pictureGet(@Valid @RequestBody PowerInfraredReqVO.PictureReqVO pictureReqVO) {

        PowerInfraredInDTO.PictureInDTO powerInfraredInDTO = new PowerInfraredInDTO.PictureInDTO();
        BeanUtils.copyProperties(pictureReqVO, powerInfraredInDTO);
        PowerInfraredOutDTO.PictureOutDTO pictureOutDTO = powerInfraredService.pictureGet(powerInfraredInDTO);
        PowerInfraredRespVO.PictureRespVO pictureRespVO = new PowerInfraredRespVO.PictureRespVO();
        BeanUtils.copyProperties(pictureOutDTO, pictureRespVO);
        return Result.ok(pictureRespVO);
    }

    @PostMapping("save")
    public Result<Object> save(@Valid @RequestBody PowerInfraredReqVO.SaveReqVO saveReqVO) {

        PowerInfraredInDTO.SaveInDTO saveInDTO = new PowerInfraredInDTO.SaveInDTO();
        BeanUtils.copyProperties(saveReqVO, saveInDTO);
        saveInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        return Result.ok(powerInfraredService.save(saveInDTO));
    }

    @PostMapping("delete/{infraredRecordId}")
    public Result<Object> save(@PathVariable String infraredRecordId) {

        powerInfraredService.delete(infraredRecordId);
        return Result.ok();
    }
}
