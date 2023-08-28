package com.imapcloud.nest.controller;


import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.annotation.SysLogIgnoreParam;
import com.imapcloud.nest.enums.message.PubMessageTypeEnum;
import com.imapcloud.nest.pojo.dto.message.PubMessageSaveDTO;
import com.imapcloud.nest.pojo.dto.message.PubMessageUpdDTO;
import com.imapcloud.nest.service.PubMessageService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  PubMessageController
 * </p>
 *
 * @author double
 * @since 2022-03-22
 */
@RestController
@RequestMapping("/nest/message")
public class PubMessageController {

    @Resource
    private PubMessageService pubMessageService;

    /**
     * 消息保存
     */
    @PostMapping("/save")
    @ResponseBody
    @SysLogIgnoreParam(value = "保存接口，参数PubMessageSaveDTO pubMessageDTO")
    public RestRes save(@RequestBody PubMessageSaveDTO pubMessageDTO) {
        return this.pubMessageService.savePubMessage(pubMessageDTO);
    }

    /**
     * 消息修改
     */
    @PostMapping("/update")
    @ResponseBody
    @SysLogIgnoreParam(value = "更新接口，PubMessageUpdDTO pubMessageUpdDTO")
    public RestRes update(@RequestBody PubMessageUpdDTO pubMessageUpdDTO) {
        return this.pubMessageService.updatePubMessage(pubMessageUpdDTO);
    }

    /**
     * 消息删除
     */
    @GetMapping("/delete/{id}")
    @ResponseBody
    public RestRes delete(@PathVariable("id") String id) {
        return this.pubMessageService.deleteBatchPubMessage(id);
    }

    /**
     * 消息推送
     */
    @GetMapping("/push/{id}")
    @ResponseBody
    public RestRes push(@PathVariable("id") Integer id) {
        return this.pubMessageService.pushPubMessage(id,null);
    }

    /**
     * 消息保存并推送
     */
    @PostMapping("/saveAndPush")
    @ResponseBody
    @SysLogIgnoreParam(value = "保存并推送接口，参数PubMessageSaveDTO pubMessageDTO")
    public RestRes saveAndPush(@RequestBody PubMessageSaveDTO pubMessageDTO) {
        return this.pubMessageService.saveAndPushPubMessage(pubMessageDTO);
    }


    /**
     * 消息分页查询
     */
    @GetMapping("/query")
    @ResponseBody
    public RestRes queryPubMessagePage(@RequestParam Integer page
                                        , @RequestParam Integer limit
                                        , @RequestParam Integer messageId
                                        , @RequestParam Integer messageState
                                        , @RequestParam Integer messageType
                                        , @RequestParam Integer messageClass
                                        , @RequestParam Long userId
                                        , @RequestParam String messageTitle) {
        return this.pubMessageService.queryPubMessagePage(page, limit, messageId, messageState, messageType, messageClass, userId , messageTitle);
    }

    /**
     * 日志分页查询
     */
    @GetMapping("/log/query")
    @ResponseBody
    public RestRes queryPubMessageLogPage(@RequestParam Integer page, @RequestParam Integer limit,
                                       @RequestParam  Integer messageId, @RequestParam  Integer messageState,
                                       @RequestParam  Integer messageType,@RequestParam Integer messageClass,@RequestParam Long userId) {
        return this.pubMessageService.queryPubMessageLogPage(page,limit,messageId,messageState,messageType,messageClass ,userId);
    }

    /**
     * 消息详情查询
     */
    @GetMapping("/queryOne/{id}")
    @ResponseBody
    public RestRes queryPubMessage(@PathVariable("id") Integer id) {
        return this.pubMessageService.queryPubMessage(id);
    }

    /**
     * 消息已读
     */
    @GetMapping("/updateState/{allRead}/{userId}/{messageClass}")
    @ResponseBody
    public RestRes updatePubMessageState(@RequestParam("id") Integer id
                                    , @PathVariable("userId") @NotNull(message = "{geoai_uos_user_id_cannot_be_empty}") Long userId
                                    , @PathVariable("allRead") @NotNull(message = "{geoai_uos_all_read_marks_cannot_be_empty}") Integer allRead
                                    , @PathVariable("messageClass") @NotNull(message = "{geoai_uos_message_type_cannot_be_empty}") Integer messageClass) {
        return this.pubMessageService.updatePubMessageState(id,userId,allRead,messageClass);
    }

    /**
     * 获取公告类型
     */
    @GetMapping("/getMessageType")
    @ResponseBody
    @SysLogIgnoreParam(value = "获取公告类型接口，参数")
    public RestRes getMessageType() {
        Map<String,Object> returnMap = new HashMap(8);
        returnMap.put("pubMessageTypeEnum",PubMessageTypeEnum.toList());
        return RestRes.ok(returnMap);
    }

    /**
     * 获取消息数量
     */
    @GetMapping("/getMessageState")
    @ResponseBody
    public RestRes getReadStateCount(@RequestParam("userId") Long userId, @RequestParam("readState") Integer readState,@RequestParam("messageClass") Integer messageClass) {
        Integer count = this.pubMessageService.getReadStateCount(userId,readState,messageClass);
        return RestRes.ok("readStateCount",count, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SEARCH_SUCCESS.getContent()));
    }

//    /**
//     * @deprecated 2.2.3，该接口废弃，统一使用文件上传接口
//     */
//    @Deprecated
//    @PostMapping("/upload")
//    @ResponseBody
//    @SysLogIgnoreParam(value = "上传文件接口-给消息公告使用，参数String mediaFile,MultipartFile fileData")
//    public RestRes uploadForMessage(String mediaFile, @RequestParam MultipartFile fileData) {
//        return this.pubMessageService.uploadForMessage(mediaFile,fileData);
//    }

}

