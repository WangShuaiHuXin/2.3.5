package com.imapcloud.nest.v2.manager.job;

import cn.hutool.core.collection.CollUtil;
import com.geoai.common.core.bean.DefaultTrustedAccessInformation;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.ResultUtils;
import com.google.common.collect.Maps;
import com.imapcloud.nest.mapper.MissionPhotoMapper;
import com.imapcloud.nest.pojo.dto.MissionPhotoVideoTotalDTO;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.feign.OrgServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 照片和视频处理器
 *
 * @author boluo
 * @date 2022-09-21
 */
@Slf4j
@Component
public class TotalPhotoAndVideoProcessor implements BasicProcessor {

    @Resource
    private OrgServiceClient orgServiceClient;

    @Resource
    private MissionPhotoMapper missionPhotoMapper;

    @Resource
    private RedisService redisService;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        try {
            log.info("#TotalPhotoAndVideoProcessor.process#");
            // 设置上下文
            DefaultTrustedAccessInformation trustedAccessInformation = new DefaultTrustedAccessInformation();
            trustedAccessInformation.setOrgCode("000");
            trustedAccessInformation.setAccountId("-1");
            trustedAccessInformation.setUsername("SYSTEM");
            TrustedAccessTracerHolder.set(trustedAccessInformation);

            Result<List<OrgSimpleOutDO>> listResult = orgServiceClient.listAllOrgSimpleInfos();
            List<OrgSimpleOutDO> orgSimpleOutDOList = ResultUtils.getData(listResult);
            if (CollUtil.isEmpty(orgSimpleOutDOList)) {
                return new ProcessResult(false, "查询单位信息失败");
            }
            for (OrgSimpleOutDO orgSimpleOutDO : orgSimpleOutDOList) {

                String orgCode = orgSimpleOutDO.getOrgCode();
                Map<String, Object> resMap = Maps.newHashMap();
                // 图片、视频总统计
                Map map = missionPhotoMapper.getTotalPhotoAndVideo(null, null, orgCode);
                resMap.put("total", map);

                // 根据标签获取图片、视频统计
                List<MissionPhotoVideoTotalDTO.PhotoTagBean> photoList = missionPhotoMapper.getTotalPhotoByTag(null, null, orgCode);
                List<MissionPhotoVideoTotalDTO.VideoTagBean> videoList = missionPhotoMapper.getTotalVideoByTag(null, null, orgCode);
                resMap.put("photoList", photoList);
                resMap.put("videoList", videoList);

                String key = String.format(RedisKeyConstantList.TOTAL_PHOTO_AND_VIDEO, orgCode);
                redisService.set(key, resMap);
            }
            return new ProcessResult(true, "处理成功");
        } finally {
            TrustedAccessTracerHolder.get();
        }
    }
}
