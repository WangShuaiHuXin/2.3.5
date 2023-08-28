package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.PubMessageEntity;
import com.imapcloud.nest.pojo.vo.PubMessageVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author double
 * @since 2022-03-22
 */
public interface PubMessageMapper extends BaseMapper<PubMessageEntity> {

    /**
     * 获取公告消息
     * @param page
     * @param messageId
     * @param messageState
     * @param messageType
     * @param messageClass
     * @param userId
     * @return
     */
    IPage<PubMessageVO> getMessagePage(@Param("page") IPage<PubMessageVO> page,
                                       @Param("messageId") Integer messageId,
                                       @Param("messageState") Integer messageState,
                                       @Param("messageType") Integer messageType,
                                       @Param("messageClass") Integer messageClass,
                                       @Param("userId") Long userId,
                                       @Param("messageTitle") String messageTitle);

    /**
     *  获取草稿状态消息
     * @param page
     * @param messageId
     * @param messageState
     * @param messageType
     * @param messageClass
     * @param userId
     * @return
     */
    IPage<PubMessageVO> getDraftMessagePage(@Param("page") IPage<PubMessageVO> page,
                                            @Param("messageId") Integer messageId,
                                            @Param("messageState") Integer messageState,
                                            @Param("messageType") Integer messageType,
                                            @Param("messageClass") Integer messageClass,
                                            @Param("userId") Long userId,
                                            @Param("messageTitle") String messageTitle);

    /**
     * 获取公告消息
     * @param page
     * @param messageId
     * @param messageState
     * @param messageType
     * @param messageClass
     * @param userId
     * @return
     */
    IPage<PubMessageVO> getMessageLogPage(@Param("page") IPage<PubMessageVO> page,
                                       @Param("messageId") Integer messageId,
                                       @Param("messageState") Integer messageState,
                                       @Param("messageType") Integer messageType,
                                       @Param("messageClass") Integer messageClass,
                                       @Param("userId") Long userId);

    /**
     *  获取草稿状态消息
     * @param page
     * @param messageId
     * @param messageState
     * @param messageType
     * @param messageClass
     * @param userId
     * @return
     */
    IPage<PubMessageVO> getDraftMessageLogPage(@Param("page") IPage<PubMessageVO> page,
                                            @Param("messageId") Integer messageId,
                                            @Param("messageState") Integer messageState,
                                            @Param("messageType") Integer messageType,
                                            @Param("messageClass") Integer messageClass,
                                            @Param("userId") Long userId);

}
