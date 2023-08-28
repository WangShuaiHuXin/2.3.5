package com.imapcloud.nest.v2.web;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.DataEquipmentPointService;
import com.imapcloud.nest.v2.service.dto.in.DataEquipmentPointInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataEquipmentPointQueryInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataEquipmentPointListOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataEquipmentPointQueryOutDTO;
import com.imapcloud.nest.v2.web.vo.req.DataEquipmentPointQueryReqVO;
import com.imapcloud.nest.v2.web.vo.req.DataEquipmentPointReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataEquipmentPointListRespVO;
import com.imapcloud.nest.v2.web.vo.resp.DataEquipmentPointQueryRespVO;
import io.swagger.annotations.Api;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApiSupport(author = "chenjiahong@geoai.com", order = 1)
@Api(value = "数据管理-设备点", tags = "数据管理-设备点")
@RequestMapping("v2/data/equipment")
@RestController
public class DataEquipmentPointController {

    @Resource
    private DataEquipmentPointService dataEquipmentPointService;

    @PostMapping("/point/add")
    public Result addEquipmentPoint(@RequestBody DataEquipmentPointReqVO reqVo) {
        DataEquipmentPointInDTO dto = new DataEquipmentPointInDTO();
        BeanUtils.copyProperties(reqVo, dto);
        dto.setEquipmentList(reqVo.getEquipmentList());
        boolean flag = dataEquipmentPointService.addEquipmentPoint(dto);
        if (flag) {
            return Result.ok();
        }
        return Result.error("执行失败");
    }

    @PostMapping("/point/edit")
    public Result editEquipmentPoint(@RequestBody DataEquipmentPointReqVO reqVo) {
        DataEquipmentPointInDTO dto = new DataEquipmentPointInDTO();
        BeanUtils.copyProperties(reqVo, dto);
        dto.setEquipmentList(reqVo.getEquipmentList());
        boolean flag = dataEquipmentPointService.editEquipmentPoint(dto);
        if (flag) {
            return Result.ok();
        }
        return Result.error("执行失败");
    }

    @DeleteMapping("/point/deleteBatch")
    public Result deleteBatchEquipmentPoint(@RequestBody List<String> deletes) {
        boolean flag = dataEquipmentPointService.deleteBatchEquipmentPoint(deletes);
        if (flag) {
            return Result.ok();
        }
        return Result.error("执行失败");
    }

    @GetMapping("/point/{orgCode}/query")
    public Result<PageResultInfo<DataEquipmentPointQueryRespVO>> queryPageEquipmentPoint(@PathVariable String orgCode, DataEquipmentPointQueryReqVO reqVO) {
        DataEquipmentPointQueryInDTO inDto = new DataEquipmentPointQueryInDTO();
        BeanUtils.copyProperties(reqVO, inDto);
        inDto.setOrgCode(orgCode);
        DataEquipmentPointQueryOutDTO outDTO = dataEquipmentPointService.queryPageEquipmentPoint(inDto);
        List<DataEquipmentPointQueryRespVO> result = new ArrayList<>();
        if (outDTO.getTotal() > 0) {
            result = outDTO.getDtos().stream().map(e -> {
                DataEquipmentPointQueryRespVO vo = new DataEquipmentPointQueryRespVO();
                BeanUtils.copyProperties(e, vo);
                List<DataEquipmentPointQueryOutDTO.DataEquipmentQueryInfoDTO> equipmentList = e.getEquipmentList();
                if (CollectionUtil.isNotEmpty(equipmentList)) {
                    List<DataEquipmentPointQueryRespVO.DataEquipmentInfo> collect = equipmentList.stream().map(item -> {
                        DataEquipmentPointQueryRespVO.DataEquipmentInfo info = new DataEquipmentPointQueryRespVO.DataEquipmentInfo();
                        info.setEquipmentId(item.getEquipmentId());
                        info.setEquipmentName(item.getEquipmentName());
                        return info;
                    }).collect(Collectors.toList());
                    vo.setEquipmentList(collect);
                }
                return vo;
            }).collect(Collectors.toList());
        }
        return Result.ok(PageResultInfo.of(outDTO.getTotal(), result));
    }

    @GetMapping("/point/{orgCode}/queryEquip/list")
    public Result<List<DataEquipmentPointListRespVO>> queryEquipPointList(@PathVariable String orgCode) {
        List<DataEquipmentPointListOutDTO> dto = dataEquipmentPointService.queryEquipPointList(orgCode);
        List<DataEquipmentPointListRespVO> collect = dto.stream().map(e -> {
            DataEquipmentPointListRespVO vo = new DataEquipmentPointListRespVO();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(collect);
    }
}
