package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.common.constant.MediaServerConstant;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.dataobj.in.PushStreamCreateInDO;
import com.imapcloud.nest.v2.manager.rest.MediaManager;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.dto.in.UavQueryInDTO;
import com.imapcloud.nest.v2.service.dto.out.UavInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.LivePlayInfoRespVO;
import com.imapcloud.nest.v2.web.vo.resp.PushStreamInfoRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 无人机管理API
 * @author Vastfy
 * @date 2023/03/30 19:11
 * @since 2.3.2
 */
@ApiSupport(author = "wumiao@geoai.com", order = 21)
@Api(value = "无人机管理API", tags = "无人机管理API")
@RequestMapping("v2/uav")
@RestController
public class UosUavController {

    @Resource
    private MediaManager mediaManager;

    @Resource
    private BaseUavService baseUavService;

    /**
     * @param uavId
     * @param serverId
     * @return
     */
    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "获取无人机推流地址", notes = "同一个无人机只会生成唯一一个推流地址")
    @GetMapping("{uavId}/push/stream/url")
    public Result<PushStreamInfoRespVO> getUavPushStreamAddress(@PathVariable String uavId, @RequestParam String serverId) {
        if(StringUtils.isEmpty(uavId)) {
            throw new BusinessException("uavId不能为空");
        }
        PushStreamCreateInDO pushStreamCreateInDO = new PushStreamCreateInDO();
        pushStreamCreateInDO.setStreamName(uavId);
        pushStreamCreateInDO.setServerId(serverId);
        Result<PushStreamInfoRespVO> res = mediaManager.createPushStreamInfo(pushStreamCreateInDO);
        return res;
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 2)
    @ApiOperation(value = "点播无人机图传实时视频", notes = "点播无人机图传")
    @PostMapping("{uavId}/live/play")
    public Result<LivePlayInfoRespVO> playUavLive(@PathVariable String uavId,
                                                  @RequestParam(required = false, defaultValue = "false") Boolean repush) {
        LivePlayInfoRespVO respVO = baseUavService.playUavLive(uavId, repush);
        return Result.ok(respVO);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 3)
    @ApiOperation(value = "分页查询无人机信息", notes = "附带基站信息")
    @GetMapping("query")
    public Result<PageResultInfo<UavInfoOutDTO>> pageUavInfos(UavQueryInDTO condition){
        PageResultInfo<UavInfoOutDTO> pageResultInfo = baseUavService.pageUavInfos(condition);
        return Result.ok(pageResultInfo);
    }

}
