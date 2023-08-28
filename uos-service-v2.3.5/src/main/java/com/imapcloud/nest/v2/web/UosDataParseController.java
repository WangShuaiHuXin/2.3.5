package com.imapcloud.nest.v2.web;

import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.DataParseService;
import com.imapcloud.nest.v2.service.dto.in.VideoSrtInDTO;
import com.imapcloud.nest.v2.service.dto.out.FpiAirlinePackageParseOutDTO;
import com.imapcloud.nest.v2.web.transformer.DataParseTransformer;
import com.imapcloud.nest.v2.web.vo.req.FpiAirlinePackageReqVO;
import com.imapcloud.nest.v2.web.vo.req.VideoSrtReqVO;
import com.imapcloud.nest.v2.web.vo.resp.FpiAirlinePackageParseRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * UOS业务数据包解析API
 * @author Vastfy
 * @date 2023/02/21 09:11
 * @since 2.2.3
 */
@ApiSupport(author = "wumiao@geoai.com", order = 12)
@Api(value = "UOS业务数据解析API", tags = "UOS业务数据解析API")
@RequestMapping("v2/data")
@RestController
@Slf4j
public class UosDataParseController {

    @Resource
    private DataParseService dataParseService;

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "解析精细巡检数据包", notes = "精细巡检数据包不会进行保存")
    @PostMapping(value = "parse/fpi", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<FpiAirlinePackageParseRespVO> parseFinePatrolInspectionData(@Validated FpiAirlinePackageReqVO params){
        try {
            FpiAirlinePackageParseOutDTO outDTO = dataParseService.parseFinePatrolInspectionData(params.getFile().getOriginalFilename(), params.getFile().getInputStream());
            return Result.ok(DataParseTransformer.INSTANCE.transform(outDTO));
        } catch (IOException e) {
            log.error("精细巡检数据包解析失败", e);
            throw new BizException("精细巡检数据包解析失败");
        }
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 2)
    @ApiOperation(value = "解析飞行轨迹文件", notes = "飞行轨迹文件不会进行保存，只会保存解析结果信息")
    @PostMapping(value = "parse/srt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> parseFlightTrackData(@Validated VideoSrtReqVO params){
        try {
            VideoSrtInDTO inDTO = new VideoSrtInDTO();
            inDTO.setVideoId(params.getVideoId());
            inDTO.setInputStream(params.getFile().getInputStream());
            inDTO.setSrtFilename(params.getFile().getOriginalFilename());
            String srtId = dataParseService.parseFlightTrackData(inDTO);
            return Result.ok(srtId);
        } catch (IOException e) {
            log.error("飞行轨迹文件解析失败", e);
            throw new BizException("飞行轨迹文件解析失败");
        }
    }

}
