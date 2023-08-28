package com.imapcloud.nest.pojo.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PubMessageSaveDTO.java
 * @Description PubMessageSaveDTO
 * @createTime 2022年03月22日 18:20:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"logContent","messageContent"})
public class PubMessageSaveDTO {

    private static final long serialVersionUID=1L;

    private Integer id;

    /**
     * 消息编码
     */
    private String messageCode;

    /**
     * 消息内容
     */
    @NotNull(message = "{geoai_uos_push_message_content_cannot_be_empty}")
    private String messageContent;

    /**
     * 消息标题
     */
    @NotNull(message = "{geoai_uos_message_title_cannot_be_empty}")
    private String messageTitle;

    /**
     * 消息状态:0 草稿；1未推送；2已推送
     */
    @NotNull(message = "{geoai_uos_message_status_cannot_be_empty}")
    private Integer messageState;

    /**
     * 消息类型0更新公告；1其他公告
     */
    @NotNull(message = "{geoai_uos_message_type_cannot_be_empty}")
    private Integer messageType;

    /**
     * 消息种类：0公告；1任务
     */
    @NotNull(message = "{geoai_uos_message_type_cannot_be_empty}")
    private Integer messageClass;

    /**
     * 更新日志-富文本
     */
    private String logContent;

    /**
     * 系统版本
     */
    private String sysVersion;

    /**
     * 创建用户id
     */
    private Long createUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 组织id
     */
    @NotNull(message = "{geoai_uos_push_unit_cannot_be_empty}")
    private List<String> companyIds;

    /**
     * 定时开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime beginTime;

    /**
     * 基站ID
     */
    private String nestId;

}
