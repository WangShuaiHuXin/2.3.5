package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.v2.manager.cps.CpsMediaManager;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.nest.v2.service.CpsMediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * media指令
 *
 * @author boluo
 * @date 2023-04-13
 */
@Slf4j
@Service
public class CpsMediaServiceImpl implements CpsMediaService {

    @Resource
    private CpsMediaManager cpsMediaManager;

    @Resource
    private MissionPhotoService missionPhotoService;

    @Resource
    private BaseNestManager baseNestManager;

    @Override
    public void dataSynStop(String nestId, Integer which) {

        cpsMediaManager.resetMediaManager(nestId, which);

        List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectByNestId(nestId);
        if (CollUtil.isEmpty(baseNestEntityOutDOList)) {
            return;
        }
        BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO = baseNestEntityOutDOList.get(0);
        String message = WebSocketRes.err()
                .topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP)
                .msg("geoai_uos_cancel_data_synchronization")
                .toJSONString();
        log.info("#CpsMediaServiceImpl.dataSynStop# message={}", message);
        missionPhotoService.pushPhotoTransMsgByWs(baseNestEntityOutDO.getUuid(), message);
    }
}
