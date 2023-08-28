package com.imapcloud.nest.pojo.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PubMessageUpdDTO.java
 * @Description PubMessageUpdDTO
 * @createTime 2022年03月22日 18:20:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"logContent","messageContent"})
public class PubMessageUpdDTO {

    private static final long serialVersionUID=1L;

    @NotNull(message = "消息id不能为空，清检查！")
    private Integer id;

    /**
     * 消息编码
     */
    private String messageCode;

    /**
     * 消息内容
     */
//    @NotNull(message = "消息内容不能为空，清检查！")
    private String messageContent;

    /**
     * 消息标题
     */
//    @NotNull(message = "消息标题不能为空，清检查！")
    private String messageTitle;

    /**
     * 消息状态:0 草稿；1未推送；2已推送
     */
//    @NotNull(message = "消息状态不能为空，清检查！")
    private Integer messageState;

    /**
     * 消息类型0更新公告；1其他公告
     */
//    @NotNull(message = "消息类型不能为空，清检查！")
    private Integer messageType;

    /**
     * 消息种类：0公告；1任务
     */
//    @NotNull(message = "消息种类不能为空，清检查！")
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
//    @NotNull(message = "推送的单位不能为空，清检查！")
    private List<String> companyIds;

    /**
     * 定时开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime beginTime;

    /**
     * 基站ID
     */
    private Integer nestId;


}
