package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.service.UosSensorAudioService;
import com.imapcloud.nest.v2.service.dto.out.AudioOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.pojo.BaseResult3;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosSensorAudioServiceImpl.java
 * @Description UosSensorAudioServiceImpl
 * @createTime 2022年08月17日 16:14:00
 */
@Service
public class UosSensorAudioServiceImpl implements UosSensorAudioService {

    @Resource
    private NestService nestService;


    /**
     * 删除喊话器音频
     *
     * @param audioId
     * @return
     */
    @Override
    public boolean deleteAudio(String nestId , Integer audioId) {
        ComponentManager cm = linkNest(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getAccessoryManagerCf().speakerDeleteAudio(CollectionUtil.newArrayList(audioId));
            if(!res.isSuccess()){
                throw new BusinessException(res.getMsg());
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 重置喊话器
     *
     * @param nestId
     * @return
     */
    @Override
    public boolean resetAudio(String nestId) {
        ComponentManager cm = linkNest(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getAccessoryManagerCf().resetSpeaker();
            if(!res.isSuccess()){
                throw new BusinessException(res.getMsg());
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 获取喊话器列表
     *
     * @param nestId
     * @return
     */
    @Override
    public List<AudioOutDTO> getAudioList(String nestId) {
        List<AudioOutDTO> audioOutDTOS = new ArrayList<>();
        ComponentManager cm = linkNest(nestId);
        if (cm != null) {
            MqttResult<BaseResult3> res = cm.getAccessoryManagerCf().listSpeakerPlay();
            if(!res.isSuccess()){
                throw new BusinessException(res.getMsg());
            }
            BaseResult3 baseResult3 = res.getRes();
            String param  = Optional.ofNullable(baseResult3).map(BaseResult3::getParam).orElseGet(()->"");
            if(StringUtils.isEmpty(param)){

            }
            audioOutDTOS = JSONUtil.toList(param,AudioOutDTO.class);
        }
        return audioOutDTOS;
    }

    /**
     *
     * @param nestId
     * @return
     */
    public ComponentManager linkNest(String nestId){
        NestEntity nest = nestService.getNestByIdIsCache(Integer.parseInt(nestId));
        ComponentManager cm = ComponentManagerFactory.getInstance(nest.getUuid());
        // 查看机巢是否连接,未连接则连接机巢
        if (ToolUtil.isEmpty(cm)) {
            nestService.initNest(nestId);
            cm = ComponentManagerFactory.getInstance(nest.getUuid());
        }
        return cm;
    }
}
