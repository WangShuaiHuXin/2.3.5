package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.UosSensorAudioService;
import com.imapcloud.nest.v2.service.dto.out.AudioOutDTO;
import com.imapcloud.nest.v2.web.transformer.AudioTransformer;
import com.imapcloud.nest.v2.web.vo.resp.AudioRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosAudioController.java
 * @Description UosAudioController
 * @createTime 2022年08月16日 11:57:00
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "", tags = "喊话器")
@RequestMapping("v2/sensor/audio/")
@RestController
public class UosSensorAudioController {

    @Resource
    private UosSensorAudioService uosSensorAudioService;

    /**
     *  删除喊话器音频
     * @param audioId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "删除喊话器音频", notes = "删除喊话器音频")
    @DeleteMapping("/deleteAudio/{nestId}")
    public Result<Boolean> deleteAudio(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空！清检查！") String nestId
                                       ,@PathVariable("audioId") @Valid @NotNull(message = "{geoai_uos_cannot_empty_audioid}") String audioId ){
        Boolean bol = this.uosSensorAudioService.deleteAudio(nestId, Integer.valueOf(audioId));
        return Result.ok(bol);
    }

    /**
     *  重置喊话器
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "重置喊话器", notes = "重置喊话器")
    @GetMapping("/resetAudio/{nestId}")
    public Result<Boolean> resetAudio(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空！清检查！") String nestId){
        Boolean bol = this.uosSensorAudioService.resetAudio(nestId);
        return Result.ok();
    }

    /**
     *  获取喊话器数据
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 3)
    @ApiOperation(value = "获取喊话器数据", notes = "获取喊话器数据")
    @GetMapping("/getAudioList/{nestId}")
    public Result<List<AudioRespVO>> getAudioList(@PathVariable("resultId") @Valid @NotNull(message = "nestId 不能为空！清检查！") String nestId){
        List<AudioOutDTO> audioOutDTOS = this.uosSensorAudioService.getAudioList(nestId);
        List<AudioRespVO> audioRespVOS = audioOutDTOS.stream()
                .map(AudioTransformer.INSTANCES::transform)
                .collect(Collectors.toList());
        return Result.ok(audioRespVOS);
    }

}
