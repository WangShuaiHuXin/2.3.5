package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.PubMessageEntity;
import com.imapcloud.nest.model.PubUserMessageEntity;
import com.imapcloud.nest.pojo.dto.message.PubMessageSaveDTO;
import com.imapcloud.nest.pojo.dto.message.PubMessageUpdDTO;
import com.imapcloud.nest.utils.RestRes;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author double
 * @since 2022-03-22
 */
public interface PubMessageService extends IService<PubMessageEntity> {

    /**
     *  保存接口
     * @param pubMessageDTO
     * @return
     */
    public RestRes savePubMessage(PubMessageSaveDTO pubMessageDTO);

    /**
     *  修改接口
     * @param pubMessageDTO
     * @return
     */
    public RestRes updatePubMessage(PubMessageUpdDTO pubMessageDTO);


    /**
     *  删除接口
     * @param ids
     * @return
     */
    public RestRes deleteBatchPubMessage(String ids);

    /**
     *  删除接口
     * @param id
     * @return
     */
    public RestRes deletePubMessage(Integer id);

    /**
     *  查询接口
     * @param id
     * @return
     */
    public RestRes queryPubMessage(Integer id);

    /**
     * 分页查询
     * @param page
     * @param limit
     * @param messageId
     * @param messageState
     * @param messageType
     * @param messageClass
     * @param userId
     * @param messageTitle
     * @return
     */
    public RestRes queryPubMessagePage(Integer page, Integer limit,
                                       Integer messageId,Integer messageState,
                                       Integer messageType,Integer messageClass,
                                       Long userId,String messageTitle);

    /**
     *  更新日志分页查询
     * @param page
     * @param limit
     * @param messageId
     * @param messageState
     * @param messageType
     * @param messageClass
     * @param userId
     * @return
     */
    public RestRes queryPubMessageLogPage(Integer page, Integer limit,
                                       Integer messageId,Integer messageState,
                                       Integer messageType,Integer messageClass,
                                       Long userId);

    /**
     * 更改阅读状态接口
     * @param messageId
     * @param userId
     * @param allRead
     * @param messageClass
     * @return
     */
    public RestRes updatePubMessageState(Integer messageId,Long userId,Integer allRead,Integer messageClass);

    /**
     * 推送接口
     * @param id
     * @param nestId
     * @return
     */
    public RestRes pushPubMessage(Integer id,String nestId);

    /**
     *  保存并推送接口
     * @param pubMessageDTO
     * @return
     */
    public RestRes saveAndPushPubMessage(PubMessageSaveDTO pubMessageDTO);

    /**
     *  保存并推送接口-针对计划任务
     * @param pubMessageDTO
     * @return
     */
    public RestRes saveAndPushPubMessageForTask(PubMessageSaveDTO pubMessageDTO);

    /**
     * 推送主功能
     * @param pubMessage
     */
    public void pushMain(PubMessageEntity pubMessage);

    /**
     *  新增自身推送人
     * @param pubMessage
     * @param pubUserMessageEntityList
     * @return
     */
    public List<PubUserMessageEntity> addSenderMessage(PubMessageEntity pubMessage, List<PubUserMessageEntity> pubUserMessageEntityList);

    /**
     *  获取数量
     * @param userId
     * @param readState
     * @param messageClass
     * @return
     */
    public Integer getReadStateCount(Long userId , Integer readState,Integer messageClass);

//    /**
//     *  上传接口-消息公告
//     * @deprecated 2.2.3，接口废弃，将在后续版本删除
//     */
//    @Deprecated
//    RestRes uploadForMessage(String mediaFile, @RequestParam MultipartFile fileData);

}
