package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.common.enums.ImMessageActionEnum;
import com.imapcloud.nest.v2.service.dto.ChannelInfoDTO;
import com.imapcloud.nest.v2.service.dto.in.ImInDTO;

import java.util.List;

/**
 * 即时通讯服务
 *
 * @author boluo
 * @date 2023-02-13
 */
public interface ImService {

    /**
     * im回调
     *
     * @param callbackInDTO 回调dto
     */
    void callback(ImInDTO.CallbackInDTO callbackInDTO);

    /**
     * 切换页面
     *
     * @param pageInDTO 页dto
     */
    void changePage(ImInDTO.PageInDTO pageInDTO);

    /**
     * 获取用户得channel信息
     *
     * @param accountIdList 帐户id列表
     * @param imMessageActionEnumList        需要的消息类型
     * @return {@link List}<{@link ChannelInfoDTO}>
     */
    List<ChannelInfoDTO> channelInfoList(List<String> accountIdList, List<ImMessageActionEnum> imMessageActionEnumList);

    /**
     * 发送消息
     *
     * @param nestListDtoNest 嵌套列表dto巢
     * @param msg             味精
     * @param channelIdList   通道id列表
     */
    void sendMsg(ImMessageActionEnum nestListDtoNest, String msg, List<String> channelIdList);
}
