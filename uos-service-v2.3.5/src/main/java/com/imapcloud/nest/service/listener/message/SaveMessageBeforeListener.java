package com.imapcloud.nest.service.listener.message;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ValidateException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.convert.PubMessageSaveToEntityConvert;
import com.imapcloud.nest.enums.message.PubMessageStateEnum;
import com.imapcloud.nest.enums.message.PubMessageTypeEnum;
import com.imapcloud.nest.model.PubMessageEntity;
import com.imapcloud.nest.pojo.dto.message.PubMessageSaveDTO;
import com.imapcloud.nest.service.event.message.SaveMessageBeforeEvent;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName SaveMessagedBeforeListener.java
 * @Description SaveMessagedBeforeListener
 * @createTime 2022年04月14日 15:12:00
 */
@Slf4j
@Service
public class SaveMessageBeforeListener extends AbstractEventListener<SaveMessageBeforeEvent> {
    /**
     * 消息保存前监听-逻辑校验 -可拆分，可合并为一个校验
     *
     * @param saveMessageBeforeEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(SaveMessageBeforeEvent saveMessageBeforeEvent) {
        log.info("【SaveMessageBeforeListener】:{}", saveMessageBeforeEvent.toString());
        PubMessageSaveDTO pubMessageSaveDTO = (PubMessageSaveDTO) saveMessageBeforeEvent.getSource();
        String msg = "";
        try {
            validate(pubMessageSaveDTO);
        } catch (ValidateException e) {
            e.printStackTrace();
            msg = e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            msg = e.getMessage();
        } finally {
            if (StringUtils.isNotEmpty(msg)) {
                throw new NestException(msg);
            }
        }
        PubMessageEntity pubMessageEntity = (PubMessageEntity) saveMessageBeforeEvent.getV();
        log.info("【SaveMessageBeforeListener】DTO->{}",pubMessageSaveDTO.toString());
        PubMessageSaveToEntityConvert.INSTANCES.updatePubMessageEntity(pubMessageSaveDTO,pubMessageEntity);
        log.info("【SaveMessageBeforeListener】entity->{}",pubMessageEntity.toString());
    }

    public void validate(PubMessageSaveDTO pubMessageSaveDTO) {
        //校验对象
        if (pubMessageSaveDTO == null) {
            throw new ValidateException(MessageUtils.getMessage(MessageEnum.GEOAI_AUTH_THE_PASSED_OBJECT_IS_NULL.getContent())
            );
        }
        //校验组织
        if (CollectionUtil.isEmpty(pubMessageSaveDTO.getCompanyIds())) {
            throw new ValidateException(MessageUtils.getMessage(MessageEnum.GEOAI_AUTH_PLEASE_SELECT_THE_UNIT_FIRST.getContent()));
        }
        //校验消息内容字段长度
        if (StringUtil.isNotEmpty(pubMessageSaveDTO.getMessageContent())
                && pubMessageSaveDTO.getMessageContent().length() > 10000) {
            throw new ValidateException(MessageUtils.getMessage(MessageEnum.GEOAI_AUTH_MESSAGE_CONTENT_FIELD_LENGTH_GREATER_THAN_10000.getContent()));
        }
        //校验系统版本字段长度
        if (StringUtil.isNotEmpty(pubMessageSaveDTO.getSysVersion())
                && pubMessageSaveDTO.getSysVersion().length() > 20) {
            throw new ValidateException(MessageUtils.getMessage(MessageEnum.GEOAI_AUTH_SYSTEM_VERSION_FIELD_LENGTH_GREATER_THAN_20.getContent()));
        }
        //校验更新公告-版本号必填
        if (PubMessageTypeEnum.MESSAGE_TYPE_0.getCode().equals(pubMessageSaveDTO.getMessageType())
                && !PubMessageStateEnum.MESSAGE_STATE_0.equals(pubMessageSaveDTO.getMessageState())
                && StringUtil.isEmpty(pubMessageSaveDTO.getSysVersion())) {
            throw new ValidateException(MessageUtils.getMessage(MessageEnum.GEOAI_AUTH_UPDATE_BULLETIN_VERSION_NUMBER_FIELD_CANNOT_BE_EMPTY.getContent()));
        }
        //校验更新日志长度大小-限制为10000
        if (StringUtil.isNotEmpty(pubMessageSaveDTO.getLogContent())
                && pubMessageSaveDTO.getLogContent().length() > 10000) {
            throw new ValidateException(MessageUtils.getMessage(MessageEnum.GEOAI_AUTH_MESSAGE_UPDATE_LOG_FIELD_LENGTH_GREATER_THAN_10000.getContent()));
        }

        //校验进行保存动作状态是否为空，为空默认赋值为草稿态
        if( pubMessageSaveDTO.getMessageState() == null ){
            pubMessageSaveDTO.setMessageState(PubMessageStateEnum.MESSAGE_STATE_0.getCode());
        }
        //逻辑校验
    }


}
